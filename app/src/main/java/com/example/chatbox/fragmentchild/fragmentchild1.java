package com.example.chatbox.fragmentchild;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatbox.Contacts;
import com.example.chatbox.R;
import com.example.chatbox.ViewProfile;
import com.example.chatbox.fragmentprant.COntacts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

import java.util.Vector;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragmentchild1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragmentchild1 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragmentchild1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragmentchild1.
     */
    // TODO: Rename and change types and number of parameters
    public static fragmentchild1 newInstance(String param1, String param2) {
        fragmentchild1 fragment = new fragmentchild1();
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
    String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       requestFragmentView = inflater.inflate(R.layout.fragment_fragmentchild1, container, false);
       mAuth = FirebaseAuth.getInstance();
       currentUserId = mAuth.getCurrentUser().getUid();
       userRef = FirebaseDatabase.getInstance().getReference().child("user");
       chatRequestRef = FirebaseDatabase.getInstance().getReference().child("request");
       myRequestList = (RecyclerView) requestFragmentView.findViewById(R.id.recylearviewId);
       myRequestList.setLayoutManager(new LinearLayoutManager(getContext()));
        contractRef = FirebaseDatabase.getInstance().getReference().child("contract");

       return requestFragmentView;
    }





    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<COntacts> options = new FirebaseRecyclerOptions.Builder<COntacts>().setQuery(chatRequestRef.child(currentUserId),COntacts.class).build();
        FirebaseRecyclerAdapter<COntacts, RequestViewHolder> adapter = new FirebaseRecyclerAdapter<COntacts, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull COntacts model) {
                    holder.itemView.findViewById(R.id.acceptButtonId).setVisibility(View.VISIBLE);
                    holder.itemView.findViewById(R.id.deletButtonId).setVisibility(View.VISIBLE);

                    final String list_user_id = getRef(position).getKey();

                    DatabaseReference getTypeRef = getRef(position).child("request_typ").getRef();

                    getTypeRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    String type = snapshot.getValue().toString();
                                    if(type.equals("received")){
                                        //Toast.makeText(getContext(),"fuc.......  "+list_user_id,Toast.LENGTH_LONG).show();
                                        userRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String name="ChatBox User", status="No User Status",imag = "Common";
                                                if(snapshot.hasChild("fname")&&snapshot.hasChild("lname")){
                                                    name = snapshot.child("fname").getValue().toString();
                                                    name += " "+snapshot.child("lname").getValue().toString();
                                                }
                                                if(snapshot.hasChild("status")){
                                                    status = snapshot.child("status").getValue().toString();
                                                }
                                                if(snapshot.hasChild("ppictur")){
                                                    imag = snapshot.child("ppictur").getValue().toString();
                                                }else{
                                                    if(snapshot.hasChild("gender")){
                                                        imag = snapshot.child("gender").getValue().toString();
                                                    }
                                                }
                                                holder.userName.setText(name);
                                                holder.status.setText(status);
                                                if(imag.equals("Male")){holder.image.setImageDrawable(getResources().getDrawable(R.drawable.defoult_image_male));}
                                                else if(imag.equals("Female")){holder.image.setImageDrawable(getResources().getDrawable(R.drawable.defoult_image_female));}
                                                else if(imag.equals("Common")){holder.image.setImageDrawable(getResources().getDrawable(R.drawable.defoult_image_common));}
                                                else Picasso.get().load(imag).into(holder.image);

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                    else{
                                        holder.userName.setVisibility(View.GONE);
                                        holder.status.setVisibility(View.GONE);
                                        holder.image.setVisibility(View.GONE);
                                        holder.acceptButton.setVisibility(View.GONE);
                                        holder.cencelButton.setVisibility(View.GONE);
                                    }
                                }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    holder.acceptButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String idd = getRef(position).getKey();
                            acceptRequest(idd);
                        }
                    });
                    holder.cencelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String idd = getRef(position).getKey();
                            deleteRequest(idd);
                        }
                    });
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String idd = getRef(position).getKey();
                            Intent intent = new Intent(getContext(), ViewProfile.class);
                            intent.putExtra("tag",idd);
                            startActivity(intent);
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
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userNmaeID);
            status = itemView.findViewById(R.id.statusID);
            acceptButton = itemView.findViewById(R.id.acceptButtonId);
            cencelButton = itemView.findViewById(R.id.deletButtonId);
            image = itemView.findViewById(R.id.profilePicID);
        }
    }
    public void acceptRequest(String receiverId){
        contractRef.child(currentUserId).child(receiverId).child("relation").setValue("friend").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    contractRef.child(receiverId).child(currentUserId).child("relation").setValue("friend").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                chatRequestRef.child(currentUserId).child(receiverId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            chatRequestRef.child(receiverId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){

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

    public void deleteRequest(String receiverId){
        chatRequestRef.child(currentUserId).child(receiverId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    chatRequestRef.child(receiverId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){ }
                        }
                    });
                }
            }
        });
    }

}