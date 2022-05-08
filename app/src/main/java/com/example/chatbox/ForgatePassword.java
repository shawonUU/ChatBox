package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgatePassword extends AppCompatActivity implements View.OnClickListener {

    private ImageButton backButton;
    private EditText email;
    private TextView resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgate_password);

        backButton = findViewById(R.id.backButtonId);
        email = findViewById(R.id.EmailId);
        resetButton = findViewById(R.id.resetButtonId);

        backButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
    }

   @Override
    public void onClick(View v) {
        if(v.getId()==R.id.backButtonId){
            onBackPressed();
        }
        if(v.getId()==R.id.resetButtonId){
            String mal = email.getText().toString().trim();
            FirebaseAuth.getInstance().sendPasswordResetEmail(mal)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(),"Check you mail",Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
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
}