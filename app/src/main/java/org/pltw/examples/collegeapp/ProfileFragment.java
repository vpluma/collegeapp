package org.pltw.examples.collegeapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.Date;

/**
 * Created by wdumas on 12/23/2014.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DOB = 0;
    //new in 1.4.1
    private static final int REQUEST_SELFIE = 1;

    private static final String KEY_FIRST_NAME = "firstname";
    private static final String FILENAME = "profile.json";

    private Profile mProfile;
    private ProfileJSONStorer mStorer;
    private TextView mFirstName;
    private EditText mEnterFirstName;
    private TextView mLastName;
    private EditText mEnterLastName;
    private Button mDoBButton;
    private Context mAppContext;
    //new as of 1.4.1
    private ImageButton mSelfieButton;
    private ImageView mSelfieView;
    private File mSelfieFile;

    private void updateDoB() {
        mDoBButton.setText(mProfile.dobToString());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DOB) {
            Date dob = (Date)data
                    .getSerializableExtra(DoBPickerFragment.EXTRA_DOB);
            mProfile.setmDateOfBirth(dob);
            updateDoB();
        }
        else if (requestCode == REQUEST_SELFIE) {
            updateSelfieView();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppContext = this.getActivity();
        Log.d(TAG, "Context: " + mAppContext);
        mStorer = new ProfileJSONStorer(mAppContext, FILENAME);
        Log.d(TAG, "onCreate Called$ ");
        try { //begin try block, this is needed in case the file specified by FILENAME does not exist
            mProfile = mStorer.load(); // get the Profile information from the file

        } catch (Exception e) { //if the file is not found do the following
            mProfile = new Profile();// create a new default Profile
            Log.e(TAG, "Error loading profile: " + FILENAME, e); //log message to let us know the profile was not loaded.
        }

        if (savedInstanceState != null) {
            mProfile.setFirstName(savedInstanceState.getString(KEY_FIRST_NAME));
            Log.i(TAG, "The name is " + mProfile.getFirstName());
        }
        //new in 1.4.1
        mSelfieFile = mProfile.getPhotoFile(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        getActivity().setTitle(R.string.profile_title);

        if (savedInstanceState != null) {
            mProfile.setFirstName(savedInstanceState.getString(KEY_FIRST_NAME));
            Log.i(TAG, "The name is " + mProfile.getFirstName());
        }

        mFirstName = (TextView)rootView.findViewById(R.id.first_name);
        mEnterFirstName = (EditText)rootView.findViewById(R.id.enter_first_name);
        mLastName = (TextView)rootView.findViewById(R.id.last_name);
        mEnterLastName = (EditText)rootView.findViewById(R.id.enter_last_name);
        mDoBButton = (Button)rootView.findViewById(R.id.dob_button);

        mFirstName.setText(mProfile.getFirstName());
        mLastName.setText(mProfile.getLastName());

        FirstNameTextChanger firstNameTextChanger = new FirstNameTextChanger();
        LastNameTextChanger lastNameTextChanger = new LastNameTextChanger();
        DoBButtonOnClickListener doBButtonOnClickListener = new DoBButtonOnClickListener();

        updateDoB();
        mDoBButton.setOnClickListener(doBButtonOnClickListener);

        mEnterFirstName.addTextChangedListener(firstNameTextChanger);

        mEnterLastName.addTextChangedListener(lastNameTextChanger);
        mAppContext = this.getActivity();
        Log.d(TAG, "Context: " + mAppContext);
        mStorer = new ProfileJSONStorer(mAppContext, FILENAME);

        //new in 1.4.1
        mSelfieButton = (ImageButton) rootView.findViewById(R.id.profile_camera);
        final Intent captureSelfie = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakeSelfie = mSelfieFile != null &&
                captureSelfie.resolveActivity(getActivity().getPackageManager()) != null;
        mSelfieButton.setEnabled(canTakeSelfie);
        if (canTakeSelfie) {
            Uri uri = Uri.fromFile(mSelfieFile);
            captureSelfie.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        mSelfieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureSelfie, REQUEST_SELFIE);
            }
        });
        mSelfieView = (ImageView) rootView.findViewById(R.id.profile_pic);
        updateSelfieView();

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState got called and it was a BLAST!!!1: " + mProfile.getFirstName());
        savedInstanceState.putString(KEY_FIRST_NAME, mProfile.getFirstName());
    }

    private class FirstNameTextChanger implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mProfile.setFirstName(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
            mFirstName.setText(mProfile.getFirstName());
        }
    }

    private class LastNameTextChanger implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mProfile.setLastName(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
            mLastName.setText(mProfile.getLastName());
        }
    }

    private class DoBButtonOnClickListener implements View.OnClickListener {
        public void onClick(View v) {
            FragmentManager fm = getActivity()
                    .getSupportFragmentManager();
            DoBPickerFragment dialog = DoBPickerFragment
                    .newInstance(mProfile.getDateOfBirth());
            dialog.setTargetFragment(ProfileFragment.this, REQUEST_DOB);
            dialog.show(fm, DIALOG_DATE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "Fragment started.");
    }

    private boolean saveProfile() {
        try {
            mStorer.save(mProfile);
            Log.d(TAG, "profile saved to file.");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving profile: ", e);
            return false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveProfile();
        Log.d(TAG, "Fragment paused.");
    }

    private void loadProfile() {
        try {
            mProfile = mStorer.load();
            Log.d(TAG, "Loaded " + mProfile.getFirstName());
            mFirstName.setText(mProfile.getFirstName());
            mLastName.setText(mProfile.getLastName());
            updateDoB();
        } catch (Exception e) {
            mProfile = new Profile();
            Log.e(TAG, "Error loading profile from: " + FILENAME, e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfile();
        Log.d(TAG, "Fragment resumed.");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "Fragment stoped.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Fragment destroyed.");
    }

    //new in 1.4.1
    private void updateSelfieView() {
        if (mSelfieFile == null) {
            mSelfieView.setImageDrawable(null);
        }
        else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mSelfieFile.getPath(), getActivity());
            mSelfieView.setImageBitmap(bitmap);
        }
    }

}

