package nl.amis.gbugen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processes the properties file and the command-line properties
 * 
 * @author preitsma
 */
public class GbuGenProperties {

    private static Logger log = LoggerFactory.getLogger(Generator.class);
    @Option(name = "-workarea", required = true, usage = "workarea from with the configurations are taken")
    public String workArea;
    @Option(name = "-configuration", required = false, usage = "configuration for which GBU will be generated, leave empty to include all outstanding configurations from the workarea")
    public String configuration;
    @Option(name = "-output.dir", required = true, usage = "path for the generated GBU files")
    public String outputDir;
    @Option(name = "-output.prefix", required = false, usage = "prefix of GBU files")
    public String outputPrefix;
    @Option(name = "-repos.url", required = true, usage = "jdbc connectstring to repository, example 'jdbc:oracle:thin:@wnota05.woningnet.nl:1521:repos'")
    public String reposUrl;
    @Option(name = "-repos.username", required = true, usage = "user to connect to repository")
    public String reposUsername;
    @Option(name = "-repos.password", required = true, usage = "password to connect to repository")
    public String reposPassword;
    @Option(name = "-repos.connectstring", required = true, usage = "connectstring that ends up in the GBU")
    public String reposConnectString;  
    @Option(name = "-forms.objectlibrary", required = false, usage = "objectlibrary for forms generation")
    public String objectLibrary;
    @Option(name = "-forms.template", required = false, usage = "template for forms generation")
    public String formsTemplate;
    @Option(name = "-include", required = false, usage = "specify with types to include: drop-triggers,ddl,tapi,forms. Leave empty to include all.")
    public String include;
    @Option(name = "-gbu.generate.altertable", required = false, usage = "true/false indicates if a target database will be specified")
    public String generateAlterTable;
    @Option(name = "-gbu.targetdb.url", required = false, usage = "jdbc connectstring to target db, example 'jdbc:oracle:thin:@wnota05.woningnet.nl:1521:ont")
    public String targetDbUrl;
    @Option(name = "-gbu.targetdb.username", required = false, usage = "user to connect to target db")
    public String targetDbUsername;
    @Option(name = "-gbu.targetdb.password", required = false, usage = "password to connect to target db")
    public String targetDbPassword;
    @Option(name = "-gbu.targetdb.connectstring", required = false, usage = "connectstring that ends up in the GBU")
    public String targetDbConnectString;
    @Option(name = "-gbu.folderirid", required = false, usage = "application container repository id")
    public String folderId;
    @Option(name = "-gbu.useririd", required = false, usage = "application container repository id")
    public String userId;
    @Option(name = "-gbu.logfilename", required = false, usage = "logfilename of dwzrun61 log, will be included in GBU")
    public String logfileName;
    @Option(name = "-generate.template.dir", required = false, usage = "directory for generated forms")
    public String dummy;
    @Option(name = "-generate.connectstring", required = false, usage = "directory for generated forms")
    public String dummy2;
    @Option(name = "-gbu-generator.dir", required = false, usage = "directory for generated forms")
    public String dummy3;

    void processOptions(String[] args) {

        log.debug("parsing options...");
        CmdLineParser parser = new CmdLineParser(this);

        args = concat(propertiesToArgs(loadProperties()), args);

        try {

            parser.parseArgument(args);

        } catch (CmdLineException e) {
            log.error("options could not be parsed...");
            log.error("message: " + e.getMessage());
            parser.setUsageWidth(120);
            System.err.println();
            System.err.println("Specify a properties file, example:  \n");
            System.err.println(" => java -Dproperties.file=generator.properties -jar gbu-generator.jar");
            System.err.println();
            System.err.println("Or provide the properties with command line options, example: \n");
            System.err.println(" => java -jar gbu-generator.jar [options...] ");
            parser.printUsage(System.err);
            System.exit(0);
        }

        log.debug("options parsed");
    }

    private Properties loadProperties() {

        String propFileLocation = System.getProperty("properties.file");
        Properties properties = new Properties();

        if (propFileLocation != null) {
            InputStream input = null;
            log.debug("loading properties file '" + propFileLocation + "' ...");

            try {
                input = new FileInputStream(new File(propFileLocation));
                properties.load(input);

                log.info("properties loaded");
            } catch (IOException ex) {
                log.error("properties could not be loaded", ex);
                System.exit(0);
            } finally {
                try {
                    input.close();
                } catch (Exception ignore) {
                }
            }
        }

        return properties;

    }

    //convert the Properties object to a String array
    private static String[] propertiesToArgs(Properties properties) {
        String[] args = new String[properties.size() * 2];
        Enumeration propsEnum = properties.propertyNames();
        int counter = 0;
        while (propsEnum.hasMoreElements()) {
            String propName = (String) propsEnum.nextElement();
            args[counter++] = "-" + propName;
            args[counter++] = properties.getProperty(propName);
        }
        return args;
    }

    //concatenate two String arrays
    private static String[] concat(String[] A, String[] B) {
        int aLen = A.length;
        int bLen = B.length;
        String[] C = new String[aLen + bLen];
        System.arraycopy(A, 0, C, 0, aLen);
        System.arraycopy(B, 0, C, aLen, bLen);
        return C;
    }
}
