package com.johnsyard.monashfriendfinder.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.johnsyard.monashfriendfinder.R;
import com.johnsyard.monashfriendfinder.RestClient;
import com.johnsyard.monashfriendfinder.widgets.ExpandAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by xuanzhang on 1/05/2017.
 */

public class HomeFragment extends Fragment {
    private View vHome;
    private TextView tvTemp;
    private TextView tvTime;
    private TextView tvName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vHome = inflater.inflate(R.layout.fragment_home, container, false);
        tvTemp = (TextView) vHome.findViewById(R.id.tv_temperature);
        tvTime = (TextView) vHome.findViewById(R.id.tv_time);
        tvName = (TextView) vHome.findViewById(R.id.tv_hi);
        //should be changed
        String latitude = "-37.8";
        String longitude = "145";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = sdf.format(new Date());
        tvTime.setText("Login time: " + dateString);
        //get student name and id from shard preference
        SharedPreferences sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String myProfile = sp.getString("myProfile", null);
        JsonObject profileJson = new JsonParser().parse(myProfile).getAsJsonObject();

        String name = profileJson.get("firstName").getAsString() + " " + profileJson.get("lastName").getAsString();
        int studentId = profileJson.get("studentId").getAsInt();

        tvName.setText("Hi, " + name);
        //get the initialized friend data
        getFriends(studentId);

        //get temperature
        new AsyncTask<String, Void, String>(){
            @Override
            protected String doInBackground(String... strings) {
                return RestClient.getTemperatureByLocation(strings[0], strings[1]);
            }
            @Override
            protected void onPostExecute(String temperature) {
                tvTemp.setText("Today's temperature is " + temperature + "°C");
            }
        }.execute(latitude, longitude);
        return vHome;
    }

    /**
     * This method is used to initialize friends data from the database.
     */
    private void getFriends(int studentId){
        new AsyncTask<Integer, Void, JsonArray>() {
            @Override
            protected JsonArray doInBackground(Integer... ints) {
                JsonArray friendsArray = null;
                friendsArray = RestClient.findCurrentFriends(ints[0]);
                return friendsArray;
            }

            @Override
            protected void onPostExecute(JsonArray friendsArray) {
                //store the data into shardpreference
                //friends and friend ids
                SharedPreferences sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor spEdit = sp.edit();
                spEdit.putString("currentFriends", friendsArray.toString());
                String friendIds = "";
                if (friendsArray.size() > 0){
                    for (int i = 0; i < friendsArray.size(); i++){
                        friendIds += friendsArray.get(i).getAsJsonObject().get("studentId").getAsString() + ",";
                    }
                    friendIds = friendIds.substring(0, friendIds.length() - 1);}
                spEdit.putString("friendIds", friendIds);
                spEdit.apply();
            }
        }.execute(studentId);
    }
}
