package com.example.sempastries;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.style.PulseRing;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import javax.net.ssl.SSLSessionBindingEvent;

public class SignInActivity extends AppCompatActivity {

    private TextInputLayout emailInputLayout, passwordInputLayout, bottomSheetEmailInputLayout;
    private TextView signUpTextView, forgotPasswordTextView;
    private EditText txtEmail, txtPassword;
    private ProgressBar progressBar, progressBar1;
    private Button signInBtn, bottomSheetSubmit;
    private final MainClass mainClass = new MainClass(this);
    private FirebaseAuth mAuth;
    private BottomSheetDialog bsd;
    private FirebaseFirestore db;
    View parentLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);

        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);

        signUpTextView = findViewById(R.id.signUpTextView);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);

        signInBtn = findViewById(R.id.btnSubmit);

        progressBar = findViewById(R.id.progressBar);

        parentLayout = findViewById(android.R.id.content);

        db = FirebaseFirestore.getInstance();

        PulseRing pulseRing = new PulseRing();
        pulseRing.setColor(getResources().getColor(R.color.dark_red_4d));
        progressBar.setIndeterminateDrawable(pulseRing);

        mAuth = FirebaseAuth.getInstance();

        txtEmail.addTextChangedListener(new SignInActivity.ValidationTextWatcher(txtEmail));
        txtPassword.addTextChangedListener(new SignInActivity.ValidationTextWatcher(txtPassword));

        forgotPasswordTextView.setOnClickListener(view -> showForgotPassword());

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!validateEmail() || !validatePassword()) {
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();
                signInBtn.setClickable(false);
                signIn(email, password, view);
            }
        });
    }

    public void showForgotPassword(){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int height = metrics.heightPixels / 2;

        bsd = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        bsd.setContentView(view);
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setPeekHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height, getResources().getDisplayMetrics()));
        bottomSheetBehavior.setDraggable(false);
        bsd.show();

        bottomSheetSubmit = view.findViewById(R.id.bottomSheetBtnSubmit);
        EditText bottomSheetTxtEmail = view.findViewById(R.id.bottomSheetTxtEmail);
        bottomSheetEmailInputLayout = view.findViewById(R.id.bottomSheetEmailInputLayout);
        progressBar1 = view.findViewById(R.id.progressBar);

        PulseRing pulseRing = new PulseRing();
        pulseRing.setColor(getResources().getColor(R.color.dark_red_4d));
        progressBar1.setIndeterminateDrawable(pulseRing);

        bottomSheetSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                if(TextUtils.isEmpty(bottomSheetTxtEmail.getText().toString().trim())){
                    bottomSheetEmailInputLayout.setError("Email Required");
                    return;
                }

                bottomSheetSubmit.setClickable(false);
                progressBar1.setVisibility(View.VISIBLE);
                String email = bottomSheetTxtEmail.getText().toString().trim();
                forgotPassword(email, view);

            }
        });
    }

    public boolean validateEmail() {
        if (txtEmail.getText().toString().trim().isEmpty()) {
            emailInputLayout.setError("Email Required");
            return false;
        }else{
            emailInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    public boolean validatePassword() {
        if (txtPassword.getText().toString().trim().isEmpty()) {
            passwordInputLayout.setError("Password Required");
            return false;
        } else {
            passwordInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public void signIn(String email, String password, View view){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            checkCompleteSignUp(user.getUid());

                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            progressBar.setVisibility(View.GONE);
                            signInBtn.setClickable(true);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            mainClass.showError("Invalid Details", view);
                        } else if (e instanceof FirebaseAuthInvalidUserException) {
                            mainClass.showError("Invalid Details", view);
                        }else{
                            mainClass.showError(e.getLocalizedMessage(), view);
                        }
                        progressBar.setVisibility(View.GONE);
                        signInBtn.setClickable(true);
                    }
                });
    }

    public void forgotPassword(String email, View view){
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    bsd.dismiss();
                    mainClass.showSuccess("Recovery email has been sent successfully", parentLayout);

                } else {
                    Log.d("forgot password error","Error Occurred");
                }
                progressBar1.setVisibility(View.GONE);
                bottomSheetSubmit.setClickable(true);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    bottomSheetEmailInputLayout.setError("Invalid Email");
                } else if (e instanceof FirebaseAuthInvalidUserException) {
                    bottomSheetEmailInputLayout.setError("Invalid Email");
                }else{
                    bottomSheetEmailInputLayout.setError(e.getLocalizedMessage());
                }

                progressBar1.setVisibility(View.GONE);
                bottomSheetSubmit.setClickable(true);
            }
        });
    }


    public void checkCompleteSignUp(String id){

        Query mQuery = db.collection("users")
                .whereEqualTo("userId", id);

        mQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                    for (DocumentSnapshot ds: task.getResult()){
                        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        intent.putExtra("USER_ID", id);
                        startActivity(intent);
                        progressBar.setVisibility(View.GONE);
                        signInBtn.setClickable(true);
                    }

                }

                if(task.getResult().size() == 0){
                    try{
                        Intent intent = new Intent(getApplicationContext(), SignupCompleteActivity.class);
                        intent.putExtra("USER_ID", id);
                        startActivity(intent);
                        progressBar.setVisibility(View.GONE);
                        signInBtn.setClickable(true);
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
                case R.id.txtEmail:
                    validateEmail();
                    break;

                case R.id.txtPassword:
                    validatePassword();
                    break;

            }
        }

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