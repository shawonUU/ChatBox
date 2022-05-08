package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class reload extends AppCompatActivity {

    private ProgressDialog lodingbar;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase fDatabase;
    private DatabaseReference root;
    private String currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reload);
        lodingbar = new ProgressDialog(this);
        initialAll();
        reload();
    }
    public void initialAll(){
        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        root = fDatabase.getReference();
        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)currentUserId = currentUser.getUid();
    }
    public void reload(){
        lodingbar.setTitle("Reloading");
        lodingbar.setMessage("Please wait..");
        lodingbar.setCanceledOnTouchOutside(true);
        lodingbar.show();
        root.child("user").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists())&&(dataSnapshot.hasChild("password"))){
                    String mail,pss;
                    pss = dataSnapshot.child("password").getValue().toString();
                    mail = currentUser.getEmail().toString();
                    mAuth.signInWithEmailAndPassword(mail,pss).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            lodingbar.dismiss();
                            if(task.isSuccessful()){
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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}