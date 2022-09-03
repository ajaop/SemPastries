package com.example.sempastries;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.sempastries.StateClass;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends ArrayAdapter<StateClass>  {

    private final Activity activity;
    private  List<StateClass> stateClasses;
    private List<StateClass> stateClass;

    public ListAdapter(Activity activity1, List<StateClass> stateClasses1){
        super(activity1, R.layout.list_item, stateClasses1);
        this.activity = activity1;
        this.stateClasses = stateClasses1;
    }


    @Override
    public int getCount() {
        int ret = 0;
        if(stateClasses!=null)
        {
            ret = stateClasses.size();
        }
        return ret;
    }
    @Override
    public StateClass getItem(int itemIndex) {
        StateClass ret = null;
        if(stateClasses!=null) {
            ret = stateClasses.get(itemIndex);
        }
        return ret;
    }
    @Override
    public long getItemId(int itemIndex) {
        return itemIndex;
    }

    @NonNull
    @Override
    public View getView (int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();

        @SuppressLint({"ViewHolder", "InflateParams"}) View view = inflater.inflate(R.layout.list_item, null, false);

        TextView name =  view.findViewById(R.id.stateName);


        StateClass stateClass = stateClasses.get(position);

        name.setText(stateClass.getName());


        return view;
    }

}
