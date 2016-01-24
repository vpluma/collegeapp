package org.pltw.examples.collegeapp;



import android.content.Context;
import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by wdumas on 12/22/2014.
 */
public class Profile implements ApplicantData{
    private static final String JSON_FIRST_NAME = "firstName";
    private static final String JSON_LAST_NAME = "lastName";
    private static final String JSON_DOB = "dob";
    //new in 1.4.1
    private static final String PHOTO_FILENAME = "IMG_PROFILE.jpg";

    private String mFirstName;
    private String mLastName;
    private Date mDateOfBirth;

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String mLastName) {
        this.mLastName = mLastName;
    }

    public Date getDateOfBirth() {
        return mDateOfBirth;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_FIRST_NAME, mFirstName);
        json.put(JSON_LAST_NAME, mLastName);
        json.put(JSON_DOB, mDateOfBirth.getTime());
        System.out.println("Date of Birth Saved: " + mDateOfBirth);
        return json;
    }

    public String dobToString() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        return df.format(mDateOfBirth.getTime());
    }

    public void setmDateOfBirth(Date mDateOfBirth) {
        this.mDateOfBirth = mDateOfBirth;
    }

    public Profile() {
        mFirstName = new String("Wyatt");
        mLastName = new String("Dumas");
        mDateOfBirth = new Date(83, 0, 24);
    }

    public Profile(JSONObject json) throws JSONException {
        mFirstName = json.getString(JSON_FIRST_NAME);
        mLastName = json.getString(JSON_LAST_NAME);
        mDateOfBirth = new Date(json.getLong(JSON_DOB));
    }
    public String getPhotoFileName() {return PHOTO_FILENAME;}
    public String toString() {
        return mFirstName + " " + mLastName + " " + mDateOfBirth.getTime();
    }

    //new as of 1.4.1
    public File getPhotoFile(Context context) {
        File externalFilesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalFilesDir==null){
            return null;
        }
        return new File(externalFilesDir, getPhotoFileName());
    }

}
