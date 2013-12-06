package nl.amis.gbugen.db;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Model object for a Designer WorkArea
 *
 * @author preitsma
 */
public class WorkArea {
    
    private Logger log = LoggerFactory.getLogger(WorkArea.class);   

    private String name;
    private List<DesignerObject> objects = null;

    /**
     * Workarea name
     *
     * @return name of the WorkArea
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns all DesignerObjects in the WorkArea
     *
     * @return
     */
    public List<DesignerObject> getObjects() {
        return objects;
    }

    public void setObjects(List<DesignerObject> objects) {
        this.objects = objects;
    }

    public void addObject(DesignerObject newObject) {
        this.objects.add(newObject);
    }

    public void addObjects(List<DesignerObject> newObjects) {
        this.objects.addAll(newObjects);
    }

    /**
     * Returns all the DesignerObjects of a certain type.
     *
     * @param type
     * @return
     */
    public List<DesignerObject> getObjectsOfType(ObjectType type) {
        List<DesignerObject> filteredList = new ArrayList();

        for (DesignerObject o : this.objects) {
            if (o.getType().equals(type.name())) {
                filteredList.add(o);
            }
        }

        return filteredList;
    }

    /**
     * Returns all the configurations of the WorkArea's
     *
     * @return
     */
    public Set<String> getConfigurations() {
        Set configs = new HashSet();
        for (DesignerObject o : this.objects) {
            configs.add(o.getConfiguration());
        }
        return configs;
    }

    public void removeDuplicates() {
        
        Set<DesignerObject> s = new TreeSet<DesignerObject>(new Comparator<DesignerObject>() {
            @Override
            public int compare(DesignerObject o1, DesignerObject o2) {
                if (o1.getIRID().equals(o2.getIRID()) && o1.getType().equals(o2.getType()) ) {
                    log.debug("...removing duplicate " + o1.getType() + " " + o1.getName());
                    return 0;
                } else {
                    return 1;
                }
            }
        });
        
        log.debug("Removing duplicates, original amount = " + objects.size() + "...");
        
        s.addAll(objects);        
        objects = new ArrayList();
                
        objects.addAll(s);
        
        log.debug("...after removal, amount = " + objects.size() + "...");                  
    }

    /**
     * Dumps the contents of the WorkArea
     *
     * @return
     */
    @Override
    public String toString() {
        String out = "";
        out += ".. amount of objects loaded: " + getObjects().size() + "\n";

        for (ObjectType type : ObjectType.values()) {
            out += ".amount of " + type.name() + " objects: " + getObjectsOfType(type).size() + "\n";
        }

        out += "coming from the following " + getConfigurations().size() + " configurations:\n";
        for (String config : getConfigurations()) {
            out += "." + config + "\n";
        }

        return out;
    }
}
