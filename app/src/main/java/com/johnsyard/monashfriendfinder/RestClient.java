package com.johnsyard.monashfriendfinder;

import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Scanner;

/**
 * This class is used for RESTful requests
 * Created by xuanzhang on 28/04/2017.
 */

public class RestClient {
    private static final String BASE_URI = "http://100.103.98.198:8080/MonashFriendFinder/webresources/entities.";
    private static final String WEATHER_API = "http://api.openweathermap.org/data/2.5/weather?";

//    public static void main(String[] args){
//        System.out.println("test: " + loginCheck("js@monash.edu", "JS12345"));
//    }

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
            //read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
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
     * This method is used for login checking
     */
    public static boolean loginCheck(String userName, String password){
        boolean result = false;
        String hashPassword = new String(Hex.encodeHex(DigestUtils.md5(password)));
        HttpURLConnection conn = null;
        Scanner inStream = null;
        final String methodPath = "profile/loginCheck";
        String textResult = "";
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
            JsonObject responseJson = new JsonParser().parse(textResult).getAsJsonObject();
            System.out.println(responseJson.get("Info").getAsString());
            result = Boolean.parseBoolean(responseJson.get("response").getAsString());
            System.out.println("new: " + responseJson.get("response").toString());
            System.out.println("new1: " + result);
//            Log.i("Info", new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            inStream.close();
            conn.disconnect();
        }
        return result;
    }

    /**
     * This method is used to set http connection
     * @param sUrl
     * @param methodType
     * @param doOutput
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

