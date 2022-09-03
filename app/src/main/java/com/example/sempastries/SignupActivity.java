package com.example.sempastries;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SignupActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private AutoCompleteTextView txtNum;
    private EditText txtPhNumber;
    private TextView txtCountry;
    private ListView listView;
    private ProgressBar progressBar;
    private BottomSheetDialog bsd;
    private RelativeLayout countryRel;
    private final MainClass mainClass = new MainClass(this);

    private ArrayList<StateClass> countryArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        txtNum = findViewById(R.id.txtNum);
        txtPhNumber = findViewById(R.id.txtPhNumber);
        txtCountry = findViewById(R.id.txtCountry);

        countryRel = findViewById(R.id.countryRel);

        requestQueue = Volley.newRequestQueue(this);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this, R.layout.list_item, getResources().getStringArray(R.array.tel_codes));

        txtNum.setAdapter(genderAdapter);
        txtNum.setDropDownBackgroundDrawable(getDrawable(R.drawable.search_bg2));

        txtPhNumber.addTextChangedListener(new PhoneTextWatcher(txtPhNumber)
        {
            @Override
            public void afterTextChanged(Editable s)
            {
                super.afterTextChanged(s);
            }
        });

        countryRel.setOnClickListener(view -> showCountry());


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
    TextView txt = view.findViewById(R.id.bottomSheetName);
    progressBar = view.findViewById(R.id.progressBar);

    txt.setText(getResources().getString(R.string.select_country));
    searchBar.setVisibility(View.VISIBLE);
    progressBar.setVisibility(View.VISIBLE);
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
                        String flag = jsonObject1.optString("flag", "Processing");


                        StateClass stateClass = new StateClass();
                        stateClass.setName(name);
                        stateClass.setUrl(flag);
                        countryArrayList.add(stateClass);

                        ListAdapter adapter = new ListAdapter(this, countryArrayList);
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener((parent, view1, position, id) -> {
                            StateClass state;
                            state = countryArrayList.get(position);
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

    public class PhoneTextWatcher implements TextWatcher
    {
        private EditText editText;

        public PhoneTextWatcher(EditText editText)
        {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {

        }

        @Override
        public void afterTextChanged(Editable s)
        {
            StringBuffer sb = new StringBuffer(s);

            if (s.length() >= 3)
            {
                char[] chars = s.toString().toCharArray();
                if (chars[2] != ' ')
                {
                    sb.insert(2,' ');
                    setContent(sb);
                }
            }

            if (s.length() >= 7)
            {
                char[] chars = s.toString().toCharArray();
                if (chars[6] != ' ')
                {
                    sb.insert(6,' ');
                    setContent(sb);
                }
            }

        }

         private void setContent(StringBuffer sb)
        {
            editText.setText(sb.toString());
            editText.setSelection(sb.length());
        }
    }
}