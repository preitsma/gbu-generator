package nl.amis.gbugen;

import nl.amis.gbugen.db.DesignerObject;
import nl.amis.gbugen.db.DesignerObjectDao;
import nl.amis.gbugen.db.ObjectType;
import nl.amis.gbugen.db.WorkArea;
import nl.amis.gbugen.db.WorkAreaDao;
import nl.amis.gbugen.file.DDLGenerator;
import nl.amis.gbugen.file.DropTriggerScriptGenerator;
import nl.amis.gbugen.file.FileGenerator;
import nl.amis.gbugen.file.FormsGenerator;
import nl.amis.gbugen.file.TAPIGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class that steers the generation.
 * 
 * 
 * @author preitsma
 */
public class Generator {

    private static Logger log = LoggerFactory.getLogger(Generator.class);
    
    private GbuGenProperties props = new GbuGenProperties();     

    /**
     * Main method
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        new Generator().doMain(args);
    }

    private void doMain(String[] args) {
        
        props.processOptions(args);
        
        FileGenerator.props = props;

        WorkAreaDao waDao = new WorkAreaDao(props.reposUrl, props.reposUsername, props.reposPassword);          
        WorkArea wa = waDao.queryAllObjects(props.workArea, props.configuration);
                       
        DesignerObjectDao dDao = new DesignerObjectDao(props.reposUrl, props.reposUsername, props.reposPassword);                   
        
        log.debug("querying parent packages of functions...");
        for (DesignerObject function : wa.getObjectsOfType(ObjectType.FUNCTION)) {
            wa.addObject( dDao.findPackageWithProcedureAndFunction(function) );
        }
        
        log.debug(" querying parent packages of procedures...");
        for (DesignerObject proc : wa.getObjectsOfType(ObjectType.PROCEDURE)) {
            wa.addObject( dDao.findPackageWithProcedureAndFunction(proc) );
        }
        
        log.debug(" querying forms with reusable modules...");
        for (DesignerObject reusableMco : wa.getObjectsOfType(ObjectType.REUSABLE_MCO)) {
            wa.addObjects( dDao.findFormsWithReusableModuleComponent(reusableMco) );
        }
        
        wa.removeDuplicates();
                
        log.info("WorkArea dump \n" + wa.toString());  
        
        FileGenerator.workArea = wa;

        DropTriggerScriptGenerator dropTriggerScriptGenerator = new DropTriggerScriptGenerator();
        if (dropTriggerScriptGenerator.isIncluded()) dropTriggerScriptGenerator.generate();

        DDLGenerator ddlGenerator = new DDLGenerator();
        if (ddlGenerator.isIncluded()) ddlGenerator.generate();

        TAPIGenerator tapiGenerator = new TAPIGenerator();
        if (tapiGenerator.isIncluded()) tapiGenerator.generate();
        
        FormsGenerator formsGenerator = new FormsGenerator();
        if (formsGenerator.isIncluded()) formsGenerator.generate();
        
        //LibraryGenerator libraryGenerator = new LibraryGenerator(wa);
        //libraryGenerator.generate();

    }
  
}
