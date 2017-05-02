package com.johnsyard.monashfriendfinder;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.johnsyard.monashfriendfinder.fragments.LoginFragment;
import com.johnsyard.monashfriendfinder.fragments.RegisterFragment;
import com.johnsyard.monashfriendfinder.widgets.ExpandAdapter;

import java.util.ArrayList;
import java.util.HashMap;

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
//        requestData();
//        setContentView(R.layout.fragment_subscription);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new LoginFragment()).commit();
    }

//    private void requestData() {
//        ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String,String>>();
//        for(int i = 1; i <= 10; i++){
//            HashMap<String, String> item = new HashMap<String, String>();
//            item.put("otherContent", "HTC-M" + i + "");
//            item.put("name", "lalala" + i);
//            item.put("movie", "ssss" + i);
//            datas.add(item);
//        }
//
//        ListView lvProduct = (ListView) findViewById(R.id.lt);
//        ExpandAdapter adapter = new ExpandAdapter(this, datas);
//        lvProduct.setAdapter(adapter);
//    }
}



