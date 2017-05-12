package com.wickedmonkstudio.chatapp.other;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Wojciech on 03.05.2017.
 */

public class Utils {
    private Context context;
    private SharedPreferences sharedPreferences;

    private static final String KEY_SHARED_PREFERENCES = "ANDROID_WEB_CHAT";
    private static final int KEY_MODE_PRIVATE =0;
    private static final String KEY_SESSION_ID="sessionId",
                                FLAG_MESSAGE = "message";

    public Utils(Context context){
        this.context=context;
        sharedPreferences=this.context.getSharedPreferences(KEY_SESSION_ID,KEY_MODE_PRIVATE);
    }

    public void storeSessionId(String sessionId){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_SESSION_ID, sessionId);
        editor.apply();
    }

    public String getSessionId(){
        return sharedPreferences.getString(KEY_SESSION_ID, null);
    }

    public String getSendMessageJSON(String message){
        String json =null;
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("flag", FLAG_MESSAGE);
            jsonObject.put("sessionId", getSessionId());
            jsonObject.put("message", message);

            json=jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }
}
