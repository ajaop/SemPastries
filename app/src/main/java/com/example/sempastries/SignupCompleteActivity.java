package com.example.sempastries;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.ybq.android.spinkit.style.PulseRing;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class SignupCompleteActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private AutoCompleteTextView txtNum;
    private TextInputLayout numInputLayout, phnNumberInputLayout, surnameInputLayout, firstnameInputLayout;
    private EditText txtPhNumber, txtSurname, txtFirstname, searchInput;
    private TextView txtCountry, txtState, txtGender, txtCity;
    private ListView listView;
    private ProgressBar progressBar;
    private BottomSheetDialog bsd;
    private RelativeLayout countryRel, stateRel, genderRel, cityRel, searchBtn;
    private Button completeSignUpBtn;
    private ProgressBar progressBar1;
    CountryListAdapter adapter;
    private String id;
    private boolean phoneNumExist = false;
    private FirebaseFirestore db;
    String requestBody;
    private final MainClass mainClass = new MainClass(this);

    private ArrayList<StateClass> countryArrayList, stateArrayList, cityArrayList, genderArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_complete);

        txtNum = findViewById(R.id.txtNum);
        txtSurname = findViewById(R.id.txtSurname);
        txtFirstname = findViewById(R.id.txtFirstname);
        txtPhNumber = findViewById(R.id.txtPhNumber);
        txtCountry = findViewById(R.id.txtCountry);
        txtState = findViewById(R.id.txtState);
        txtGender = findViewById(R.id.txtGender);
        txtCity = findViewById(R.id.txtCity);

        numInputLayout = findViewById(R.id.numInputLayout);
        phnNumberInputLayout = findViewById(R.id.phnNumberInputLayout);
        surnameInputLayout = findViewById(R.id.surnameInputLayout);
        firstnameInputLayout = findViewById(R.id.firstnameInputLayout);

        countryRel = findViewById(R.id.countryRel);
        stateRel = findViewById(R.id.stateRel);
        genderRel = findViewById(R.id.genderRel);
        cityRel = findViewById(R.id.cityRel);

        progressBar1 = findViewById(R.id.progressBar1);
        completeSignUpBtn = findViewById(R.id.btnSubmit);

        PulseRing pulseRing = new PulseRing();
        pulseRing.setColor(getResources().getColor(R.color.dark_red_4d));
        progressBar1.setIndeterminateDrawable(pulseRing);

        requestQueue = Volley.newRequestQueue(this);
        db = FirebaseFirestore.getInstance();

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this, R.layout.list_item, getResources().getStringArray(R.array.tel_codes));

        txtNum.setAdapter(genderAdapter);
        txtNum.setDropDownBackgroundDrawable(getDrawable(R.drawable.search_bg2));

        txtPhNumber.addTextChangedListener(new SignupCompleteActivity.ValidationTextWatcher(txtPhNumber));
        txtSurname.addTextChangedListener(new SignupCompleteActivity.ValidationTextWatcher(txtSurname));
        txtFirstname.addTextChangedListener(new SignupCompleteActivity.ValidationTextWatcher(txtFirstname));

        countryRel.setOnClickListener(view -> showCountry());
        stateRel.setOnClickListener(view -> showState());
        genderRel.setOnClickListener(view -> showGender());
        cityRel.setOnClickListener(view -> showCity());

        Intent intent = getIntent();
        if(intent != null){
            id = intent.getStringExtra("USER_ID");
        }
        completeSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!validatePhone() || !validateSurname() || !validateFirstName()){
                    return;
                }

                if(TextUtils.isEmpty(txtGender.getText().toString().trim())){
                    txtGender.requestFocus();
                    showGender();
                    return;
                }

                if(TextUtils.isEmpty(txtCountry.getText().toString().trim())){
                    txtCountry.requestFocus();
                    showCountry();
                    return;
                }

                if(TextUtils.isEmpty(txtState.getText().toString().trim())){
                    txtState.requestFocus();
                    showState();
                    return;
                }

                if(TextUtils.isEmpty(txtCity.getText().toString().trim())){
                    txtCity.requestFocus();
                    showCity();
                    return;
                }

                String countryCode = txtNum.getText().toString().trim().replace("+", "");
                String num = txtPhNumber.getText().toString().trim();
                String surname = txtSurname.getText().toString().trim();
                String firstname = txtFirstname.getText().toString().trim();
                String gender = txtGender.getText().toString().trim();
                String country = txtCountry.getText().toString().trim();
                String state = txtState.getText().toString().trim();
                String city = txtCity.getText().toString().trim();

                String aa = num.substring(0,1);

                String phoneNumber;
                if(aa.matches("0")){
                    phoneNumber = countryCode + " " + num.replaceFirst("0", "");
                } else {
                    phoneNumber = countryCode + " " + num;
                }

                checkPhoneNumber(phoneNumber);
                if(!phoneNumExist){
                    phnNumberInputLayout.requestFocus();
                    return;
                }

                progressBar1.setVisibility(View.VISIBLE);
                completeSignUpBtn.setClickable(false);
                completeSignUp(phoneNumber, surname, firstname, gender, country, state, city, view);
            }

        });
    }

    public boolean validatePhone(){
        if (txtPhNumber.getText().toString().trim().isEmpty()) {
            phnNumberInputLayout.setError("Phone Number Required");
            phnNumberInputLayout.requestFocus();
            return false;

        } else if (txtPhNumber.getText().toString().trim().length() < 10) {
            phnNumberInputLayout.setError("Invalid Phone Number");
            phnNumberInputLayout.requestFocus();

            return false;

        } else {
            phnNumberInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    public boolean validateSurname(){
        if (txtSurname.getText().toString().trim().isEmpty()) {
            surnameInputLayout.setError("Surname Required");
            surnameInputLayout.requestFocus();
            return false;

        } else {
            surnameInputLayout.setErrorEnabled(false);
        }

        return true;
    }


    public boolean validateFirstName(){
        if (txtFirstname.getText().toString().trim().isEmpty()) {
            firstnameInputLayout.setError("Firstname Required");
            firstnameInputLayout.requestFocus();
            return false;

        } else {
            firstnameInputLayout.setErrorEnabled(false);
        }

        return true;
    }


    public void completeSignUp(String phoneNumber, String surname, String firstname, String gender, String country, String state, String city, View view){
        Log.d("signup", "phone " + phoneNumber + "\n"+
                                    "surname " + surname + "\n"+
                                    "firstname " + firstname + "\n"+
                                    "gender " + gender + "\n"+
                                    "country " + country + "\n"+
                                    "state " + state + "\n"+
                                     "city " + city);

        Log.d("userId", id);

        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("userId", id);
        user.put("phone", phoneNumber);
        user.put("surname", surname);
        user.put("firstname", firstname);
        user.put("gender", gender);
        user.put("country", country);
        user.put("state", state);
        user.put("city", city);


        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        mainClass.showSuccess("Registration completed successfully", view);
                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        intent.putExtra("USER_ID", id);
                        startActivity(intent);
                        progressBar1.setVisibility(View.GONE);
                        completeSignUpBtn.setClickable(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        mainClass.showError("Error in completing registration", view);
                        progressBar1.setVisibility(View.GONE);
                        completeSignUpBtn.setClickable(true);
                    }
                });
    }




    public void showCountry(){
    WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();
    DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
    int height = metrics.heightPixels / 4;

    bsd = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
    @SuppressLint("InflateParams")
    View view = getLayoutInflater().inflate(R.layout.bottom_sheet_list, null);
    bsd.setContentView(view);
    BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
    bottomSheetBehavior.setPeekHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics()));
    bottomSheetBehavior.setDraggable(false);
    bsd.show();

     listView = view.findViewById(R.id.listView);
     RelativeLayout searchBar = view.findViewById(R.id.searchBar);
     searchInput = view.findViewById(R.id.searchInput);
     searchBtn = view.findViewById(R.id.layout1);
    TextView txt = view.findViewById(R.id.bottomSheetName);
    progressBar = view.findViewById(R.id.progressBar);

    txt.setText(getResources().getString(R.string.select_country));
    searchBar.setVisibility(View.VISIBLE);

        PulseRing pulseRing = new PulseRing();
        pulseRing.setColor(getResources().getColor(R.color.dark_red_4d));
        progressBar.setIndeterminateDrawable(pulseRing);
    progressBar.setVisibility(View.VISIBLE);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter(charSequence.toString(), countryArrayList);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(view.getContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
    getCountry();

}

