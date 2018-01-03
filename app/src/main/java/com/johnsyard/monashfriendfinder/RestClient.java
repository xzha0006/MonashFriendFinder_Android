package com.johnsyard.monashfriendfinder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.johnsyard.monashfriendfinder.entities.MovieInfo;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.PrintWriter;
import java.io.SyncFailedException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Scanner;

/**
 * This class is used for RESTful requests
 * Created by xuanzhang on 28/04/2017.
 */

public class RestClient {

    private static final String BASE_URI = "http://49.127.156.172:8080/MonashFriendFinder/webresources/entities.";
    private static final String WEATHER_API = "http://api.openweathermap.org/data/2.5/weather?";

    public static void main(String[] args){
        System.out.println("test: " + getMovieInfo("Batman"));
    }

    /**
     * This method is used google API to get Info and image url
     * @param movie
     * @return
     */
    public static MovieInfo getMovieInfo(String movie) {
        movie = movie.replace(" ", "+");
        MovieInfo result = new MovieInfo();
        HttpURLConnection conn = null;
        String url = "";
        String textResult = "";
        String API_key = "AIzaSyAWEUYcQNc5EI0shEmjtvNiqjbYoSUVnEA";
        String SEARCH_ID_cx = "001385470827581850993:olsi6ty9avi";
        final String methodPath = "https://www.googleapis.com/customsearch/v1?key=" + API_key + "&cx=" + SEARCH_ID_cx + "&q=" + movie + "&num=3";

        try {
            url = methodPath;
            //open the connection
            conn = setConnection(url, "GET", false);
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input stream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
            inStream.close();
            JsonObject response = new JsonParser().parse(textResult).getAsJsonObject();
            JsonArray items = response.get("items").getAsJsonArray();
            if (items.size() > 0){
                JsonArray movieInfo = items.get(0).getAsJsonObject().get("pagemap").getAsJsonObject().get("movie").getAsJsonArray();
                //get image url
                String imgUrl = movieInfo.get(0).getAsJsonObject().get("image").getAsString();
                String description = movieInfo.get(0).getAsJsonObject().get("description").getAsString();

                result.setImage(getMovieImage(imgUrl));
                result.setDescription(description);
            }
//            Log.i("Info", new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return result;
    }

    /**
     * This is used to get the image from a url
     * @param imgUrl
     * @return
     */
    private static Bitmap getMovieImage(String imgUrl) {
        Bitmap bitmap = null;
        HttpURLConnection connImg = null;
        try {
            connImg = setConnection(imgUrl, "GET", false);
            //read the image
             bitmap = BitmapFactory.decodeStream(connImg.getInputStream());
//            Log.i("Info", new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connImg.disconnect();
        }
        return bitmap;
    }

    /**
     * This method is used for getting favorite units
     */
    public static JsonArray getFavoriteUnits() {
        //initialise
        JsonArray favoriteUnits = new JsonArray();
        HttpURLConnection conn = null;
        String textResult = "";
        final String methodPath = "profile/favouriteUnitFrequency";
        try {
            //open the connection
            conn = setConnection(BASE_URI + methodPath, "GET", false);
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input stream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
            inStream.close();
            favoriteUnits = new JsonParser().parse(textResult).getAsJsonArray();
//            Log.i("Info", new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return favoriteUnits;
    }

    /**
     * This method is used to get the nearest student.
     * @return
     */
    public static JsonArray getNearestStudents(String id, String latitude, String longitude) {
        //initialise
        JsonArray nearestStudents = new JsonArray();
        HttpURLConnection conn = null;
        String textResult = "";
        final String methodPath = "profile/favouriteUnitFrequency";
        try {
            //open the connection
            conn = setConnection(BASE_URI + methodPath + id + "/" + latitude + "/" + longitude, "GET", false);
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input stream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
            inStream.close();
            nearestStudents = new JsonParser().parse(textResult).getAsJsonArray();
//            Log.i("Info", new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return nearestStudents;
    }

    /**
     * This method is used for deleting student
     * @param friendships
     */
    public static void deleteFriends(String friendships){
        //initialise
        HttpURLConnection conn = null;
        final String methodPath = "friendship/upload";
        try {
            //open the connection
            conn = setConnection(BASE_URI + methodPath, "PUT", true);
            conn.setFixedLengthStreamingMode(friendships.getBytes().length);
            //Send the POST out
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(friendships);
            out.flush();
            out.close();
//            Log.i("Info", new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
    }

    /**
     * This method is used for adding student
     * @param friendships
     */
    public static void addFriends(String friendships){
        //initialise
        HttpURLConnection conn = null;
        final String methodPath = "friendship/";
        try {
            //open the connection
            conn = setConnection(BASE_URI + methodPath, "POST", true);
            conn.setFixedLengthStreamingMode(friendships.getBytes().length);
            //Send the POST out
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(friendships);
            out.flush();
            out.close();
//            Log.i("Info", new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

    }
    /**
     * This method is used to get students' locations by ids. The ids format should be "1,2,3"
     * @param ids
     * @return
     */
    public static JsonArray findLocationsByIds(String ids) {
        JsonArray locations = new JsonArray();
        HttpURLConnection conn = null;
        String url = "";
        String textResult = "";
        final String methodPath = "location/findByStudentIds/";

        try {
            url = BASE_URI + methodPath + ids;
            //open the connection
            conn = setConnection(url, "GET", false);
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input stream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
            inStream.close();
            locations = new JsonParser().parse(textResult).getAsJsonArray();

//            Log.i("Info", new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return locations;
    }

    /**
     * This method is used to match student. geting both friend profile and starting date
     * @param studentId
     */
    public static JsonArray findCurrentFriends(int studentId) {
        //initialise
        //We use two requests here. Because of the rule of preventing reverse friendship.
        HttpURLConnection conn = null;
        HttpURLConnection conn2 = null;
        String url = "";
        String url2 = "";
        String textResult = "";
        String textResult2 = "";
        JsonArray friendshipArray = new JsonArray();
        JsonArray friendshipsArray;
        JsonArray friendshipsArray2;
        final String methodPath = "friendship/findByStudOneId/";
        final String methodPath2 = "friendship/findByStudTwoId/";

        try {
            url = BASE_URI + methodPath + studentId;
            url2 = BASE_URI + methodPath2 + studentId;

            //open the connection
            conn = setConnection(url, "GET", false);
            conn2 = setConnection(url2, "GET", false);

            Scanner inStream = new Scanner(conn.getInputStream());
            Scanner inStream2 = new Scanner(conn2.getInputStream());
            //read the input stream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
            inStream.close();
            friendshipsArray = new JsonParser().parse(textResult).getAsJsonArray();

            while (inStream2.hasNextLine()) {
                textResult2 += inStream2.nextLine();
            }
            inStream2.close();
            friendshipsArray2 = new JsonParser().parse(textResult2).getAsJsonArray();

            if (friendshipsArray.size() > 0){
                for (int i = 0; i < friendshipsArray.size(); i++){
                    JsonObject friendship = friendshipsArray.get(i).getAsJsonObject();
                    JsonObject friend = friendship.get("studentTwoId").getAsJsonObject();
                    JsonElement startingDate = friendship.get("startingDate");
                    JsonElement friendshipId = friendship.get("friendshipId");

                    JsonObject newFriendshipObject = new JsonObject();
                    newFriendshipObject.add("friend", friend);
                    newFriendshipObject.add("startingDate", startingDate);
                    newFriendshipObject.add("friendshipId", friendshipId);
                    friendshipArray.add(newFriendshipObject);
                }
            }

            if (friendshipsArray2.size() > 0){
                for (int j = 0; j < friendshipsArray2.size(); j++){
                    JsonObject friendship = friendshipsArray2.get(j).getAsJsonObject();
                    JsonObject friend = friendship.get("studentOneId").getAsJsonObject();
                    JsonElement startingDate = friendship.get("startingDate");
                    JsonElement friendshipId = friendship.get("friendshipId");

                    JsonObject newFriendshipObject = new JsonObject();
                    newFriendshipObject.add("friend", friend);
                    newFriendshipObject.add("startingDate", startingDate);
                    newFriendshipObject.add("friendshipId", friendshipId);
                    friendshipArray.add(newFriendshipObject);
                }
            }
//            Log.i("Info", new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return friendshipArray;
    }

    /**
     * This method is used to match student.
     * @param studentId
     * @param keywords
     */
    public static JsonArray matchFriendsByAnyKeywords(int studentId, String keywords) {
        //initialise
        HttpURLConnection conn = null;
        String url = "";
        String textResult = "";
        JsonArray friendsArray = null;
        final String methodPath = "profile/matchFriendsByAnyKeywords/";

        try {
            url = BASE_URI + methodPath + studentId + keywords;
            //open the connection
            conn = setConnection(url.replace(" ", "%20"), "GET", false);
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input stream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
            inStream.close();
            friendsArray = new JsonParser().parse(textResult).getAsJsonArray();
//            Log.i("Info", new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return friendsArray;
    }

    /**
     * This method is used to call the OpenWeatherMap API to get the temperature.
     * parameters are latitude, longitude
     * @param latitude
     * @param longitude
     * @return
     */
    public static String getTemperatureByLocation(String latitude, String longitude) {
        final String apiKey = "3246fde5301a8a80174dfd6588498359";
        double temperature = 0;
        String temp = "";
        //initialise
        HttpURLConnection conn = null;
        String textResult = "";
        //Making HTTP request
        try {
            String url = WEATHER_API + "lat=" + latitude + "&lon=" + longitude + "&APPID=" + apiKey;
            //open the connection
            conn = setConnection(url, "GET", false);
            //Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input stream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
            inStream.close();
            JsonObject weather = new JsonParser().parse(textResult).getAsJsonObject();
            temperature = weather.get("main").getAsJsonObject().get("temp").getAsDouble() - 273.15;
            DecimalFormat df = new DecimalFormat("#.##");
            temp = df.format(temperature);
            Log.i("Info", new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return temp;
    }

    /**
     * This method is used to create a new profile
     * @param jsProfile
     */
    public static void createProfile(String jsProfile) {
        //initialise
        HttpURLConnection conn = null;
        final String methodPath = "profile/";
        try {
            //open the connection
            conn = setConnection(BASE_URI + methodPath, "POST", true);
            conn.setFixedLengthStreamingMode(jsProfile.getBytes().length);
            //Send the POST out
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(jsProfile);
            out.flush();
            out.close();
//            Log.i("Info", new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
    }

    /**
     * create a location record while logining.
     * @param locationJson
     */
    public static void createLocationRecord(String locationJson) {
        //initialise
        HttpURLConnection conn = null;
        final String methodPath = "location/";
        try {
            //open the connection
            conn = setConnection(BASE_URI + methodPath, "POST", true);
            conn.setFixedLengthStreamingMode(locationJson.getBytes().length);
            //Send the POST out
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(locationJson);
            out.flush();
            out.close();
//            Log.i("Info", new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
    }

    /**
     * This is used to upload profile
     * @param newProfile
     */
    public static void upload(String newProfile) {
        //initialise
        HttpURLConnection conn = null;
        final String methodPath = "profile/upload";
        try {
            //open the connection
            conn = setConnection(BASE_URI + methodPath, "PUT", true);
            conn.setFixedLengthStreamingMode(newProfile.getBytes().length);
            //Send the POST out
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(newProfile);
            out.flush();
            out.close();
//            Log.i("Info", new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
    }


    /**
     * This method is used for login checking
     */
    public static JsonObject loginCheck(String userName, String password){
        String hashPassword = new String(Hex.encodeHex(DigestUtils.md5(password)));
        HttpURLConnection conn = null;
        Scanner inStream = null;
        final String methodPath = "profile/loginCheck";
        String textResult = "";
        JsonObject responseJson = null;
        try {

            conn = setConnection(BASE_URI + methodPath, "POST", true);
//            JSONObject loginJson = new JSONObject();
//            loginJson.put("userName", userName);
//            loginJson.put("password", hashPassword);

            JsonObject loginJson = new JsonObject();
            loginJson.addProperty("userName", userName);
            loginJson.addProperty("password", hashPassword);

            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(loginJson.toString());
            out.close();

            inStream = new Scanner(conn.getInputStream());
            //read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
            System.out.println(textResult);
            responseJson = new JsonParser().parse(textResult).getAsJsonObject();
//            System.out.println(responseJson.get("Info").getAsString());
//            result = Boolean.parseBoolean(responseJson.get("response").getAsString());
//            System.out.println("new: " + responseJson.get("response").toString());
//            System.out.println("new1: " + result);
//            Log.i("Info", new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            inStream.close();
            conn.disconnect();
        }
        return responseJson;
    }

    /**
     * get geo info
     */
    public static String getAddress(String lat, String longi) {
        //initialise
        String addressInfo = "";
        HttpURLConnection conn = null;
        Scanner inStream = null;
        String textResult = "";
        final String methodPath = "https://maps.googleapis.com/maps/api/geocode/json?language=en&latlng=" + lat + "," + longi + "&key=AIzaSyDBZb4pV22JXFIKpTiu1Dg9HwD8RqFVeXE";
        try {
            //open the connection
            conn = setConnection(methodPath, "GET", false);
            inStream = new Scanner(conn.getInputStream());
            //read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
            System.out.println(textResult);
            JsonObject responseJson = new JsonParser().parse(textResult).getAsJsonObject();
            addressInfo = responseJson.get("results").getAsJsonArray().get(0).getAsJsonObject().get("formatted_address").getAsString();
//            Log.i("Info", new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
            inStream.close();
        }
        return addressInfo;
    }

    /**
     * get location records from database
     */
    public static JsonArray getLocationRecords(String studentId) {
        //initialise
        JsonArray locations = new JsonArray();
        HttpURLConnection conn = null;
        Scanner inStream = null;
        String textResult = "";
        final String methodPath = "location/findByStudentId/" + studentId;
        try {
            //open the connection
            conn = setConnection(BASE_URI + methodPath, "GET", false);
            inStream = new Scanner(conn.getInputStream());
            //read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
            System.out.println(textResult);
            locations = new JsonParser().parse(textResult).getAsJsonArray();
//            Log.i("Info", new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
            inStream.close();
        }
        return locations;
    }

    /**
     * get location frequency from database
     */
    public static JsonArray getLocationFrequency(String studentId, String start, String end) {
        //initialise
        JsonArray locationFrequency = new JsonArray();
        start = start.replace(" ", "%20");
        end = end.replace(" ", "%20");
        HttpURLConnection conn = null;
        Scanner inStream = null;
        String textResult = "";
        final String methodPath = "location/findLocationsByStudIdAndDatetime/"+studentId+"/"+start+"/" + end;
        try {
            //open the connection
            conn = setConnection(BASE_URI + methodPath, "GET", false);
            inStream = new Scanner(conn.getInputStream());
            //read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
            System.out.println(textResult);
            locationFrequency = new JsonParser().parse(textResult).getAsJsonArray();
//            Log.i("Info", new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
            inStream.close();
        }
        return locationFrequency;
    }

    /**
     * This method is used to set http connection
     * @param sUrl
     * @param methodType
     * @param doOutput if need to write output
     * @return
     */
    private static HttpURLConnection setConnection(String sUrl, String methodType, boolean doOutput) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(sUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod(methodType);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(doOutput);
//            Log.i("Info", new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}

