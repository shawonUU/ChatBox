package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
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

public class ViewProfile extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout loadingLayout,seeMoreButton,extraView,buttonAdd,buttonCencel;
    private ImageButton backButton,chatButton;
    private CircleImageView profilePicVied;
    private TextView userName,status,birthdayView,genderView,addButton,cencelbutton;
    private String receiverId="",relation_status="new";
    int cker = 0,ck=1;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase fDatabase;
    private DatabaseReference root,chatRequestRf,contractRef;
    private String currentUserId,Name="";
    private ProgressDialog lodingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){ receiverId = bundle.getString("tag");}
        initialAll();
         chatRequestRf = FirebaseDatabase.getInstance().getReference().child("request");
        contractRef = FirebaseDatabase.getInstance().getReference().child("contract");
        checkup();
        loadingLayout = findViewById(R.id.loadingLaoutId);
        buttonAdd = findViewById(R.id.buttonAbbId);
        //buttonCencel = findViewById(R.id.buttonCancelId);
        seeMoreButton = findViewById(R.id.seeMoreId);
        extraView = findViewById(R.id.extraLayout);
        chatButton = findViewById(R.id.chatButtonId);
        backButton = findViewById(R.id.backButtonId);
        profilePicVied = findViewById(R.id.profilePicId);
        userName = findViewById(R.id.userNameId);
        status = findViewById(R.id.statusId);
        genderView = findViewById(R.id.genderViewId);
        birthdayView = findViewById(R.id.birthdayViewId);
        addButton = findViewById(R.id.addFriendId);
        cencelbutton = findViewById(R.id.removeFriendId);

        backButton.setOnClickListener(this);
        seeMoreButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
        cencelbutton.setOnClickListener(this);
        chatButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        manageReguest();
        getData(receiverId);
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
        if(v.getId()==R.id.chatButtonId){
            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            intent.putExtra("tag",receiverId);
            startActivity(intent);
        }
        if(v.getId()==R.id.seeMoreId){
            if(extraView.getVisibility()==View.VISIBLE){extraView.setVisibility(View.GONE);}
            else extraView.setVisibility(View.VISIBLE);
        }
        if(v.getId()==R.id.addFriendId){AddButton();}
        if(v.getId()==R.id.removeFriendId){Cencelbutton();}
    }
    public void getData(String id){

        //loadingLayout.setVisibility(View.VISIBLE);
        lodingbar = new ProgressDialog(this);

        lodingbar.setMessage("Loding..");
        lodingbar.setCanceledOnTouchOutside(false);
        lodingbar.show();

        initialAll();
        root.child("user").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists())&&(dataSnapshot.hasChild("fname"))&&(dataSnapshot.hasChild("lname"))){
                    String fn = dataSnapshot.child("fname").getValue().toString();
                    String ln = dataSnapshot.child("lname").getValue().toString();
                    String ful = fn+" "+ln;
                    Name = ful;
                    userName.setText(ful);
                }
                if((dataSnapshot.exists())&&(dataSnapshot.hasChild("status"))){
                    String sts = dataSnapshot.child("status").getValue().toString();
                    status.setText(sts);
                }
                else{
                    status.setText("No user status");
                }
                if((dataSnapshot.exists())&&(dataSnapshot.hasChild("gender"))){
                    String gnd = "Gender: "+dataSnapshot.child("gender").getValue().toString();
                    genderView.setText(gnd);
                }
                if((dataSnapshot.exists())&&(dataSnapshot.hasChild("birthday"))){
                    String bdy = "Dete of birth: "+dataSnapshot.child("birthday").getValue().toString();
                    birthdayView.setText(bdy);
                }
                if(dataSnapshot.exists()&& dataSnapshot.hasChild("gender") && dataSnapshot.child("gender").getValue().toString().equals("Male")){
                    profilePicVied.setImageDrawable(getResources().getDrawable(R.drawable.defoult_image_male));
                }
                if(dataSnapshot.exists()&& dataSnapshot.hasChild("gender") && dataSnapshot.child("gender").getValue().toString().equals("Female")){
                    profilePicVied.setImageDrawable(getResources().getDrawable(R.drawable.defoult_image_female));
                }
                if(dataSnapshot.exists()&& dataSnapshot.hasChild("gender") && dataSnapshot.child("gender").getValue().toString().equals("Common")){
                    profilePicVied.setImageDrawable(getResources().getDrawable(R.drawable.defoult_image_common));
                }
                if((dataSnapshot.exists())&&(dataSnapshot.hasChild("ppictur"))){
                    String imageurl = dataSnapshot.child("ppictur").getValue().toString();
                    Picasso.get().load(imageurl).into(profilePicVied);
                }

                lodingbar.dismiss();
                loadingLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkup(){

    }

    public void manageReguest(){
        initialAll();
        ck = 1;

        FirebaseDatabase.getInstance().getReference().child("user").child(receiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()&&snapshot.hasChild("userstatus")){
                    if(snapshot.child("userstatus").hasChild("status")){
                        String ss = snapshot.child("userstatus").child("status").getValue().toString();
                        if(ss.equals("Deleted")){
                            addButton.setVisibility(View.GONE);
                            cencelbutton.setVisibility(View.VISIBLE);
                            cencelbutton.setText("The account has Deactivated");
                            ck = 0;
                        }
                        else{


                            //////



                            if(!currentUserId.equals(receiverId)){
                                chatRequestRf.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.hasChild(currentUserId)){
                                            if(snapshot.child(currentUserId).hasChild(receiverId)){
                                                relation_status = snapshot.child(currentUserId).child(receiverId).child("request_typ").getValue().toString();
                                                if(relation_status.equals("sent")){
                                                    addButton.setVisibility(View.VISIBLE);
                                                    cencelbutton.setVisibility(View.GONE);
                                                    addButton.setText("Cancel Request");
                                                }
                                                else if(relation_status.equals("received")){
                                                    addButton.setVisibility(View.VISIBLE);
                                                    cencelbutton.setVisibility(View.VISIBLE);
                                                    addButton.setText("Accept");
                                                    cencelbutton.setText("Delete");
                                                }
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                contractRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.hasChild(currentUserId)){
                                            if(snapshot.child(currentUserId).hasChild(receiverId)){
                                                relation_status = snapshot.child(currentUserId).child(receiverId).child("relation").getValue().toString();
                                                if(relation_status.equals("friend")){
                                                    addButton.setVisibility(View.VISIBLE);
                                                    cencelbutton.setVisibility(View.GONE);
                                                    chatButton.setVisibility(View.VISIBLE);
                                                    addButton.setText("Unfriend");
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                if(relation_status.equals("new")){
                                    //Toast.makeText(getApplicationContext(),relation_status,Toast.LENGTH_LONG).show();
                                    addButton.setVisibility(View.VISIBLE);
                                    cencelbutton.setVisibility(View.GONE);
                                    addButton.setText("Add Friend");
                                }

                            }
                            else{ buttonAdd.setVisibility(View.GONE); }


                            /////

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(ck==1){ }
    }


    public void AddButton(){
        String btnTx = addButton.getText().toString();
        if(btnTx.equals("Add Friend")){
            chatRequestRf.child(currentUserId).child(receiverId).child("request_typ").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        chatRequestRf.child(receiverId).child(currentUserId).child("request_typ").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    addButton.setText("Cancel Request");
                                    String curentDate, currentTime;
                                    Calendar calendar = Calendar.getInstance();
                                    SimpleDateFormat currntDat = new SimpleDateFormat("MMM dd, yyy");
                                    curentDate = currntDat.format(calendar.getTime());

                                    SimpleDateFormat currntTim = new SimpleDateFormat("hh:mm a");
                                    currentTime = currntTim.format(calendar.getTime());
                                    HashMap<String,String> notificationMap = new HashMap<>();
                                    notificationMap.put("from",currentUserId);
                                    notificationMap.put("type","request");
                                    notificationMap.put("time",currentTime);
                                    notificationMap.put("date",curentDate);
                                    FirebaseDatabase.getInstance().getReference().child("notification").child(receiverId).push().setValue(notificationMap);
                                }
                            }
                        });
                    }
                }
            });
        }
        if(btnTx.equals("Cancel Request")){
            chatRequestRf.child(currentUserId).child(receiverId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        chatRequestRf.child(receiverId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    addButton.setText("Add Friend");
                                }
                            }
                        });
                    }
                }
            });
        }
        if(btnTx.equals("Accept")){
            contractRef.child(currentUserId).child(receiverId).child("relation").setValue("friend").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        contractRef.child(receiverId).child(currentUserId).child("relation").setValue("friend").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    chatRequestRf.child(currentUserId).child(receiverId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                chatRequestRf.child(receiverId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            addButton.setText("Unfriend");
                                                            cencelbutton.setVisibility(View.GONE);
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
            });
        }
        if(btnTx.equals("Unfriend")){
            contractRef.child(currentUserId).child(receiverId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        contractRef.child(receiverId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    addButton.setText("Add Friend");
                                    chatButton.setVisibility(View.GONE);
                                    removeFromChatlist();
                                }
                            }
                        });
                    }
                }
            });
        }

    }

    public void Cencelbutton(){
        String btnTx = cencelbutton.getText().toString();
        if(btnTx.equals("Delete")){
            chatRequestRf.child(currentUserId).child(receiverId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        chatRequestRf.child(receiverId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    addButton.setText("Add Friend");
                                    cencelbutton.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void removeFromChatlist() {
        cker = 0;
        FirebaseDatabase.getInstance().getReference().child("userchat").child(currentUserId).child(receiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()&&snapshot.hasChild("id")){
                    String catId = snapshot.child("id").getValue().toString();
                    FirebaseDatabase.getInstance().getReference().child("chatuser").child(currentUserId).child(catId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                cker++;
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseDatabase.getInstance().getReference().child("userchat").child(receiverId).child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()&&snapshot.hasChild("id")){
                    String catId = snapshot.child("id").getValue().toString();
                    FirebaseDatabase.getInstance().getReference().child("chatuser").child(receiverId).child(catId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                cker++;
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}