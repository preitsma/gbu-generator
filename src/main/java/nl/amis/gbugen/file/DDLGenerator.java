package nl.amis.gbugen.file;

import java.io.IOException;
import nl.amis.gbugen.db.ObjectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DDLGenerator, generates the DDL GBU file
 * 
 * @author preitsma
 */
public class DDLGenerator extends FileGenerator {

    private Logger log = LoggerFactory.getLogger(DDLGenerator.class); 
    
    @Override
    protected String getGenType() {
        return "ddl";
    }   

    /**
     * Main class that sequentially builds up the GBU.
     * 
     */
    @Override
    public void generate() {
        log.info("---- start generating ddl gbu file    ----");   
                        
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

            writeCountObjects(0);  //some unknown something?

            writeObjectsOfType(ObjectType.VIEW_DEFINITION);

            writeCountObjects(0);  //some unknown something 

            writeCountObjects(0);  //some unknown something 

            writeObjectsOfType(ObjectType.SEQUENCE);

            writeCountObjects(0); //writeSynonyms();

            writeObjectsOfType(ObjectType.PROCEDURE);

            writeObjectsOfType(ObjectType.FUNCTION);

            writeCountObjects(0);  //some unknown something 

            writeObjectsOfType(ObjectType.PLSQL_MODULE);

            writeBytes(0x00, 2);
            //real work ends

            writeClosingPart();

            writeLogFileName();

        } catch (IOException ioe) {
            log.error("error ocurrend writing the gbu ddl file", ioe);
        } finally {
            closeFile();
        }
        log.info("---- finished writing to gbu ddl file ----\n");    
    }

    /**
     * Write byte sequence to denote object type DDL into GBU stream
     */
    void writeObjectDefinition() throws IOException {
        log.info("writing object definition");
        
        writeBytes(new byte[]{0x02, 0x00, 0x00, 0x00}); // denotes table generation

        //optionally generate alter table statements
        if ("true".equals(props.generateAlterTable)) {
            log.info(". with alter table statement generation");
              writeBytes(new byte[]{0x01, 0x00, 0x00, 0x00});
        } else {
            log.info(". without alter table statement generation");
             writeBytes(new byte[]{0x00, 0x00, 0x00, 0x00});
        }
    }

    void writeTargetDbInfo() throws IOException {
        log.info("writing target db info");
        if ("true".equals(props.generateAlterTable)) {         

            // write connection.target_schema for ALTER TABLE option          
            log.info(". target schema user: " + props.targetDbUsername);
            writeVarLength(props.targetDbUsername);

            log.info(". target schema password: " + props.targetDbPassword);
            writeVarLength(props.targetDbPassword);
            
            //get the service name 
            String targetServiceName = props.targetDbConnectString;                     
            log.info(". target service name: " + targetServiceName);
            writeVarLength(targetServiceName);
            
        } else {
            writeBytes(new byte[]{0x00, 0x00, 0x00, 0x00});

            // write database version
            String dbVersion = "Oracle9i";
            log.info(". target db version: " + dbVersion);
            writeVarLength(dbVersion);
            
            writeBytes(new byte[]{0x00, 0x00, 0x00, 0x00});

           }              
    }
        
    void writeObjectProperties() throws IOException {
        log.info("writing object properties");


        writeBytes(new byte[]{0x00, 0x00, 0x00, 0x00});
        writeBytes(new byte[]{0x00, 0x00, 0x00, 0x00});
        writeBytes(new byte[]{0x00, 0x00, 0x00, 0x00});

        // optionally generate alter table statements
//        if ("true".equals(props.generateAlterTable)) {
//            writeBytes(new byte[]{0x01, 0x00, 0x00, 0x00});
//            writeBytes(new byte[]{0x01, 0x00, 0x00, 0x00});
//            writeBytes(new byte[]{0x01, 0x00, 0x00, 0x00});
//            writeBytes(new byte[]{0x00, 0x00, 0x00, 0x00});
//            writeBytes(new byte[]{0x00, 0x00, 0x00, 0x00});
//            writeBytes(new byte[]{0x00, 0x00, 0x00, 0x00});
//        } else {
            writeBytes(new byte[]{0x00, 0x00, 0x00, 0x00});
            writeBytes(new byte[]{0x00, 0x00, 0x00, 0x00});
            writeBytes(new byte[]{0x00, 0x00, 0x00, 0x00});
            writeBytes(new byte[]{0x00, 0x00, 0x00, 0x00});
            writeBytes(new byte[]{0x00, 0x00, 0x00, 0x00});
            writeBytes(new byte[]{0x00, 0x00, 0x00, 0x00});
 //       }
        writeBytes(new byte[]{0x00, 0x00, 0x00, 0x00});
        writeByte(0x00);
    }

    void writeClosingPart() throws IOException {
        log.info("writing closing part");
        writeBytes(0x00, 132);
    }
    
}
