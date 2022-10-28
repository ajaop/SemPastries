package com.example.sempastries.Dashboard;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sempastries.MainClass;
import com.example.sempastries.PicassoCircular;
import com.example.sempastries.PlaceHolder;
import com.example.sempastries.R;
import com.example.sempastries.SignInActivity;
import com.example.sempastries.StateClass;
import com.github.ybq.android.spinkit.style.PulseRing;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashboardActivity extends AppCompatActivity {

    private ImageView profilePic;
    private final MainClass mainClass = new MainClass(this);
    private FirebaseFirestore db;
    private TextView nameTxt;
    private ProgressBar progressBar;
    private RecyclerView rvPastry;
    private RequestQueue requestQueue;
    private String userId;
    private ArrayList<StateClass> classArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        profilePic = findViewById(R.id.profilePic);
        progressBar = findViewById(R.id.progressBar);
        db = FirebaseFirestore.getInstance();
        nameTxt = findViewById(R.id.nameTxt);
        rvPastry = findViewById(R.id.rvPastry);
        Intent intent = getIntent();
        requestQueue = Volley.newRequestQueue(this);


        PulseRing pulseRing = new PulseRing();
        pulseRing.setColor(getResources().getColor(R.color.dark_red_4d));
        progressBar.setIndeterminateDrawable(pulseRing);

        if(intent != null){
            userId = intent.getStringExtra("USER_ID");
        }else{
            mainClass.showError("Unknown Error", findViewById(android.R.id.content).getRootView());
            Intent intent1 = new Intent(getApplicationContext(), SignInActivity.class);
            startActivity(intent1);
        }

        Picasso.get()
                .load(R.drawable.profile)
                .transform(new PicassoCircular())
                .into(profilePic);

        loadDefaults(userId);
    }

    private void loadDefaults(String id){
        progressBar.setVisibility(View.VISIBLE);
        Query mQuery = db.collection("users")
                .whereEqualTo("userId", id);

        mQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                    for (DocumentSnapshot ds: task.getResult()){
                        nameTxt.setText(ds.getString("firstname"));
                        getItems();
                    }

                }

                if(task.getResult().size() == 0){
                    try{
                        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                        startActivity(intent);
                    }catch (NullPointerException e){
                        Log.e(TAG, "NullPointerException: "+ e.getMessage());
                    }

                }
                progressBar.setVisibility(View.GONE);
            }



        });
    }

    public void getItems(){
        classArrayList = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        String url = "https://ig-food-menus.herokuapp.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PlaceHolder placeHolder = retrofit.create(PlaceHolder.class);

        placeHolder
                .getPastry()
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                String resp = response.body().string();
                                Log.d("img", response.toString());
                                JSONArray jsonArray = new JSONArray(resp);
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject responseObj = jsonArray.getJSONObject(i);
                                    StateClass stateClass = new StateClass();
                                    stateClass.setUrl(responseObj.getString("img"));
                                    stateClass.setName(responseObj.getString("dsc"));
                                    classArrayList.add(stateClass);
                                }

                                DashboardRecyclerViewAdapter adapter=new DashboardRecyclerViewAdapter(classArrayList,DashboardActivity.this);
                                GridLayoutManager layoutManager=new GridLayoutManager(DashboardActivity.this,2);
                                rvPastry.setLayoutManager(layoutManager);
                                rvPastry.setAdapter(adapter);
                                progressBar.setVisibility(View.GONE);

                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }



                        } else {
                            JSONObject jsonObject;
                            try {
                                if (response.errorBody() != null) {
                                    String error = response.errorBody().string();
                                    jsonObject = new JSONObject(error);
                                    String userMessage = jsonObject.getString("message");
                                    Log.d("error", userMessage);
                                } else {
                                    Log.d("error", "Error");
                                }
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        Log.d("CountryFailure", t.toString());
                    }
                });

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage(getString(R.string.sure_to_exit))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.no),
                        (dialog, which) -> dialog.dismiss())
                .setNegativeButton(getString(R.string.yes_exit),
                        (dialog, which) -> finishAffinity());

        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
    }
}