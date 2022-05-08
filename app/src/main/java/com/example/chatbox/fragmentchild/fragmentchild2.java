package com.example.chatbox.fragmentchild;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatbox.Contacts;
import com.example.chatbox.Myprofile;
import com.example.chatbox.R;
import com.example.chatbox.ViewProfile;
import com.example.chatbox.updateInfo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragmentchild2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragmentchild2 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragmentchild2() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragmentchild2.
     */
    // TODO: Rename and change types and number of parameters
    public static fragmentchild2 newInstance(String param1, String param2) {
        fragmentchild2 fragment = new fragmentchild2();
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


    private View view;
    Contacts myadepter;
    private RecyclerView recyclerView;
    private TextView textView;
    private SearchView searchView;
    private LinearLayout searchOuter;


    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase fDatabase;
    private DatabaseReference root,root1,root2;
    private String currentUserId,mailOrName="flname";

    Vector<String> name = new Vector<String>();
    Vector<String> stas = new Vector<String>();
    Vector<String> pict = new Vector<String>();
    Vector<String> uidd = new Vector<String>();

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //Toast.makeText(getContext(),"ONcreat start",Toast.LENGTH_LONG).show();
        view = inflater.inflate(R.layout.fragment_fragmentchild2, container, false);
        recyclerView = view.findViewById(R.id.recylearviewId);
        textView = view.findViewById(R.id.testTextId);
        searchView = view.findViewById(R.id.searchViewId);
        searchOuter = view.findViewById(R.id.searchOuterId);


        initialAll();
        //searchView.setIconifiedByDefault(false);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
                TextView searchNameButton,searchMailButton;
                LinearLayout invisibleButton,visibleButton;
                ViewGroup viewGroup = v.findViewById(android.R.id.content);
                AlertDialog.Builder bilder = new AlertDialog.Builder(getContext());
                View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_alert_dialog,viewGroup,false);
                bilder.setView(view);

                AlertDialog alertDialog = bilder.create();
                alertDialog.setCancelable(false);
                searchNameButton = view.findViewById(R.id.searchNameId);
                searchMailButton = view.findViewById(R.id.searchMailId);
                invisibleButton = view.findViewById(R.id.invisibleId);
                visibleButton = view.findViewById(R.id.visibleId);
                invisibleButton.setVisibility(View.GONE);
                visibleButton.setVisibility(View.VISIBLE);
                searchNameButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mailOrName = "flname";
                        searchView.setQueryHint("Search by name..");
                        alertDialog.dismiss();
                    }
                });
                searchMailButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mailOrName = "mail";
                        searchView.setQueryHint("Search by mail..");
                        alertDialog.dismiss();
                    }
                });


               // alertDialog.show();
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

                lp.copyFrom(alertDialog.getWindow().getAttributes());
                lp.width = 450;
                alertDialog.getWindow().setAttributes(lp);
            }
        });
        processSeatch("");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                processSeatch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                processSeatch(newText);
                return false;
            }
        });


        return view;
    }

    public void processSeatch(String s){ // .orderByChild(mailOrName).startAt(s).endAt(s+"\uf8ff").
        FirebaseDatabase.getInstance().getReference().child("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.clear();stas.clear();pict.clear();uidd.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    if(dataSnapshot.hasChild("userstatus")&&dataSnapshot.child("userstatus").child("status").getValue().toString().equals("Deleted")){

                    }
                    else{
                        String ml="",nm="ChatBox User",st="No User Status",im="Common",ui="";
                        if(dataSnapshot.hasChild("fname")&&dataSnapshot.hasChild("lname")){
                            nm = dataSnapshot.child("fname").getValue().toString();
                            nm += " "+dataSnapshot.child("lname").getValue().toString();
                        }
                        if(dataSnapshot.hasChild("status")){
                            st = dataSnapshot.child("status").getValue().toString();
                        }
                        if(dataSnapshot.hasChild("ppictur")){
                            im = dataSnapshot.child("ppictur").getValue().toString();
                        }else{
                            if(dataSnapshot.hasChild("gender")){
                                im = dataSnapshot.child("gender").getValue().toString();
                            }
                        }
                        if(dataSnapshot.hasChild("uid")){
                            ui = dataSnapshot.child("uid").getValue().toString();
                        }
                        if(dataSnapshot.hasChild("mail")){
                            ml = dataSnapshot.child("mail").getValue().toString();
                        }
                        boolean isFound,isFound2;
                        isFound = nm.contains(s);
                        isFound2 = ml.contains(s);
                        if(isFound||isFound2){
                            name.add(nm);stas.add(st);pict.add(im);uidd.add(ui);
                        }
                    }

                }
                myadepter = new Contacts(view.getContext(),name,stas,pict,uidd);
                recyclerView.setAdapter(myadepter);
                recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                myadepter.setOnItemClickListener(new Contacts.ClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {
                        viewProfile(uidd.get(position));
                    }

                    @Override
                    public void onItemLongClick(int position, View v) {
                        viewProfile(uidd.get(position));
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void initialAll(){
        mAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        root = fDatabase.getReference();
        root1 = fDatabase.getReference();
        root2 = fDatabase.getReference();
        currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)currentUserId = currentUser.getUid();
    }

    public void viewProfile(String id){
        if(id.equals(currentUserId)){
            Intent intent = new Intent(view.getContext(), Myprofile.class);
            intent.putExtra("tag",id);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(view.getContext(), ViewProfile.class);
            intent.putExtra("tag",id);
            startActivity(intent);
        }

    }

}