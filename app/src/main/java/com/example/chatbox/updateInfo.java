package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class updateInfo extends AppCompatActivity implements View.OnClickListener{
    private ImageView backButton;
    private TextView cencelButton,updateButton;
    private EditText FirstName,LastName,BirthDay,Email,Password,ConfirmPassword,statusEdit;
    private RadioGroup gender;
    private RadioButton genarateRadiButton;
    private Random generator;
    private String tag;


    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase fDatabase;
    private DatabaseReference root;
    private String currentUserId;
    private ProgressDialog lodingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);
        initialAll();
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){ tag = bundle.getString("tag");}
        backButton = findViewById(R.id.backButtonId);
        FirstName = findViewById(R.id.first_name);
        LastName = findViewById(R.id.last_name);
        BirthDay = findViewById(R.id.birthday);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        gender = findViewById(R.id.radioGroup);
        ConfirmPassword = findViewById(R.id.confirm_password);
        statusEdit = findViewById(R.id.statusEditId);
        cencelButton = findViewById(R.id.cencelButtonId);
        updateButton = findViewById(R.id.updateuttonId);

        backButton.setOnClickListener(this);
        cencelButton.setOnClickListener(this);
        updateButton.setOnClickListener(this);
        vesibleUnvesible();
    }
    public void initialAll(){
        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        root = fDatabase.getReference();
        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)currentUserId = currentUser.getUid();
    }
    public void vesibleUnvesible(){
        //Toast.makeText(getApplicationContext(), tag, Toast.LENGTH_SHORT).show();
        if(tag.equals("name")){
            FirstName.setVisibility(View.VISIBLE);
            LastName.setVisibility(View.VISIBLE);
        }
        if(tag.equals("status")){
            statusEdit.setVisibility(View.VISIBLE);
        }
        if(tag.equals("birthday")){
            BirthDay.setVisibility(View.VISIBLE);
        }
        if(tag.equals("gender")){
            gender.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.backButtonId){
            goBack();
        }
        if(v.getId()==R.id.cencelButtonId){
            goBack();
        }
        if(v.getId()==R.id.updateuttonId){
            initialAll();
            lodingbar = new ProgressDialog(this);
            lodingbar.setTitle("Updating Info");
            lodingbar.setMessage("Please wait, While we updating your info");
            lodingbar.setCanceledOnTouchOutside(true);
            lodingbar.show();
            if(tag.equals("name")){
               updateName();
            }
            if(tag.equals("status")){
                updateStatus();
            }
            if(tag.equals("birthday")){
                updateBirthday();
            }
            if(tag.equals("gender")){
                updateGender();
            }
        }
    }
    public void goBack(){
        onBackPressed();
    }
    String Fname,Lname,Birth,genDar,Mail,Pass,Cpass,Status;
    public void updateName(){
        int checkAll = 1;
        Fname = FirstName.getText().toString().trim();
        Lname = LastName.getText().toString().trim();
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
        if(checkAll==1){
            initialAll();
           root.child("user").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    dataSnapshot.getRef().child("fname").setValue(Fname);


                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),""+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
            root.child("user").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    dataSnapshot.getRef().child("lname").setValue(Lname);
                    lodingbar.dismiss();

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),""+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });

            root.child("user").child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    dataSnapshot.getRef().child("flname").setValue(Fname+" "+Lname);
                    lodingbar.dismiss();
                    Toast.makeText(getApplicationContext(),"Data has updated successfully",Toast.LENGTH_SHORT).show();
                    goBack();

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),""+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void updateStatus(){
        Status = statusEdit.getText().toString();
        if(Status.equals("")){
            Toast.makeText(getApplicationContext(),"Enter status",Toast.LENGTH_SHORT).show();
            statusEdit.setError("Enter your first name");
            statusEdit.requestFocus();
            return;
        }
        else{
            root.child("user").child(currentUserId).child("status").setValue(Status).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    lodingbar.dismiss();
                    Toast.makeText(getApplicationContext(),"Data has updated successfully",Toast.LENGTH_SHORT).show();
                    goBack();
                }
            });
        }
    }
    public void updateBirthday(){
        Birth = BirthDay.getText().toString().trim();
        if(Birth.equals("")){
            Toast.makeText(getApplicationContext(),"Enter your Birthday",Toast.LENGTH_SHORT).show();
            BirthDay.setError("Enter your Birthday");
            BirthDay.requestFocus();
            return;
        }
        else{
            Birth = checkBirthdayValidity(Birth);
            if(Birth.equals("error")){
                Toast.makeText(getApplicationContext(),"The Birthday is not valid",Toast.LENGTH_SHORT).show();
                BirthDay.setError("Enter your valid Birthday");
                BirthDay.requestFocus();
                return;
            }
            else{
                root.child("user").child(currentUserId).child("birthday").setValue(Birth).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        lodingbar.dismiss();
                        Toast.makeText(getApplicationContext(),"Data has updated successfully",Toast.LENGTH_SHORT).show();
                        goBack();
                    }
                });
            }
        }
    }
    public void updateGender(){
        int selectedId = gender.getCheckedRadioButtonId();
        if(selectedId==-1){
            Toast.makeText(getApplicationContext(),"Select your gender",Toast.LENGTH_SHORT).show();
            gender.requestFocus();
            return;
        }
        else{
            genarateRadiButton = (RadioButton) findViewById(selectedId);
            genDar = genarateRadiButton.getText().toString();
            root.child("user").child(currentUserId).child("gender").setValue(genDar);
            lodingbar.dismiss();
            Toast.makeText(getApplicationContext(),"Data has updated successfully",Toast.LENGTH_SHORT).show();
            goBack();
        }
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
