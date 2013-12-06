package nl.amis.gbugen.file;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import nl.amis.gbugen.GbuGenProperties;
import nl.amis.gbugen.db.DesignerObject;
import nl.amis.gbugen.db.ObjectType;
import nl.amis.gbugen.db.WorkArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all Generators, contains utility methods generic functions
 * 
 * @author preitsma
 */
public abstract class FileGenerator {

    private static Logger log = LoggerFactory.getLogger(FileGenerator.class);
    
    public static String baseDir = null;
    public static WorkArea workArea;
    public static GbuGenProperties props;
    
    private DataOutputStream out = null;
    protected byte[] countPostfix = new byte[]{0x00, 0x00};

    protected FileGenerator() {
        assert (workArea != null);
        assert (props != null);
    }

    protected abstract String getGenType();

    public abstract void generate();

    public boolean isIncluded() {
        return "".equals(props.include) || props.include.toLowerCase().contains(getGenType());
    }

    /**
     * Opens a GBU file
     * 
     * @throws IOException 
     */
    protected void openFile() throws IOException {
        openFile("gbu");
    }

    /**
     * Opens a file taking an extension as a input parameter
     * 
     * @param extension
     * @throws IOException 
     */
    protected void openFile(String extension) throws IOException {
        String completeFileName = createFileNamePlusDir(getGenType()) + "." + extension;
        out = new DataOutputStream(new FileOutputStream(completeFileName));
        log.info("created new file: '" + completeFileName + "'");
    }

    /**    
     * Closes the file.
     */
    protected void closeFile() {
        if (out != null) {
            log.info("closing file ...");
            try {
                out.close();
            } catch (Exception ignore) {
            }
        }
    }

    /**
     * Returns current date in String format
     * 
     * @return 
     */
    protected static String getCurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
        return dateFormat.format(new Date());
    }

    /**
     * Returns the filename prefixed with the base dir
     * and makes sure the dir exists
     * 
     * @param fileType
     * @return 
     */
    protected String createFileNamePlusDir(String fileType) {
        if (baseDir == null) {
            createBaseDir();
        }
        String subDir = baseDir + "\\" + getGenType();
        makeSureDirExists(subDir);
        return subDir + "\\" + fileType;
    }

    /**
     * Returns the base dir and makes sure it exists.
     * 
     */
    protected static void createBaseDir() {
        baseDir = "";
        if (props.outputDir != null) {
            baseDir = props.outputDir;
        }
        makeSureDirExists(baseDir);
        baseDir += "\\";
    }

    private static void makeSureDirExists(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            if (file.mkdir()) {
                log.info(".directory '" + dir + "'is created");
            } else {
                log.error(".failed to create directory '" + dir + "'");
            }
        }
    }

    private static String createOracleConnectString(String connectString, String username, String password) {
        return username + "/" + password + "@" + connectString;
    }

    protected static String getServiceNameFromJDBCConnectString(String url) {
        return url.substring(url.lastIndexOf(":") + 1, url.length());
    }

    protected void writeByte(int byteValue) throws IOException {
        out.writeByte(byteValue);
    }

    protected void writeBytes(int byteValue, int n) throws IOException {
        for (int i = 0; i < n; i++) {
            writeByte(byteValue);
        }
    }

    protected void writeBytes(String value) throws IOException {
        out.writeBytes(value);
    }

    protected void writeBytes(byte[] bytes) throws IOException {
        out.write(bytes);
    }

    protected void writeCountObjects(int countObjects) throws IOException {
        writeByte(countObjects);
        writeByte(countObjects >>> 8);
        writeBytes(countPostfix);
    }

    protected void writeVarLength(String value) throws IOException {
        writeByte(value.length());
        writeBytes(value);
    }

    protected void writeFixedLength(String value, int length) throws IOException {
        int amountOfPrefixFillers = length - value.length();
        writeByte(length);
        writeBytes(0x20, amountOfPrefixFillers);
        writeBytes(value);
    }

    /**
     * Write GBU header containing connection details and work area name.
     */
    protected void writeHeader() throws IOException {
        log.info("writing header");
        writeByte(0x03);
        writeByte(0x00);

        String connectString = createOracleConnectString(props.reposConnectString, props.reposUsername, props.reposPassword);
        log.info(".connection string: " + connectString + " ...");
        out.writeByte(connectString.length());
        out.writeBytes(connectString);

        writeByte(0x00); // fillers

        log.info(".workarea name: " + workArea.getName() + " ...");
        writeVarLength(workArea.getName());


        String folderIRID = props.folderId;
        log.info(".folder irid: " + folderIRID + " ...");
        writeFixedLength(folderIRID, 0x2b);

    }

    /**
     * Write the User Id 
     * 
     * @throws IOException 
     */
    protected void writeUserIRID() throws IOException {
        log.info("writing user irid");
        String userIRID = props.userId;
        log.info(".user irid: " + userIRID + " ...");
        writeVarLength(userIRID);
    }

    /**
     * Write the logfile name
     * 
     * @throws IOException 
     */
    protected void writeLogFileName() throws IOException {
        log.info("writing logfile info");

        String now = "Now";
        log.info(".now: " + now + " ...");
        writeVarLength(now);

        log.info(".logfile name: " + props.logfileName + " ...");
        writeVarLength(props.logfileName);
    }

    /**
     * Write output info.
     * 
     * @throws IOException 
     */
    protected void writeOutputInfo() throws IOException {
        log.info("writing output info");

        log.info(".output prefix: " + getGenType());
        writeVarLength(getGenType());

        String gbuOutputDir = (baseDir + getGenType()).replace("\\\\", "\\");
        log.info(".output dir: " + gbuOutputDir);
        writeVarLength(gbuOutputDir);

        makeSureDirExists(gbuOutputDir);
    }

    protected void writeObjectsOfType(ObjectType type) throws IOException {
        writeObjectsOfType(type, "%s");
    }

    protected void writeObjectsOfType(ObjectType type, String template) throws IOException {
        log.info("writing object of type : " + type);
        int numberOfObjects = workArea.getObjectsOfType(type).size();
        log.info(". amount of objects : " + numberOfObjects);
        writeCountObjects(numberOfObjects);

        for (DesignerObject tableObject : workArea.getObjectsOfType(type)) {
            String tName = tableObject.getName().replaceAll("\\$", "\\\\\\$");
            String object = template.replaceFirst("%s", tName);
            log.info(".. IRID: " + tableObject.getIRID() + " name: " + object + " " + tableObject.getObjectVersion() + "  from config " + tableObject.getConfiguration() + "");
            writeVarLength(tableObject.getIRID());
            writeVarLength(object);
        }
    }
}
