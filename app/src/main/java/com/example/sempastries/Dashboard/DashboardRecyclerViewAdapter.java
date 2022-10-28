package com.example.sempastries.Dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sempastries.PicassoCircular;
import com.example.sempastries.R;
import com.example.sempastries.StateClass;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DashboardRecyclerViewAdapter extends RecyclerView.Adapter<DashboardRecyclerViewAdapter.RecyclerViewHolder> {

   private ArrayList<StateClass> stateClassArrayList;
   private Context mcontext;

   public DashboardRecyclerViewAdapter(ArrayList<StateClass> stateClassArrayList, Context mcontext) {
      this.stateClassArrayList = stateClassArrayList;
      this.mcontext = mcontext;
   }

   @NonNull
   @Override
   public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      // Inflate Layout
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pastry, parent, false);
      return new RecyclerViewHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
      // Set the data to textview and imageview.
      StateClass stateClass = stateClassArrayList.get(position);
      Picasso.get()
              .load(stateClass.getUrl())
              .into(holder.pastryImg);
      holder.pastryName.setText(stateClass.getName());
   }

   @Override
   public int getItemCount() {
      // this method returns the size of recyclerview
      return stateClassArrayList.size();
   }

   public class RecyclerViewHolder extends RecyclerView.ViewHolder {

      private TextView pastryName;
      private ImageView pastryImg;

      public RecyclerViewHolder(@NonNull View itemView) {
         super(itemView);
         pastryImg = itemView.findViewById(R.id.pastryImg);
         pastryName = itemView.findViewById(R.id.pastryName);
      }
   }
}
