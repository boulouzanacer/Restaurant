package com.safesoft.uk2015.restopro.databases.FireBird;

/**
 * Created by UK2015 on 31/05/2016.
 */
public interface IMDB {
    boolean CreateDB();
    boolean Connect();
    void Disconnect();

    boolean StartTransAction();
    boolean CommitTransAction();
    boolean RollbackTransAction();
    boolean BackupTo();
    //IDao *CreateDao(DaoType type)=0;
    boolean IsValid();
}
