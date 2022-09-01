package com.example.sempastries;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import java.text.DecimalFormat;

public class SignupActivity extends AppCompatActivity {

    private AutoCompleteTextView txtNum;
    private EditText txtPhNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        txtNum = findViewById(R.id.txtNum);
        txtPhNumber = findViewById(R.id.txtPhNumber);

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


    }



    public class PhoneTextWatcher implements TextWatcher
    {
        private EditText editText;
        private boolean isDelete;
        private int lastContentLength;

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