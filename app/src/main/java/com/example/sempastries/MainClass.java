package com.example.sempastries;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

public class MainClass {

    private final Context context1;

    public MainClass(Context context) {
        this.context1 = context;
    }


    String def;

    public String getError(byte[] data) {
        String js = new String(data);
        try {
            JSONObject jsonObject = new JSONObject(js);
            def = jsonObject.getString("message");
        } catch (JSONException e) {
            def = context1.getString(R.string.unknown_err);
        }

        return def;
    }
}
