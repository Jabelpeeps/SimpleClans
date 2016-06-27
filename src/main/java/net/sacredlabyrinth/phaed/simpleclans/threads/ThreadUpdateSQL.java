package net.sacredlabyrinth.phaed.simpleclans.threads;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

/**
 *
 * @author NeT32
 */
public class ThreadUpdateSQL extends Thread {

    PreparedStatement statement;
    String[] params;

    public ThreadUpdateSQL(PreparedStatement _statement, String... _params) {
        statement = _statement;
        params = _params;
    }
    
    @Override
    public void run() {
        try {
            int j = 1;
            for ( String each : params ) {
                statement.setString( j++, each );
            }
//            for ( int i = 0; i < params.length; i++ ) {
//                statement.setString( i + 1, params[i] );
//            }
            statement.executeUpdate(); 
        }
        catch (SQLException ex) {
            if (!ex.toString().contains("not return ResultSet")) {
                try {
                    SimpleClans.getLog().severe("[Thread] Error at SQL " + statement.getParameterMetaData() );
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
                ex.printStackTrace();
            }
        }
    }
}
