package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatbox.fragmentprant.fragmentparent1;
import com.example.chatbox.fragmentprant.fragmentparent2;
import com.example.chatbox.fragmentprant.fragmentparent3;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private AppBarLayout mainlay1;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ImageButton menuButton;
    private RelativeLayout verification_layout,loadingLaout,mainLaout;
    private LinearLayout mainlay0;
    private TextView resendButton,reloadButton,logoutButton,chatBoxMessageText;
    public CircleImageView profilePicButton;
    public int ck = 1,ckk=1,ck1 = 1,delet=0;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase fDatabase;
    private DatabaseReference root;
    private String currentUserId;
    private ProgressDialog lodingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialAll();

        viewPager = findViewById(R.id.viewPagerId);
        tabLayout = findViewById(R.id.tabeLayoutId);

        verification_layout = findViewById(R.id.verificationLayought);
        resendButton = findViewById(R.id.reSendVerificationMail);
        reloadButton = findViewById(R.id.reloadId);
        logoutButton = findViewById(R.id.logoutId);
        menuButton = findViewById(R.id.menuId);
        chatBoxMessageText = findViewById(R.id.chatBoxMessageId);
        profilePicButton = findViewById(R.id.profilePicId);
        loadingLaout = findViewById(R.id.loadingLaoutId);

        mainLaout = findViewById(R.id.mainLaout);
        mainlay0 = findViewById(R.id.mainLaout0);
        mainlay1 = findViewById(R.id.mainLaout1);

        menuButton.setOnClickListener(this);
        resendButton.setOnClickListener(this);
        reloadButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
        profilePicButton.setOnClickListener(this);

        setFragment();
        OnStart();
    }

    private void setFragment() {
        tabLayout = findViewById(R.id.tabeLayoutId);
        viewPager = findViewById(R.id.viewPagerId);
        viewPageAdepter adepter = new viewPageAdepter(getSupportFragmentManager());
        adepter.setFragment(new fragmentparent1(),"Chat");
        adepter.setFragment(new fragmentparent2(),"Request");
        adepter.setFragment(new fragmentparent3(),"Friends");
        viewPager.setAdapter(adepter);
        tabLayout.setupWithViewPager(viewPager);
    }


    public void initialAll(){
        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        root = fDatabase.getReference();
        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)currentUserId = currentUser.getUid();
    }



    public void OnStart(){
        initialAll();
        if(currentUser==null){
            sendUserToLoginActivity();
            finish();
        }
        else{
            getDeviceToken();
            visibaleunvisibale();
            setdata();
            updateStatus("Active");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(currentUser!=null){
            //Toast.makeText(this, delet+" com st", Toast.LENGTH_SHORT).show();
            if(delet==0)updateStatus("Offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(currentUser!=null){
            //Toast.makeText(this, delet+" com de", Toast.LENGTH_SHORT).show();
            if(delet==0)updateStatus("Offline");
        }
    }

    public void getDeviceToken(){
            String deviceToken = FirebaseInstanceId.getInstance().getToken();
            FirebaseDatabase.getInstance().getReference().child("user").child(currentUserId).child("token").setValue(deviceToken);
        //Toast.makeText(this, deviceToken+"", Toast.LENGTH_SHORT).show();

    }

    public void updateStatus(String status){

        ckk = 1;
        //Toast.makeText(this, " comm "+status+"  "+ck, Toast.LENGTH_SHORT).show();
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

        FirebaseDatabase.getInstance().getReference().child("user").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(ckk==1){
                    //Toast.makeText(getApplicationContext(), ck, Toast.LENGTH_SHORT).show();
                    ckk = 0;
                    if(snapshot.exists()&&snapshot.hasChild("userstatus")){
                        if(snapshot.child("userstatus").child("status").getValue().toString().equals("Deleted")){
                            //Toast.makeText(getApplicationContext(), " true...", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            FirebaseDatabase.getInstance().getReference().child("user").child(currentUserId).child("userstatus").updateChildren(activeMap);
                        }
                    }
                    else{
                        FirebaseDatabase.getInstance().getReference().child("user").child(currentUserId).child("userstatus").updateChildren(activeMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.reSendVerificationMail){
           sentVerificationmail();
        }
        if(v.getId()==R.id.reloadId){
            reload();
        }
        if(v.getId()==R.id.logoutId){
            logout();
        }
        if(v.getId()==R.id.menuId){
           Intent intent = new Intent(getApplicationContext(),MenuLayout.class);
           startActivity(intent);
           finish();
        }
        if(v.getId()==R.id.profilePicId){
            Intent intent = new Intent(getApplicationContext(),Myprofile.class);
            startActivity(intent);
        }
    }

    public void logout(){
        new AlertDialog.Builder(this)
                .setTitle("Delete this account?")
                .setMessage("If you delete this account, it will no longer be possible to recover.\n\nDo you want to delete your account?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseDatabase.getInstance().getReference().child("user").child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    //Toast.makeText(MainActivity.this, "clear all", Toast.LENGTH_SHORT).show();
                                        currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                   // Toast.makeText(MainActivity.this, "Deeeeeeee", Toast.LENGTH_SHORT).show();
                                                    delet = 1;
                                                    sendUserToLoginActivity();
                                                }
                                            }
                                        });
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("No", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    public void reload(){
        ck = 1;
        FirebaseDatabase.getInstance().getReference().child("user").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()&&snapshot.hasChild("password")&&ck==1){
                    String ps = snapshot.child("password").getValue().toString();
                    String ml = currentUser.getEmail().toString();
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(ml,ps).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                ck=0;
                                OnStart();
                            }
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        /*Intent intent = new Intent(getApplicationContext(),reload.class);
        startActivity(intent);
        finish();*/
    }
    public void sendUserToLoginActivity(){
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public void sentVerificationmail(){
        currentUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"Verification mail has been send",Toast.LENGTH_SHORT).show();
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
    public void visibaleunvisibale(){
        initialAll();
         if(!currentUser.isEmailVerified()){
             Toast.makeText(getApplicationContext(),"Mail is not verified",Toast.LENGTH_SHORT).show();
             chatBoxMessageText.setText("We created a new account for you on ChatBox with the email address \""+currentUser.getEmail()+"\". We send a verification mail to the email address. please verify the email address. after verification you can sign in to your new account.");
             verification_layout.setVisibility(View.VISIBLE);
             viewPager.setVisibility(View.GONE);
             tabLayout.setVisibility(View.GONE);
             menuButton.setVisibility(View.GONE);
             mainlay0.setVisibility(View.GONE);
             mainlay1.setVisibility(View.GONE);
         }
         else{
                 verification_layout.setVisibility(View.GONE);
                 viewPager.setVisibility(View.VISIBLE);
                 tabLayout.setVisibility(View.VISIBLE);
                 menuButton.setVisibility(View.VISIBLE);
                 mainlay0.setVisibility(View.VISIBLE);
                 mainlay1.setVisibility(View.VISIBLE);
         }
    }

    public void setdata(){

        mainLaout.setVisibility(View.GONE);
        mainlay1.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        lodingbar = new ProgressDialog(this);
        lodingbar.setMessage("Loding..");
        lodingbar.setCanceledOnTouchOutside(false);
        lodingbar.show();
        initialAll();
        root.child("user").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()&& dataSnapshot.hasChild("gender") && dataSnapshot.child("gender").getValue().toString().equals("Male")){
                    profilePicButton.setImageDrawable(getResources().getDrawable(R.drawable.defoult_image_male));
                }
                if(dataSnapshot.exists()&& dataSnapshot.hasChild("gender") && dataSnapshot.child("gender").getValue().toString().equals("Female")){
                    profilePicButton.setImageDrawable(getResources().getDrawable(R.drawable.defoult_image_female));
                }
                if(dataSnapshot.exists()&& dataSnapshot.hasChild("gender") && dataSnapshot.child("gender").getValue().toString().equals("Common")){
                    profilePicButton.setImageDrawable(getResources().getDrawable(R.drawable.defoult_image_common));
                }
                if((dataSnapshot.exists())&&(dataSnapshot.hasChild("ppictur"))){
                    String imageurl = dataSnapshot.child("ppictur").getValue().toString();
                    Picasso.get().load(imageurl).into(profilePicButton);
                }
                lodingbar.dismiss();
                mainLaout.setVisibility(View.VISIBLE);
                mainlay1.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}