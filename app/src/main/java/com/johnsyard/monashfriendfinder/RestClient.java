package com.johnsyard.monashfriendfinder;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.johnsyard.monashfriendfinder.entities.Profile;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * This class is used for RESTful requests
 * Created by xuanzhang on 28/04/2017.
 */

public class RestClient {
    private static final String BASE_URI = "http://100.103.98.198:8080/MonashFriendFinder/webresources/entities.";

    public static void main(String[] args){
        System.out.println("test: " + loginCheck("js@monash.edu", "JS12345"));
    }

    public static String findAllCourses() {
        final String methodPath = "/entities.profile/";
        //initialise
        URL url = null;
        HttpURLConnection conn = null;
        String textResult = "";
        //Making HTTP request
        try {
            url = new URL(BASE_URI + methodPath);
            //open the connection
            conn = (HttpURLConnection) url.openConnection();
            //set the timeout
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            //set the connection method to GET
            conn.setRequestMethod("GET");
            // add http headers to set your response type to json
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            //Read the response
            Scanner inStream = new Scanner(conn.getInputStream());
            //read the input steream and store it as string
            while (inStream.hasNextLine()) {
                textResult += inStream.nextLine();
            }
            Log.i("Info", new Integer(conn.getResponseCode()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return textResult;
    }

    /**
     * This method is used to create a new profile
     * @param profile
     */
    public static void createProfile(Profile profile) {
        //initialise
        URL url = null;
        HttpURLConnection conn = null;
        final String methodPath = "/student.course/";
        try {
            Gson gson = new Gson();
            String stringCourseJson = gson.toJson(profile);
            url = new URL(BASE_URI + methodPath);
            //open the connection
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(stringCourseJson.getBytes().length);
            conn.setRequestProperty("Content-Type", "application/json");
            //Send the POST out
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(stringCourseJson);
            out.close();
            Log.i("Info", new Integer(conn.getResponseCode()).toString());
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
        String hashPassword = DigestUtils.md5Hex(password);
        HttpURLConnection conn = null;
        Scanner inStream = null;
        final String methodPath = "profile/loginCheck";
        String textResult = "";
        try {
            conn = setConnection(methodPath, "POST", true);
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
     * @param methodPath
     * @param methodType
     * @param doOutput
     * @return
     */
    private static HttpURLConnection setConnection(String methodPath, String methodType, boolean doOutput) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(BASE_URI + methodPath);
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

