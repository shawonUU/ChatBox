package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText firstNameView, lastNameView, statusView, dethBirtdView;
    private RadioGroup genderView;
    private RadioButton maleButton,femaleButton,commonButton,genarateRadiButton;
    private TextView updateButton;
    private ImageButton backButton;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase fDatabase;
    private DatabaseReference root;
    private String currentUserId;
    private ProgressDialog lodingbar;

    String Fname,Lname,Birth,genDar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        backButton = findViewById(R.id.backButtonId);
        firstNameView = findViewById(R.id.firstNameId);
        statusView = findViewById(R.id.statusId);
        lastNameView = findViewById(R.id.lastNameId);
        dethBirtdView = findViewById(R.id.dateBirthId);
        genderView = findViewById(R.id.radioGroupId);
        maleButton = findViewById(R.id.maleId);
        femaleButton = findViewById(R.id.femaleId);
        commonButton = findViewById(R.id.commonId);
        updateButton = findViewById(R.id.updateButtonId);

        initialAll();
        getAndSet();

        updateButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
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
        if(v.getId()==R.id.updateButtonId){
            updteInfo();
        }
        if(v.getId()==R.id.backButtonId){
            onBackPressed();
        }
    }

    private void getAndSet() {
        FirebaseDatabase.getInstance().getReference().child("user").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()&&snapshot.hasChild("fname")){
                    firstNameView.setText(snapshot.child("fname").getValue().toString());
                }
                if(snapshot.exists()&&snapshot.hasChild("lname")){
                    lastNameView.setText(snapshot.child("lname").getValue().toString());
                }
                if(snapshot.exists()&&snapshot.hasChild("status")){
                    statusView.setText(snapshot.child("status").getValue().toString());
                }
                else{
                    statusView.setText("No User Status");
                }
                if(snapshot.exists()&&snapshot.hasChild("birthday")){
                    dethBirtdView.setText(convertDate(snapshot.child("birthday").getValue().toString()));
                }
                if(snapshot.exists()&&snapshot.hasChild("gender")){
                    String gnd = snapshot.child("gender").getValue().toString();
                    if(gnd.equals("Male")){genderView.check(R.id.maleId);}
                    if(gnd.equals("Female")){genderView.check(R.id.femaleId);}
                    if(gnd.equals("Common")){genderView.check(R.id.commonId);}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updteInfo() {
        int checkAll=1;
        Fname = firstNameView.getText().toString().trim();
        Lname = lastNameView.getText().toString().trim();
        Birth = dethBirtdView.getText().toString().trim();

        if(Fname.equals("")){
            checkAll = 0;
            Toast.makeText(getApplicationContext(),"Enter your first name",Toast.LENGTH_SHORT).show();
            firstNameView.setError("Enter your first name");
            firstNameView.requestFocus();
            return;
        }
        if(Lname.equals("")){
            checkAll = 0;
            Toast.makeText(getApplicationContext(),"Enter your last name",Toast.LENGTH_SHORT).show();
            lastNameView.setError("Enter your last name");
            lastNameView.requestFocus();
            return;
        }
        if(Birth.equals("")){
            checkAll = 0;
            Toast.makeText(getApplicationContext(),"Enter your Birthday",Toast.LENGTH_SHORT).show();
            dethBirtdView.setError("Enter your Birthday");
            dethBirtdView.requestFocus();
            return;
        }
        else{
            Birth = checkBirthdayValidity(Birth);
            if(Birth.equals("error")){
                checkAll = 0;
                Toast.makeText(getApplicationContext(),"The Birthday is not valid",Toast.LENGTH_SHORT).show();
                dethBirtdView.setError("Enter your valid Birthday");
                dethBirtdView.requestFocus();
                return;
            }
        }
        int selectedId = genderView.getCheckedRadioButtonId();
        if(selectedId==-1){
            checkAll = 0;
            Toast.makeText(getApplicationContext(),"Select your gender",Toast.LENGTH_SHORT).show();
            genderView.requestFocus();
            return;
        }
        else{
            genarateRadiButton = (RadioButton) findViewById(selectedId);
            genDar = genarateRadiButton.getText().toString();
        }
        if(checkAll==1){
            lodingbar = new ProgressDialog(this);
            lodingbar.setTitle("Updating your profile info");
            lodingbar.setMessage("Please wait..");
            lodingbar.setCanceledOnTouchOutside(true);
            lodingbar.show();
            setProfileInfo();

        }
    }

    private void setProfileInfo() {
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("user").child(currentUserId);
        dref.child("fname").setValue(Fname).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    dref.child("lname").setValue(Lname).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                dref.child("flname").setValue(Fname+" "+Lname).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            dref.child("birthday").setValue(Birth).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        dref.child("gender").setValue(genDar).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    lodingbar.dismiss();
                                                                    Toast.makeText(getApplicationContext(),"Data is uploaded Successfully!",Toast.LENGTH_SHORT).show();
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
                    });
                }
            }
        });
    }

    public String convertDate(String d){
        String mnt ,nmb="";
       String ar[];
       ar = d.split(" ");
       mnt = ar[1];
       nmb += ar[0]+"-";
        if(mnt.equals("January,")) nmb += "1-";
        if(mnt.equals("February,")) nmb += "2-";
        if(mnt.equals("March,")) nmb += "3-";
        if(mnt.equals("April,")) nmb += "4-";
        if(mnt.equals("May,")) nmb += "5-";
        if(mnt.equals("June,")) nmb += "6-";
        if(mnt.equals("July,")) nmb += "7-";
        if(mnt.equals("August,")) nmb += "8-";
        if(mnt.equals("September,")) nmb += "9-";
        if(mnt.equals("October,")) nmb += "10-";
        if(mnt.equals("November,")) nmb += "11-";
        if(mnt.equals("December,")) nmb += "12-";
        nmb += ar[2];
        return nmb;
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