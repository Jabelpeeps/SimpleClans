package net.sacredlabyrinth.phaed.simpleclans.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

/**
 * @author cc_madelg
 */
public class MySQLCore extends AbstractDBCore {

    private SimpleClans plugin = SimpleClans.getInstance();
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
        log = plugin.getLogger();
        usingThreads = plugin.getSettingsManager().getUseThreads();
        initialize();
    }

    private void initialize() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection( "jdbc:mysql://" + host + 
                                                      ":" + port + 
                                                      "/" + database + 
                                                      "?useUnicode=true&characterEncoding=utf-8", username, password );
        }
        catch (ClassNotFoundException e) {
            log.severe("ClassNotFoundException! " + e.getMessage());
        }
        catch (SQLException e) {
            log.severe("SQLException! " + e.getMessage());
        }
        
        if (!existsTable("sc_clans")) {
            SimpleClans.log("Creating table: sc_clans");

            String query = "CREATE TABLE IF NOT EXISTS `sc_clans` ( "
                            + "`id` bigint(20) NOT NULL auto_increment, "
                            + "`verified` tinyint(1) default '0', "
                            + "`tag` varchar(25) NOT NULL, "
                            + "`color_tag` varchar(25) NOT NULL, "
                            + "`name` varchar(100) NOT NULL, "
                            + "`friendly_fire` tinyint(1) default '0', "
                            + "`founded` bigint NOT NULL, "
                            + "`last_used` bigint NOT NULL, "
                            + "`packed_allies` text NOT NULL, "
                            + "`packed_rivals` text NOT NULL, "
                            + "`packed_bb` mediumtext NOT NULL, "
                            + "`cape_url` varchar(255) NOT NULL, "
                            + "`flags` text NOT NULL, "
                            + "`balance` double(64,2), "
                            + "PRIMARY KEY  (`id`), "
                            + "UNIQUE KEY `uq_simpleclans_1` (`tag`));";
            execute(query);
        }

        if (!existsTable("sc_players")) {
            SimpleClans.log("Creating table: sc_players");

            String query = "CREATE TABLE IF NOT EXISTS `sc_players` ( "
                            + "`id` bigint(20) NOT NULL auto_increment, "
                            + "`name` varchar(16) NOT NULL, "
                            + "`uuid` CHAR(36) NOT NULL, "
                            + "`leader` tinyint(1) default '0', "
                            + "`tag` varchar(25) NOT NULL, "
                            + "`friendly_fire` tinyint(1) default '0', "
                            + "`neutral_kills` int(11) default NULL, "
                            + "`rival_kills` int(11) default NULL, "
                            + "`civilian_kills` int(11) default NULL, "
                            + "`deaths` int(11) default NULL, "
                            + "`last_seen` bigint NOT NULL, "
                            + "`join_date` bigint NOT NULL, "
                            + "`trusted` tinyint(1) default '0', "
                            + "`flags` text NOT NULL, "
                            + "`packed_past_clans` text, "
                            + "PRIMARY KEY  (`id`), "
                            + "UNIQUE INDEX `uq_player_uuid` (`uuid`)"
                            + ");";
            execute(query);
        }

        if (!existsTable("sc_kills")) {
            SimpleClans.log("Creating table: sc_kills");

            String query = "CREATE TABLE IF NOT EXISTS `sc_kills` ( "
                            + "`kill_id` bigint(20) NOT NULL auto_increment, "
                            + "`attacker` varchar(16) NOT NULL, "
                            + "`attacker_tag` varchar(16) NOT NULL, "
                            + "`attacker_uuid` CHAR(36) NOT NULL, "
                            + "`victim` varchar(16) NOT NULL, "
                            + "`victim_tag` varchar(16) NOT NULL, "
                            + "`victim_uuid` CHAR(36) NOT NULL, "
                            + "`kill_type` varchar(1) NOT NULL, "
                            + "PRIMARY KEY  (`kill_id`));";
            execute(query);
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
        catch (SQLException e) {
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
        catch (SQLException e) {
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
    @SuppressWarnings( "resource" )
    @Override
    public ResultSet getResultSet(PreparedStatement query, Class<?>[] types, Object...params  ) {
        try {
            if (types != null) {
                query = setupStatement( query, types, params );
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
                query = setupStatement( query, types, params );
            }
            try {
                query.executeUpdate();
            }
            finally {
                query.close();
            }
        }
        catch (SQLException ex) {
            log.severe(ex.getMessage());
            log.severe("query: " + query);
        }
    }
}
