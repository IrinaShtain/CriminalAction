package shtainyky.com.criminalaction.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static shtainyky.com.criminalaction.database.CrimeDbSchema.Cols.DATE;
import static shtainyky.com.criminalaction.database.CrimeDbSchema.Cols.SOLVED;
import static shtainyky.com.criminalaction.database.CrimeDbSchema.Cols.SUSPECT;
import static shtainyky.com.criminalaction.database.CrimeDbSchema.Cols.SUSPECT_PHONE;
import static shtainyky.com.criminalaction.database.CrimeDbSchema.Cols.TITLE;
import static shtainyky.com.criminalaction.database.CrimeDbSchema.Cols.UUID;
import static shtainyky.com.criminalaction.database.CrimeDbSchema.CrimeTable.NAME;

public class CrimeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";
    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + NAME + "(" +
        " _id integer primary key autoincrement, " +
        UUID + ", " + TITLE + ", " + DATE + ", " + SOLVED + ", " + SUSPECT + ", " + SUSPECT_PHONE + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
