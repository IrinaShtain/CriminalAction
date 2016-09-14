package shtainyky.com.criminalaction;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment{
    private Crime mCrime;
    private TextView mTitleField;
    private File mPhotoFile;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallSuspectButton;
    private CheckBox mSolvedCheckBox;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private static final String ARG_CRIME_ID = "arg_crime_id";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID crimeId = (UUID)getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime, container, false);
        //*********mTitleField
        mTitleField = (TextView)view.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //********mDateButton
        mDateButton = (Button)view.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });
        //********mTimeButton
        mTimeButton = (Button)view.findViewById(R.id.crime_time);
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        //********mSolvedCheckBox
        mSolvedCheckBox = (CheckBox) view.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCrime.setSolved(b);
            }
        });
        //********mReportButton
        mReportButton = (Button)view.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .setChooserTitle(getString(R.string.send_report))
                        .createChooserIntent();

//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("text/plain");
//                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
//                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
//                intent = Intent.createChooser(intent, getString(R.string.send_report));
                startActivity(intent);
            }
        });
        //*********mCallSuspectButton

        mCallSuspectButton = (Button)view.findViewById(R.id.crime_suspect_call);
        mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCrime.getSuspectPhone() != null)
                {
                    Intent dialSuspect = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mCrime.getSuspectPhone()));
                    startActivity(dialSuspect);
                }
            }
        });
        //*********mSuspectButton
        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button)view.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
            if (mCrime.getSuspectPhone() != null)
                mCallSuspectButton.setText(getString(R.string.crime_suspect_call, mCrime.getSuspect(), mCrime.getSuspectPhone()));
            else
            {
                mCallSuspectButton.setText(getString(R.string.crime_no_suspect_call));
                mCallSuspectButton.setEnabled(false);
            }
        }
        else {
            mCallSuspectButton.setText(getString(R.string.crime_no_suspect_call));
            mCallSuspectButton.setEnabled(false);
            }


        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        //**********
        mPhotoButton = (ImageButton)view.findViewById(R.id.crime_camera);
        final Intent capturePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null &&
                capturePhoto.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        if (canTakePhoto)
        {
            Uri uri = Uri.fromFile(mPhotoFile);
            capturePhoto.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(capturePhoto, REQUEST_PHOTO);
            }
        });
        //*********
        mPhotoView = (ImageView) view.findViewById(R.id.crime_photo);
        updatePhotoView();
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                PhotoFragment dialog = PhotoFragment.newInstance(mCrime);
                dialog.show(manager, "Photo");
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_DATE)
        {
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        }
        else
            if (requestCode == REQUEST_TIME)
            {
                Date date = (Date)data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
                mCrime.setDate(date);
                updateTime();
            }
        else if (requestCode == REQUEST_CONTACT && data != null)
            {
                String suspect;
                String idNumber;
                Uri contactUri = data.getData();
                String[] queryFields = new String[]{ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID};
                Cursor cursor = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
                try {
                    if (cursor.getCount() == 0)
                        return;
                    cursor.moveToFirst();
                    suspect = cursor.getString(0);
                    idNumber = cursor.getString(1);
                    mCrime.setSuspect(suspect);
                    mSuspectButton.setText(suspect);
                }
                finally {
                    cursor.close();
                }

                Uri numberUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                Cursor pCur = getActivity().getContentResolver().query(
                        numberUri,
                        new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                        new String[]{idNumber}, null);
                try {
                    if (pCur.getCount() == 0) {
                        mCallSuspectButton.setEnabled(false);
                        Toast.makeText(getActivity(), getString(R.string.crime_no_suspect_number), Toast.LENGTH_LONG).show();
                        return;
                    }
                    pCur.moveToFirst();
                    String number = pCur.getString(0);
                    mCrime.setSuspectPhone(number);
                    mCallSuspectButton.setText(getString(R.string.crime_suspect_call, suspect, number));
                    mCallSuspectButton.setEnabled(true);
                }
                finally {
                    pCur.close();
                }

            }
        else
                if (requestCode == REQUEST_PHOTO)
                {
                    updatePhotoView();
                }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_for_removing, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_item_remove_crime:
                CrimeLab.get(getActivity()).removeCrime(mCrime);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void updateDate() {
        mDateButton.setText(DateFormat.format("EEEE, dd MMM yyyy ", mCrime.getDate()));
    }

    private void updateTime() {
        mTimeButton.setText(DateFormat.format("hh:mm:ss a", mCrime.getDate()));
    }

    private  String getCrimeReport()
    {
        String solvedString = null;
        if (mCrime.isSolved())
            solvedString = getString(R.string.crime_report_solved);
        else
            solvedString = getString(R.string.crime_report_unsolved);

        String dateFormat = "EEE, dd MMM yyyy, hh:mm:ss a";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null)
            suspect = getString(R.string.crime_report_no_suspect);
        else
            suspect = getString(R.string.crime_report_suspect, mCrime.getSuspect());

        return getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
    }
    private void updatePhotoView()
    {
        if (mPhotoFile == null || !mPhotoFile.exists())
            mPhotoView.setImageDrawable(null);
        else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}
