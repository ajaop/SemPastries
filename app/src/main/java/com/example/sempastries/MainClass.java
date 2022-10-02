package com.example.sempastries;

import android.content.Context;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

    public ArrayList<StateClass> getStates() {
        ArrayList<StateClass> stateClassArrayList;
        stateClassArrayList = new ArrayList<>();

        stateClassArrayList.add((new StateClass("Abia", "", false, 1)));
        stateClassArrayList.add((new StateClass("Adamawa", "", false, 2)));
        stateClassArrayList.add((new StateClass("Akwa Ibom  ", "", false, 3)));
        stateClassArrayList.add((new StateClass("Anambra", "", false, 4)));
        stateClassArrayList.add((new StateClass("Bauchi", "", false, 5)));
        stateClassArrayList.add((new StateClass("Bayelsa", "", false, 6)));
        stateClassArrayList.add((new StateClass("Benue", "", false, 7)));
        stateClassArrayList.add((new StateClass("Borno", "", false, 8)));
        stateClassArrayList.add((new StateClass("Cross River", "", false, 9)));
        stateClassArrayList.add((new StateClass("Delta", "", false, 10)));
        stateClassArrayList.add((new StateClass("Ebonyi", "", false, 11)));
        stateClassArrayList.add((new StateClass("Edo", "", false, 12)));
        stateClassArrayList.add((new StateClass("Ekiti", "", false, 13)));
        stateClassArrayList.add((new StateClass("Enugu ", "", false, 14)));
        stateClassArrayList.add((new StateClass("Federal Capital Territory Abuja", "", false, 15)));
        stateClassArrayList.add((new StateClass("Gombe", "", false, 16)));
        stateClassArrayList.add((new StateClass("Imo", "", false, 17)));
        stateClassArrayList.add((new StateClass("Jigawa", "", false, 18)));
        stateClassArrayList.add((new StateClass("Kaduna", "", false, 19)));
        stateClassArrayList.add((new StateClass("Kano", "", false, 20)));
        stateClassArrayList.add((new StateClass("Katsina", "", false, 21)));
        stateClassArrayList.add((new StateClass("Kebbi", "", false, 22)));
        stateClassArrayList.add((new StateClass("Kogi", "", false, 23)));
        stateClassArrayList.add((new StateClass("Kwara", "", false, 24)));
        stateClassArrayList.add((new StateClass("Lagos", "", false, 25)));
        stateClassArrayList.add((new StateClass("Nasarawa", "", false, 26)));
        stateClassArrayList.add((new StateClass("Niger", "", false, 27)));
        stateClassArrayList.add((new StateClass("Ogun", "", false, 28)));
        stateClassArrayList.add((new StateClass("Ondo", "", false, 29)));
        stateClassArrayList.add((new StateClass("Osun", "", false, 30)));
        stateClassArrayList.add((new StateClass("Oyo", "", false, 31)));
        stateClassArrayList.add((new StateClass("Plateau", "", false, 32)));
        stateClassArrayList.add((new StateClass("Rivers", "", false, 33)));
        stateClassArrayList.add((new StateClass("Sokoto", "", false, 34)));
        stateClassArrayList.add((new StateClass("Taraba", "", false, 35)));
        stateClassArrayList.add((new StateClass("Yobe", "", false, 36)));
        stateClassArrayList.add((new StateClass("Zamfara", "", false, 37)));

        return stateClassArrayList;
    }

    public ArrayList<StateClass> getGender() {
        ArrayList<StateClass> stateClassArrayList;
        stateClassArrayList = new ArrayList<>();

        stateClassArrayList.add((new StateClass("Male", "", false, 1)));
        stateClassArrayList.add((new StateClass("Female", "", false, 2)));

        return stateClassArrayList;
    }

    public ArrayList<StateClass> getCity() {
        ArrayList<StateClass> stateClassArrayList;
        stateClassArrayList = new ArrayList<>();

        stateClassArrayList.add((new StateClass("Ibadan", "", false, 1)));
        stateClassArrayList.add((new StateClass("Ogbomoso", "", false, 2)));
        stateClassArrayList.add((new StateClass("Oyo", "", false, 3)));
        stateClassArrayList.add((new StateClass("Iseyin", "", false, 4)));
        stateClassArrayList.add((new StateClass("Egbeda", "", false, 5)));
        stateClassArrayList.add((new StateClass("Akanran", "", false, 6)));
        stateClassArrayList.add((new StateClass("Iyana-Ofa", "", false, 7)));
        stateClassArrayList.add((new StateClass("Moniya", "", false, 8)));
        stateClassArrayList.add((new StateClass("Okeho", "", false, 9)));
        stateClassArrayList.add((new StateClass("Idi-Ayunre", "", false, 10)));
        stateClassArrayList.add((new StateClass("Igbo-Ora", "", false, 11)));
        stateClassArrayList.add((new StateClass("Jobele", "", false, 12)));
        stateClassArrayList.add((new StateClass("Iresa-Adu", "", false, 13)));
        stateClassArrayList.add((new StateClass("Eruwa", "", false, 14)));
        stateClassArrayList.add((new StateClass("Ajaawa", "", false, 15)));
        stateClassArrayList.add((new StateClass("Kisi", "", false, 16)));
        stateClassArrayList.add((new StateClass("Igboho", "", false, 17)));
        stateClassArrayList.add((new StateClass("Ido", "", false, 18)));
        stateClassArrayList.add((new StateClass("Otu", "", false, 19)));
        stateClassArrayList.add((new StateClass("Ayete", "", false, 20)));
        stateClassArrayList.add((new StateClass("Igbeti", "", false, 21)));
        stateClassArrayList.add((new StateClass("Ikoyi-Ile", "", false, 22)));
        stateClassArrayList.add((new StateClass("Ago-Amodu", "", false, 23)));
        stateClassArrayList.add((new StateClass("Saki", "", false, 24)));
        stateClassArrayList.add((new StateClass("Iwere-Ile", "", false, 25)));
        stateClassArrayList.add((new StateClass("Tede", "", false, 26)));

        return stateClassArrayList;
    }

    public void showError(String s, View view) {
        Snackbar snackbar = Snackbar.make(view, "" + s, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(context1.getResources().getColor(R.color.dark_red_80));
        snackbar.show();
    }

    public void showSuccess(String s, View view) {
        Snackbar snackbar = Snackbar.make(view, "" + s, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(context1.getResources().getColor(R.color.dark_gray));
        snackbar.show();
    }

}
