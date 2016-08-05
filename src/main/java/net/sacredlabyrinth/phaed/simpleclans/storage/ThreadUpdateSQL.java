package net.sacredlabyrinth.phaed.simpleclans.storage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

/**
 *
 * @author NeT32
 */
public class ThreadUpdateSQL extends Thread {

    PreparedStatement statement;
    Class<?>[] types;
    Object[] params;

    public ThreadUpdateSQL(PreparedStatement _statement, Class<?>[] _types, Object..._params ) {
        statement = _statement;
        types = _types;
        params = _params;
    }
    
    @Override
    public void run() {
        try {
            if ( types != null ) {
                
                for ( int i = 0; i < params.length; i++ ) {
                    
                    if ( types[i].equals( String.class ) ) {
                        statement.setString( i + 1, (String) params[i] );
                    }
                    else if ( types[i].equals( int.class ) ) {
                        statement.setInt( i + 1, (int) params[i] );
                    }
                    else if ( types[i].equals( long.class ) ) {
                        statement.setLong( i + 1, (long) params[i] );
                    }
                    else if ( types[i].equals( double.class ) ) {
                        statement.setDouble( i + 1, (double) params[i] );
                    }
                }
            }
            statement.executeUpdate(); 
        }
        catch (SQLException ex) {
            if (!ex.toString().contains("not return ResultSet")) {
                SimpleClans.getInstance().getLogger().severe("[Thread] Error at SQL " + Arrays.asList( params ));
                ex.printStackTrace();
            }
        }
    }
}
