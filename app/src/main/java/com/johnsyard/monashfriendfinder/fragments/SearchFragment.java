package com.johnsyard.monashfriendfinder.fragments;

import android.app.Fragment;
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
    View vSearch;
    Button btSearch;
    MultiSelectionSpinner multiSelectionSpinner;
    ListView lvResults;
    TextView tvTitle;
    Button btViewInMap;
    Button btAddFriend;
    String keywords;
    int studentId;
    int KEYWORD_NUM = 9;

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
        multiSelectionSpinner.setSelection(new int[]{0, 1});
        keywords = "/course/study mode";
        multiSelectionSpinner.setListener(this);
        //get user's studentId
        SharedPreferences sp = getActivity().getSharedPreferences("myProfile", 0);
        studentId = sp.getInt("myStudentId", -1);

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
                            JsonArray friendsArray = null;
                            if (studentId < 0){
                                Toast.makeText(getActivity().getApplicationContext(), "There is an error with user's studentId.", Toast.LENGTH_LONG).show();
                            }else{
                                String keywords = strings[0];
                                friendsArray = RestClient.matchFriendsByAnyKeywords(studentId, keywords);
                            }
                            return friendsArray;
                        }

                        @Override
                        protected void onPostExecute(JsonArray friendsArray) {
                            if (friendsArray != null){
                                //has matched results
                                ArrayList<HashMap<String, String>> data = formatData(friendsArray);
                                ExpandAdapter adapter = new ExpandAdapter(getActivity().getApplicationContext(), data);
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

        return vSearch;
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


            HashMap<String, String> item = new HashMap<>();
            item.put("name", friend.get("firstName").getAsString() + " " + friend.get("lastName").getAsString());
            item.put("movie", "Favorite Movie: " + friend.get("favouriteMovie").getAsString());
            item.put("otherContent", otherContent);
            datas.add(item);
        }
        return datas;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void selectedIndices(List<Integer> indices) {
    }

    @Override
    public void selectedStrings(List<String> strings) {
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
