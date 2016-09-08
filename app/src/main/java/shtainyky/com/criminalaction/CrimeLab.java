package shtainyky.com.criminalaction;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import shtainyky.com.criminalaction.database.CrimeBaseHelper;
import shtainyky.com.criminalaction.database.CrimeCursorWrapper;
import shtainyky.com.criminalaction.database.CrimeDbSchema.CrimeTable;

import static shtainyky.com.criminalaction.database.CrimeDbSchema.Cols.DATE;
import static shtainyky.com.criminalaction.database.CrimeDbSchema.Cols.SOLVED;
import static shtainyky.com.criminalaction.database.CrimeDbSchema.Cols.SUSPECT;
import static shtainyky.com.criminalaction.database.CrimeDbSchema.Cols.TITLE;
import static shtainyky.com.criminalaction.database.CrimeDbSchema.Cols.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;
   // private List<Crime> mCrimes;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }
    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
       // mCrimes = new ArrayList<>();
    }
    public void addCrime(Crime crime)
    {
       // mCrimes.add(crime);
        ContentValues values = getContentValues(crime);
        mDatabase.insert(CrimeTable.NAME, null, values);
    }
    public void removeCrime(Crime crime)
    {
        String value = crime.getId().toString();
        mDatabase.delete(CrimeTable.NAME, UUID + "= ?",new String[]{value});
//        for (int i = 0; i < mCrimes.size(); i++) {
//            if (mCrimes.get(i).getId() == crime.getId()) {
//                mCrimes.remove(i);
//                break;
//            }
//        }
    }
    public List<Crime> getmCrimes() {
        //return mCrimes;
        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }
        finally {
            cursor.close();
        }
        return crimes;
    }
    public Crime getCrime(UUID id)
    {
        CrimeCursorWrapper cursor = queryCrimes(UUID + " = ?",new String[]{id.toString()} );
        try
        {
            if (cursor.getCount() == 0)return null;
            cursor.moveToFirst();
            return cursor.getCrime();
        }
        finally {
            cursor.close();
        }
//        for (Crime crime:mCrimes)
//            if (crime.getId().equals(id))
//                return crime;

    }
    public void updateCrime(Crime crime)
    {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        mDatabase.update(CrimeTable.NAME, values, UUID + " = ?", new String[]{uuidString});
    }
    private static ContentValues getContentValues(Crime crime)
    {
        ContentValues values = new ContentValues();
        values.put(UUID, crime.getId().toString());
        values.put(TITLE, crime.getTitle());
        values.put(DATE, crime.getDate().getTime());
        values.put(SUSPECT, crime.getSuspect());
        values.put(SOLVED, crime.isSolved()? 1:0);

        return values;
    }
    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs)
    {
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new CrimeCursorWrapper(cursor);
    }
}
