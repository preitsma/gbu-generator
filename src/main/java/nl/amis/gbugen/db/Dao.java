package nl.amis.gbugen.db;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Superclass for the Dao's (data access objects)
 * 
 *
 * @author preitsma
 */
abstract public class Dao {

    protected EntityManagerFactory entityManagerFactory = null;
    protected Logger log = LoggerFactory.getLogger(WorkAreaDao.class);

    /**
     * Constructor initializes an EntityManagerFactory with the right
     * connectstring and db credentials.
     * 
     * @param url
     * @param username
     * @param password 
     */
    public Dao(String url, String username, String password) {
        Map daoProps = new HashMap();
        daoProps.put("hibernate.connection.url", url);
        daoProps.put("hibernate.connection.username", username);
        daoProps.put("hibernate.connection.password", password);
        entityManagerFactory = Persistence.createEntityManagerFactory("generic", daoProps);
        
        log.info("checking for database presence..");        
        assert(databaseIsThere());
        log.info("present");
    } 

    //test for database presence.
    private boolean databaseIsThere() {
        try {
            EntityManager em = entityManagerFactory.createEntityManager();
            String result = (String) em.createNativeQuery("select * from dual").getSingleResult();
            return true;
        } catch (Exception e) {
            log.error("database exception", e);
            return false;
        } finally {
            destroy();
        }
    }

    /**
     * cleans up the EntityManagerFactory
     */
    protected void destroy() {
        entityManagerFactory.close();
    }
}
