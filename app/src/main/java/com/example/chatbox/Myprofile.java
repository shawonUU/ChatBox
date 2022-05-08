package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class Myprofile extends AppCompatActivity implements View.OnClickListener {
    private ImageButton backButton;
    private ImageView addProfilepicButton,editUserNameButton,editStatusButton,editBirthdayButton,editgenderButton;
    private TextView userNametxtView,statusTextView,birthDayView,genderView,seeMoreLasetxt;
    private LinearLayout seeMoreButton,extraviewButton,lodingLaout,mainLaout;
    private CircleImageView profilepicView;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase fDatabase;
    private DatabaseReference root;
    private String currentUserId;
    private ProgressDialog lodingbar;
    private StorageReference userProfilePic;
    private FirebaseStorage firebaseStorage;
    private String layoutName;
    private int glaryInt = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);
        initialAll();
        backButton = findViewById(R.id.backButtonId);
        profilepicView = findViewById(R.id.profilePicId);
        addProfilepicButton = findViewById(R.id.addProfilepicId);
        editUserNameButton = findViewById(R.id.editUserNameId);
        editStatusButton = findViewById(R.id.editStatusId);
        userNametxtView = findViewById(R.id.userNameId);
        statusTextView = findViewById(R.id.statusId);
        seeMoreButton = findViewById(R.id.seeMoreId);
        birthDayView = findViewById(R.id.birthdayViewId);
        genderView = findViewById(R.id.genderViewId);
        editBirthdayButton = findViewById(R.id.editBirthdayId);
        editgenderButton = findViewById(R.id.editGenderId);
        extraviewButton = findViewById(R.id.extraLayout);
        seeMoreLasetxt = findViewById(R.id.seeMoreLaseId);
        lodingLaout = findViewById(R.id.lodingLayoutId);
        mainLaout = findViewById(R.id.mainLayoutId);

        addProfilepicButton.setOnClickListener(this);
        editUserNameButton.setOnClickListener(this);
        editStatusButton.setOnClickListener(this);
        seeMoreButton.setOnClickListener(this);
        editBirthdayButton.setOnClickListener(this);
        editgenderButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        setProfileData();

    }
    public void initialAll(){
        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        root = fDatabase.getReference();
        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)currentUserId = currentUser.getUid();
        firebaseStorage = FirebaseStorage.getInstance();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==glaryInt && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            //Toast.makeText(getApplicationContext(),"sob ok",Toast.LENGTH_SHORT).show();
            Uri imagUri = data.getData();
            /*CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);*/


            lodingbar = new ProgressDialog(this);
            lodingbar.setTitle("Uploading Profile Image");
            lodingbar.setMessage("Please wait..");
            lodingbar.setCanceledOnTouchOutside(false);
            lodingbar.show();
            userProfilePic = firebaseStorage.getReference().child("ppictur");

            StorageReference filePath = userProfilePic.child(currentUserId+".jpg");
            filePath.putFile(imagUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    Uri dnUri = uriTask.getResult();

                    String downloadUrl = dnUri.toString();
                    root.child("user").child(currentUserId).child("ppictur").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            lodingbar.dismiss();
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Picture uploaded successfully",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Picture is not uploaded",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Picture is not uploaded", Toast.LENGTH_SHORT).show();
                }
            });
        }
       /* if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            Toast.makeText(getApplicationContext(),"crop ok",Toast.LENGTH_SHORT).show();
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                initialAll();
                Uri resultUri = result.getUri();
                Toast.makeText(getApplicationContext(),"crop image peyechi",Toast.LENGTH_SHORT).show();
                lodingbar = new ProgressDialog(this);
                lodingbar.setTitle("Uploading Profile Image");
                lodingbar.setMessage("Please wait..");
                lodingbar.setCanceledOnTouchOutside(false);
                lodingbar.show();
                userProfilePic = firebaseStorage.getReference().child("ppictur");

                StorageReference filePath = userProfilePic.child(currentUserId+".jpg");
                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri dnUri = uriTask.getResult();

                       String downloadUrl = dnUri.toString();
                        root.child("user").child(currentUserId).child("ppictur").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                lodingbar.dismiss();
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),"Picture uploaded successfully",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"Picture is not uploaded",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(getApplicationContext(), "Picture is not uploaded", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.backButtonId){
            onBackPressed();
        }
        if(v.getId()==R.id.addProfilepicId){
            setProfilePic();
        }
        if(v.getId()==R.id.editUserNameId){
            callToUpdate("name");
        }
        if(v.getId()==R.id.editStatusId){
            callToUpdate("status");
        }
        if(v.getId()==R.id.editBirthdayId){
            callToUpdate("birthday");
        }
        if(v.getId()==R.id.editGenderId){
            callToUpdate("gender");
        }
        if(v.getId()==R.id.seeMoreId){
           if(extraviewButton.getVisibility()==View.VISIBLE){
               extraviewButton.setVisibility(View.GONE);
               seeMoreLasetxt.setText("See More");
           }
            else{
                extraviewButton.setVisibility(View.VISIBLE);
                seeMoreLasetxt.setText("See Less");
            }
        }
    }
    public void callToUpdate(String s){
        Intent intent = new Intent(getApplicationContext(),updateInfo.class);
        intent.putExtra("tag",s);
        startActivity(intent);
    }
    public void setProfileData(){


        lodingLaout.setVisibility(View.VISIBLE);
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
                        userNametxtView.setText(ful);
                    }
                    if((dataSnapshot.exists())&&(dataSnapshot.hasChild("status"))){
                       String sts = dataSnapshot.child("status").getValue().toString();
                        statusTextView.setText(sts);
                    }
                    else{
                        statusTextView.setText("Upload your status");
                    }
                    if((dataSnapshot.exists())&&(dataSnapshot.hasChild("gender"))){
                       String gnd = "Gender: "+dataSnapshot.child("gender").getValue().toString();
                        genderView.setText(gnd);
                    }
                    if((dataSnapshot.exists())&&(dataSnapshot.hasChild("birthday"))){
                        String bdy = "Dete of birth: "+dataSnapshot.child("birthday").getValue().toString();
                        birthDayView.setText(bdy);
                    }
                    if(dataSnapshot.exists()&& dataSnapshot.hasChild("gender") && dataSnapshot.child("gender").getValue().toString().equals("Male")){
                        profilepicView.setImageDrawable(getResources().getDrawable(R.drawable.defoult_image_male));
                    }
                    if(dataSnapshot.exists()&& dataSnapshot.hasChild("gender") && dataSnapshot.child("gender").getValue().toString().equals("Female")){
                        profilepicView.setImageDrawable(getResources().getDrawable(R.drawable.defoult_image_female));
                    }
                    if(dataSnapshot.exists()&& dataSnapshot.hasChild("gender") && dataSnapshot.child("gender").getValue().toString().equals("Common")){
                        profilepicView.setImageDrawable(getResources().getDrawable(R.drawable.defoult_image_common));
                    }
                    if((dataSnapshot.exists())&&(dataSnapshot.hasChild("ppictur"))){
                        String imageurl = dataSnapshot.child("ppictur").getValue().toString();
                        Picasso.get().load(imageurl).into(profilepicView);
                    }

                    lodingbar.dismiss();
                    lodingLaout.setVisibility(View.GONE);
                    mainLaout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }
    public void setProfilePic(){
        //Toast.makeText(getApplicationContext(),"gallery open ok",Toast.LENGTH_SHORT).show();
        Intent gallery = new Intent();
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery,glaryInt);
    }
}




/**

 <androidx.cardview.widget.CardView
 android:layout_height="60dp"
 android:layout_width="60dp"
 app:cardCornerRadius="30dp">
 <ImageView
 android:id="@+id/profilePicId"
 android:layout_width="match_parent"
 android:layout_height="match_parent"
 android:src="@drawable/defoult_image_male"/>
 </androidx.cardview.widget.CardView>

 **/