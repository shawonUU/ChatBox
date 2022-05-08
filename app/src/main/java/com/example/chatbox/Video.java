package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class Video extends AppCompatActivity implements View.OnClickListener {

    private VideoView videoView;
    private ImageButton backButton,downloadButton,deleteButton,detailsButton;
    private LinearLayout imageLayout,videoLayout,audioLayout;
    private ImageView audioPlay,image;
    private  Uri uri;
    private Messages messages;
    String sender,receiver,id;
    public MediaPlayer mMediaplayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            sender = bundle.getString("sender");
            receiver = bundle.getString("receiver");
            id = bundle.getString("id");
        }
       // Toast.makeText(getApplicationContext(),sender+" "+receiver+" "+id, Toast.LENGTH_LONG).show();



        backButton = findViewById(R.id.backButtonId);
        videoView = (VideoView) findViewById(R.id.videoViewId);
        imageLayout = findViewById(R.id.imageLayoutId);
        videoLayout = findViewById(R.id.videoLayoutId);
        audioLayout = findViewById(R.id.audioLayoutId);
        audioPlay = findViewById(R.id.audioPlayId);
        image = findViewById(R.id.imageView);

        downloadButton = findViewById(R.id.downloadButtonId);
        deleteButton = findViewById(R.id.deleteButtonId);
        detailsButton = findViewById(R.id.detailsButtonId);

        backButton.setOnClickListener(this);
        downloadButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        detailsButton.setOnClickListener(this);

        messages = new Messages();
        getdata();
        audioPlay.setImageDrawable(getResources().getDrawable(R.drawable.play_icon));
        audioPlay.setOnClickListener(this);
    }

    public void getdata(){
        FirebaseDatabase.getInstance().getReference().child("message").child(sender).child(receiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()&&snapshot.hasChild(id)){
                    messages = snapshot.child(id).getValue(Messages.class);
                    setData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setData(){

        if(messages.getTyp().equals("video")){
            videoLayout.setVisibility(View.VISIBLE);
            playVideo(messages.getMessage());
        }
        if(messages.getTyp().equals("audio")){
            audioLayout.setVisibility(View.VISIBLE);
            mMediaplayer = new MediaPlayer();
            mMediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try { mMediaplayer.setDataSource(messages.getMessage()); } catch (IOException e) { }
            mMediaplayer.prepareAsync();
        }
        if(messages.getTyp().equals("image")){
            imageLayout.setVisibility(View.VISIBLE);
            Picasso.get().load(messages.getMessage()).into(image);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
       if(messages.getTyp().equals("audio")){
           if(mMediaplayer.isPlaying()){
               while(mMediaplayer.isPlaying()){ mMediaplayer.pause(); }
               audioPlay.setImageDrawable(getResources().getDrawable(R.drawable.play_icon));
           }
       }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.backButtonId){
            OnBackPressed();
        }
        if(v.getId()==R.id.audioPlayId){
            audioPlayer();
        }
        if(v.getId()==R.id.downloadButtonId){
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(messages.getMessage()));
            startActivity(intent);
        }
        if(v.getId()==R.id.deleteButtonId){
            FirebaseDatabase.getInstance().getReference().child("message").child(sender).child(receiver).child(messages.getMid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        FirebaseDatabase.getInstance().getReference().child("message").child(receiver).child(sender).child(messages.getMid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    OnBackPressed();
                                }
                            }
                        });
                    }
                }
            });
        }
        if(v.getId()==R.id.detailsButtonId){
            AlertDialog.Builder builder = new AlertDialog.Builder(Video.this);
            builder.setTitle("File Details..");
            builder.setMessage("Type : "+messages.getTyp()+"\nSent Time : "+messages.getDatetime()+"\nStatus : "+messages.getStatus());
            builder.show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        OnBackPressed();
    }

    public void OnBackPressed(){
        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra("tag",receiver);
        startActivity(intent);
        finish();
    }

    public void audioPlayer(){
        if(mMediaplayer.isPlaying()){
            while(mMediaplayer.isPlaying()){ mMediaplayer.pause(); }
            audioPlay.setImageDrawable(getResources().getDrawable(R.drawable.play_icon));
        }
        else{
            while(!mMediaplayer.isPlaying()){mMediaplayer.start();}
            audioPlay.setImageDrawable(getResources().getDrawable(R.drawable.paush_icon));
        }
    }
    public void playVideo(String uriString){
        //Uri ur = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.shawon);
        //String str = "https://firebasestorage.googleapis.com/v0/b/lol-videos-8dc74.appspot.com/o/Blog_Images%2Fvideo%3A10142?alt=media&token=9f7734fa-f714-4838-bd65-8a4d594ec2ce";

        Uri uri2 = Uri.parse(uriString);
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri2);
        videoView.requestFocus();
        videoView.start();

        ProgressDialog progDailog = ProgressDialog.show(this, "Please wait ...", "Retrieving data ...", true);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        if(percent >=5){
                            //video have completed buffering
                            videoView.start();
                            progDailog.dismiss();
                        }
                    }
                });
            }
        });
    }
}