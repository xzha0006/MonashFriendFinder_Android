package com.johnsyard.monashfriendfinder.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.johnsyard.monashfriendfinder.R;
import com.johnsyard.monashfriendfinder.RestClient;
import com.johnsyard.monashfriendfinder.widgets.ExpandAdapter;
import com.johnsyard.monashfriendfinder.widgets.MultiSelectionSpinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.R.attr.key;
import static android.R.attr.mode;

/**
 * Created by xuanzhang on 1/05/2017.
 */

public class SearchFragment extends Fragment implements MultiSelectionSpinner.OnMultipleItemsSelectedListener {
    private View vSearch;
    private Button btSearch;
    private MultiSelectionSpinner multiSelectionSpinner;
    private ListView lvResults;
    private TextView tvTitle;
    private Button btViewInMap;
    private Button btAddFriend;
    private String keywords; //add the selected items to a string
    private int studentId;
    private int KEYWORD_NUM = 9;

    private ExpandAdapter adapter = null;
    private SharedPreferences sp;
    private String myProfileString;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vSearch = inflater.inflate(R.layout.fragment_search, container, false);
        btSearch = (Button) vSearch.findViewById(R.id.bt_search);
        multiSelectionSpinner = (MultiSelectionSpinner) vSearch.findViewById(R.id.multi_spinner);
        lvResults = (ListView) vSearch.findViewById(R.id.lt_results);
        tvTitle = (TextView) vSearch.findViewById(R.id.tv_result_title);
        btViewInMap = (Button) vSearch.findViewById(R.id.bt_view_in_map);
        btAddFriend = (Button) vSearch.findViewById(R.id.bt_add_friend);
        sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        myProfileString = sp.getString("myProfile", null);
        JsonObject myProfileJson = new JsonParser().parse(myProfileString).getAsJsonObject();
        studentId = myProfileJson.get("studentId").getAsInt();

        String[] array = {"course", "study mode", "suburb", "nationality", "native language", "favourite sport", "favourite movie", "favourite unit", "current job"};
        multiSelectionSpinner.setItems(array);
        multiSelectionSpinner.setSelection(new int[]{8});
        multiSelectionSpinner.setListener(this);
        //initialize keywords in case user may search directly without pick.
        keywords = "/current job////////";
        //get user's studentId

