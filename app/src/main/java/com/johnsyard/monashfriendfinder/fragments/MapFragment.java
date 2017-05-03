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
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.johnsyard.monashfriendfinder.R;
import com.johnsyard.monashfriendfinder.RestClient;
import com.johnsyard.monashfriendfinder.widgets.ExpandAdapter;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapquest.mapping.maps.OnMapReadyCallback;
import com.mapquest.mapping.maps.MapboxMap;
import com.mapquest.mapping.maps.MapView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is the map fragment
 * Created by xuanzhang on 3/05/2017.
 */

public class MapFragment extends Fragment {
    private View vMap;
    //map attributes
    private MapboxMap mMapboxMap;
    private MapView mMapView;


    private final LatLng CAULFIELD = new LatLng(-37.7749, 143.4194);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vMap = inflater.inflate(R.layout.fragment_friend_map, container, false);
        //to fix the mapbox error
        MapboxAccountManager.start(getActivity().getApplicationContext(), "BbVuA8V5P9aPDtmn");

        mMapView = (MapView) vMap.findViewById(R.id.mapquestMapView);
        mMapView.onCreate(savedInstanceState);

        //get user's studentId
        SharedPreferences sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String currentFriends = sp.getString("currentFriends", null);
        final JsonArray currentFriendsJson = new JsonParser().parse(currentFriends).getAsJsonArray();

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mMapboxMap = mapboxMap;
                mMapboxMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CAULFIELD, 11));
                //loop to add marker
                if (currentFriendsJson.size() > 0){
                    for (int i = 0; i < currentFriendsJson.size(); i++){
                        addMarker(mMapboxMap, currentFriendsJson.get(i).getAsJsonObject());
                    }
                }
            }
        });

        return vMap;
    }

    private void addMarker (MapboxMap mapboxMap, JsonObject friend){
        String otherContent = "Student Id: " + friend.get("studentId").getAsString() + "\n" +
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
        markerOptions.position(CAULFIELD);
        markerOptions.title(friend.get("firstName").getAsString() + " " + friend.get("lastName").getAsString());
        markerOptions.snippet(otherContent);
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
