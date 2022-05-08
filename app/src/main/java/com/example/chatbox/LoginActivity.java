package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView creatAccount,login,forgetpass;
    private EditText Email,Password;
    private ProgressDialog lodingbar;


    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase fDatabase;
    private DatabaseReference root;
    private String currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        creatAccount = findViewById(R.id.creat_new_account);
        initialAll();

        login = findViewById(R.id.login_button);
        Email = findViewById(R.id.login_email);
        Password = findViewById(R.id.login_password);
        forgetpass = findViewById(R.id.forget_password);

        lodingbar = new ProgressDialog(this);
        login.setOnClickListener(this);
        creatAccount.setOnClickListener(this);
        forgetpass.setOnClickListener(this);
    }

    public void initialAll(){
        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        root = fDatabase.getReference();
        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)currentUserId = currentUser.getUid();
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser!=null){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.creat_new_account){
            Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
            startActivity(intent);
            finish();
        }
        if(v.getId()==R.id.login_button){
           loginUsertoAccount();
        }
        if(v.getId()==R.id.forget_password){
          Intent intent = new Intent(getApplicationContext(),ForgatePassword.class);
          startActivity(intent);
        }
    }
    public  void loginUsertoAccount(){
        int checkAll=1;
        String mail,pss;
        mail = Email.getText().toString().trim();
        pss = Password.getText().toString().trim();
        if(mail.equals("")){
            checkAll = 0;
            Toast.makeText(getApplicationContext(),"Enter your email",Toast.LENGTH_SHORT).show();
            Email.setError("Enter your email");
            Email.requestFocus();
            return;
        }
        else{
            if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
                checkAll = 0;
                Toast.makeText(this,"Enter any valid email address",Toast.LENGTH_SHORT).show();
                Email.setError("Enter any valid email address");
                Email.requestFocus();
                return;
            }
        }
        if(pss.equals("")){
            checkAll = 0;
            Toast.makeText(this,"Enter any password",Toast.LENGTH_SHORT).show();
            Password.setError("Enter any password");
            Password.requestFocus();
            return;
        }
        if(checkAll==1){
            lodingbar.setTitle("Sign In");
            lodingbar.setMessage("Please wait");
            lodingbar.setCanceledOnTouchOutside(true);
            lodingbar.show();
            mAuth.signInWithEmailAndPassword(mail,pss).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    lodingbar.dismiss();
                    if(task.isSuccessful()){
                        initialAll();
                       root.child("user").child(currentUserId).child("password").setValue(pss);
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                         task.getException().toString().trim();
                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}