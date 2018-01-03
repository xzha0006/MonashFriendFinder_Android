package com.johnsyard.monashfriendfinder;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.johnsyard.monashfriendfinder.fragments.HomeFragment;
import com.johnsyard.monashfriendfinder.fragments.LoginFragment;
import com.johnsyard.monashfriendfinder.fragments.RegisterFragment;
import com.johnsyard.monashfriendfinder.widgets.ExpandAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * This activity is used for login interface
 *
 * Created by xuanzhang on 09/05/2017.
 */

public class LoginActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
//        requestData();
//        setContentView(R.layout.fragment_subscription);
        SharedPreferences sp = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String skipLogin = sp.getString("skipLogin", null);
        FragmentManager fragmentManager = getFragmentManager();
        //skip login
        if (skipLogin != null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else{
            fragmentManager.beginTransaction().replace(R.id.content_frame, new LoginFragment()).commit();
        }
    }

}



