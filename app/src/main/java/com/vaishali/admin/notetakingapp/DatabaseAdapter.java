package com.vaishali.admin.notetakingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 7/5/2016.
 */
public class DatabaseAdapter {
    DbHelper helper;

    public DatabaseAdapter(Context context) {
        helper = new DbHelper(context);
    }

    public long insertData(Notes note) {
        SQLiteDatabase object = helper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DbHelper.TITLE,note.getTitle());
        contentValues.put(DbHelper.CONTENT,note.getContent());
        contentValues.put(DbHelper.EMAIL,note.getEmail());


        long id = object.insert(DbHelper.TABLE_NAME,null,contentValues);
        return id;
    }

    public List<Notes> getAllData(String mail)
    {
        List<Notes> notes = new ArrayList<Notes>();
        String query = "Select * FROM " + DbHelper.TABLE_NAME + " WHERE " + DbHelper.EMAIL + " =  \"" + mail + "\""; ;
        SQLiteDatabase db=helper.getReadableDatabase();
        Cursor cursor=db.rawQuery(query,null);
        if (cursor.moveToFirst()){
            do {
                Notes model = new Notes();
                Integer t=cursor.getColumnIndex(DbHelper.TITLE);
                Integer c=cursor.getColumnIndex(DbHelper.CONTENT);

                model.setTitle(cursor.getString(t));

                model.setContent(cursor.getString(c));


                notes.add(model);
            }while (cursor.moveToNext());
        }


        Log.d("student data", notes.toString());
        return notes;
    }

    public String getData(String username)
    {
        String query = "SELECT * FROM " + DbHelper.TABLE_NAME + " WHERE " + DbHelper.EMAIL + " =  \"" + username + "\"";
        SQLiteDatabase db=helper.getWritableDatabase();

        Cursor cursor=db.rawQuery(query,null);
        if (cursor.getCount() < 1) {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String password = cursor.getString(2);
        cursor.close();
        return password;

    }
    public void deleteARow(String title){
        SQLiteDatabase db= helper.getWritableDatabase();
        db.delete(DbHelper.TABLE_NAME, DbHelper.TITLE + " = ?", new String[] { title });
        db.close();
    }






    static class DbHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "cusdatabase.db";
        private static final String TABLE_NAME = "NOTES";
        private static final int DATABASE_VERSION = 104;
        private static final String UID = "_id";
        private static final String TITLE = "Title";
        private static final String CONTENT = "Content";
        private static final String EMAIL = "Email";


        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + UID + " INTEGER PRIMARY KEY," + TITLE + " TEXT NOT NULL," + CONTENT+ " TEXT NOT NULL ," + EMAIL + " VARCHAR(255));";
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;



        private Context c;

        public DbHelper(Context c) {

            super(c,DATABASE_NAME,null,DATABASE_VERSION);
            this.c = c;

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub

            try {
                db.execSQL(CREATE_TABLE);

            } catch (SQLException e) {
                Toast.makeText(c,"Unsuccessful"+e,Toast.LENGTH_SHORT).show();
            }


        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            try {


                db.execSQL(DROP_TABLE);
                onCreate(db);
            } catch (SQLException e) {
                Toast.makeText(c,"Unsuccessful"+e,Toast.LENGTH_SHORT).show();
            }
        }



    }
}
