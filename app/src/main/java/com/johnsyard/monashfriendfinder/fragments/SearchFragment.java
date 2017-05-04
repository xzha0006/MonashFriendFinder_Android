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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.johnsyard.monashfriendfinder.R;
import com.johnsyard.monashfriendfinder.RestClient;
import com.johnsyard.monashfriendfinder.widgets.ExpandAdapter;
import com.johnsyard.monashfriendfinder.widgets.MultiSelectionSpinner;

import java.util.ArrayList;
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


        String[] array = {"course", "study mode", "suburb", "nationality", "native language", "favourite sport", "favourite movie", "favourite unit", "current job"};
        multiSelectionSpinner.setItems(array);
        multiSelectionSpinner.setSelection(new int[]{8});
        keywords = "/current job";
        multiSelectionSpinner.setListener(this);
        //get user's studentId
        SharedPreferences sp = getActivity().getSharedPreferences("userInfo", 0);
        String myprofile = sp.getString("myProfile", null);
        JsonObject myProfileJson = new JsonParser().parse(myprofile).getAsJsonObject();
        studentId = myProfileJson.get("studentId").getAsInt();

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
                            JsonArray peopleArray = null;
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
                ArrayList<Integer> deletedItems = new ArrayList<Integer>();
                if (dataList.size() != 0) {
                    for (int i = 0; i < dataSize; i++) {
                        if (isSelected.get(i)) {
                            String studentIdContent = dataList.get(i).get("studentId");
                            String studentIdStr = studentIdContent.substring(studentIdContent.indexOf(":") + 1).trim();
                            ids += studentIdStr + " ";
                            deletedItems.add(i);
                            isSelected.put(i, false);
                            //get stranger info
//                            String studentIdContent = dataList.get(i).get("stranger");
                        }
                    }
                    //delete in desc order
                    for (int j = deletedItems.size() - 1; j >= 0; j--) {
                        dataList.remove(j);
                    }
                    //tell adapter to change the data
                    adapter.notifyDataSetChanged();
                    //remove the extra spaces
                    ids = ids.trim();
                    //refresh the friend page.
//                    getStrangers(studentId); add this latter
                    Toast.makeText(getActivity().getApplicationContext(), ids + "is added", Toast.LENGTH_LONG).show();
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
     * This method is used to filter friends and ge strangers
     * @param peopleArray
     * @return
     */
    private JsonArray getStrangers(JsonArray peopleArray){
        SharedPreferences sp = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
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
