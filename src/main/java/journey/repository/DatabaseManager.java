package journey.repository;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import journey.ReadCSV;
import journey.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



/**
 * Static utility class to make queries to the database.
 */
public final class DatabaseManager {
    private final String databasePath;
    private static final Logger log = LogManager.getLogger();
    private static DatabaseManager instance;

    /**
     * Constructs a new database manager.
     */
    private DatabaseManager() {
        String path = DatabaseManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = URLDecoder.decode(path, StandardCharsets.UTF_8);
        File jarDir = new File(path);
        databasePath = jarDir.getParentFile() + "/database.db";
    }


    private DatabaseManager(String url) {
        this.databasePath = url;
        setup();
    }

    /**
     * Constructs a new database manager from a specified url.

     * @param url the desired path to database.
     */
    public static DatabaseManager initialiseWithUrl(String url) {
        if (instance == null) {
            instance = new DatabaseManager(url);
        }
        return instance;
    }


    /**
     * Connects to the database.

     * @return a connection.
     */
    public Connection connect() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:".concat(databasePath);
            conn = DriverManager.getConnection(url);
            log.info("Connected to database.");
        } catch (SQLException | NullPointerException e) {
            log.fatal(e);
        }
        return conn;
    }

    /**
     * Singleton method to get current Instance if exists otherwise create it.

     * @return the single instance DatabaseSingleton
     */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
            Boolean noDB = null;
            Connection conn = null;
            try {
                conn = instance.connect();
                if (conn != null) {
                    try (Statement statement = conn.createStatement()) {
                        ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM main.sqlite_master "
                                + "WHERE name = 'Stations'");
                        noDB = (rs.getInt(1) == 0);
                    }
                }
                Utils.closeConn(conn);
            } catch (Exception e) {
                log.error(e);
            } finally {
                Utils.closeConn(conn);
            }

            // Sets up the instance and imports data if the database is not already set up
            if (noDB != null && noDB) {
                instance.setup();
                ReadCSV.readStations();
            }
        }
        return instance;
    }



    /**
     * Sets up the database if not yet set up.
     */
    public void setup() {
        InputStream is = getClass().getResourceAsStream("/sql/init_db.sql");
        executeSQLScript(is);
    }

    /**
     * Reads and executes all statements within the sql file provided
     * Note that each statement must be separated by '--Break' this is not a desired limitation but allows for a much
     * wider range of statement types.
     * @param sqlFile input stream of file containing sql statements for execution (separated by --Break)
     */
    private void executeSQLScript(InputStream sqlFile) {
        String s;
        StringBuffer sb = new StringBuffer();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(sqlFile))) {
            while((s=br.readLine()) != null) {
                sb.append(s);
            }

            String[] individualStatements = sb.toString().split("--Break");
            try (Connection conn = this.connect();
                 Statement statement = conn.createStatement()) {
                for (String singleStatement : individualStatements) {
                    statement.executeUpdate(singleStatement);
                }
            }
        } catch (FileNotFoundException e) {
            log.error("Error could not find specified database initialisation file", e);
        } catch (IOException e) {
            log.error("Error working with database initialisation file", e);
        } catch (SQLException e) {
            log.error("Error executing sql statements in database initialisation file", e);
        }
    }
}