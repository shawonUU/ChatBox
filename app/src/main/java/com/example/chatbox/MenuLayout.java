package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MenuLayout extends AppCompatActivity implements View.OnClickListener {
    private ImageButton backButton;
    private TextView settingButton,logoutButton,deletButton,userNmaeView,personalButton,changePassButton;
    private LinearLayout lodingLaout,mainLaout,viewProfileButton,expandLaoutButton;
    private CircleImageView profilePic;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase fDatabase;
    private DatabaseReference root;
    private String currentUserId;
    private ProgressDialog lodingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_layout);
        initialAll();

        backButton = findViewById(R.id.backButtonId);
        logoutButton = findViewById(R.id.logoutId);
        settingButton = findViewById(R.id.settingsId);
        deletButton = findViewById(R.id.deletId);
        lodingLaout = findViewById(R.id.loadingLaoutId);
        mainLaout = findViewById(R.id.mainLaoutId);
        viewProfileButton = findViewById(R.id.viewProfileId);
        userNmaeView = findViewById(R.id.userNameId);
        profilePic = findViewById(R.id.profilePicId);

        expandLaoutButton = findViewById(R.id.expandLayoutId);
        personalButton = findViewById(R.id.personalInfoId);
        changePassButton = findViewById(R.id.changePasswordId);

        settingButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
        deletButton.setOnClickListener(this);
        viewProfileButton.setOnClickListener(this);
        personalButton.setOnClickListener(this);
        changePassButton.setOnClickListener(this);

        setData();
    }
    public void initialAll(){
        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        root = fDatabase.getReference();
        currentUser = mAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.viewProfileId){
            Intent intent = new Intent(getApplicationContext(),Myprofile.class);
            startActivity(intent);
        }
        if(v.getId()==R.id.backButtonId){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
        if(v.getId()==R.id.settingsId){
            if(expandLaoutButton.getVisibility()==View.VISIBLE){
                expandLaoutButton.setVisibility(View.GONE);
            }
            else{
                expandLaoutButton.setVisibility(View.VISIBLE);
            }
        }
        if(v.getId()==R.id.personalInfoId){
            Intent intent = new Intent(getApplicationContext(),SettingActivity.class);
            startActivity(intent);
        }
        if(v.getId()==R.id.changePasswordId){
            Intent intent = new Intent(getApplicationContext(),PasswordActivity.class);
            startActivity(intent);
        }
        if(v.getId()==R.id.logoutId){

            updateStatus("Offline");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Logout?");

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        mAuth.signOut();
                        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }catch (Exception e) {
                        Toast.makeText(getApplicationContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    dialog.cancel();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();

        }
        if(v.getId()==R.id.deletId){
             new AlertDialog.Builder(this)
                    .setTitle("Delete your account?")
                    .setMessage("If you delete your account, it will no longer be possible to recover.\n\nDo you want to delete your account?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            deleteAccount();
                        }
                    })
                    .setNegativeButton("No", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
    public void updateStatus(String status){
        String curentDate, currentTime;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currntDat = new SimpleDateFormat("MMM dd, yyy");
        curentDate = currntDat.format(calendar.getTime());

        SimpleDateFormat currntTim = new SimpleDateFormat("hh:mm a");
        currentTime = currntTim.format(calendar.getTime());

        HashMap<String, Object> activeMap = new HashMap<>();
        activeMap.put("date",curentDate);
        activeMap.put("time",currentTime);
        activeMap.put("status",status);

        FirebaseDatabase.getInstance().getReference().child("user").child(currentUserId).child("userstatus").updateChildren(activeMap);
    }
    public void setData(){

        lodingbar = new ProgressDialog(this);
        lodingbar.setMessage("Loding..");
        lodingbar.setCanceledOnTouchOutside(false);
        lodingbar.show();

        initialAll();
        root.child("user").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists())&&(dataSnapshot.hasChild("fname"))&&(dataSnapshot.hasChild("lname"))){
                    String fn = dataSnapshot.child("fname").getValue().toString();
                    String ln = dataSnapshot.child("lname").getValue().toString();
                    String ful = fn+" "+ln;
                    userNmaeView.setText(ful);
                }
                if(dataSnapshot.exists()&& dataSnapshot.hasChild("gender") && dataSnapshot.child("gender").getValue().toString().equals("Male")){
                    profilePic.setImageDrawable(getResources().getDrawable(R.drawable.defoult_image_male));
                }
                if(dataSnapshot.exists()&& dataSnapshot.hasChild("gender") && dataSnapshot.child("gender").getValue().toString().equals("Female")){
                    profilePic.setImageDrawable(getResources().getDrawable(R.drawable.defoult_image_female));
                }
                if(dataSnapshot.exists()&& dataSnapshot.hasChild("gender") && dataSnapshot.child("gender").getValue().toString().equals("Common")){
                    profilePic.setImageDrawable(getResources().getDrawable(R.drawable.defoult_image_common));
                }
                if((dataSnapshot.exists())&&(dataSnapshot.hasChild("ppictur"))){
                    String imageurl = dataSnapshot.child("ppictur").getValue().toString();
                    Picasso.get().load(imageurl).into(profilePic);
                }

               lodingbar.dismiss();
                lodingLaout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteAccount() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FirebaseDatabase.getInstance().getReference().child("user").child(currentUserId).child("mail").setValue(null);
                    FirebaseDatabase.getInstance().getReference().child("user").child(currentUserId).child("fname").setValue("CatBox");
                    FirebaseDatabase.getInstance().getReference().child("user").child(currentUserId).child("lname").setValue("User");
                    FirebaseDatabase.getInstance().getReference().child("user").child(currentUserId).child("status").setValue("No user Status");
                    FirebaseDatabase.getInstance().getReference().child("user").child(currentUserId).child("ppictur").setValue(null);

                    updateStatus("Deleted");
                    Toast.makeText(MenuLayout.this, "The account has Deleted", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}