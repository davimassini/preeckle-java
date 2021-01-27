package com.example.preeckle.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.preeckle.MainActivity;
import com.example.preeckle.R;
import com.example.preeckle.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameField;
    private EditText emailField;
    private EditText passwordField;
    private EditText confirmPasswordField;
    private Button signUpButton;
    private TextView signInButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameField = (EditText) findViewById(R.id.completeNameField);
        emailField = (EditText) findViewById(R.id.emailRegisterField);
        passwordField = (EditText) findViewById(R.id.passwordRegisterField);
        confirmPasswordField = (EditText) findViewById(R.id.passwordConfirmField);
        signUpButton = (Button) findViewById(R.id.signUpConfirmButton);
        signInButton = (TextView) findViewById(R.id.signInButton);

        mAuth = FirebaseAuth.getInstance();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                createNewUser();
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendUserToLoginActivity();
            }
        });


        onStart();
    }

    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            sendUserToMainActivity();
        }

    }
    public void sendUserToLoginActivity() {
        Intent loginActivity = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginActivity);
        finish();
    }

    private void sendUserToMainActivity() {
        Intent mainActivity = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }

    public void createNewUser() {
        String completeName = nameField.getText().toString();
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();

        if(TextUtils.isEmpty(completeName)) {
            Toast.makeText(this, "Your name field is empty, please write your name.", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Your email field is empty, please write your email.", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Your password field is empty, please write your password.", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Your confirm password field is empty, please write your confirm password.", Toast.LENGTH_SHORT).show();
        }else if(!password.equals(confirmPassword)) {
            Toast.makeText(this, "Your confirm password doesn't match with your password.", Toast.LENGTH_SHORT).show();
        }else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "Successfull register!", Toast.LENGTH_SHORT).show();
                                sendUserToMainActivity();

                                String currentUserID = mAuth.getCurrentUser().getUid();
                                String nameFull = nameField.getText().toString();;
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

                                HashMap userHashMap = new HashMap();
                                userHashMap.put("nameFull", nameFull);
                                userHashMap.put("userID", currentUserID);

                                userRef.updateChildren(userHashMap);
                            }else {
                                String message = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
