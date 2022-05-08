package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView alredyhaveAnAccount,signUpButton;
    private EditText FirstName,LastName,BirthDay,Email,Password,ConfirmPassword;
    private ImageView shoHidButton1, shoHidButton2;
    private RadioGroup gender;
    private RadioButton genarateRadiButton;
    private Random generator;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference root;
    private ProgressDialog lodingbar;

    private int shohid = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        root = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        generator = new Random();
        alredyhaveAnAccount = findViewById(R.id.already_have_account);
        signUpButton = findViewById(R.id.signup_button);
        FirstName = findViewById(R.id.first_name);
        LastName = findViewById(R.id.last_name);
        BirthDay = findViewById(R.id.birthday);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        gender = findViewById(R.id.radioGroup);
        ConfirmPassword = findViewById(R.id.confirm_password);
        shoHidButton1 = findViewById(R.id.showHidPassId1);
        shoHidButton2 = findViewById(R.id.showHidPassId2);

        lodingbar = new ProgressDialog(this);
        alredyhaveAnAccount.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
        shoHidButton1.setOnClickListener(this);
        shoHidButton2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.already_have_account){
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
            finish();
        }
        if(v.getId()==R.id.signup_button){
            creatAcunt();
        }
        if(v.getId()==R.id.showHidPassId1){
            showHiddPass();
        }
        if(v.getId()==R.id.showHidPassId2){
            showHiddPass();
        }
    }

    public void showHiddPass(){
       if(shohid==0){
           Password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
           ConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
           shoHidButton1.setImageDrawable(getResources().getDrawable(R.drawable.show_pass_icon));
           shoHidButton2.setImageDrawable(getResources().getDrawable(R.drawable.show_pass_icon));
           shohid = 1;
       }
       else{
           Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
           ConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
           shoHidButton1.setImageDrawable(getResources().getDrawable(R.drawable.hid_pass_icon));
           shoHidButton2.setImageDrawable(getResources().getDrawable(R.drawable.hid_pass_icon));
           shohid = 0;
       }
    }
    String Fname,Lname,Birth,genDar,Mail,Pass,Cpass;
    public void creatAcunt(){
        int checkAll=1;
        Fname = FirstName.getText().toString().trim();
        Lname = LastName.getText().toString().trim();
        Birth = BirthDay.getText().toString().trim();
        Mail = Email.getText().toString().trim();
        Pass = Password.getText().toString().trim();
        Cpass = ConfirmPassword.getText().toString().trim();
        if(Fname.equals("")){
            checkAll = 0;
            Toast.makeText(getApplicationContext(),"Enter your first name",Toast.LENGTH_SHORT).show();
            FirstName.setError("Enter your first name");
            FirstName.requestFocus();
            return;
        }
        if(Lname.equals("")){
            checkAll = 0;
            Toast.makeText(getApplicationContext(),"Enter your last name",Toast.LENGTH_SHORT).show();
            LastName.setError("Enter your last name");
            LastName.requestFocus();
            return;
        }
        if(Birth.equals("")){
            checkAll = 0;
            Toast.makeText(getApplicationContext(),"Enter your Birthday",Toast.LENGTH_SHORT).show();
            BirthDay.setError("Enter your Birthday");
            BirthDay.requestFocus();
            return;
        }
        else{
            Birth = checkBirthdayValidity(Birth);
            if(Birth.equals("error")){
                checkAll = 0;
                Toast.makeText(getApplicationContext(),"The Birthday is not valid",Toast.LENGTH_SHORT).show();
                BirthDay.setError("Enter your valid Birthday");
                BirthDay.requestFocus();
                return;
            }
        }
        int selectedId = gender.getCheckedRadioButtonId();
        if(selectedId==-1){
            checkAll = 0;
            Toast.makeText(getApplicationContext(),"Select your gender",Toast.LENGTH_SHORT).show();
            gender.requestFocus();
            return;
        }
        else{
            genarateRadiButton = (RadioButton) findViewById(selectedId);
            genDar = genarateRadiButton.getText().toString();
        }
        if(Mail.equals("")){
            checkAll = 0;
            Toast.makeText(getApplicationContext(),"Enter your email",Toast.LENGTH_SHORT).show();
            Email.setError("Enter your email");
            Email.requestFocus();
            return;
        }
        else{
            if(!Patterns.EMAIL_ADDRESS.matcher(Mail).matches()){
                checkAll = 0;
                Toast.makeText(this,"Enter any valid email address",Toast.LENGTH_SHORT).show();
                Email.setError("Enter any valid email address");
                Email.requestFocus();
                return;
            }
        }
        if(Pass.equals("")){
            checkAll = 0;
            Toast.makeText(this,"Enter any password",Toast.LENGTH_SHORT).show();
            Password.setError("Enter any password");
            Password.requestFocus();
            return;
        }
        else{
            if(Pass.length()<6){
                checkAll = 0;
                Toast.makeText(this,"Password length must have to be minimum of 6 character",Toast.LENGTH_SHORT).show();
                Password.setError("password length must have to be a minimum of 6 character");
                Password.requestFocus();
                return;
            }
        }
        if(Cpass.equals("")){
            checkAll = 0;
            Toast.makeText(this,"Enter the password again for confirming",Toast.LENGTH_SHORT).show();
            ConfirmPassword.setError("Enter the password again for confirming");
            ConfirmPassword.requestFocus();
            return;
        }
        if(!Pass.equals(Cpass)){
            checkAll = 0;
            Toast.makeText(this,"These two password are not same",Toast.LENGTH_SHORT).show();
            ConfirmPassword.setError("These two password are not same");
            Password.setError("These two password are not same");
            Password.requestFocus();
            return;
        }
        if(checkAll==1){
            lodingbar.setTitle("Creating new account");
            lodingbar.setMessage("Please wait, While we creating a new account for you");
            lodingbar.setCanceledOnTouchOutside(true);
            lodingbar.show();
            mAuth.createUserWithEmailAndPassword(Mail,Pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    lodingbar.dismiss();
                      if(task.isSuccessful()){
                          String currentUserId = mAuth.getCurrentUser().getUid();
                          root.child("user").child(currentUserId).setValue("");
                          setProfileInfo();
                      }
                      else{
                          Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                      }
                }
            });
        }
    }

    public void setProfileInfo(){
        sentVerificationMail();
        currentUser = mAuth.getCurrentUser();
        String currentUserId = currentUser.getUid();
        HashMap<String,String> profileHasMap = new HashMap<>();

        profileHasMap.put("uid",currentUserId);
        profileHasMap.put("fname",Fname);
        profileHasMap.put("lname",Lname);
        profileHasMap.put("flname",Fname+" "+Lname);
        profileHasMap.put("birthday",Birth);
        profileHasMap.put("gender",genDar);
        profileHasMap.put("password",Pass);
        profileHasMap.put("mail",Mail);
        root.child("user").child(currentUserId).setValue(profileHasMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    sentVerificationMail();
                }
            }
        });
    }
    public void sentVerificationMail(){
        currentUser = mAuth.getCurrentUser();
        currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),"Account Created Successfully. Please verified your email address",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public String checkBirthdayValidity(String s){
        String rs="";
        int dy=0,mn=0,yr=0,ck=0;
        for(int i=0; i<s.length(); i++){
            if(s.charAt(i)>='0'&&s.charAt(i)<='9'){
                if(ck==0){dy = ((dy*10) + (s.charAt(i)-'0'));}
                if(ck==1){mn = ((mn*10) + (s.charAt(i)-'0'));}
                if(ck==2){yr = ((yr*10) + (s.charAt(i)-'0'));}
            }
            else{ck++;}
        }
        if((dy>0&&dy<32)&&(mn>0&&mn<13)&&yr>0){
            int lpr=0;
            if(yr%400==0||(yr%100!=0&&yr%4==0)){lpr=1;}
            if(((mn==2||mn==4||mn==6||mn==9||mn==11)&&dy==31)||(mn==2&&dy==30)||(mn==2&&dy==29&&lpr==0)){
                return "error";
            }
            else{
                if(dy<10) rs += "0";
                rs += String.valueOf(dy);rs += " ";
                if(mn==1)rs += "January, ";
                if(mn==2)rs += "February, ";
                if(mn==3)rs += "March, ";
                if(mn==4)rs += "April, ";
                if(mn==5)rs += "May, ";
                if(mn==6)rs += "June, ";
                if(mn==7)rs += "July, ";
                if(mn==8)rs += "August, ";
                if(mn==9)rs += "September, ";
                if(mn==10)rs += "October, ";
                if(mn==11)rs += "November, ";
                if(mn==12)rs += "December, ";
                rs += String.valueOf(yr);
                return  rs;
            }

        }
        else{
            return "error";
        }
    }
}
