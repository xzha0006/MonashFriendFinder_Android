package com.johnsyard.monashfriendfinder;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.johnsyard.monashfriendfinder.fragments.LoginFragment;
import com.johnsyard.monashfriendfinder.fragments.RegisterFragment;

/**
 * This activity is used for login part
 *
 * Created by xuanzhang on 27/04/2017.
 */

public class LoginActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
//        setContentView(R.layout.fragment_subscription);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new LoginFragment()).commit();

        //login button listener
//        Button loginBtn = (Button) findViewById(R.id.bt_login);
//        loginBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });

        //register button listener
//        Button registerBtn = (Button) findViewById(R.id.bt_register);
//        registerBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                FragmentManager fragmentManager = getFragmentManager();
//                fragmentManager.beginTransaction().replace(R.id.content_login, new RegisterFragment()).commit();
//            }
//        });
    }
}



