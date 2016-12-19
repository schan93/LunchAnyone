package com.example.schan.model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by schan on 12/15/16.
 */

public class Group {

    Group() {
    }

    public Group(String groupName, String restaurant, String description, Integer memberCount, String imageUrl) {
        this.groupName = groupName;
        this.restaurant = restaurant;
        this.description = description;
        this.memberCount = memberCount;
        this.imageUrl = imageUrl;
    }

    private String groupName;
    private String restaurant;
    private String description;
    private Integer memberCount;
    private String imageUrl;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static ArrayList<Group> getGroupsFromFile(String filename, Context context){
        final ArrayList<Group> groupList = new ArrayList<>();

        try {
            // Load data
            String jsonString = loadJsonFromAsset("Groups.json", context);
            JSONObject json = new JSONObject(jsonString);
            JSONArray groups = json.getJSONArray("groups");

            // Get Group objects from data
            for(int i = 0; i < groups.length(); i++){
                Group group = new Group();

                group.setGroupName(groups.getJSONObject(i).getString("groupName"));
                group.setDescription(groups.getJSONObject(i).getString("description"));
                group.setImageUrl(groups.getJSONObject(i).getString("imageUrl"));
                group.setRestaurant(groups.getJSONObject(i).getString("restaurant"));
                group.setMemberCount(groups.getJSONObject(i).getInt("memberCount"));
                groupList.add(group);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return groupList;
    }

    private static String loadJsonFromAsset(String filename, Context context) {
        String json = null;

        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        }
        catch (java.io.IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return json;
    }

}
