package com.safesoft.uk2015.restopro.databases.FireBird;

/**
 * Created by UK2015 on 31/05/2016.
 */
public class function_FB implements BizApp {
    
    public static Connection_Firebird fd = new Connection_Firebird();
    @Override
    public boolean GetFBDB(String server, String db, String user, String pwd) {
        fd = new Connection_Firebird(server, db, user, pwd);
        // fd = new Connection_Firebird();
		/*ParaKey p = ParaKey.ADDRESS;
		SetParam(p, server);
		p = ParaKey.DATABASE;
		SetParam(p, db);
		p = ParaKey.MASTER;
		SetParam(p, user);
		p = ParaKey.DB_PASSWORD;
		SetParam(p, pwd);*/
        //===================================
        //  UserLoginTask mAuthTask = new UserLoginTask();
        //  mAuthTask.execute();
        //===================================
        //boolean bl = true;
        boolean bl = fd.Connect();
        fd.Disconnect();
        return bl;
    }
}
