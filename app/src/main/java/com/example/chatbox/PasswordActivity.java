package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText currPass,newPass,conPass;
    private ImageButton backButton;
    private TextView updateButton,shoHidTxt;
    private ImageView shoHidButton;
    private LinearLayout shoHid;

    public String curPass,currps,Pass,Cpass;


    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase fDatabase;
    private DatabaseReference root;
    private String currentUserId;
    private ProgressDialog lodingbar;

    private int shohid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
       initialAll();


        backButton = findViewById(R.id.backButtonId);
        currPass = findViewById(R.id.currPassId);
        newPass = findViewById(R.id.newPassId);
        conPass = findViewById(R.id.conpassId);
        updateButton = findViewById(R.id.updatebuttonId);
        shoHidButton = findViewById(R.id.shoHidButtonId);
        shoHidTxt = findViewById(R.id.shoHidTxtId);
        shoHid = findViewById(R.id.shoHidId);

        backButton.setOnClickListener(this);
        updateButton.setOnClickListener(this);
        shoHid.setOnClickListener(this);

        FirebaseDatabase.getInstance().getReference().child("user").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()&&snapshot.hasChild("password")){
                    curPass = snapshot.child("password").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void initialAll(){
        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        root = fDatabase.getReference();
        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)currentUserId = currentUser.getUid();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.backButtonId){onBackPressed();}
        if(v.getId()==R.id.updatebuttonId){
            updatePassword();
        }
        if(v.getId()==R.id.shoHidId){
            shoHidPass();
        }
    }

    public void shoHidPass(){
        if(shohid==0){
            shoHidButton.setImageDrawable(getResources().getDrawable(R.drawable.show_pass_icon));
            shoHidTxt.setText("Hid Password");
            currPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            newPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            conPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            shohid = 1;
        }
        else{
            shoHidButton.setImageDrawable(getResources().getDrawable(R.drawable.hid_pass_icon));
            shoHidTxt.setText("Show Password");
            currPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            newPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            conPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            shohid = 0;
        }
    }

    private void updatePassword() {
        int checkAll = 1;

        currps = currPass.getText().toString();
        Pass = newPass.getText().toString().trim();
        Cpass = conPass.getText().toString().trim();
        if(currps.equals("")){
            checkAll=0;
            Toast.makeText(this,"Enter current password",Toast.LENGTH_SHORT).show();
            currPass.setError("Enter correct password");
            currPass.requestFocus();
        }
        if(!currps.equals(curPass)){
            checkAll=0;
            Toast.makeText(this,"The password is not correct",Toast.LENGTH_SHORT).show();
            currPass.setError("Enter correct password");
            currPass.requestFocus();
        }
        if(Pass.equals("")){
            checkAll = 0;
            Toast.makeText(this,"Enter any password",Toast.LENGTH_SHORT).show();
            newPass.setError("Enter any password");
            newPass.requestFocus();
            return;
        }
        else{
            if(Pass.length()<6){
                checkAll = 0;
                Toast.makeText(this,"Password length must have to be minimum of 6 character",Toast.LENGTH_SHORT).show();
                newPass.setError("password length must have to be a minimum of 6 character");
                newPass.requestFocus();
                return;
            }
        }
        if(Cpass.equals("")){
            checkAll = 0;
            Toast.makeText(this,"Enter the password again for confirming",Toast.LENGTH_SHORT).show();
            conPass.setError("Enter the password again for confirming");
            conPass.requestFocus();
            return;
        }
        if(!Pass.equals(Cpass)){
            checkAll = 0;
            Toast.makeText(this,"These two password are not same",Toast.LENGTH_SHORT).show();
            conPass.setError("These two password are not same");
            newPass.setError("These two password are not same");
            newPass.requestFocus();
            return;
        }
        if(checkAll==1){
            lodingbar = new ProgressDialog(this);
            lodingbar.setTitle("Uploading password");
            lodingbar.setMessage("Please wait..");
            lodingbar.setCanceledOnTouchOutside(true);
            lodingbar.show();


            saveNewPassword();
        }
    }

    public void saveNewPassword(){

        final String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email,curPass);

        FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FirebaseAuth.getInstance().getCurrentUser().updatePassword(Pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                FirebaseDatabase.getInstance().getReference().child("user").child(currentUserId).child("password").setValue(Pass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            lodingbar.dismiss();
                                            Toast.makeText(getApplicationContext(), "Password is Changed", Toast.LENGTH_SHORT).show();
                                            onBackPressed();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }
}