package com.johnsyard.monashfriendfinder.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.johnsyard.monashfriendfinder.R;
import com.johnsyard.monashfriendfinder.RestClient;
import com.johnsyard.monashfriendfinder.dbmanager.DBManager;
import com.johnsyard.monashfriendfinder.widgets.ExpandAdapter;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapquest.mapping.maps.OnMapReadyCallback;
import com.mapquest.mapping.maps.MapboxMap;
import com.mapquest.mapping.maps.MapView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is the friend map fragment
 * Created by xuanzhang on 3/05/2017.
 */

public class FriendMapFragment extends Fragment {
    private View vMap;
    //map attributes
    private MapboxMap mMapboxMap;
    private MapView mMapView;
    private DBManager dbManager;
    private LatLng userPosition;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dbManager = new DBManager(getActivity());
        //get location from SQLite database
        try
        {
            dbManager.open();
        } catch(SQLException e)
        {
            e.printStackTrace();
        }

        Cursor cursor = dbManager.selectUser("1");
        cursor.moveToFirst();
        String lat = cursor.getString(1);
        String longi = cursor.getString(2);
        dbManager.close();
        //should be changed
        userPosition = new LatLng(Double.parseDouble(lat), Double.parseDouble(longi));

        vMap = inflater.inflate(R.layout.fragment_friend_map, container, false);
        //to fix the mapbox error
        MapboxAccountManager.start(getActivity().getApplicationContext(), "BbVuA8V5P9aPDtmn");

        mMapView = (MapView) vMap.findViewById(R.id.mapquestMapView);
        mMapView.onCreate(savedInstanceState);
        //get user's studentId
        SharedPreferences sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String currentFriends = sp.getString("currentFriends", null);
        String friendIds = sp.getString("friendIds", "");
        final JsonArray currentFriendsJson = new JsonParser().parse(currentFriends).getAsJsonArray();
            //get locations and set friend markers
            new AsyncTask<String, Void, JsonArray>() {
                //get location first
                @Override
                protected JsonArray doInBackground(String... strings) {
                    JsonArray locations = null;
                    if (strings[0].length() > 0){
                        locations = RestClient.findLocationsByIds(strings[0]);}
                    return locations;
                }

                //then set markers
                @Override
                protected void onPostExecute(final JsonArray locations) {

                    mMapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(MapboxMap mapboxMap) {
                            mMapboxMap = mapboxMap;
                            mMapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 5));
                            //set user marker
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(userPosition);

                            IconFactory iconFactory = IconFactory.getInstance(getActivity());
                            Drawable iconDrawable = ContextCompat.getDrawable(getActivity(), R.mipmap.blue_marker);
                            Icon icon = iconFactory.fromDrawable(iconDrawable);

                            markerOptions.setIcon(icon);
                            markerOptions.snippet("This is your location.");
                            mapboxMap.addMarker(markerOptions);
                            //loop to add marker
                            if (currentFriendsJson.size() > 0 && locations != null && locations.size() > 0) {
                                //use two loop to match the locations
                                for (int i = 0; i < currentFriendsJson.size(); i++) {
                                    JsonObject friend = currentFriendsJson.get(i).getAsJsonObject();
                                    for (int j = 0; j < locations.size(); j++) {
                                        JsonObject location = locations.get(j).getAsJsonObject();
                                        //there is a nested structure in location
                                        if (friend.get("studentId").getAsInt() == location.get("studentId").getAsJsonObject().get("studentId").getAsInt()) {
                                            LatLng position = new LatLng(location.get("latitude").getAsDouble(), location.get("longitude").getAsDouble());
                                            addMarker(mMapboxMap, friend, position);
                                        }
                                    }

                                }
                            }
                        }
                    });
                }
            }.execute(friendIds);

        return vMap;
    }

    /**
     * This method is used to add friend markers
     * @param mapboxMap
     * @param friend
     * @param position
     */
    private void addMarker (MapboxMap mapboxMap, JsonObject friend, LatLng position){
        String detail = "Student Id: " + friend.get("studentId").getAsString() + "\n" +
                "Study Mode: " + friend.get("studyMode").getAsString() + "\n" +
                "Course: " + friend.get("course").getAsString() + "\n" +
                "Gender: " + friend.get("gender").getAsString() + "\n" +
                "Date Of Birth: " + friend.get("dateOfBirth").getAsString() + "\n" +
                "Email: " + friend.get("email").getAsString() + "\n" +
                "Address: " + friend.get("address").getAsString() + "\n" +
                "Nationality: " + friend.get("nationality").getAsString() + "\n" +
                "Native Language: " + friend.get("nativeLanguage").getAsString() + "\n" +
                "Favourite Sport: " + friend.get("favouriteSport").getAsString() + "\n" +
                "Favourite Unit: " + friend.get("favouriteUnit").getAsString() + "\n" +
                "Current Job: " + friend.get("currentJob").getAsString();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        markerOptions.title("Student Name: " + friend.get("firstName").getAsString() + " " + friend.get("lastName").getAsString());

        IconFactory iconFactory = IconFactory.getInstance(getActivity());
        Drawable iconDrawable = ContextCompat.getDrawable(getActivity(), R.mipmap.green_marker);
        Icon icon = iconFactory.fromDrawable(iconDrawable);

        markerOptions.setIcon(icon);
        markerOptions.snippet(detail);
        mapboxMap.addMarker(markerOptions);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
}
