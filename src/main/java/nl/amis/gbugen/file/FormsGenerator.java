package nl.amis.gbugen.file;

import java.io.IOException;
import nl.amis.gbugen.db.ObjectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Forms Generator, generates the Forms GBU file
 * 
 * @author preitsma
 */
public class FormsGenerator extends FileGenerator {

    private Logger log = LoggerFactory.getLogger(DDLGenerator.class);   
    
    @Override
    protected String getGenType() {
        return "fmb";
    }

    /**
     * Main class that sequentially builds up the GBU.
     * 
     */
    @Override
    public void generate() {
        log.info("---- start generating forms gbu file    ----");
        try {

            openFile();

            writeHeader();

            writeBytes(new byte[]{0x00, 0x00, 0x00, 0x00});

            writeObjectDefinition();
            
            //for some reasons the amount of objects take 2 bytes in the forms gbu, 
            //so no postfix
            countPostfix = new byte[] {};
                     
            //objects        
            writeObjectsOfType(ObjectType.GENERAL_MODULE, "%s.fmb");

            //templates
            String template = props.formsTemplate;
            writeObjectsOfType(ObjectType.GENERAL_MODULE, template);

            //olb
            writeBytes(new byte[]{0x00, 0x00});
            String objectLibrary = props.objectLibrary;
            writeObjectsOfType(ObjectType.GENERAL_MODULE, objectLibrary);

            writeBytes(0x00, 261);

            //short names take a count postfix again                       
            countPostfix = new byte[] {0x00, 0x00};
            writeObjectsOfType(ObjectType.GENERAL_MODULE);

            //closing part
            writeClosingPart();
            
            writeLogFileName();

        } catch (IOException ioe) {
            log.error("error ocurrend writing the forms file", ioe);
        } finally {
            closeFile();
        }
        log.info("---- finished writing to forms file ----\n");

    }

    /**
     * Write byte sequence to denote object type DDL into GBU stream
     */
    private void writeObjectDefinition() throws IOException {
        log.info("writing object definition");
        writeBytes(new byte[]{0x00, 0x00, 0x00, 0x00});
        writeBytes(new byte[]{0x03, 0x00});
    }       

    protected void writeClosingPart() throws IOException {
        writeBytes(0x00, 24);
    }
}
