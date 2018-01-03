package com.johnsyard.monashfriendfinder.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
 * This is the friend fragment
 * Created by xuanzhang on 1/05/2017.
 */

public class FriendFragment extends Fragment {
    private View vFriend;
    private ListView lvFriends;
    private TextView tvTitle;
    private Button btViewInMap;
    private Button btDelete;

    private ExpandAdapter adapter = null;
    private SharedPreferences sp;
    private int studentId;
    private String myProfileString;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //??????????
        super.onCreateView(inflater, container, savedInstanceState);

        vFriend = inflater.inflate(R.layout.fragment_friend, container, false);
        lvFriends = (ListView) vFriend.findViewById(R.id.lt_friends);
        tvTitle = (TextView) vFriend.findViewById(R.id.tv_title);
        btViewInMap = (Button) vFriend.findViewById(R.id.bt_view_in_map);
        btDelete = (Button) vFriend.findViewById(R.id.bt_delete);

        //get user's studentId
        sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        myProfileString = sp.getString("myProfile", null);
        JsonObject profileJson = new JsonParser().parse(myProfileString).getAsJsonObject();

        studentId = profileJson.get("studentId").getAsInt();
        //fresh the friend data
        refreshContent(studentId, sp);
        //delete friends
        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<Integer, Boolean> isSelected = adapter.getIsSelected();
                ArrayList<HashMap<String, String>> dataList = adapter.getList();
                int dataSize = dataList.size();
                ArrayList<Integer> deletedItems = new ArrayList<Integer>();

                JsonArray friendships = new JsonArray();
                //use a for loop to fresh the data and get a json array of friendship
                if (dataList.size() != 0){
                    for (int i = 0; i < dataSize; i++){
                        if (isSelected.get(i)){
                            deletedItems.add(i);
                            JsonObject friend = new JsonParser().parse(dataList.get(i).get("friend")).getAsJsonObject();
                            isSelected.put(i, false);
                            friendships.add(endFriendship(friend));
                        }
                    }

                    for (int j = deletedItems.size() - 1; j >= 0; j--){
                        dataList.remove(j);
                    }
                    //tell adapter to change the data
                    adapter.notifyDataSetChanged();

                    new AsyncTask<JsonArray, Void, String>(){
                        @Override
                        protected String doInBackground(JsonArray... jsonArrays) {
                            RestClient.deleteFriends(jsonArrays[0].toString());
                            //refresh friend information
                            return "";

                        }
                        @Override
                        protected void onPostExecute(String s) {
                            HomeFragment.initializeFriends(studentId, sp);
                        }
                    }.execute(friendships);
                    //refresh the friend page.

                }
            }
        });

        //active the map
        btViewInMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new FriendMapFragment()).commit();
            }
        });

        return vFriend;
    }

    /**
     * This method is used to create a json object of friendship for ending
     * @param person
     * @return
     */
    private JsonObject endFriendship(JsonObject person){
        JsonObject friendship = new JsonObject();
        //get friendships
        JsonArray friendshipArray = new JsonParser().parse(sp.getString("friendships", "[]")).getAsJsonArray();

        JsonObject myProfile = new JsonParser().parse(myProfileString).getAsJsonObject();
        int myId = myProfile.get("studentId").getAsInt();
        int personId = person.get("studentId").getAsInt();
        //get the starting date and friendship id
        for (int i = 0; i < friendshipArray.size(); i++){
            JsonObject oldFriendship = friendshipArray.get(i).getAsJsonObject();
            JsonObject friend = oldFriendship.get("friend").getAsJsonObject();
            if (friend.get("studentId").getAsInt() == personId){
                friendship.add("friendshipId", oldFriendship.get("friendshipId"));
                friendship.add("startingDate", oldFriendship.get("startingDate"));
            }
        }

        //set ending time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = sdf.format(new Date());
        friendship.addProperty("endingDate", dateString);

        if (myId < personId){
            friendship.add("studentOneId", myProfile);
            friendship.add("studentTwoId", person);
        }else{
            friendship.add("studentOneId", person);
            friendship.add("studentTwoId", myProfile);
        }
        return friendship;
    }

    /**
     * This method is used to set the result list
     */
    private void refreshContent(int studentId, SharedPreferences sp){
        HomeFragment.initializeFriends(studentId, sp);
        //get friend array from sharepreference
        JsonArray friendsArray = new JsonParser().parse(sp.getString("currentFriends", "[]")).getAsJsonArray();
        if (friendsArray.size() != 0){
            //has matched results
            ArrayList<HashMap<String, String>> data = formatData(friendsArray);
            adapter = new ExpandAdapter(getActivity(), data);
            //show the data
            tvTitle.setText("Your Friends:");
            lvFriends.setAdapter(adapter);
            btViewInMap.setVisibility(View.VISIBLE);
            btDelete.setVisibility(View.VISIBLE);
        }else {
            tvTitle.setText("Sorry, you do not have any friend yet.");
        }
    }
    /**
     * This method is for adapter data formatting
     * @param friendsArray
     * @return
     */
    private ArrayList<HashMap<String, String>> formatData(JsonArray friendsArray) {
        ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String,String>>();
        for(int i = 0; i < friendsArray.size(); i++){
            JsonObject friend = friendsArray.get(i).getAsJsonObject();
            String otherContent = "Study Mode: " + friend.get("studyMode").getAsString() + "\n" +
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


            HashMap<String, String> item = new HashMap<>();
            item.put("friend", friend.toString());
            item.put("name", friend.get("firstName").getAsString() + " " + friend.get("lastName").getAsString());
            item.put("movie", "Favorite Movie: " + friend.get("favouriteMovie").getAsString());
            item.put("studentId", "Student Id: " + friend.get("studentId").getAsString());
            item.put("otherContent", otherContent);
            datas.add(item);
        }
        return datas;
    }
}
