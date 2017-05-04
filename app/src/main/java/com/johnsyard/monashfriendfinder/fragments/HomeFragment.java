package com.johnsyard.monashfriendfinder.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
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
import com.johnsyard.monashfriendfinder.dbmanager.DBManager;
import com.johnsyard.monashfriendfinder.widgets.ExpandAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by xuanzhang on 1/05/2017.
 */

public class HomeFragment extends Fragment {
    final int BASE_LATITUDE = -35;
    final int BASE_LONGITUDE = 145;
    final int LAT_LONG_RANGE = 3;

    private View vHome;
    private TextView tvTemp;
    private TextView tvTime;
    private TextView tvName;

    private SharedPreferences sp;
    private DBManager dbManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vHome = inflater.inflate(R.layout.fragment_home, container, false);
        tvTemp = (TextView) vHome.findViewById(R.id.tv_temperature);
        tvTime = (TextView) vHome.findViewById(R.id.tv_time);
        tvName = (TextView) vHome.findViewById(R.id.tv_hi);
        //get location
        JsonObject location = initLocation();
        //insert info into database
        try {
            dbManager.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String latitude = location.get("latitude").toString();
        String longitude = location.get("longitude").toString();

        dbManager.insertUser("1", latitude, longitude);
        dbManager.close();
        //get time stamp
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = sdf.format(new Date());
        tvTime.setText("Login time: " + dateString);

        //get student name and id from shard preference
        sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String myProfile = sp.getString("myProfile", null);
        JsonObject profileJson = new JsonParser().parse(myProfile).getAsJsonObject();

        String name = profileJson.get("firstName").getAsString() + " " + profileJson.get("lastName").getAsString();
        int studentId = profileJson.get("studentId").getAsInt();

        tvName.setText("Hi, " + name);
        //get the initialized friend data
        initializeFriends(studentId, sp);

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
    public static void initializeFriends(int studentId, final SharedPreferences spSelf){
        new AsyncTask<Integer, Void, JsonArray>() {
            @Override
            protected JsonArray doInBackground(Integer... ints) {
                JsonArray friendshipArray = null;
                friendshipArray = RestClient.findCurrentFriends(ints[0]);
                return friendshipArray;
            }

            @Override
            protected void onPostExecute(JsonArray friendshipArray) {
                //store the data into shardpreference
                //friends and friend ids
                SharedPreferences.Editor spEdit = spSelf.edit();
                spEdit.putString("friendships", friendshipArray.toString());
                //get friends from friendships
                JsonArray friendsArray = new JsonArray();
                if (friendshipArray.size() > 0){
                    for (int i = 0; i < friendshipArray.size(); i++){
                        JsonObject friend = friendshipArray.get(i).getAsJsonObject().get("friend").getAsJsonObject();
                        friendsArray.add(friend);
                    }
                }
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

    /**
     * Create user location randomly range is -35 +- 4 and 145 +- 4
     * @return location
     */
    private JsonObject initLocation(){
        double delta = Math.random() * LAT_LONG_RANGE;
        double latitude = BASE_LATITUDE + delta;
        double longitude = BASE_LONGITUDE + delta;
        JsonObject location = new JsonObject();
        location.addProperty("latitude", latitude);
        location.addProperty("longitude", longitude);
        return location;
    }
}
