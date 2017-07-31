package com.codepath.nytimessearch.models;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by STEPHAN987 on 7/29/2017.
 */

public class Article implements Serializable {
    String webUrl;
    String headline;

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    String thumbNail;

    public Article(JSONObject jsonObject){
        try{
            this.webUrl = jsonObject.getString("web_url");
            this.headline = jsonObject.getJSONObject("headline").getString("main");

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");
            if (multimedia.length() > 0){
                this.thumbNail = "http://www.nytimes.com/" + multimedia.getJSONObject(0).getString("url");
            }
            else
            {
                this.thumbNail = Uri.parse("android.resource://com.codepath.nytimessearch/drawable/no_image").toString();
                //this.thumbNail = "";
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    public static ArrayList<Article> fromJSONArray(JSONArray jsonArray){
        ArrayList<Article> results = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            try {
                results.add(new Article(jsonArray.getJSONObject(i)));
            } catch(JSONException e){
                e.printStackTrace();
            }
        }
        return results;
    }
}
