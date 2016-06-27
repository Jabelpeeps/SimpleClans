package net.sacredlabyrinth.phaed.simpleclans.storage;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

/**
 *
 * @author cc_madelg
 */
public class SQLiteCore implements DBCore {
    private Logger log;
    private Connection connection;
    private String dbLocation;
    private String dbName;
    private File file;

    /**
     *
     * @param dbLocation
     */
    public SQLiteCore(String dbLocation) {
        this.dbName = "SimpleClans";
        this.dbLocation = dbLocation;
        this.log = SimpleClans.getLog();

        initialize();
    }

    private void initialize() {
        if (file == null) {
            File dbFolder = new File(dbLocation);

            if (dbName.contains("/") || dbName.contains("\\") || dbName.endsWith(".db")) {
                log.severe("The database name can not contain: /, \\, or .db");
                return;
            }
            if (!dbFolder.exists()) {
                dbFolder.mkdir();
            }
            file = new File(dbFolder.getAbsolutePath() + File.separator + dbName + ".db");
        }

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
        }
        catch (SQLException ex) {
            log.severe("SQLite exception on initialize " + ex);
        }
        catch (ClassNotFoundException ex) {
            log.severe("You need the SQLite library " + ex);
        }
    }

    /**
     * @return connection
     */
    @Override
    public Connection getConnection()  {
        if (connection == null) {
            initialize();
        }
        return connection;
    }

    /**
     * @return whether connection can be established
     */
    @Override
    public Boolean checkConnection() {
        return getConnection() != null;
    }

    /**
     * Close connection
     */
    @Override
    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        }
        catch (Exception e) {
            log.severe("Failed to close database connection! " + e.getMessage());
        }
    }

    /**
     * Execute a statement
     * @param query
     * @return
     */
    @Override
    public Boolean execute(String query) {
        try {
            getConnection().createStatement().execute(query);
            return true;
        }
        catch (SQLException ex) {
            log.severe(ex.getMessage());
            log.severe("query: " + query);
            return false;
        }
    }

    /**
     * Check whether a table exists
     *
     * @param table
     * @return
     */
    @Override
    public Boolean existsTable(String table) {
        try (ResultSet tables = getConnection().getMetaData().getTables(null, null, table, null)){ 
            return tables.next();
        }
        catch (SQLException e) {
            log.severe("Failed to check if table " + table + " exists: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check whether a column exists
     *
     * @param table
     * @param column
     * @return
     */
    @Override
    public Boolean existsColumn(String table, String column) {
        try (ResultSet col = getConnection().getMetaData().getColumns(null, null, table, column)){
            return col.next();
        }
        catch (Exception e) {
            log.severe("Failed to check if column " + column + " exists in table " + table + " : " + e.getMessage());
            return false;
        }
    }

    @Override
    public ResultSet getResultSet( PreparedStatement query, String...params  ) {
        try {
            for ( int i = 0; i < params.length; i++ ) {
                query.setString( i + 1, params[i] );
            }
            if (query.execute())
                return query.getResultSet();
        }
        catch (SQLException ex) {
            log.severe(ex.getMessage());
            log.severe("query: " + query);
        }
        return null;
    }

    @Override
    public void executeUpdate( PreparedStatement query, String...params ) {
        try {
            for ( int i = 0; i < params.length; i++ ) {
                query.setString( i + 1, params[i] );
            }
            query.executeUpdate();
        }
        catch (SQLException ex) {
            log.severe(ex.getMessage());
            log.severe("query: " + query);
        }
    }
}
