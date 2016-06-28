package net.sacredlabyrinth.phaed.simpleclans.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.threads.ThreadUpdateSQL;

/**
 * @author cc_madelg
 */
public class MySQLCore implements DBCore {

    private Logger log;
    private Connection connection;
    private String host;
    private String username;
    private String password;
    private String database;
    private boolean usingThreads;
    private int port;

    public MySQLCore(String _host, String _database, int _port, String _username, String _password) {
        database = _database;
        port = _port;
        host = _host;
        username = _username;
        password = _password;
        log = SimpleClans.getLog();
        usingThreads = SimpleClans.getInstance().getSettingsManager().getUseThreads();
        initialize();
    }

    private void initialize() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=utf-8", username, password);
        }
        catch (ClassNotFoundException e) {
            log.severe("ClassNotFoundException! " + e.getMessage());
        }
        catch (SQLException e) {
            log.severe("SQLException! " + e.getMessage());
        }
    }

    @Override
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                initialize();
            }
        }
        catch (SQLException e) {
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
     *
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
        try (ResultSet tables = getConnection().getMetaData().getTables(null, null, table, null) ) {           
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
        try (ResultSet col = getConnection().getMetaData().getColumns(null, null, table, column) ) {
            return col.next();
        }
        catch (Exception e) {
            log.severe("Failed to check if column " + column + " exists in table " + table + " : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Execute a statement
     *
     * @param query
     * @return the results of the query or null
     */
    @Override
    public ResultSet getResultSet(PreparedStatement query, Class<?>[] types, Object...params  ) {
        try {
            if (types != null) {
                for ( int i = 0; i < params.length; i++ ) {
                    
                    if ( types[i].equals( String.class ) ) {
                        query.setString( i + 1, (String) params[i] );
                    }
                    else if ( types[i].equals( int.class ) ) {
                        query.setInt( i + 1, (int) params[i] );
                    }
                    else if ( types[i].equals( long.class ) ) {
                        query.setLong( i + 1, (long) params[i] );
                    }
                    else if ( types[i].equals( double.class ) ) {
                        query.setDouble( i + 1, (double) params[i] );
                    }
                }
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
    public void executeUpdate(PreparedStatement query, Class<?>[] types, Object...params ) {
        if ( usingThreads ) {
            new ThreadUpdateSQL(query, types, params).start();
            return;
        }
        try {
            if (types != null) {
                for ( int i = 0; i < params.length; i++ ) {
                    
                    if ( types[i].equals( String.class ) ) {
                        query.setString( i + 1, (String) params[i] );
                    }
                    else if ( types[i].equals( int.class ) ) {
                        query.setInt( i + 1, (int) params[i] );
                    }
                    else if ( types[i].equals( long.class ) ) {
                        query.setLong( i + 1, (long) params[i] );
                    }
                    else if ( types[i].equals( double.class ) ) {
                        query.setDouble( i + 1, (double) params[i] );
                    }
                }
            }
            query.executeUpdate();
        }
        catch (SQLException ex) {
            log.severe(ex.getMessage());
            log.severe("query: " + query);
        }
    }
}
