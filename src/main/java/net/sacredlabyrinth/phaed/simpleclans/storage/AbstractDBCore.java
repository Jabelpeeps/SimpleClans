package net.sacredlabyrinth.phaed.simpleclans.storage;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class AbstractDBCore implements DBCore {

    public PreparedStatement setupStatement( PreparedStatement query, Class<?>[] types, Object...params ) 
            throws SQLException {
         
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
         return query;       
     }
}
