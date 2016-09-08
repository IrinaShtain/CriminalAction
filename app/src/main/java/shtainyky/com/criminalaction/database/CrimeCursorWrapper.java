package shtainyky.com.criminalaction.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;

import shtainyky.com.criminalaction.Crime;

import static shtainyky.com.criminalaction.database.CrimeDbSchema.Cols.DATE;
import static shtainyky.com.criminalaction.database.CrimeDbSchema.Cols.SOLVED;
import static shtainyky.com.criminalaction.database.CrimeDbSchema.Cols.SUSPECT;
import static shtainyky.com.criminalaction.database.CrimeDbSchema.Cols.TITLE;
import static shtainyky.com.criminalaction.database.CrimeDbSchema.Cols.UUID;

public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(UUID));
        String title = getString(getColumnIndex(TITLE));
        long date = getLong(getColumnIndex(DATE));
        int isSolved = getInt(getColumnIndex(SOLVED));
        String suspect = getString(getColumnIndex(SUSPECT));

        Crime crime = new Crime(java.util.UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setSuspect(suspect);
        return crime;
    }
}
