package com.example.sempastries;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;

import androidx.annotation.NonNull;

import java.util.List;

public class RadioButtonAdapter extends ArrayAdapter<StateClass> {

   private final Activity activity;
   private final List<StateClass> stateClasses;
   private RadioButtonListener radioButtonListener;
   int selectedPosition = -1;

   public RadioButtonAdapter(Activity activity1, List<StateClass> stateClasses1){
      super(activity1, R.layout.radiobutton_layout, stateClasses1);
      this.activity = activity1;
      this.stateClasses = stateClasses1;
   }
   @Override
   public long getItemId(int itemIndex) {
      return itemIndex;
   }

   @NonNull
   @Override
   public View getView (int position, View convertView, @NonNull ViewGroup parent) {
      LayoutInflater inflater = activity.getLayoutInflater();

      @SuppressLint({"ViewHolder", "InflateParams"}) View view = inflater.inflate(R.layout.radiobutton_layout, null, false);

      RadioButton chk =  view.findViewById(R.id.chk);

      StateClass stateClass = stateClasses.get(position);

      chk.setText(stateClass.getName());
      chk.setChecked(position == selectedPosition);
      chk.setTag(position);

      chk.setOnCheckedChangeListener((buttonView, isChecked) -> {
         if(radioButtonListener != null){
            selectedPosition = (Integer) buttonView.getTag();
            radioButtonListener.getRadioButtonListener(position);
            notifyDataSetChanged();
         }
      });

      return view;
   }

   public interface RadioButtonListener{
      void getRadioButtonListener(int position);
   }

   public void setRadioButtonListener(RadioButtonListener radioButtonListener) {
      this.radioButtonListener = radioButtonListener;
   }
}
