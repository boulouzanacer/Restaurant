package com.safesoft.uk2015.restopro.databases.FireBird;

/**
 * Created by UK2015 on 31/05/2016.
 */
public abstract class absDB {
    public static void showSQLException(java.sql.SQLException e) {
        /* Notice that a SQLException is actually a chain of SQLExceptions,
         * let's not forget to print all of them
         */
        java.sql.SQLException next = e;
        while (next != null) {
            System.out.println(next.getMessage());
            System.out.println("Error Code: " + next.getErrorCode());
            System.out.println("SQL State: " + next.getSQLState());
            next = next.getNextException();
        }
    }
}
