package com.example.chatbox.fragmentprant;

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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatbox.ChatActivity;
import com.example.chatbox.R;
import com.example.chatbox.ViewProfile;
import com.example.chatbox.fragmentchild.fragmentchild1;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragmentparent3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragmentparent3 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragmentparent3() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragmentparent3.
     */
    // TODO: Rename and change types and number of parameters
    public static fragmentparent3 newInstance(String param1, String param2) {
        fragmentparent3 fragment = new fragmentparent3();
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
    private SearchView searchView;
    private DatabaseReference chatRequestRef,userRef,contractRef;
    private FirebaseAuth mAuth;
    String currentUserId,searchText="";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestFragmentView = inflater.inflate(R.layout.fragment_fragmentparent3, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("user");
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("contract");
        myRequestList = (RecyclerView) requestFragmentView.findViewById(R.id.recylearviewId);
        searchView = requestFragmentView.findViewById(R.id.searchViewId);
        myRequestList.setLayoutManager(new LinearLayoutManager(getContext()));

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });


        searchText = "";
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                process(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                process(newText);
                return false;
            }
        });

        return requestFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        process("");
    }

    public void process(String s){
        FirebaseRecyclerOptions<COntacts> options = new FirebaseRecyclerOptions.Builder<COntacts>().setQuery(chatRequestRef.child(currentUserId),COntacts.class).build();

        FirebaseRecyclerAdapter<COntacts,RequestViewHolder> adapter = new FirebaseRecyclerAdapter<COntacts, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull COntacts model) {
                holder.itemView.findViewById(R.id.chatButtonId).setVisibility(View.VISIBLE);

                final String list_user_id = getRef(position).getKey();

                userRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name="ChatBox User", status="No User Status",imag = "Common",mail="";
                        //contains
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
                        if(snapshot.hasChild("userstatus")){
                            String actsts = snapshot.child("userstatus").child("status").getValue().toString();
                            if(actsts.equals("Active")){holder.activeStatusButtin.setVisibility(View.VISIBLE);}
                        }
                        if(snapshot.hasChild("mail")){
                            mail = snapshot.child("mail").getValue().toString();
                        }

                        boolean isFound = name.contains(s);
                        boolean isFound2 = mail.contains(s);
                        if(isFound||isFound2){holder.itemView.setVisibility(View.VISIBLE);}
                        else{
                            holder.itemView.setVisibility(View.GONE);
                            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                            params.height = 0;
                            params.width = 0;
                            holder.itemView.setLayoutParams(params);
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

                holder.chatButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String idd = getRef(position).getKey();
                        Intent intent = new Intent(getContext(), ChatActivity.class);
                        intent.putExtra("tag",idd);
                        startActivity(intent);
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
        ImageButton chatButton;
        LinearLayout activeStatusButtin;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userNmaeID);
            status = itemView.findViewById(R.id.statusID);
            acceptButton = itemView.findViewById(R.id.acceptButtonId);
            cencelButton = itemView.findViewById(R.id.deletButtonId);
            chatButton = itemView.findViewById(R.id.chatButtonId);
            image = itemView.findViewById(R.id.profilePicID);
            activeStatusButtin = itemView.findViewById(R.id.greenCircleId);
        }
    }
}