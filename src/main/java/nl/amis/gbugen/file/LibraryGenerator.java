package nl.amis.gbugen.file;

import java.io.IOException;
import nl.amis.gbugen.db.ObjectType;
import org.hibernate.cfg.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Library Generator, generates the Library GBU file
 * 
 * @author preitsma
 */
public class LibraryGenerator extends FileGenerator {

    private Logger log = LoggerFactory.getLogger(DDLGenerator.class);   

    @Override
    protected String getGenType() {
        return "library";
    }

    @Override
    public void generate() {
         throw new NotYetImplementedException("library generator is not yet implemented");
        
 //       log.info("---- start generating library gbu file    ----");
 //       try {

       
            
//            openFile();
//
//            writeHeader();
//
//         
//            writeObjectDefinition();
//            
//            //for some reasons the amount of objects take 2 bytes in the forms gbu, 
//            //so no postfix
//            countPostfix = new byte[] {};
//
//            //objects        
//            //writeObjectsOfType(ObjectType.GENERAL_MODULE, writeType.APPEND, ".fmb");
//
//          
//            writeBytes(0x00, 261);
//
//            //short names take a count postfix again                       
//            countPostfix = new byte[] {0x00, 0x00};
//            writeObjectsOfType(ObjectType.GENERAL_MODULE);
//
//            //closing part
//            writeClosingPart();
//            
//            writeLogFileName();
//
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        } finally {
//            closeFile();
//        }
 //       log.info("---- finished writing to library file ----\n");

    }

    /**
     * Write byte sequence to denote object type DDL into GBU stream
     */
    void writeObjectDefinition() throws IOException {
        log.info("writing object definition");
        writeBytes(new byte[]{0x00, 0x00, 0x00, 0x00});
        writeBytes(new byte[]{0x00, 0x00, 0x00, 0x00});
        writeBytes(new byte[]{0x03, 0x00, 0x00, 0x00});
    }       

    protected void writeClosingPart() throws IOException {
        writeBytes(0x00, 24);
    }
}
