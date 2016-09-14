package shtainyky.com.criminalaction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class PhotoFragment extends DialogFragment {

    private static Crime mCrime;

    public static PhotoFragment newInstance(Crime crime) {
        mCrime = crime;
        return new PhotoFragment();
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo, null);
        File mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
        Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        imageView.setImageBitmap(bitmap);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.crime_photo)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                })
                .create();
    }
}
