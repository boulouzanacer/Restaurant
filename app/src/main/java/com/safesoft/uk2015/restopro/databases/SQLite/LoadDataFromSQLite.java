package com.safesoft.uk2015.restopro.databases.SQLite;

import android.content.Context;
import android.util.Log;

import com.safesoft.uk2015.restopro.PostData.PostData_orders;
import com.safesoft.uk2015.restopro.PostData.PostData_Bon2;

import java.util.ArrayList;

/**
 * Created by UK2015 on 03/07/2016.
 */
public class LoadDataFromSQLite {
    private static DATABASE controller;
    private Context _context;

    public LoadDataFromSQLite(Context context){
        _context = context;
        controller = new DATABASE(_context);
    }

    public ArrayList<PostData_orders> getDataOrders() {

        ArrayList<PostData_orders> orders_list = new ArrayList<>();
        orders_list.clear();
        orders_list = controller.select_all_data_of_all_orders();
        Log.v("TRACKKK", "================================================" + orders_list.size());
        return orders_list;
    }

    public ArrayList<PostData_Bon2> getDataReceipt(String num_bon) {

        ArrayList<PostData_Bon2> receipt_list = new ArrayList<>();
        receipt_list.clear();
        String selectQuery = "SELECT " +
                "    Bon2.BON2ID ," +
                "    Bon2.NUM_BON ," +
                "    Bon2.CODE ," +
                "    Bon2.CODE_S," +
                "    Bon2.RECORDID2 ," +
                "    Bon2.PRODUIT ," +
                "    Bon2.QUANTITY ," +
                "    Bon2.TVA ," +
                "    Bon2.PV_TTC ," +
                "    Bon2.MONTANT_HT ," +
                "    Bon2.EMPORTER, " +
                "    Bon2.IMP_COM, " +
                "    Menu.DES_IMP, " +
                "    Tables.NOM_SERVEUR, " +
                "    Tables.TABLE_NUMBER " +
                " FROM Bon2 LEFT JOIN Menu ON (Bon2.CODE == Menu.CODE)" +
                " JOIN Tables ON (Bon2.NUM_BON == Tables.NUM_BON)" +
                " WHERE Bon2.NUM_BON = '" + num_bon + "' " +
                " GROUP BY " +
                "    Bon2.BON2ID ," +
                "    Bon2.NUM_BON," +
                "    Bon2.CODE," +
                "    Bon2.CODE_S," +
                "    Bon2.RECORDID2," +
                "    Bon2.PRODUIT," +
                "    Bon2.QUANTITY," +
                "    Bon2.TVA ," +
                "    Bon2.PV_TTC," +
                "    Bon2.MONTANT_HT," +
                "    Bon2.EMPORTER, " +
                "    Bon2.IMP_COM, " +
                "    Menu.DES_IMP, " +
                "    Tables.NOM_SERVEUR, " +
                "    Tables.TABLE_NUMBER " +
                "    ORDER BY 2,5,3 DESC";

       // String selectQuery = "SELECT * FROM  Bon2 WHERE NUM_BON = '"+num_bon+"'";
        receipt_list = controller.get_bon2_from_database(selectQuery);

        return receipt_list;
    }
}
