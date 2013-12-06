package nl.amis.gbugen.db;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Dao object to query single Designer Objects
 *
 * @author preitsma
 */
public class DesignerObjectDao extends Dao {

    /**
     * Constructor
     *
     * @param url
     * @param username
     * @param password
     */
    public DesignerObjectDao(String url, String username, String password) {
        super(url, username, password);
    }

    /**
     * Query the parent packages for a Procedure or Function through Designer
     * ci_module_networks.
     *
     *
     * @param workAreaName
     * @param configuration
     * @return
     */
    public DesignerObject findPackageWithProcedureAndFunction(DesignerObject childObject) {

        log.debug("querying parent of " + childObject.getType() + " " + childObject.getName() +  " met IRID " + childObject.getIRID() + "... ");

        String findParentQuery =
                  "select tpe.name type,                                                        \n "
                + "       parent_obj.name name,                                                 \n "
                + "       parent_obj.irid irid,                                                 \n "
                + "       'derived' configuration ,                                             \n "
                + "       'unimportant' obj_vlabel                                              \n "
                + "from   i$sdd_object_versions       parent_obj                                \n "
                + ",      i$rm_element_types          tpe                                       \n "
                + ",      ci_module_networks          cmn                                       \n "
                + "where  parent_obj.logical_type_id = tpe.id                                   \n "
                + "and    parent_obj.irid            = cmn.parent_module_reference              \n "
                + "and    cmn.child_module_reference = :childIrid                               \n ";

        log.debug("query: \n" + findParentQuery);

        DesignerObject parentObject = null;

        try {
            EntityManager em = entityManagerFactory.createEntityManager();
            Query query = em.createNativeQuery(findParentQuery, DesignerObject.class);

            query.setParameter("childIrid", childObject.getIRID());

            parentObject = (DesignerObject) query.getSingleResult();

        } catch (Exception e) {
            log.error("error ocurred during query of parentOjbect", e);
        } finally {
            destroy();
        }

        log.debug("...found parent " + parentObject.getType() + " " + parentObject.getName() + ".");

        return parentObject;
    }

    /**
     * Query all Forms (GENERAL_MODULES) that include a certain Reusable Module
     * Component
     *
     *
     * @param workAreaName
     * @param configuration
     * @return
     */
    public List<DesignerObject> findFormsWithReusableModuleComponent(DesignerObject childObject) {

        log.debug("querying forms with " + childObject.getType() + " " + childObject.getName() +  " met IRID " + childObject.getIRID() + "... ");

        String findParentQuery =
                  " select type                                                                  \n "
                + " ,      name                                                                  \n "
                + " ,      irid                                                                  \n "
                + " ,      'derived' configuration                                               \n "
                + " ,      'unimportant' obj_vlabel                                              \n "
                + " from  (select obj.name                                                       \n "
                + "        ,      obj.irid                                                       \n "
                + "        ,      obj.sequence_in_branch                                         \n "
                + "        ,      tpe.name type                                                  \n "
                + "        ,      row_number() over (partition by  obj.name                      \n "
                + "                            order by      obj.sequence_in_branch desc ) rn    \n "
                + "        from ci_module_components mco                                         \n "
                + "        ,    ci_module_component_inclusions mci                               \n "
                + "        ,    i$sdd_object_versions obj                                        \n "
                + "        ,    i$rm_element_types    tpe                                        \n "
                + "        where mci.general_module_reference = obj.irid                         \n "
                + "        and   mco.irid = mci.module_component_reference                       \n "
                + "        and   obj.logical_type_id = tpe.id                                    \n "
                + "        and   mci.module_component_reference = :mcoIrid)                      \n "
                + " where rn=1                                                                   \n ";
        
        log.debug("query: \n" + findParentQuery);

        List<DesignerObject> parentObjects = new ArrayList<DesignerObject>();

        try {
            EntityManager em = entityManagerFactory.createEntityManager();
            Query query = em.createNativeQuery(findParentQuery, DesignerObject.class);

            query.setParameter("mcoIrid", childObject.getIRID());

            parentObjects = query.getResultList();

        } catch (Exception e) {
             log.error("error ocurred during query of parentOjbect", e);
        } finally {
            destroy();
        }
        
        for (DesignerObject dObj : parentObjects) {
             log.debug("...found form " + dObj.getType() + " " + dObj.getName() + ".");   
        }
              
        return parentObjects;
        
    
      }
}
