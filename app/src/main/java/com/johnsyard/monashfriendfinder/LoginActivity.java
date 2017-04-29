package com.johnsyard.monashfriendfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

/**
 * This activity is used for login part
 *
 * Created by xuanzhang on 27/04/2017.
 */

public class LoginActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
        setContentView(R.layout.fragment_subscription);
//        Button loginBtn = (Button) findViewById(R.id.bt_login);
//        loginBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
    }
}