public void getCountry(){
    countryArrayList = new ArrayList<>();

    String urlCountry = "https://countriesnow.space/api/v0.1/countries/flag/images";

    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlCountry,
            null,
            response -> {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int k = 0; k < jsonArray.length(); k++) {
                        JSONObject jsonObject1 = jsonArray.optJSONObject(k);
                        String name = jsonObject1.optString("name", "Processing");

                        StateClass stateClass = new StateClass();
                        stateClass.setName(name);
                        countryArrayList.add(stateClass);


                        adapter = new CountryListAdapter(this, countryArrayList);
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener((parent, view1, position, id) -> {
                            StateClass state;
                            state = (StateClass) parent.getItemAtPosition(position);
                            txtCountry.setText(state.getName());
                            bsd.dismiss();
                        });
                        progressBar.setVisibility(View.GONE);

                    }
                } catch (JSONException e) {
                    Toast.makeText(this, ""+getString(R.string.unknown_err), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    bsd.dismiss();
                }
            },error -> {
        if(error.networkResponse == null){
            Toast.makeText(getApplicationContext(), ""+getString(R.string.network_err), Toast.LENGTH_SHORT).show();
        } else {
            String error1 = mainClass.getError(error.networkResponse.data);
            Toast.makeText(getApplicationContext(), ""+error1, Toast.LENGTH_SHORT).show();
        }
        progressBar.setVisibility(View.GONE);
        bsd.dismiss();
    });
    requestQueue.add(request);
}

    public void showState(){
        if(txtCountry.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(), "Select Country", Toast.LENGTH_SHORT).show();

           }else {
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            int height = metrics.heightPixels / 2;

            bsd = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
            @SuppressLint("InflateParams")
            View view = getLayoutInflater().inflate(R.layout.bottom_sheet_list, null);
            bsd.setContentView(view);
            BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
            bottomSheetBehavior.setPeekHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics()));
            bottomSheetBehavior.setDraggable(false);
            bsd.show();

            listView = view.findViewById(R.id.listView);
            RelativeLayout searchBar = view.findViewById(R.id.searchBar);
            searchInput = view.findViewById(R.id.searchInput);
            searchBtn = view.findViewById(R.id.layout1);
            TextView txt = view.findViewById(R.id.bottomSheetName);
            progressBar = view.findViewById(R.id.progressBar);

            PulseRing pulseRing = new PulseRing();
            pulseRing.setColor(getResources().getColor(R.color.dark_red_4d));
            progressBar.setIndeterminateDrawable(pulseRing);

            txt.setText(getResources().getString(R.string.select_state));
            searchBar.setVisibility(View.VISIBLE);

            searchInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    filter(charSequence.toString(), stateArrayList);

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            searchBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (view.requestFocus()) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(view.getContext().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            });

            if(txtCountry.getText().toString().trim().toLowerCase(Locale.ROOT).contains("nigeria")){

                stateArrayList = mainClass.getStates();
                adapter = new CountryListAdapter(SignupCompleteActivity.this, stateArrayList);
                listView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);

                listView.setOnItemClickListener((parent, view1, position, id) -> {
                    StateClass state;
                    state = (StateClass) parent.getItemAtPosition(position);
                    txtState.setText(state.getName());
                    bsd.dismiss();
                });
            }

        }
    }

    public void showCity(){
        if(txtState.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(), "Select State", Toast.LENGTH_SHORT).show();

        }else {
            WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            int height = metrics.heightPixels / 2;

            bsd = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
            @SuppressLint("InflateParams")
            View view = getLayoutInflater().inflate(R.layout.bottom_sheet_list, null);
            bsd.setContentView(view);
            BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
            bottomSheetBehavior.setPeekHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics()));
            bottomSheetBehavior.setDraggable(false);
            bsd.show();

            listView = view.findViewById(R.id.listView);
            RelativeLayout searchBar = view.findViewById(R.id.searchBar);
            searchInput = view.findViewById(R.id.searchInput);
            searchBtn = view.findViewById(R.id.layout1);
            TextView txt = view.findViewById(R.id.bottomSheetName);
            progressBar = view.findViewById(R.id.progressBar);

            PulseRing pulseRing = new PulseRing();
            pulseRing.setColor(getResources().getColor(R.color.dark_red_4d));
            progressBar.setIndeterminateDrawable(pulseRing);

            txt.setText(getResources().getString(R.string.select_city));
            searchBar.setVisibility(View.VISIBLE);

            searchInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    filter(charSequence.toString(), cityArrayList);

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            searchBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (view.requestFocus()) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(view.getContext().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            });

            if(txtState.getText().toString().trim().toLowerCase(Locale.ROOT).contains("oyo")){

                cityArrayList = mainClass.getCity();
                adapter = new CountryListAdapter(SignupCompleteActivity.this, cityArrayList);
                listView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);

                listView.setOnItemClickListener((parent, view1, position, id) -> {
                    StateClass state;
                    state = (StateClass) parent.getItemAtPosition(position);
                    txtCity.setText(state.getName());
                    bsd.dismiss();
                });
            }

        }
    }

    public void showGender(){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int height = metrics.heightPixels / 4;

        bsd = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_list, null);
        bsd.setContentView(view);
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setPeekHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics()));
        bottomSheetBehavior.setDraggable(false);
        bsd.show();

        listView = view.findViewById(R.id.listView);
        RelativeLayout searchBar = view.findViewById(R.id.searchBar);
        searchInput = view.findViewById(R.id.searchInput);
        TextView txt = view.findViewById(R.id.bottomSheetName);
        progressBar = view.findViewById(R.id.progressBar);

        PulseRing pulseRing = new PulseRing();
        pulseRing.setColor(getResources().getColor(R.color.dark_red_4d));
        progressBar.setIndeterminateDrawable(pulseRing);

        progressBar.setVisibility(View.GONE);
        txt.setText(getResources().getString(R.string.select_gender));

            genderArrayList = mainClass.getGender();

        RadioButtonAdapter adapter = new RadioButtonAdapter(this, genderArrayList);
        listView.setAdapter(adapter);
        adapter.setRadioButtonListener(new RadioButtonAdapter.RadioButtonListener() {
            @Override
            public void getRadioButtonListener(int position) {
                StateClass stateClass = genderArrayList.get(position);
                txtGender.setText(stateClass.getName());
                bsd.dismiss();
            }
        });

    }


    private void filter(String text, ArrayList<StateClass> filterArrayList) {
        ArrayList<StateClass> filteredlist = new ArrayList<>();

        for (StateClass item : filterArrayList) {

            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {

        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            adapter.filterList(filteredlist);
        }
    }

    public void checkPhoneNumber(String phone){

        Query mQuery = db.collection("users")
                .whereEqualTo("phone", phone);

        mQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                    for (DocumentSnapshot ds: task.getResult()){
                        phnNumberInputLayout.setError("Phone Number Exists");
                        progressBar.setVisibility(View.GONE);
                        completeSignUpBtn.setClickable(true);
                        phoneNumExist = false;
                    }

                }

                if(task.getResult().size() == 0){
                    try{
                        progressBar.setVisibility(View.GONE);
                        completeSignUpBtn.setClickable(true);
                        phoneNumExist = true;
                    }catch (NullPointerException e){
                        Log.e(TAG, "NullPointerException: "+ e.getMessage());
                    }

                }
            }

        });

    }

    private class ValidationTextWatcher implements TextWatcher {
        private View view;

        private ValidationTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.txtPhNumber:
                    validatePhone();
                    break;

                case R.id.txtSurname:
                    validateSurname();
                    break;

                case R.id.txtFirstname:
                    validateFirstName();
                    break;

            }
        }

    }

}