        //do the search
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (keywords.equals("/////////")){
                    Toast.makeText(getActivity().getApplicationContext(), "Keywords can not be empty, please select at lease one keyword.", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    //call the RestClient.matchFriendsByAnyKeywords
                    new AsyncTask<String, Void, JsonArray>() {
                        @Override
                        protected JsonArray doInBackground(String... strings) {
                            JsonArray peopleArray;
                            JsonArray strangerArray = null;
                            String keywords = strings[0];
                            peopleArray = RestClient.matchFriendsByAnyKeywords(studentId, keywords);
                            if (peopleArray != null && peopleArray.size() > 0) {
                                strangerArray = getStrangers(peopleArray);
                            }
                            return strangerArray;
                        }

                        @Override
                        protected void onPostExecute(JsonArray strangerArray) {
                            if (strangerArray != null && strangerArray.size() > 0){
                                //if it has matched results
                                ArrayList<HashMap<String, String>> data = formatData(strangerArray);
                                adapter = new ExpandAdapter(getActivity().getApplicationContext(), data);
                                //show the data and buttons
                                lvResults.setAdapter(adapter);
                                tvTitle.setVisibility(View.VISIBLE);
                                btViewInMap.setVisibility(View.VISIBLE);
                                btAddFriend.setVisibility(View.VISIBLE);
                            }else {
                                Toast.makeText(getActivity().getApplicationContext(), "No student matches the keywords.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }.execute(keywords);
                }
            }
        });

        //add friend once friend is added
        btAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<Integer, Boolean> isSelected = adapter.getIsSelected();
                ArrayList<HashMap<String, String>> dataList = adapter.getList();
                String ids = "";
                int dataSize = dataList.size();
                ArrayList<Integer> deletedItems = new ArrayList<>();

                JsonArray friendships = new JsonArray();

                //use a for loop to fresh the data and get a json array of friendship
                if (dataList.size() != 0) {
                    for (int i = 0; i < dataSize; i++) {
                        if (isSelected.get(i)) {
//                            String studentIdContent = dataList.get(i).get("studentId");
//                            String studentIdStr = studentIdContent.substring(studentIdContent.indexOf(":") + 1).trim();
//                            ids += studentIdStr + " ";
                            deletedItems.add(i);
                            isSelected.put(i, false);
                            //get stranger info and create friendship
                            JsonObject stranger = new JsonParser().parse(dataList.get(i).get("stranger")).getAsJsonObject();
                            friendships.add(createFriendship(stranger));
                        }
                    }
                    //delete in desc order
                    for (int j = deletedItems.size() - 1; j >= 0; j--) {
                        dataList.remove(j);
                    }
                    //tell adapter to change the data
                    adapter.notifyDataSetChanged();
                    //refresh the friend page.
                    new AsyncTask<JsonArray, Void, Void>(){
                        @Override
                        protected Void doInBackground(JsonArray... jsonArrays) {
                            RestClient.addFriends(jsonArrays[0].toString());
                            //refresh friend information
                            HomeFragment.initializeFriends(studentId, sp);
                            return null;
                        }
                    }.execute(friendships);
                    System.out.println(friendships);
                }
            }
        });

        //active the map
        btViewInMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, new SearchMapFragment()).commit();
            }
        });
        return vSearch;
    }

    /**
     * This method is used to create a json object of friendship
     * @param person
     * @return
     */
    private JsonObject createFriendship(JsonObject person){
        JsonObject friendship = new JsonObject();
        //set start time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = sdf.format(new Date());
        friendship.addProperty("startingDate", dateString);
        friendship.add("endingDate", null);

        JsonObject myProfile = new JsonParser().parse(myProfileString).getAsJsonObject();
        int myId = myProfile.get("studentId").getAsInt();
        int personId = person.get("studentId").getAsInt();
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
     * This method is used to filter friends and ge strangers
     * @param peopleArray
     * @return
     */
    private JsonArray getStrangers(JsonArray peopleArray){
        String friendIds = sp.getString("friendIds", null);
        JsonArray strangerArray = new JsonArray();
        //if has friends
        if (friendIds.length() > 0) {
            boolean isFriend = false;
            String[] ids = friendIds.split(",");
            //filter friends
            for (int i = 0; i < peopleArray.size(); i++) {
                JsonObject person = peopleArray.get(i).getAsJsonObject();
                for (String id : ids) {
                    if (person.get("studentId").getAsInt() == Integer.parseInt(id)) {
                        isFriend = true;
                        break;
                    }
                }
                if (!isFriend) {
                    strangerArray.add(person);
                }
                isFriend = false;
            }
        } else { //if no friend
            strangerArray = peopleArray;
        }
        //store the stranger
        SharedPreferences.Editor spEdit = sp.edit();
        spEdit.putString("strangers", strangerArray.toString());
        //get and store stranger ids for search map
        String strangerIds = "";
        if (strangerArray.size() > 0) {
            for (int i = 0; i < strangerArray.size(); i++) {
                strangerIds += strangerArray.get(i).getAsJsonObject().get("studentId").getAsString() + ",";
            }
            strangerIds = strangerIds.substring(0, strangerIds.length() - 1);
        }
        spEdit.putString("strangerIds", strangerIds);
        spEdit.apply();

        return strangerArray;
    }
    /**
     * This method is for adapter data formatting and filter friends.
     * @param strangerArray
     * @return datas
     */
    private ArrayList<HashMap<String, String>> formatData(JsonArray strangerArray) {
        ArrayList<HashMap<String, String>> datas = new ArrayList<HashMap<String,String>>();
        for(int i = 0; i < strangerArray.size(); i++){

            JsonObject stranger = strangerArray.get(i).getAsJsonObject();
            String otherContent = "Study Mode: " + stranger.get("studyMode").getAsString() + "\n" +
                    "Course: " + stranger.get("course").getAsString() + "\n" +
                    "Gender: " + stranger.get("gender").getAsString() + "\n" +
                    "Date Of Birth: " + stranger.get("dateOfBirth").getAsString() + "\n" +
                    "Email: " + stranger.get("email").getAsString() + "\n" +
                    "Address: " + stranger.get("address").getAsString() + "\n" +
                    "Nationality: " + stranger.get("nationality").getAsString() + "\n" +
                    "Native Language: " + stranger.get("nativeLanguage").getAsString() + "\n" +
                    "Favourite Sport: " + stranger.get("favouriteSport").getAsString() + "\n" +
                    "Favourite Unit: " + stranger.get("favouriteUnit").getAsString() + "\n" +
                    "Current Job: " + stranger.get("currentJob").getAsString();

            HashMap<String, String> item = new HashMap<>();
            //put stranger inside the item
            item.put("stranger", stranger.toString());
            item.put("name", stranger.get("firstName").getAsString() + " " + stranger.get("lastName").getAsString());
            item.put("movie", "Favorite Movie: " + stranger.get("favouriteMovie").getAsString());
            item.put("studentId", "Student Id: " + stranger.get("studentId").getAsString());
            item.put("otherContent", otherContent);
            datas.add(item);
        }
        return datas;
    }

    @Override
    public void getSelectedStrings(List<String> strings) {
        keywords = "";
        //construct keywords
        if (strings.size() > 0) {
            for (String keyword : strings) {
                keywords += "/" + keyword;
            }
        }
        //make up the slashes "///"
        for (int i = 0; i < KEYWORD_NUM - strings.size();i++){
            keywords += "/";
        }
        Toast.makeText(getActivity().getApplicationContext(), keywords, Toast.LENGTH_LONG).show();
    }
}
