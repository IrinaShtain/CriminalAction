package shtainyky.com.criminalaction;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimes;
    private Context mcontext;

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }
    private CrimeLab(Context context) {
        mCrimes = new ArrayList<>();
        mcontext = context;
    }
    public void addCrime(Crime crime)
    {
        mCrimes.add(crime);
    }
    public void removeCrime(Crime crime)
    {
        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId() == crime.getId()) {
                mCrimes.remove(i);
                break;
            }
        }
    }
    public List<Crime> getmCrimes() {
        return mCrimes;
    }
    public Crime getCrime(UUID id)
    {
        for (Crime crime:mCrimes)
            if (crime.getId().equals(id))
                return crime;
    return null;
    }
}
