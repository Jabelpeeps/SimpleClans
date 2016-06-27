package net.sacredlabyrinth.phaed.simpleclans.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author phaed
 */
public interface DBCore
{
    /**
     * @return connection
     */
    Connection getConnection();

    /**
     * @return whether connection can be established
     */
    Boolean checkConnection();

    /**
     * Close connection
     */
    void close();

    /**
     * Execute a statement
     * @param query
     * @return
     */
    Boolean execute(String query);

    /**
     * Check whether a table exists
     * @param table
     * @return
     */
    Boolean existsTable(String table);
    
    /**
     * Check whether a colum exists
     *
     * @param tabell
     * @param colum
     * @return
     */
    Boolean existsColumn(String tabell, String colum);

    ResultSet getResultSet( PreparedStatement query, String...params );

    void executeUpdate( PreparedStatement query, String...params );
}
