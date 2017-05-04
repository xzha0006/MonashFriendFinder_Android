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

import java.util.ArrayList;
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
        String myProfile = sp.getString("myProfile", null);
        JsonObject profileJson = new JsonParser().parse(myProfile).getAsJsonObject();

        studentId = profileJson.get("studentId").getAsInt();
        //fresh the friend data
        HomeFragment.initializeFriends(studentId, sp);

        //delete friends
        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<Integer, Boolean> isSelected = adapter.getIsSelected();
                ArrayList<HashMap<String, String>> dataList = adapter.getList();
                String ids = "";
                int dataSize = dataList.size();
                ArrayList<Integer> deletedItems = new ArrayList<Integer>();
                if (dataList.size() != 0){
                    for (int i = 0; i < dataSize; i++){
                        if (isSelected.get(i)){
                            String studentIdContent = dataList.get(i).get("studentId");
                            String studentIdStr = studentIdContent.substring(studentIdContent.indexOf(":") + 1).trim();
                            ids += studentIdStr + " ";
                            deletedItems.add(i);
                            isSelected.put(i, false);
                        }
                    }

                    for (int j = deletedItems.size() - 1; j >= 0; j--){
                        dataList.remove(j);
                    }
                    //tell adapter to change the data
                    adapter.notifyDataSetChanged();
                    //remove the extra spaces
                    ids = ids.trim();
                    //refresh the friend page.
                    //can be put into ays
//                    getFriends(studentId);
                    HomeFragment.initializeFriends(studentId, sp);
                    //get friend array from sharepreference
                    JsonArray friendArray = new JsonParser().parse(sp.getString("currentFriends", "[]")).getAsJsonArray();
                    setContent(friendArray);
                    Toast.makeText(getActivity().getApplicationContext(), ids, Toast.LENGTH_LONG).show();
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
     * This method is used to set the result list
     */
    private void setContent(JsonArray friendsArray){
        if (friendsArray.size() != 0){
            //has matched results
            ArrayList<HashMap<String, String>> data = formatData(friendsArray);
            adapter = new ExpandAdapter(getActivity().getApplicationContext(), data);
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
