package com.example.chatbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatbox.Notification.APIService;
import com.example.chatbox.Notification.Client;
import com.example.chatbox.Notification.Data;
import com.example.chatbox.Notification.MyResponse;
import com.example.chatbox.Notification.Sender;
import com.example.chatbox.Notification.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView userImage;
    private TextView userName,userLastSeen;
    private EditText messageTextInput;
    private ImageButton messageSentButton,imageSentButton;
    private RecyclerView recyclerView,loading;
    private LinearLayout chatButtonLayout,fileSentButton;

    APIService apiService;
    boolean notify = false;
    int xx = 1,yy = 1;

    private Toolbar chatToolBar;

    
    private String messageReciverId,chatListId1=null,chatListId2=null,receiverExist = "no";
    private String usernm="ChatBox User",img="Common";
    private final List<Messages> messagesList = new ArrayList<>();
    private final List<Messages> messagesList2 = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdepter messageAdepter;
    private int r1,r2,a1,a2,ds,cud,glaryInt=1,chekstr=1,ck=1;
    
    
    private DatabaseReference datarf;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase fDatabase;
    private DatabaseReference rootref,root;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private String currentUserId,messageText,messageTyp;
    private ProgressDialog lodingbar;
    /// noti
    String userid;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // Toast.makeText(getApplicationContext(),"creat",Toast.LENGTH_LONG).show();
        setContentView(R.layout.activity_chat);
        cud = 0;
        chekstr = 1;
        /// for notification

         apiService = Client.getClient("https://fcm.googleapis.com").create(APIService.class);



        /// end
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            messageReciverId = bundle.getString("tag");
        }
        /// noti
        intent = getIntent();
        userid = intent.getStringExtra("userid");
        //end noti
        initialAll();
        datarf = FirebaseDatabase.getInstance().getReference().child("user");

       initializeControlars();
        getdata();

        messageSentButton.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) {
                messageText = messageTextInput.getText().toString();messageTyp="text";
                messageText.trim();
                messageText = removeNewLine(messageText);
                sentMessage();
                messageTextInput.setText("");
        }});
        imageSentButton.setOnClickListener(this);
        fileSentButton.setOnClickListener(this);

        chatUpdate();
    }

    public String removeNewLine(String s){
        String rs="";
        int ln = s.length();
        int l=0,r=ln-1;
        for(int i=0; i<ln; i++){
            if(s.charAt(i)!='\n'&&s.charAt(i)!=' '){
                l = i; break;
            }
        }

        for(int i=ln-1; i>=0; i--){
            if(s.charAt(i)!='\n'&&s.charAt(i)!=' '){
                r = i; break;
            }
        }
        for(int i=l; i<=r; i++){
            rs += s.charAt(i);
        }
        return rs;
    }

    @Override
    protected void onStart() {
        super.onStart();
        ck = 1;
       FirebaseDatabase.getInstance().getReference().child("user").child(messageReciverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()&&snapshot.hasChild("userstatus")){
                    if(snapshot.child("userstatus").hasChild("status")&&ck==1){
                        String ss = snapshot.child("userstatus").child("status").getValue().toString();
                        if(ss.equals("Deleted")){
                            //Toast.makeText(ChatActivity.this, "Deleted", Toast.LENGTH_LONG).show();
                            fileSentButton.setVisibility(View.INVISIBLE);
                            messageSentButton.setVisibility(View.INVISIBLE);
                            imageSentButton.setVisibility(View.GONE);
                            messageTextInput.setEnabled(false);
                            messageTextInput.setText("The account has Deactivated");
                        }
                        ck = 0;
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
            if(v.getId()==R.id.send_image_btn){
                sendImageMessage();
            }
            if(v.getId()==R.id.send_file_btn){
                sendFileMessage();
            }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    public void initialAll(){
        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        rootref = fDatabase.getReference();
        root = fDatabase.getReference();
        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)currentUserId = currentUser.getUid();
    }
    private void initializeControlars(){
       // Toast.makeText(getApplicationContext(),"iniii..",Toast.LENGTH_LONG).show();
      chatToolBar = findViewById(R.id.chat_toolber);
        setSupportActionBar(chatToolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar,null);
        actionBar.setCustomView(actionBarView);


        userImage = findViewById(R.id.custom_profile_image);
        userName = findViewById(R.id.custom_profile_name);
        userLastSeen = findViewById(R.id.custom_user_last_seen);
        messageTextInput = findViewById(R.id.input_message);
        messageSentButton = findViewById(R.id.send_message_btn);
        imageSentButton = findViewById(R.id.send_image_btn);
        fileSentButton = findViewById(R.id.send_file_btn);
        chatButtonLayout = findViewById(R.id.chat_linear_layout);

        recyclerView = findViewById(R.id.privat_messages_of_users);
        loading = findViewById(R.id.loading);
        messageAdepter = new MessageAdepter(messagesList,messageReciverId,ChatActivity.this);
        linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(messageAdepter);
        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
    }

    public void chatUpdate(){
      //  Toast.makeText(getApplicationContext(),"come",Toast.LENGTH_LONG).show();
        root.child("message").child(currentUserId).child(messageReciverId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //Toast.makeText(getApplicationContext(),"come in",Toast.LENGTH_LONG).show();
                Messages messages = snapshot.getValue(Messages.class);
                messagesList.add(messages);
                messageAdepter.notifyDataSetChanged();
                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount());
                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() );
                lastMessageManage(messagesList);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Messages messages = snapshot.getValue(Messages.class);
                messagesList2.add(messages);
               lastMessageManage(messagesList2);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void lastMessageManage(List<Messages> messagesList){
        //Toast.makeText(getApplicationContext(),"... "+messagesList.get(messagesList.size()-1).getStatus().toString(),Toast.LENGTH_LONG).show();

        String smg="",rmg="",mg="",slm="",rlm="";

        //Toast.makeText(getApplicationContext(),messagesList.size(),Toast.LENGTH_LONG).show();
          Messages messages = messagesList.get(messagesList.size()-1);
            /*if (messages.getFrom().equals(currentUserId)) { smg += "You : "; }else{ rmg += "You : "; }
            if(messages.getTyp().equals("text")){
                mg = messages.getMessage();
                if(mg.length()>10){ smg += mg.substring(0,9)+"..\n";rmg += mg.substring(0,9)+"..\n"; }
                else{ smg += mg+".\n";rmg += mg+".\n"; }
            }
            else{ smg += "Sent "+messages.getTyp()+".\n";rmg += "Sent "+messages.getTyp()+".\n"; }
            if(messages.getStatus().equals("Seen")&&messages.getFrom().equals(currentUserId)){
                smg += "Seen "+messages.getStatusdatetime()+".";
            }
            else{
                smg += "Sent "+messages.getDatetime()+".";
            }
            if(messages.getStatus().equals("Seen")&&messages.getFrom().equals(messageReciverId)){
                rmg += "Seen "+messages.getStatusdatetime()+".";
            }
            else{
                rmg += "Sent "+messages.getDatetime()+".";
            }*/






        FirebaseDatabase.getInstance().getReference().child("userchat").child(currentUserId).child(messageReciverId).child("lastmessage").setValue(messages);
        FirebaseDatabase.getInstance().getReference().child("userchat").child(messageReciverId).child(currentUserId).child("lastmessage").setValue(messages);
    }



    public void getdata(){
        recyclerView.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
        chatButtonLayout.setVisibility(View.GONE);
        lodingbar = new ProgressDialog(this);
        lodingbar.setMessage("Loding..");
        lodingbar.setCanceledOnTouchOutside(false);
        lodingbar.show();

        datarf.child(messageReciverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.hasChild("fname")&&snapshot.hasChild("lname")){
                        usernm = snapshot.child("fname").getValue().toString();
                        usernm += " "+snapshot.child("lname").getValue().toString();
                        userName.setText(usernm);
                    }
                    if(snapshot.hasChild("ppictur")){
                        img = snapshot.child("ppictur").getValue().toString();
                        Picasso.get().load(img).into(userImage);
                    }
                    else{
                        if(snapshot.hasChild("gender")){
                            img = snapshot.child("gender").getValue().toString();
                            if(img.equals("Male")){
                                userImage.setImageDrawable(getResources().getDrawable(R.drawable.defoult_image_male));
                            }
                            else if(img.equals("Female")){
                                userImage.setImageDrawable(getResources().getDrawable(R.drawable.defoult_image_female));
                            }
                            else if(img.equals("Common")){
                                userImage.setImageDrawable(getResources().getDrawable(R.drawable.defoult_image_common));
                            }
                        }
                    }
                    if(snapshot.hasChild("userstatus")){
                        String tim,dat,sts;
                        tim = snapshot.child("userstatus").child("time").getValue().toString();
                        dat = snapshot.child("userstatus").child("date").getValue().toString();
                        sts = snapshot.child("userstatus").child("status").getValue().toString();
                        if(sts.equals("Active")){userLastSeen.setText("Active Now");}
                        else{userLastSeen.setText("Last Active "+dat+" "+tim);}
                    }
                }
                lodingbar.dismiss();
                recyclerView.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
                chatButtonLayout.setVisibility(View.VISIBLE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==glaryInt && resultCode==RESULT_OK && data!=null && data.getData()!=null){

            Uri imagUri = data.getData();
            //File file = new File(imagUri.getPath());


            lodingbar = new ProgressDialog(this);
            lodingbar.setTitle("Sending");
            lodingbar.setMessage("Please wait..");
            lodingbar.setCanceledOnTouchOutside(false);
            lodingbar.show();
            storageReference = firebaseStorage.getReference().child("image");

            /// get current time for making file name part
            String curentDate, currentTime;
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat currntDat = new SimpleDateFormat("MMM dd, yyy");
            curentDate = currntDat.format(calendar.getTime());

            SimpleDateFormat currntTim = new SimpleDateFormat("HH:mm:ss.SSS");
            currentTime = currntTim.format(calendar.getTime());
            ///end  get current time for making file name part



            StorageReference Folder = FirebaseStorage.getInstance().getReference().child("image");
            String naM = currentUserId+messageReciverId+curentDate+currentTime;
            String namePart = naM.replace(' ','s');
            StorageReference file_name = Folder.child(namePart+imagUri.getLastPathSegment());
            file_name.putFile(imagUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    /// get File type part
                    String metaData = taskSnapshot.getMetadata().getContentType().toString();
                    String[] part = metaData.split("/");
                    String fileType = part[0];
                    /// end get File type part

                    messageTyp = fileType;
                    if((!fileType.equals("image")) && (!fileType.equals("video")) && (!fileType.equals("audio"))){ messageTyp = "file"; }

                    file_name.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            messageText = String.valueOf(uri);
                            sentMessage();
                            lodingbar.dismiss();
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Message is not sent", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    //calculating progress percentage
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    //displaying percentage in progress dialog
                    lodingbar.setMessage("Uploaded " + ((int) progress) + "%\nPlease Wait...");
                }
            });





          /*  // getting file extension part
            String extns="",fileName = imagUri.toString();
            String s1 = fileName.replace('/','a');
            String s2 = s1.replace('\\','a');
            fileName = s2.replace(':','a');
            Toast.makeText(getApplicationContext(),fileName,Toast.LENGTH_LONG).show();
            String[] array = fileName.split("\\.");
            if(array.length!=0){ extns = ".";extns += array[array.length-1]; }
            //Toast.makeText(getApplicationContext(),fileName+"  "+array.length+"  "+extns,Toast.LENGTH_LONG).show();
            // end getting file extension part*/




        }
    }


    public void sendImageMessage(){
        Intent gallery = new Intent();
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery,glaryInt);
    }
    public void sendFileMessage(){
        String[] mimeTypes =
                {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf",
                        "application/zip",
                        "application/*"
                };

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent,"ChooseFile"), glaryInt);
    }


    public void sentMessage(){
        String curentDate, currentTime;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currntDat = new SimpleDateFormat("MMM dd, yyy");
        curentDate = currntDat.format(calendar.getTime());

        SimpleDateFormat currntTim = new SimpleDateFormat("hh:mm a");
        currentTime = currntTim.format(calendar.getTime());


            if(!TextUtils.isEmpty(messageText)){
                messageSentButton.setEnabled(false);
                fileSentButton.setEnabled(false);
                imageSentButton.setEnabled(false);
                //messageTextInput.setEnabled(false);
               // messageSentButton.setBackgroundColor(Color.parseColor("#ffffff"));
                String messageSenderRef = "message/"+currentUserId+"/"+messageReciverId;
                String messageReceiverRef = "message/"+messageReciverId+"/"+currentUserId;

                DatabaseReference userMessageKeyRef = rootref.child("message").child(currentUserId).child(messageReciverId).push();
                String messagePushId = userMessageKeyRef.getKey();



                Map messageTextBody = new HashMap();
                messageTextBody.put("message",messageText);
                messageTextBody.put("typ",messageTyp);
                messageTextBody.put("from",currentUserId);
                messageTextBody.put("datetime",curentDate+"  "+currentTime);
                messageTextBody.put("status","Sending..");
                messageTextBody.put("mid",messagePushId);
                messageTextBody.put("statusdatetime",curentDate+"  "+currentTime);

                Map messageBodyDetails = new HashMap();
                messageBodyDetails.put(messageSenderRef+"/"+messagePushId,messageTextBody);
                messageBodyDetails.put(messageReceiverRef+"/"+messagePushId,messageTextBody);

                rootref.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {




                        DatabaseReference d1;
                        d1 = FirebaseDatabase.getInstance().getReference().child("message");
                        d1.child(currentUserId).child(messageReciverId).child(messagePushId).child("status").setValue("Sent");
                        d1.child(messageReciverId).child(currentUserId).child(messagePushId).child("status").setValue("Seen");
                        r1=0;r2=0;a1=0;a2=0;ds=0;
                        manageChatList();

                        //// for notification
                        xx = 1;
                        FirebaseDatabase.getInstance().getReference().child("user").child(currentUserId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()&&snapshot.hasChild("flname")&&xx==1){
                                    String nnn = snapshot.child("flname").getValue().toString();
                                    notify = true;
                                    sentNotification(messageReciverId, nnn, messageText); xx = 0;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        ///// end part

                    }
                });

            }
    }

    public void sentNotification(String rsvrid, String sndrnm, String msg){
        yy = 1;
        FirebaseDatabase.getInstance().getReference().child("user").child(messageReciverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()&&snapshot.hasChild("token")&&yy==1){
                    String tkn = snapshot.child("token").getValue().toString();yy=0;
                    Data data = new Data(currentUserId,R.mipmap.ic_launcher,sndrnm+": "+msg, "New Message",userid);

                    Sender sender = new Sender(data,tkn);
                   apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if(response.code()==200&&response.body().success!=1){
                                Toast.makeText(ChatActivity.this, "NOT SENT NOTIFICATION", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                notify = false;
                                Toast.makeText(ChatActivity.this, "SENT NOTIFICATION", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
   public void manageChatList(){

        DatabaseReference ucrf,curf , listKeyref1,listKeyref2;
        ucrf = FirebaseDatabase.getInstance().getReference().child("userchat");
       curf = FirebaseDatabase.getInstance().getReference().child("chatuser");


       listKeyref1 = curf.child(currentUserId).push();
       String userPuscuId1 = listKeyref1.getKey();

       listKeyref2 = curf.child(messageReciverId).push();
       String userPuscuId2 = listKeyref2.getKey();

        ucrf.child(currentUserId).child(messageReciverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               // Toast.makeText(getApplicationContext(),"boka 1",Toast.LENGTH_LONG).show();
                if(snapshot.exists()&&snapshot.hasChild("id")&&r1==0&&a1==0){
                    chatListId1 = snapshot.child("id").getValue().toString();

                    curf.child(currentUserId).child(chatListId1).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                //Toast.makeText(getApplicationContext(),"remove1",Toast.LENGTH_LONG).show();
                                r1=1;if(a1==0)add1(userPuscuId1);
                            }
                        }
                    });
                }
                else{
                    //Toast.makeText(getApplicationContext(),a1+" else..1",Toast.LENGTH_LONG).show();
                    r1 = 1;if(a1==0){add1(userPuscuId1);}
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       ucrf.child(messageReciverId).child(currentUserId).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
              // Toast.makeText(getApplicationContext(),"boka 2",Toast.LENGTH_LONG).show();
               if(snapshot.exists()&&snapshot.hasChild("id")&&r2==0&&a2==0){
                   chatListId2 = snapshot.child("id").getValue().toString();
                   curf.child(messageReciverId).child(chatListId2).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful()){
                               //Toast.makeText(getApplicationContext(),"remove2",Toast.LENGTH_LONG).show();
                               r2 = 1;if(a2==0)add2(userPuscuId2);
                           }
                       }
                   });
               }
               else{
                   //Toast.makeText(getApplicationContext(),a2+" else..2",Toast.LENGTH_LONG).show();
                   r2 = 1;if(a2==0){add2(userPuscuId2);}
               }
           }
           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });






    }

    public void add1(String userPuscuId1){

        if(r1==1&&a1==0){
            a1=1;
        DatabaseReference ucrf,curf , listKeyref1,listKeyref2;
        ucrf = FirebaseDatabase.getInstance().getReference().child("userchat");
        curf = FirebaseDatabase.getInstance().getReference().child("chatuser");

        curf.child(currentUserId).child(userPuscuId1).child("id").setValue(messageReciverId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    ucrf.child(currentUserId).child(messageReciverId).child("id").setValue(userPuscuId1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                //Toast.makeText(getApplicationContext(),ds+" add1",Toast.LENGTH_SHORT).show();
                                if(ds==1){
                                     messageSentButton.setEnabled(true);
                                    fileSentButton.setEnabled(true);
                                    imageSentButton.setEnabled(true);
                                    //messageTextInput.setEnabled(true);
                                    messageTextInput.requestFocus();
                                    //messageSentButton.setBackgroundColor(Color.parseColor("#000"));
                                }else ds++;
                            }
                        }
                    });
                }
            }
        });
        }
    }

    public void add2(String userPuscuId2){
        if(r2==1&&a2==0){
            a2=1;
        DatabaseReference ucrf,curf , listKeyref1,listKeyref2;
        ucrf = FirebaseDatabase.getInstance().getReference().child("userchat");
        curf = FirebaseDatabase.getInstance().getReference().child("chatuser");

        curf.child(messageReciverId).child(userPuscuId2).child("id").setValue(currentUserId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    ucrf.child(messageReciverId).child(currentUserId).child("id").setValue(userPuscuId2).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                //Toast.makeText(getApplicationContext(),ds+" add2",Toast.LENGTH_SHORT).show();
                                if(ds==1){
                                    messageSentButton.setEnabled(true);
                                    fileSentButton.setEnabled(true);
                                    imageSentButton.setEnabled(true);
                                   // messageTextInput.setEnabled(true);
                                    messageTextInput.requestFocus();
                                    //messageSentButton.setBackgroundColor(Color.parseColor("#000"));
                                }else ds++;
                            }
                        }
                    });
                }
            }
        });
        }

    }

}