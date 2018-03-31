package com.anuntah.takenotes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Bhavit Yadav on 17-02-2018.
 */

public class NotesOpenHelper extends SQLiteOpenHelper{

    private static NotesOpenHelper notesOpenHelper;

    public static NotesOpenHelper getInstance(Context context){
        if(notesOpenHelper==null){
            notesOpenHelper= new NotesOpenHelper(context.getApplicationContext());
        }
        return notesOpenHelper;
    }


    private NotesOpenHelper(Context context) {
        super(context, Constants.DB_NAME, null, Constants.VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String s="CREATE TABLE "+Constants.notes.NAME+" ("+
                Constants.notes.COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +Constants.notes.COL_TITLE+" TEXT,"
                +Constants.notes.COL_TIME+" INTEGER,"
                +Constants.notes.COL_DESCRIPTION+" TEXT)";
        sqLiteDatabase.execSQL(s);
        String s1="CREATE TABLE "+Constants.label.NAME+" ("
                +Constants.label.COL_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
                +Constants.label.COL_LABEL+" TEXT,"
                +Constants.label.COL_NOTES_ID+" INTEGER,"
                +Constants.label.COL_ISCHECKED+" TEXT,"
        +"FOREIGN KEY (" + Constants.label.COL_NOTES_ID + ") REFERENCES " + Constants.notes.NAME + " (" + Constants.notes.COL_ID + ") ON DELETE CASCADE)";
        sqLiteDatabase.execSQL(s1);
    }
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
