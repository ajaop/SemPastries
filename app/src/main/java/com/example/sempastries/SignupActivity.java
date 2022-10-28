package com.example.sempastries;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.ybq.android.spinkit.style.PulseRing;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    private TextInputLayout emailInputLayout, passwordInputLayout;
    private EditText txtEmail, txtPassword;
    private ProgressBar progressBar;
    private Button signUpBtn;
    private TextView signInTextView;
    private final MainClass mainClass = new MainClass(this);
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);

        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);

        signUpBtn = findViewById(R.id.btnSubmit);

        signInTextView = findViewById(R.id.signInTextView);

        progressBar = findViewById(R.id.progressBar);

        PulseRing pulseRing = new PulseRing();
        pulseRing.setColor(getResources().getColor(R.color.dark_red_4d));
        progressBar.setIndeterminateDrawable(pulseRing);

        mAuth = FirebaseAuth.getInstance();

        txtEmail.addTextChangedListener(new ValidationTextWatcher(txtEmail));
        txtPassword.addTextChangedListener(new ValidationTextWatcher(txtPassword));

        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validateEmail() || !validatePassword()) {
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();
                signUpBtn.setClickable(false);
                signUp(email, password, view);
            }
        });
    }

    public boolean validateEmail() {
        if (txtEmail.getText().toString().trim().isEmpty()) {
            emailInputLayout.setError("Email Required");
            return false;
        } else {
            String emailId = txtEmail.getText().toString().trim();
            Boolean isValid = Patterns.EMAIL_ADDRESS.matcher(emailId).matches();

            if (!isValid) {
                emailInputLayout.setError("Invalid Email Address");
                return false;
            } else{
                emailInputLayout.setErrorEnabled(false);
            }

        }

        return true;
    }

    public boolean validatePassword() {
        if (txtPassword.getText().toString().trim().isEmpty()) {
            passwordInputLayout.setError("Password Required");
            return false;

        } else if (txtPassword.getText().toString().trim().length() < 6) {
            passwordInputLayout.setError("Password must be at least 6 characters");
            return false;

        } else {
            passwordInputLayout.setErrorEnabled(false);
        }

        return true;
    }


    public void signUp(String email, String password, View v) {
        Log.d("User details", "email: " + email + "\n" +
                "password: " + password);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(), SignupCompleteActivity.class);
                            intent.putExtra("USER_ID", user.getUid());
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            mainClass.showError("Authentication failed", v);
                        }
                        progressBar.setVisibility(View.GONE);
                        signUpBtn.setClickable(true);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if (e instanceof FirebaseAuthUserCollisionException) {
                            mainClass.showError("Email already in use ", v);
                        } else if (e instanceof FirebaseAuthWeakPasswordException) {
                            String errorCode = ((FirebaseAuthWeakPasswordException) e).getReason();
                            mainClass.showError(errorCode, v);
                        } else {
                            mainClass.showError(e.getLocalizedMessage(), v);
                        }
                        progressBar.setVisibility(View.GONE);

                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //reload();
        }
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
                case R.id.txtEmail:
                    validateEmail();
                    break;

                case R.id.txtPassword:
                    validatePassword();
                    break;

            }
        }

    }
}