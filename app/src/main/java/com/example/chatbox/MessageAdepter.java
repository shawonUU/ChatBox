package com.example.chatbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Message;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Slide;
import de.hdodenhof.circleimageview.CircleImageView;



import static com.example.chatbox.R.drawable.defoult_image_common;

public class MessageAdepter extends RecyclerView.Adapter<MessageAdepter.MessageViewHolder> {
    private List<Messages> userMessageList;
    private String reciverId;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase fDatabase;
    private DatabaseReference root;
    private String currentUserId;
    private ProgressDialog lodingbar;
    private Context context;
    public MediaPlayer mMediaplayer;

    String mdt,msts;

    public MessageAdepter(List<Messages> userMessageList,String reciverId,android.content.Context context){
        this.userMessageList = userMessageList;
        this.reciverId = reciverId;
        this.context = context;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        /// text part
        public TextView senderMessageText,receiverMessageText;
        public CircleImageView receiverProfileImage;
        public LinearLayout gravityLayout,senderLayout,receiverLayout;
        private RelativeLayout textMeaagaeLayout;
        public TextView tsub,tsdb,trub,trdb;
        ///end of text part

        /// image part
        public CircleImageView imageReceiverProfileImage;
        public LinearLayout imageGravityLayout,imageReceiverLayout,imageSenderLayout;
        private RelativeLayout imageMessageLayout;
        public TextView imagetsub,imagetsdb,imagetrub,imagetrdb;
        private ImageView senderMessageImage,receiverMessageImage;
        /// end image part

        /// video part
        public VideoView video_senderMessageText,video_receiverMessageText;
        public CircleImageView video_receiverProfileImage;
        public LinearLayout video_gravityLayout,video_senderLayout,video_receiverLayout;
        private RelativeLayout video_textMeaagaeLayout;
        public TextView video_tsub,video_tsdb,video_trub,video_trdb;
        ///end of video part


        /// audio part
        public TextView audio_senderMessageText,audio_receiverMessageText;
        public CircleImageView audio_receiverProfileImage;
        public LinearLayout audio_gravityLayout,audio_senderLayout,audio_receiverLayout;
        private RelativeLayout audio_textMeaagaeLayout;
        public TextView audio_tsub,audio_tsdb,audio_trub,audio_trdb;
        ///audio of text part


        /// file part
        public ImageView file_senderMessageText,file_receiverMessageText;
        public CircleImageView file_receiverProfileImage;
        public LinearLayout file_gravityLayout,file_senderLayout,file_receiverLayout;
        private RelativeLayout file_textMeaagaeLayout;
        public TextView file_tsub,file_tsdb,file_trub,file_trdb;
        ///end of file part

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            ///for text part
            senderMessageText = (TextView)itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = (TextView)itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage = (CircleImageView)itemView.findViewById(R.id.message_profile_image);
            gravityLayout = (LinearLayout) itemView.findViewById(R.id.gravity_layoutId);
            senderLayout = (LinearLayout) itemView.findViewById(R.id.sender_layoutId);
            receiverLayout = (LinearLayout) itemView.findViewById(R.id.receiver_layoutId);
            textMeaagaeLayout = (RelativeLayout) itemView.findViewById(R.id.textMessageLayoutId);

            tsub = (TextView) itemView.findViewById(R.id.senderDateShowId);
            tsdb = (TextView) itemView.findViewById(R.id.senderStatusShowId);

            trub = (TextView) itemView.findViewById(R.id.reciverDateShowId);
            trdb = (TextView) itemView.findViewById(R.id.reciverStatusShowId);
            // end text part

            ///for image part
            senderMessageImage = (ImageView) itemView.findViewById(R.id.image_sender_message_text);
            receiverMessageImage = (ImageView) itemView.findViewById(R.id.image_receiver_message_text);
            imageReceiverProfileImage = (CircleImageView)itemView.findViewById(R.id.image_message_profile_image);
            imageGravityLayout = (LinearLayout) itemView.findViewById(R.id.image_gravity_layoutId);
            imageSenderLayout = (LinearLayout) itemView.findViewById(R.id.image_sender_layoutId);
            imageReceiverLayout = (LinearLayout) itemView.findViewById(R.id.image_receiver_layoutId);
            imageMessageLayout = (RelativeLayout) itemView.findViewById(R.id.imageMessageLayoutId);

            imagetsub = (TextView) itemView.findViewById(R.id.image_senderDateShowId);
            imagetsdb = (TextView) itemView.findViewById(R.id.image_senderStatusShowId);

            imagetrub = (TextView) itemView.findViewById(R.id.image_reciverDateShowId);
            imagetrdb = (TextView) itemView.findViewById(R.id.image_reciverStatusShowId);
            // end image part


            ///for video part
            video_senderMessageText = (VideoView) itemView.findViewById(R.id.video_sender_message_text);
            video_receiverMessageText = (VideoView) itemView.findViewById(R.id.video_receiver_message_text);
            video_receiverProfileImage = (CircleImageView)itemView.findViewById(R.id.video_message_profile_image);
            video_gravityLayout = (LinearLayout) itemView.findViewById(R.id.video_gravity_layoutId);
            video_senderLayout = (LinearLayout) itemView.findViewById(R.id.video_sender_layoutId);
            video_receiverLayout = (LinearLayout) itemView.findViewById(R.id.video_receiver_layoutId);
            video_textMeaagaeLayout = (RelativeLayout) itemView.findViewById(R.id.video_textMessageLayoutId);

            video_tsub = (TextView) itemView.findViewById(R.id.video_senderDateShowId);
            video_tsdb = (TextView) itemView.findViewById(R.id.video_senderStatusShowId);

            video_trub = (TextView) itemView.findViewById(R.id.video_reciverDateShowId);
            video_trdb = (TextView) itemView.findViewById(R.id.video_reciverStatusShowId);
            // end video part


            ///for audio part
            audio_senderMessageText = (TextView)itemView.findViewById(R.id.audio_sender_message_text);
            audio_receiverMessageText = (TextView)itemView.findViewById(R.id.audio_receiver_message_text);
            audio_receiverProfileImage = (CircleImageView)itemView.findViewById(R.id.audio_message_profile_image);
            audio_gravityLayout = (LinearLayout) itemView.findViewById(R.id.audio_gravity_layoutId);
            audio_senderLayout = (LinearLayout) itemView.findViewById(R.id.audio_sender_layoutId);
            audio_receiverLayout = (LinearLayout) itemView.findViewById(R.id.audio_receiver_layoutId);
            audio_textMeaagaeLayout = (RelativeLayout) itemView.findViewById(R.id.audio_textMessageLayoutId);

            audio_tsub = (TextView) itemView.findViewById(R.id.audio_senderDateShowId);
            audio_tsdb = (TextView) itemView.findViewById(R.id.audio_senderStatusShowId);

            audio_trub = (TextView) itemView.findViewById(R.id.audio_reciverDateShowId);
            audio_trdb = (TextView) itemView.findViewById(R.id.audio_reciverStatusShowId);
            // end audio part



            ///for file part
            file_senderMessageText = (ImageView) itemView.findViewById(R.id.file_sender_message_text);
            file_receiverMessageText = (ImageView) itemView.findViewById(R.id.file_receiver_message_text);
            file_receiverProfileImage = (CircleImageView)itemView.findViewById(R.id.file_message_profile_image);
            file_gravityLayout = (LinearLayout) itemView.findViewById(R.id.file_gravity_layoutId);
            file_senderLayout = (LinearLayout) itemView.findViewById(R.id.file_sender_layoutId);
            file_receiverLayout = (LinearLayout) itemView.findViewById(R.id.file_receiver_layoutId);
            file_textMeaagaeLayout = (RelativeLayout) itemView.findViewById(R.id.file_textMessageLayoutId);

            file_tsub = (TextView) itemView.findViewById(R.id.file_senderDateShowId);
            file_tsdb = (TextView) itemView.findViewById(R.id.file_senderStatusShowId);

            file_trub = (TextView) itemView.findViewById(R.id.file_reciverDateShowId);
            file_trdb = (TextView) itemView.findViewById(R.id.file_reciverStatusShowId);
            // end file part
        }
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_message_layout,parent,false);
        initialAll();

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
            Messages messages = userMessageList.get(position);
            String formUserId = messages.getFrom();
            String MessageTyp = messages.getTyp();
            String mid = messages.getMid();

        String curentDate, currentTime;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currntDat = new SimpleDateFormat("MMM dd, yyy");
        curentDate = currntDat.format(calendar.getTime());

        SimpleDateFormat currntTim = new SimpleDateFormat("hh:mm a");
        currentTime = currntTim.format(calendar.getTime());



            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference dd = FirebaseDatabase.getInstance().getReference().child("message");




            /// for message seen
            if(!currentUserId.equals(formUserId)){

                dd.child(reciverId).child(currentUserId).child(mid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()&&snapshot.hasChild("status")){
                            if(snapshot.child("status").getValue().equals("Sent")){
                                dd.child(reciverId).child(currentUserId).child(mid).child("status").setValue("Seen");
                                dd.child(reciverId).child(currentUserId).child(mid).child("statusdatetime").setValue(curentDate+"  "+currentTime);
                                dd.child(currentUserId).child(reciverId).child(mid).child("statusdatetime").setValue(curentDate+"  "+currentTime);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            /// end message seen part

        // receiver Message profile set part
            root = FirebaseDatabase.getInstance().getReference().child("user").child(formUserId);
            root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.hasChild("ppictur")){
                        String im = snapshot.child("ppictur").getValue().toString();

                        Picasso.get().load(im).into(holder.receiverProfileImage); /// for tex message
                        Picasso.get().load(im).into(holder.imageReceiverProfileImage); // for image message
                        Picasso.get().load(im).into(holder.video_receiverProfileImage); /// for video message
                        Picasso.get().load(im).into(holder.audio_receiverProfileImage); /// for audio message
                        Picasso.get().load(im).into(holder.file_receiverProfileImage); /// for file message
                    }
                    else{
                        String gd = "Common";
                        if(snapshot.hasChild("gender")){
                            gd = snapshot.child("gender").getValue().toString();
                        }
                        if(gd.equals("Male")){
                            holder.receiverProfileImage.setImageResource(R.drawable.defoult_image_male); // for text
                            holder.imageReceiverProfileImage.setImageResource(R.drawable.defoult_image_male); // for image
                            holder.video_receiverProfileImage.setImageResource(R.drawable.defoult_image_male); // for video
                            holder.audio_receiverProfileImage.setImageResource(R.drawable.defoult_image_male); // for audio
                            holder.file_receiverProfileImage.setImageResource(R.drawable.defoult_image_male); // for file
                        }
                        else if(gd.equals("Female")){
                            holder.receiverProfileImage.setImageResource(R.drawable.defoult_image_female); // for text
                            holder.imageReceiverProfileImage.setImageResource(R.drawable.defoult_image_female); // for image
                            holder.video_receiverProfileImage.setImageResource(R.drawable.defoult_image_female); // for video
                            holder.audio_receiverProfileImage.setImageResource(R.drawable.defoult_image_female); // for audio
                            holder.file_receiverProfileImage.setImageResource(R.drawable.defoult_image_female); // for file
                        }
                        else{
                            holder.receiverProfileImage.setImageResource(defoult_image_common); // for text
                            holder.imageReceiverProfileImage.setImageResource(defoult_image_common); // for image
                            holder.video_receiverProfileImage.setImageResource(defoult_image_common); // for video
                            holder.audio_receiverProfileImage.setImageResource(defoult_image_common); // for audio
                            holder.file_receiverProfileImage.setImageResource(defoult_image_common); // for file
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        // end receiver message profile set part



     //// set message date time status data part
        FirebaseDatabase.getInstance().getReference().child("message").child(currentUserId).child(reciverId).child(mid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()&&snapshot.hasChild("datetime")){
                    mdt = snapshot.child("datetime").getValue().toString();

                    // for txt
                    holder.tsub.setText(mdt);
                    holder.trub.setText(mdt);
                    // end for txt

                    // for image
                    holder.imagetsub.setText(mdt);
                    holder.imagetrub.setText(mdt);
                    // end image

                   // for video
                    holder.video_tsub.setText(mdt);
                    holder.video_trub.setText(mdt);
                    // end for video

                    // for audio
                    holder.audio_tsub.setText(mdt);
                    holder.audio_trub.setText(mdt);
                    // end for audio

                    // for file
                    holder.file_tsub.setText(mdt);
                    holder.file_trub.setText(mdt);
                    // end for file

                }
                if(snapshot.exists()&&snapshot.hasChild("status")){
                    msts = snapshot.child("status").getValue().toString();
                    if(snapshot.child("from").getValue().toString().equals(currentUserId)&&snapshot.child("status").getValue().toString().equals("Seen")){
                        msts += " "+snapshot.child("statusdatetime").getValue().toString();
                    }
                    // for text
                    holder.tsdb.setText(msts);
                    holder.trdb.setText(msts);
                    // end for text

                    // end for image
                    holder.imagetsdb.setText(msts);
                    holder.imagetrdb.setText(msts);
                    // end for image

                    // for video
                    holder.video_tsdb.setText(msts);
                    holder.video_trdb.setText(msts);
                    // end for video

                    // for audio
                    holder.audio_tsdb.setText(msts);
                    holder.audio_trdb.setText(msts);
                    // end for audio

                    // for file
                    holder.file_tsdb.setText(msts);
                    holder.file_trdb.setText(msts);
                    // end for file
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        /// end set message date time status data part


        /// control visible permision part
        if(MessageTyp.equals("text")){
            holder.textMeaagaeLayout.setVisibility(View.VISIBLE);
            holder.imageMessageLayout.setVisibility(View.GONE);
            holder.video_textMeaagaeLayout.setVisibility(View.GONE);
            holder.audio_textMeaagaeLayout.setVisibility(View.GONE);
            holder.file_textMeaagaeLayout.setVisibility(View.GONE);
        }
        if(MessageTyp.equals("image")){
            holder.imageMessageLayout.setVisibility(View.VISIBLE);
            holder.textMeaagaeLayout.setVisibility(View.GONE);
            holder.video_textMeaagaeLayout.setVisibility(View.GONE);
            holder.audio_textMeaagaeLayout.setVisibility(View.GONE);
            holder.file_textMeaagaeLayout.setVisibility(View.GONE);
        }
        if(MessageTyp.equals("video")){
            holder.video_textMeaagaeLayout.setVisibility(View.VISIBLE);
            holder.imageMessageLayout.setVisibility(View.GONE);
            holder.textMeaagaeLayout.setVisibility(View.GONE);
            holder.audio_textMeaagaeLayout.setVisibility(View.GONE);
            holder.file_textMeaagaeLayout.setVisibility(View.GONE);
        }
        if(MessageTyp.equals("audio")){
            holder.audio_textMeaagaeLayout.setVisibility(View.VISIBLE);
            holder.imageMessageLayout.setVisibility(View.GONE);
            holder.textMeaagaeLayout.setVisibility(View.GONE);
            holder.video_textMeaagaeLayout.setVisibility(View.GONE);
            holder.file_textMeaagaeLayout.setVisibility(View.GONE);
        }
        if(MessageTyp.equals("file")){
            holder.file_textMeaagaeLayout.setVisibility(View.VISIBLE);
            holder.imageMessageLayout.setVisibility(View.GONE);
            holder.textMeaagaeLayout.setVisibility(View.GONE);
            holder.video_textMeaagaeLayout.setVisibility(View.GONE);
            holder.audio_textMeaagaeLayout.setVisibility(View.GONE);
        }
        /// end control visible permision part

        // for audio




            if(formUserId.equals(currentUserId)){
                ///for text part
                holder.senderLayout.setVisibility(View.VISIBLE);
                holder.receiverLayout.setVisibility(View.GONE);
                holder.gravityLayout.setGravity(GravityCompat.END);

                holder.receiverProfileImage.setVisibility(View.INVISIBLE);
                holder.senderMessageText.setText(messages.getMessage());
                /// end for text part

                ///for image part
                holder.imageSenderLayout.setVisibility(View.VISIBLE);
                holder.imageReceiverLayout.setVisibility(View.GONE);
                holder.imageGravityLayout.setGravity(GravityCompat.END);

                holder.imageReceiverProfileImage.setVisibility(View.INVISIBLE);
                Picasso.get().load(messages.getMessage()).fit().centerCrop().into(holder.senderMessageImage);
                /// end for image part



                ///for video part
                holder.video_senderLayout.setVisibility(View.VISIBLE);
                holder.video_receiverLayout.setVisibility(View.GONE);
                holder.video_gravityLayout.setGravity(GravityCompat.END);

                holder.video_receiverProfileImage.setVisibility(View.INVISIBLE);


               /* Uri uri = Uri.parse(messages.getMessage());
                MediaController mediaController = new MediaController(context);
                holder.video_senderMessageText.setMediaController(mediaController);
                holder.video_senderMessageText.setVideoURI(uri);
                holder.video_senderMessageText.requestFocus();
                holder.video_senderMessageText.start();*/


                /// end for video part


                ///for audio part
                holder.audio_senderLayout.setVisibility(View.VISIBLE);
                holder.audio_receiverLayout.setVisibility(View.GONE);
                holder.audio_gravityLayout.setGravity(GravityCompat.END);

                holder.audio_receiverProfileImage.setVisibility(View.INVISIBLE);
                //holder.audio_senderMessageText.setText(messages.getMessage());
                /// end for audio part


                ///for file part
                holder.file_senderLayout.setVisibility(View.VISIBLE);
                holder.file_receiverLayout.setVisibility(View.GONE);
                holder.file_gravityLayout.setGravity(GravityCompat.END);

                holder.file_receiverProfileImage.setVisibility(View.INVISIBLE);
                /// end for file part




                 /* Picasso.get().load(messages.getMessage()).into(holder.senderMessageImage, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() { }
                    @Override
                    public void onError(Exception e) {holder.senderMessageImage.setBackgroundResource(defoult_image_common); }
                });*/
            }
            else{
                /// for text part
                holder.senderLayout.setVisibility(View.GONE);
                holder.receiverLayout.setVisibility(View.VISIBLE);
                holder.gravityLayout.setGravity(GravityCompat.START);

                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setText(messages.getMessage());
                ///end for text part

                /// for image part
                holder.imageSenderLayout.setVisibility(View.GONE);
                holder.imageReceiverLayout.setVisibility(View.VISIBLE);
                holder.imageGravityLayout.setGravity(GravityCompat.START);

                holder.imageReceiverProfileImage.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).fit().centerCrop().into(holder.receiverMessageImage);
                ///end for image part


                /// for video part
                holder.video_senderLayout.setVisibility(View.GONE);
                holder.video_receiverLayout.setVisibility(View.VISIBLE);
                holder.video_gravityLayout.setGravity(GravityCompat.START);

                holder.video_receiverProfileImage.setVisibility(View.VISIBLE);

               /* Uri uri = Uri.parse(messages.getMessage());
                MediaController mediaController = new MediaController(context);
                holder.video_receiverMessageText.setMediaController(mediaController);
                holder.video_receiverMessageText.setVideoURI(uri);
                holder.video_receiverMessageText.requestFocus();
                holder.video_receiverMessageText.start();*/

                ///end for video part



                /// for audio part
                holder.audio_senderLayout.setVisibility(View.GONE);
                holder.audio_receiverLayout.setVisibility(View.VISIBLE);
                holder.audio_gravityLayout.setGravity(GravityCompat.START);

                holder.audio_receiverProfileImage.setVisibility(View.VISIBLE);
                //holder.audio_receiverMessageText.setText(messages.getMessage());
                ///end for audio part


                /// for file part
                holder.file_senderLayout.setVisibility(View.GONE);
                holder.file_receiverLayout.setVisibility(View.VISIBLE);
                holder.file_gravityLayout.setGravity(GravityCompat.START);

                holder.file_receiverProfileImage.setVisibility(View.VISIBLE);
                ///end for file part

            }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(userMessageList.get(position).getTyp().equals("video")){
                        Intent intent = new Intent(context,Video.class);
                        String uriString = userMessageList.get(position).getMessage();
                        intent.putExtra("sender",currentUserId);
                        intent.putExtra("receiver",reciverId);
                        intent.putExtra("id",userMessageList.get(position).getMid());
                        context.startActivity(intent);
                        ((Activity)context).finish();
                    }
                if(userMessageList.get(position).getTyp().equals("image")){
                    Intent intent = new Intent(context,Video.class);
                    String uriString = userMessageList.get(position).getMessage();
                    intent.putExtra("sender",currentUserId);
                    intent.putExtra("receiver",reciverId);
                    intent.putExtra("id",userMessageList.get(position).getMid());
                    context.startActivity(intent);
                    ((Activity)context).finish();
                }
                if(userMessageList.get(position).getTyp().equals("audio")){

                    Intent intent = new Intent(context,Video.class);
                    String uriString = userMessageList.get(position).getMessage();
                    intent.putExtra("sender",currentUserId);
                    intent.putExtra("receiver",reciverId);
                    intent.putExtra("id",userMessageList.get(position).getMid());
                    context.startActivity(intent);
                    ((Activity)context).finish();
                }

                if(userMessageList.get(position).getTyp().equals("file")){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(messages.getMessage()));
                    context.startActivity(intent);
                }
                    if(holder.tsub.getVisibility()==View.VISIBLE){
                            /// for text part
                            holder.tsub.setVisibility(View.GONE);
                            holder.tsdb.setVisibility(View.GONE);
                            holder.trub.setVisibility(View.GONE);
                            holder.trdb.setVisibility(View.GONE);
                            /// end for text part

                            /// for image part
                            holder.imagetsub.setVisibility(View.GONE);
                            holder.imagetsdb.setVisibility(View.GONE);
                            holder.imagetrub.setVisibility(View.GONE);
                            holder.imagetrdb.setVisibility(View.GONE);
                            /// end for image part


                            /// for video part
                            holder.video_tsub.setVisibility(View.GONE);
                            holder.video_tsdb.setVisibility(View.GONE);
                            holder.video_trub.setVisibility(View.GONE);
                            holder.video_trdb.setVisibility(View.GONE);
                            /// end for video part


                            /// for audio part
                            holder.audio_tsub.setVisibility(View.GONE);
                            holder.audio_tsdb.setVisibility(View.GONE);
                            holder.audio_trub.setVisibility(View.GONE);
                            holder.audio_trdb.setVisibility(View.GONE);
                            /// end for audio part

                            /// for file part
                            holder.file_tsub.setVisibility(View.GONE);
                            holder.file_tsdb.setVisibility(View.GONE);
                            holder.file_trub.setVisibility(View.GONE);
                            holder.file_trdb.setVisibility(View.GONE);
                            /// end for file part
                        }
                        else{
                            ///for text part
                            holder.tsub.setVisibility(View.VISIBLE);
                            holder.tsdb.setVisibility(View.VISIBLE);
                            holder.trub.setVisibility(View.VISIBLE);
                            holder.trdb.setVisibility(View.VISIBLE);
                            /// end for text part

                            ///for image part
                            holder.imagetsub.setVisibility(View.VISIBLE);
                            holder.imagetsdb.setVisibility(View.VISIBLE);
                            holder.imagetrub.setVisibility(View.VISIBLE);
                            holder.imagetrdb.setVisibility(View.VISIBLE);
                            /// end for image part

                            ///for video part
                            holder.video_tsub.setVisibility(View.VISIBLE);
                            holder.video_tsdb.setVisibility(View.VISIBLE);
                            holder.video_trub.setVisibility(View.VISIBLE);
                            holder.video_trdb.setVisibility(View.VISIBLE);
                            /// end for video part

                            ///for audio part
                            holder.audio_tsub.setVisibility(View.VISIBLE);
                            holder.audio_tsdb.setVisibility(View.VISIBLE);
                            holder.audio_trub.setVisibility(View.VISIBLE);
                            holder.audio_trdb.setVisibility(View.VISIBLE);
                            /// end for text part

                            ///for file part
                            holder.file_tsub.setVisibility(View.VISIBLE);
                            holder.file_tsdb.setVisibility(View.VISIBLE);
                            holder.file_trub.setVisibility(View.VISIBLE);
                            holder.file_trdb.setVisibility(View.VISIBLE);
                            /// end for file part
                        }
                }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TextView copyButton,downloadButton,deleteButton,openButton,cancelButton;
                ViewGroup viewGroup = v.findViewById(android.R.id.content);
                AlertDialog.Builder bilder = new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.custom_alert_dialog,viewGroup,false);
                bilder.setView(view);
                AlertDialog alertDialog = bilder.create();


                copyButton = view.findViewById(R.id.copyButtonId);
                downloadButton = view.findViewById(R.id.downloadButtonId);
                deleteButton = view.findViewById(R.id.deleteButtonId);
                openButton = view.findViewById(R.id.openButtonId);
                cancelButton = view.findViewById(R.id.cancelButtonId);
                if(userMessageList.get(position).getTyp().equals("text")){
                    downloadButton.setVisibility(View.GONE);
                    openButton.setVisibility(View.GONE);
                }
                else{ copyButton.setVisibility(View.GONE); }
                copyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setClipboard(context, userMessageList.get(position).getMessage());
                        alertDialog.dismiss();
                    }
                });
                openButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         Intent intent = new Intent(context,Video.class);
                        String uriString = userMessageList.get(position).getMessage();
                        intent.putExtra("sender",currentUserId);
                        intent.putExtra("receiver",reciverId);
                        intent.putExtra("id",userMessageList.get(position).getMid());
                        context.startActivity(intent);
                        ((Activity)context).finish();
                        alertDialog.dismiss();
                    }
                });
                downloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(userMessageList.get(position).getMessage()));
                        context.startActivity(intent);
                        alertDialog.dismiss();
                    }
                });
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase.getInstance().getReference().child("message").child(currentUserId).child(reciverId).child(userMessageList.get(position).getMid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Intent intent = new Intent(context,reload2.class);
                                    intent.putExtra("receiver",reciverId);
                                    context.startActivity(intent);
                                    ((Activity)context).finish();
                                    alertDialog.dismiss();
                                }
                            }
                        });
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

                lp.copyFrom(alertDialog.getWindow().getAttributes());
                lp.width = 350;
                alertDialog.getWindow().setAttributes(lp);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }

    public void setClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    public void initialAll(){
        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        root = fDatabase.getReference();
        currentUser = mAuth.getCurrentUser();
        currentUserId = currentUser.getUid();
    }


}
