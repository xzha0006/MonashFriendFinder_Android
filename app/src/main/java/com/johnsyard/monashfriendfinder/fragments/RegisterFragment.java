package com.johnsyard.monashfriendfinder.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.johnsyard.monashfriendfinder.R;
import com.johnsyard.monashfriendfinder.RestClient;
import com.johnsyard.monashfriendfinder.entities.Profile;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This is the register fragment
 * Created by xuanzhang on 28/04/2017.
 */

public class RegisterFragment extends Fragment {
    private View vRegister;
    private Button btSetDate;
    private Button btSubmit;
    private Button btCancel;
    //information text
    private EditText etEmail;
    private EditText etPassword;
    private EditText etPasswordAgain;
    //    private EditText subscriptionDatetime;
//    private EditText studentId;
    private EditText etFirstName;
    private EditText etLastName;
    private TextView tvDateOfBirth;
    private RadioButton rbMale;
    private RadioButton rbFemale;
    private Spinner sCourse;
    private Spinner sStudyMode;
    private EditText etAddress;
    private EditText etSuburb;
    private Spinner sNationality;
    private Spinner sNativeLanguage;
    private Spinner sFavouriteSport;
    private EditText etFavouriteMovie;
    private Spinner sFavouriteUnit;
    private EditText etCurrentJob;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vRegister = inflater.inflate(R.layout.fragment_subscription, container, false);
        btSetDate = (Button) vRegister.findViewById(R.id.bt_setdate);
        btSubmit = (Button) vRegister.findViewById(R.id.bt_submit);
        btCancel = (Button) vRegister.findViewById(R.id.bt_cancel);

        //get the data
        etEmail = (EditText) vRegister.findViewById(R.id.et_email_register);
        etPassword = (EditText) vRegister.findViewById(R.id.et_password_create);
        etPasswordAgain = (EditText) vRegister.findViewById(R.id.et_password_again);
        etFirstName = (EditText) vRegister.findViewById(R.id.et_firstname);
        etLastName = (EditText) vRegister.findViewById(R.id.et_lastname);
        tvDateOfBirth = (TextView) vRegister.findViewById(R.id.tv_datepicker_dob);
        rbMale = (RadioButton) vRegister.findViewById(R.id.rbt_male);
        rbFemale = (RadioButton) vRegister.findViewById(R.id.rbt_female);
        sCourse = (Spinner) vRegister.findViewById(R.id.s_course);
        sStudyMode = (Spinner) vRegister.findViewById(R.id.s_studymode);
        etAddress = (EditText) vRegister.findViewById(R.id.et_address);
        etSuburb = (EditText) vRegister.findViewById(R.id.et_suburb);
        sNationality = (Spinner) vRegister.findViewById(R.id.s_nation);
        sNativeLanguage = (Spinner) vRegister.findViewById(R.id.s_language);
        sFavouriteSport = (Spinner) vRegister.findViewById(R.id.s_favorite_sport);
        etFavouriteMovie = (EditText) vRegister.findViewById(R.id.et_favorite_movie);
        sFavouriteUnit = (Spinner) vRegister.findViewById(R.id.s_favorite_unit);
        etCurrentJob = (EditText) vRegister.findViewById(R.id.et_job);


        //date picker for selecting date of birth
        btSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });

        //submit register information
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String passwordAgain = etPasswordAgain.getText().toString();
                //get current date time
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateString = sdf.format(new Date());
                String subscriptionDatetime = dateString;

                String firstName = etFirstName.getText().toString();
                String lastName = etLastName.getText().toString();
                String dateOfBirth = tvDateOfBirth.getText().toString();
                String gender = "";
                if (rbMale.isChecked()) {
                    gender = "Male";
                } else if (rbFemale.isChecked()) {
                    gender = "Female";
                }
                String course = sCourse.getSelectedItem().toString();
                String studyMode = sStudyMode.getSelectedItem().toString();
                String address = etAddress.getText().toString();
                String suburb = etSuburb.getText().toString();
                String nationality = sNationality.getSelectedItem().toString();
                String nativeLanguage = sNativeLanguage.getSelectedItem().toString();
                String favouriteSport = sFavouriteSport.getSelectedItem().toString();
                String favouriteMovie = etFavouriteMovie.getText().toString();
                String favouriteUnit = sFavouriteUnit.getSelectedItem().toString();
                String currentJob = etCurrentJob.getText().toString();

                //validate input
                if (email.isEmpty()) {
                    etEmail.setError("Email is required!");
                    return;
                }
                if (password.isEmpty()) {
                    etPassword.setError("Password is required!");
                    return;
                }
                if (passwordAgain.isEmpty()) {
                    etPasswordAgain.setError("Re-enter password is required!");
                    return;
                }
                //check the password inputs
                if (!password.equals(passwordAgain)) {
                    etPassword.setText("");
                    etPasswordAgain.setText("");
                    etPassword.setError("Password inputs are not same.");
                    return;
                }
                if (firstName.isEmpty()) {
                    etFirstName.setError("First Name is required!");
                    return;
                }
                if (lastName.isEmpty()) {
                    etLastName.setError("Last Name is required!");
                    return;
                }
                if (address.isEmpty()) {
                    etAddress.setError("Address is required!");
                    return;
                }
                if (suburb.isEmpty()) {
                    etSuburb.setError("Suburb is required!");
                    return;
                }
                if (favouriteMovie.isEmpty()) {
                    etFavouriteMovie.setError("Favorite Movie is required!");
                    return;
                }
                if (currentJob.isEmpty()) {
                    etCurrentJob.setError("Current Job is required!");
                    return;
                }
                //constructor the profile object
                Profile profile = new Profile();
                profile.setEmail(email);
                profile.setPassword(password);
                profile.setFirstName(firstName);
                profile.setLastName(lastName);
                profile.setAddress(address);
                profile.setCourse(course);
                profile.setCurrentJob(currentJob);
                profile.setDateOfBirth(dateOfBirth);
                profile.setFavouriteMovie(favouriteMovie);
                profile.setFavouriteSport(favouriteSport);
                profile.setFavouriteUnit(favouriteUnit);
                profile.setGender(gender);
                profile.setNationality(nationality);
                profile.setNativeLanguage(nativeLanguage);
                profile.setStudyMode(studyMode);
                profile.setSubscriptionDatetime(subscriptionDatetime);
                profile.setSuburb(suburb);

                //store the profile in local
//                SharedPreferences spMyProfile = getActivity().getSharedPreferences("myProfile", Context.MODE_PRIVATE);
//                String sMyProfile = spMyProfile.getString("myProfile", null);
//                String jsMyProfile = new Gson().toJson(profile);
//
//                SharedPreferences.Editor eMyProfile = spMyProfile.edit();
//                eMyProfile.putString("myProfile", jsMyProfile);
//                eMyProfile.apply();
                new AsyncTask<String, Void, String>() {
                    @Override
                    protected void onPreExecute() {
                        Toast.makeText(getActivity().getApplicationContext(), "Your profile is submitting...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected String doInBackground(String... strings) {
                        RestClient.createProfile(strings[0]);
                        return "Registered successfully!";
                    }

                    @Override
                    protected void onPostExecute(String response) {
                        Toast.makeText(getActivity().getApplicationContext(), response, Toast.LENGTH_LONG).show();
                    }
                }.execute(new Gson().toJson(profile));
            }
        });

        //cancel and go back to login page.
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new LoginFragment()).commit();
            }
        });

        return vRegister;
    }

    private boolean emptyCheck() {

        return false;
    }
}

