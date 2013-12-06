package nl.amis.gbugen.file;

import java.io.IOException;
import nl.amis.gbugen.db.ObjectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TAPI Generator, generates the TAPI GBU file.
 * 
 * @author preitsma
 */
public class TAPIGenerator extends DDLGenerator {

    private Logger log = LoggerFactory.getLogger(DDLGenerator.class);
    
    @Override
    protected String getGenType() {
        return "tapi";
    } 

    /**
     * Main class that sequentially builds up the GBU.
     * 
     */
    @Override
    public void generate() {
        log.info("---- start generating TAPI gbu file    ----"); 
             
        try {
            openFile();

            writeHeader();

            writeBytes(new byte[]{0x00, 0x00, 0x00, 0x00});

            writeObjectDefinition();

            writeTargetDbInfo();

            writeBytes(new byte[]{0x00, 0x00, 0x00, 0x00});
            writeBytes(new byte[]{0x00, 0x00, 0x00, 0x00});

            writeOutputInfo();

            writeObjectProperties();

            //writeUserIRID(); not so relevant for Woningnet

            //now the real work begins

            writeObjectsOfType(ObjectType.TABLE_DEFINITION);

            //real work ends

            writeClosingPart();

            writeLogFileName();

        } catch (IOException ioe) {
            log.error("error ocurrend writing the TAPI gbu file", ioe);
        } finally {
            closeFile();
        }
        log.info("---- finished writing to tapi file ----\n");    
    }

    /**
     * Write byte sequence to denote object type TAPI into GBU stream
     */
    @Override
    void writeObjectDefinition() throws IOException {
        log.info("writing object definition");
        writeBytes(new byte[]{0x05, 0x00, 0x00, 0x00}); // denotes tapi generation
        writeBytes(new byte[]{0x06, 0x00, 0x00, 0x00});
    }

    /**
     * Write byte sequence denoting properties for table generation
     */
    @Override
    void writeObjectProperties() throws IOException {

        writeBytes(new byte[]{0x01, 0x00, 0x00, 0x00, 0x00});
        
    }

    @Override
    void writeClosingPart() throws IOException {
        writeBytes(0x00, 172);
    }

}
