package nl.amis.gbugen.db;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * Dao object to query all objects in a WorkArea
 * 
 * @author preitsma
 */
public class WorkAreaDao extends Dao {

    /**
     * Constructor
     * 
     * @param url
     * @param username
     * @param password 
     */
    public WorkAreaDao(String url, String username, String password) {
        super(url, username, password);
    }

    /**
     * Query all objects 
     * 
     * @param workAreaName
     * @param configuration
     * @return 
     */
    public WorkArea queryAllObjects(String workAreaName, String configuration) {

        log.debug("querying designer objects for workarea '" + workAreaName + "'");
        
        String allObjectsQuery = constructQuery(configuration);

        log.debug("query: \n" + allObjectsQuery);

        WorkArea workArea = new WorkArea();
        workArea.setName(workAreaName);

        try {
            EntityManager em = entityManagerFactory.createEntityManager();
            Query query = em.createNativeQuery(allObjectsQuery, DesignerObject.class);

            if ("".equals(configuration) ) { //if no configuration is given, specify workarea
                  query.setParameter("workArea", workAreaName);
            }

            workArea.setObjects(query.getResultList());
        } catch (Exception e) {
            log.error("error ocurred during query of objects from a workarea", e);
        } finally {
            destroy();
        }

        return workArea;
    }

    private String constructQuery(String configuration) {   
        
        String objectsWithConfigurationQuery =
                  "select *                                                                          \n"
                + "from (select nvl(plm.plsql_module_type, tpe.name) type                            \n"
                + "      ,      member_obj.name name                                                 \n"
                + "      ,      cmb.object_ivid                                                      \n"
                + "      ,      cmb.object_irid irid                                                 \n"
                + "      ,      member_obj.sequence_in_branch obj_seq                                \n"
                + "      ,      member_obj.vlabel obj_vlabel                                         \n"
                + "      ,      cfg.name configuration                                               \n"
                + "      ,      config_obj.sequence_in_branch config_seq                             \n"
                + "      ,      config_obj.vlabel config_vlabel                                      \n"
                + "      ,      row_number() over (partition by  tpe.name                            \n"
                + "                                ,             member_obj.name                     \n"
                + "                                order by member_obj.sequence_in_branch desc ) rn  \n"
                + "      from   i$sdd_configuration_members cmb                                      \n"
                + "      ,      i$sdd_configurations        cfg                                      \n"
                + "      ,      i$sdd_object_versions       config_obj                               \n"
                + "      ,      i$sdd_object_versions       member_obj                               \n"
                + "      ,      i$rm_element_types          tpe                                      \n"
                + "      ,      ci_plsql_modules            plm                                      \n"
                + "      where  cfg.ivid = cmb.config_ivid                                           \n"
                + "      and    cfg.name IN (%s)                                                     \n"
                + "      and    cfg.ivid               = config_obj.ivid                             \n"
                + "      and    cmb.object_ivid        = member_obj.ivid                             \n"
                + "      and    tpe.id                 = member_obj.logical_type_id                  \n"
                + "      and    plm.irid (+)           = cmb.object_irid)                            \n"
                + "where rn=1";

        String configurationsWithWorkAreaQuery =
                  "  select cfg.name                                         \n"
                + "  from  sdd_configurations cfg                            \n"
                + "  ,     sdd_workarea_spec_entries wse                     \n"
                + "  ,     sdd_workareas was                                 \n"
                + "  where cfg.ivid = wse.config_ivid                        \n"
                + "  and   wse.workarea_irid = was.irid                      \n"
                + "  and   cfg.name not like '%Baseline%'                    \n";
              
        if (!"".equals(configuration)) { //given configurations are used in the IN-clause  
            
            return objectsWithConfigurationQuery.replaceFirst("%s", "'" + configuration.replaceAll(",", "','") + "'"); 
            
        }  else {  //otherwise we put in the subquery to find all workarea's
            
           configurationsWithWorkAreaQuery += "and was.name = :workArea      ";          
           return objectsWithConfigurationQuery.replaceFirst("%s", configurationsWithWorkAreaQuery);    
    
        }

    }
}
