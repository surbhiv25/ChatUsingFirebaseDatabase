package com.ezeia.politicalparty.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.Locale;

public class Database
{
    private static final String TAG = "DBAdapterKenya";
    private static final String DATABASE_NAME = "DBAdapterKenya";
    private static final int DATABASE_VERSION = 1;
    private boolean isDBOpenflag = false;

    private static final String DATABASE_TABLE_tblUserDetails = "tblUserDetails";
    private static final String DATABASE_CREATE_TABLE_tblUserDetails = "create table tblUserDetails(UserID text not null,UserName text not null,Password text not null," +
            "District text not null, Scheme text not null, Role text not null, LoginTime text not null, IsInstalled text not null);";

    private static final String DATABASE_TABLE_tblSchemes = "tblSchemes";
    private static final String DATABASE_CREATE_TABLE_tblSchemes = "create table tblSchemes(SchemeID text not null,SchemeName text not null);";

    private final Context context;

    private Database.DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    Locale locale  = new Locale("en", "UK");
    String pattern = "###.##";
    DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);

    public Database(Context ctx)
    {
        this.context = ctx;
        DBHelper = new Database.DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try
            {
                db.execSQL(DATABASE_CREATE_TABLE_tblUserDetails);
                db.execSQL(DATABASE_CREATE_TABLE_tblSchemes);
            } catch (Exception e) {
                Log.e(TAG, "Error: onCreate db");
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try
            {
                db.execSQL("DROP TABLE IF EXISTS tblUserDetails");
                db.execSQL("DROP TABLE IF EXISTS tblSchemes");
                onCreate(db);

            } catch (Exception e) {
                Log.e(TAG, "Error: onUpgrade db");
            }
        }
    }

    // ---opens the database---
    public Database open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        isDBOpenflag = true;
        return this;
    }

    public boolean isDBOpen()
    {
        isDBOpenflag = false;
        return isDBOpenflag;
    }

    // ---closes the database---
    public void close() {
        DBHelper.close();
    }

    public void saveUserDetails(String UserID,String UserName,String Password,String District,
                                             String Scheme,String Role,String LoginTime,String IsInstalled)
    {
        //create table tblUserDetails(UserID text null,UserName text null,Password text null," +
        //            "District text null, Scheme text null, Role text null, LoginTime text null, IsInstalled text null);";

        ContentValues values=new ContentValues();
        values.put("UserID", UserID);
        values.put("UserName", UserName);
        values.put("Password", Password);
        values.put("District", District);
        values.put("Scheme", Scheme);
        values.put("Role", Role);
        values.put("LoginTime", LoginTime);
        values.put("IsInstalled", IsInstalled);

        db.insert(DATABASE_TABLE_tblUserDetails, null, values);

    }

    public void saveSchemes(String SchemeID,String SchemeName)
    {
        ContentValues values=new ContentValues();
        values.put("SchemeID", SchemeID);
        values.put("SchemeName", SchemeName);

        db.insert(DATABASE_TABLE_tblSchemes, null, values);

    }

    public void Delete_tblData()
    {
        db.execSQL("DELETE FROM tblUserDetails");
        db.execSQL("DELETE FROM tblSchemes");
    }

    public String checkIfLoginCorrect(String UserName, String Password)
    {
        String hmap = "NA";
        Cursor cur = null;
        try {
            open();
            String qry="select UserName,Password,District,Scheme,Role from tblUserDetails where UserName='"+UserName+"' and Password='"+Password+"'";
            cur = db.rawQuery(qry,null);

            if(cur.getCount() > 0)
            {
                if(cur.moveToFirst())
                {
               /* for(int i=0;i < cur.getCount();i++)
                {*/
                    hmap = cur.getString(0)+"^"+cur.getString(1)+"^"+cur.getString(2)+"^"+cur.getString(3)+"^"+cur.getString(4);
                    cur.moveToNext();
                    //}
                }
            }
            else
            {
                hmap = "NA";
            }
        } finally {
            if(cur!=null) {
                cur.close();
            }
            close();
        }
        return hmap;
    }

    public LinkedHashMap<String,String> fetchGroupsList()
    {
        LinkedHashMap<String,String> hmap = new LinkedHashMap<>();
        Cursor cur = null;
        String role = "District_Admin";
        try {
            open();
            String qry="select UserName,District,Scheme from tblUserDetails where Role='"+role+"' ";
            cur = db.rawQuery(qry,null);

            if(cur.getCount() > 0)
            {
                if(cur.moveToFirst())
                {
                    for(int i=0;i < cur.getCount();i++)
                    {
                        hmap.put(cur.getString(0),cur.getString(1)+"^"+cur.getString(2));
                        cur.moveToNext();
                    }
                }
            }
        } finally {
            if(cur!=null) {
                cur.close();
            }
            close();
        }
        return hmap;
    }

    public String fetchUserByArea(String District,String Scheme)
    {
        String hmap = "--";
        Cursor cur = null;
        String role = "District_Admin";
        try {
            open();
            String qry="select UserName from tblUserDetails where Role='"+role+"' and District='"+District+"' and Scheme='"+Scheme+"'";
            cur = db.rawQuery(qry,null);

            if(cur.getCount() > 0)
            {
                if(cur.moveToFirst())
                {
                    hmap = cur.getString(0);
                    cur.moveToNext();
                }
            }
        } finally {
            if(cur!=null) {
                cur.close();
            }
            close();
        }
        return hmap;
    }

    public LinkedHashMap<String,String> fetchSchemes()
    {
        LinkedHashMap<String,String> hmap = new LinkedHashMap<>();
        Cursor cur = null;
        try {
            open();
            String qry="select SchemeID,SchemeName from tblSchemes";
            cur = db.rawQuery(qry,null);

            if(cur.getCount() > 0)
            {
                if(cur.moveToFirst())
                {
                    for(int i=0;i < cur.getCount();i++)
                    {
                        hmap.put(cur.getString(0),cur.getString(1));
                        cur.moveToNext();
                    }
                }
            }
        } finally {
            if(cur!=null) {
                cur.close();
            }
            close();
        }
        return hmap;
    }


/*
    public int countNumberOFTextFile()  throws IOException
    {
        // int entryCount;sdfsfsf

        Cursor cursorE2 = db.rawQuery("SELECT Count(*) from tblMessageTextFileContainer where FileFlag=0", null);
        int chkI = 0;
        try {
            if (cursorE2.moveToFirst()) {

                if (cursorE2.getInt(0) > 0) {
                    chkI = 1;
                } else {
                    chkI = 0;
                }
            }

        } finally {
            if(cursorE2!=null) {
                cursorE2.close();
            }
        }
        return chkI;
    }

   */
}
