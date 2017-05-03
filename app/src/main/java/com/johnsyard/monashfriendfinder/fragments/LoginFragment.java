package com.johnsyard.monashfriendfinder.fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.johnsyard.monashfriendfinder.LoginActivity;
import com.johnsyard.monashfriendfinder.MainActivity;
import com.johnsyard.monashfriendfinder.R;
import com.johnsyard.monashfriendfinder.RestClient;

/**
 * This is the login fragment.
 * Created by xuanzhang on 30/04/2017.
 */

public class LoginFragment extends Fragment {
    private View vLogin;
    Button btLogin;
    Button btRegister;
    EditText etUserName;
    EditText etPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vLogin = inflater.inflate(R.layout.fragment_login, container, false);
        btLogin = (Button) vLogin.findViewById(R.id.bt_login);
        btRegister = (Button) vLogin.findViewById(R.id.bt_register);
        etUserName = (EditText) vLogin.findViewById(R.id.et_user_name);
        etPassword = (EditText) vLogin.findViewById(R.id.et_password);

        //login
        btLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String username = etUserName.getText().toString();
                String password = etPassword.getText().toString();
                if (username.isEmpty()) {
                    etUserName.setError("User Name is required!");
                    return;
                }
                if (password.isEmpty()) {
                    etPassword.setError("User Name is required!");
                    return;
                }
                Boolean result;
                new AsyncTask<String, Void, JsonObject>(){
                    //
                    @Override
                    protected void onPreExecute(){
                        Toast.makeText(getActivity().getApplicationContext(), "Processing...", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    protected JsonObject doInBackground(String... strings) {
                        JsonObject result = RestClient.loginCheck(strings[0], strings[1]);
                        return result;
                    }
                    @Override
                    protected void onPostExecute(JsonObject response) {
                        boolean isChecked = Boolean.parseBoolean(response.get("response").getAsString());
                        if (isChecked) {
                            //get user's student id
                            JsonObject myProfile = response.get("userInfo").getAsJsonObject();
                            //store the id in local
                            SharedPreferences sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                            SharedPreferences.Editor spEdit = sp.edit();
                            spEdit.putString("myProfile", myProfile.toString());
                            spEdit.apply();
                            //clear the data and go to home page
                            Toast.makeText(getActivity().getApplicationContext(), "Login Successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            etUserName.setText("");
                            etPassword.setText("");
                        }
                        //if false, clear the edittexts
                        else {
                            etUserName.setText("");
                            etPassword.setText("");
                            Toast.makeText(getActivity().getApplicationContext(), "Wrong user name or password, please try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                }.execute(username, password);
                //check the username and password
            }
        });

        //register as a new user, call the register fragment
        btRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new RegisterFragment()).commit();
            }
        });

        return vLogin;
    }
}
