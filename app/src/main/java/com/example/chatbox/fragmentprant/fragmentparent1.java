package com.example.chatbox.fragmentprant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatbox.ChatActivity;
import com.example.chatbox.Messages;
import com.example.chatbox.R;
import com.example.chatbox.ViewProfile;
import com.example.chatbox.reload2;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragmentparent1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragmentparent1 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragmentparent1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragmentparent1.
     */
    // TODO: Rename and change types and number of parameters
    public static fragmentparent1 newInstance(String param1, String param2) {
        fragmentparent1 fragment = new fragmentparent1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    View requestFragmentView;
    RecyclerView myRequestList;
    private DatabaseReference chatRequestRef,userRef,contractRef;
    private FirebaseAuth mAuth;
    String currentUserId,receiverUserId;
    String userArray[];
    public int dx = 0,cker=0,r1=0,r2=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestFragmentView = inflater.inflate(R.layout.fragment_fragmentparent1, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("user");
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("chatuser");
        myRequestList = (RecyclerView) requestFragmentView.findViewById(R.id.recylearviewId);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myRequestList.setLayoutManager(linearLayoutManager);

        return requestFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<COntacts> options = new FirebaseRecyclerOptions.Builder<COntacts>().setQuery(chatRequestRef.child(currentUserId),COntacts.class).build();
        FirebaseRecyclerAdapter<COntacts,RequestViewHolder> adapter = new FirebaseRecyclerAdapter<COntacts, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull COntacts model) {
                 String list_user_id = getRef(position).getKey();
                chatRequestRef.child(currentUserId).child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()&&snapshot.hasChild("id")){

                            receiverUserId = snapshot.child("id").getValue().toString();




                            userRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String name="ChatBox User", status="No User Status",imag = "Common";
                                    if(snapshot.hasChild("fname")&&snapshot.hasChild("lname")){
                                        name = snapshot.child("fname").getValue().toString();
                                        name += " "+snapshot.child("lname").getValue().toString();
                                    }
                                    if(snapshot.hasChild("ppictur")){
                                        imag = snapshot.child("ppictur").getValue().toString();
                                    }else{
                                        if(snapshot.hasChild("gender")){
                                            imag = snapshot.child("gender").getValue().toString();
                                        }
                                    }
                                    holder.userName.setText(name);

                                    holder.status.setText("");


                                    if(imag.equals("Male")){holder.image.setImageDrawable(getResources().getDrawable(R.drawable.defoult_image_male));}
                                    else if(imag.equals("Female")){holder.image.setImageDrawable(getResources().getDrawable(R.drawable.defoult_image_female));}
                                    else if(imag.equals("Common")){holder.image.setImageDrawable(getResources().getDrawable(R.drawable.defoult_image_common));}
                                    else Picasso.get().load(imag).into(holder.image);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });



                            FirebaseDatabase.getInstance().getReference().child("userchat").child(receiverUserId).child(currentUserId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()&&snapshot.hasChild("lastmessage")){
                                        Messages messages = snapshot.child("lastmessage").getValue(Messages.class);
                                        if(messages.getFrom().equals(receiverUserId)&&!(messages.getStatus().equals("Seen"))){
                                            holder.blueButton.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });



                            FirebaseDatabase.getInstance().getReference().child("userchat").child(currentUserId).child(receiverUserId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()&&snapshot.hasChild("lastmessage")){
                                       Messages messages = snapshot.child("lastmessage").getValue(Messages.class);

                                        String smg="",rmg="",mg="",slm="",rlm="";
                                        if (messages.getFrom().equals(currentUserId)) { smg += "You : "; }
                                        if(messages.getTyp().equals("text")){
                                            mg = messages.getMessage();
                                            if(mg.length()>10){ smg += mg.substring(0,9)+"..\n";}
                                            else{ smg += mg+".\n";}
                                        }
                                        else{ smg += "Sent "+messages.getTyp()+".\n";}
                                        if(messages.getStatus().equals("Seen")&&messages.getFrom().equals(currentUserId)){
                                            smg += "Seen "+messages.getStatusdatetime()+".";
                                        }
                                        else{
                                            smg += "Sent "+messages.getDatetime()+".";
                                        }
                                        holder.status.setText(smg);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });











                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String idd = getRef(position).getKey();
                        DatabaseReference ds = FirebaseDatabase.getInstance().getReference().child("chatuser");
                        ds.child(currentUserId).child(idd).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()&&snapshot.hasChild("id")){
                                    String rcvrId = snapshot.child("id").getValue().toString();
                                    Intent intent = new Intent(getContext(), ChatActivity.class);
                                    intent.putExtra("tag",rcvrId);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {

                        TextView copyButton,downloadButton,deleteButton,openButton,cancelButton,converButton;
                        ViewGroup viewGroup = v.findViewById(android.R.id.content);
                        AlertDialog.Builder bilder = new AlertDialog.Builder(getContext());
                        View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_alert_dialog,viewGroup,false);
                        bilder.setView(view);
                        AlertDialog alertDialog = bilder.create();


                        copyButton = view.findViewById(R.id.copyButtonId);
                        downloadButton = view.findViewById(R.id.downloadButtonId);
                        deleteButton = view.findViewById(R.id.deleteButtonId);
                        openButton = view.findViewById(R.id.openButtonId);
                        cancelButton = view.findViewById(R.id.cancelButtonId);
                        converButton = view.findViewById(R.id.converButtonId);
                        deleteButton.setText("Delete from chat");
                        converButton.setVisibility(View.VISIBLE);

                        copyButton.setVisibility(View.GONE);
                        openButton.setVisibility(View.GONE);
                        downloadButton.setVisibility(View.GONE);

                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                cker = 0;r1=0;r2=0;
                                FirebaseDatabase.getInstance().getReference().child("userchat").child(currentUserId).child(receiverUserId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()&&snapshot.hasChild("id")&&r1==0){
                                            String catId = snapshot.child("id").getValue().toString();
                                            FirebaseDatabase.getInstance().getReference().child("chatuser").child(currentUserId).child(catId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        cker++;r1=1;
                                                        if(cker==2){
                                                            alertDialog.dismiss();
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                                FirebaseDatabase.getInstance().getReference().child("userchat").child(receiverUserId).child(currentUserId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()&&snapshot.hasChild("id")&&r2==0){
                                            String catId = snapshot.child("id").getValue().toString();
                                            FirebaseDatabase.getInstance().getReference().child("chatuser").child(receiverUserId).child(catId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        cker++;r2=1;
                                                        if(cker==2){
                                                            alertDialog.dismiss();
                                                        }
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

                        });

                        converButton.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                cker = 0;r1=0;r2=0;
                                alertDialog.dismiss();

                                new AlertDialog.Builder(getContext())
                                        .setTitle("Delete Conversation?")
                                        .setMessage("If you delete your conversation, all your data will be permanently deleted.\n\nDo you want to delete your conversation?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {






                                                FirebaseDatabase.getInstance().getReference().child("userchat").child(currentUserId).child(receiverUserId).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if(snapshot.exists()&&snapshot.hasChild("id")&&r1==0){
                                                            String catId = snapshot.child("id").getValue().toString();
                                                            FirebaseDatabase.getInstance().getReference().child("chatuser").child(currentUserId).child(catId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        cker++;r1=1;
                                                                        if(cker==3){
                                                                            alertDialog.dismiss();
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });


                                                FirebaseDatabase.getInstance().getReference().child("userchat").child(receiverUserId).child(currentUserId).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if(snapshot.exists()&&snapshot.hasChild("id")&&r2==0){
                                                            String catId = snapshot.child("id").getValue().toString();
                                                            FirebaseDatabase.getInstance().getReference().child("chatuser").child(receiverUserId).child(catId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        cker++;r2=1;
                                                                        if(cker==3){
                                                                            alertDialog.dismiss();
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                                FirebaseDatabase.getInstance().getReference().child("message").child(currentUserId).child(receiverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            FirebaseDatabase.getInstance().getReference().child("message").child(receiverUserId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        cker++;
                                                                        if(cker==3){
                                                                            alertDialog.dismiss();
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });




                                            }
                                        }).setNegativeButton("No", null)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();

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
                        lp.width = 450;
                        alertDialog.getWindow().setAttributes(lp);


                        return false;
                    }
                });
            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.find_friend_layout,parent,false);
                RequestViewHolder holder = new RequestViewHolder(view);
                return holder;
            }
        };

        myRequestList.setAdapter(adapter);
        adapter.startListening();
    }
    public static class RequestViewHolder extends RecyclerView.ViewHolder{

        TextView userName, status,acceptButton,cencelButton;
        CircleImageView image;
        ImageButton chatButton;
        LinearLayout blueButton;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userNmaeID);
            status = itemView.findViewById(R.id.statusID);
            acceptButton = itemView.findViewById(R.id.acceptButtonId);
            cencelButton = itemView.findViewById(R.id.deletButtonId);
            chatButton = itemView.findViewById(R.id.chatButtonId);
            image = itemView.findViewById(R.id.profilePicID);
            blueButton = itemView.findViewById(R.id.blueCircleId);
        }
    }
}