package nl.amis.gbugen.db;

import java.util.List;
import javax.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dao object to query Triggers
 * 
 * @author preitsma
 */
public class TriggerDao extends Dao {

    private Logger log = LoggerFactory.getLogger(TriggerDao.class);   

    /**
     * Constructor 
     * 
     * @param url
     * @param username
     * @param password 
     */
    public TriggerDao(String url, String username, String password) {
       super(url, username, password);
    }  

    /**
     * Queries the triggers of a certain table
     * 
     * @param tableName
     * @return 
     */
    public List<String> queryTriggersForTable(String tableName) {

        String triggersForTableQuery =
                  "select trigger_name             "
                + "from   all_triggers             "
                + "where  table_name = :tableName  ";
                             
        try {
            EntityManager em = entityManagerFactory.createEntityManager();
            
            return em.createNativeQuery(triggersForTableQuery)
                    .setParameter("tableName", tableName)
                    .getResultList();
        } catch (Exception e) {
            log.error("error ocurred during query of triggers", e);
        } finally {
            destroy();
        }  
        
        return null;
    }
   
}
