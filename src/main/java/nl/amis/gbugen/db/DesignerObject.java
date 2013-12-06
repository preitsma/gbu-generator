package nl.amis.gbugen.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Model object for a Designer Repository Object
 *  
 * @author preitsma
 */
@Entity
public class DesignerObject {
    
    @Id
    @Column(name="irid")
    private String IRID;
    
    @Column(name="name")
    private String name;
    
    @Column(name="obj_vlabel")
    private String objectVersion;
    
    @Column(name="type")
    private String type;
    
    @Column(name="configuration")
    private String configuration; 

    public String getObjectVersion() {
        return objectVersion;
    }

    public void setObjectVersion(String objectVersion) {
        this.objectVersion = objectVersion;
    }

    public String getIRID() {
        return IRID;
    }

    public void setIRID(String irid) {
        this.IRID = irid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }
    
    
        
}
