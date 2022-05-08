package com.example.chatbox.fragmentprant;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatbox.R;
import com.example.chatbox.fragmentchild.fragmentchild1;
import com.example.chatbox.fragmentchild.fragmentchild2;
import com.example.chatbox.viewPageAdepter;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragmentparent2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragmentparent2 extends Fragment {

    private TabLayout tabLayout2;
    private ViewPager viewPager2;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragmentparent2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragmentparent2.
     */
    // TODO: Rename and change types and number of parameters
    public static fragmentparent2 newInstance(String param1, String param2) {
        fragmentparent2 fragment = new fragmentparent2();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_fragmentparent2, container, false);
        addFragment(view);
        return view;
    }
    private void addFragment(View view) {
        tabLayout2 = view.findViewById(R.id.tabeLayoutId2);
        viewPager2 = view.findViewById(R.id.viewPagerId2);
        viewPageAdepter adepter2 = new viewPageAdepter(getChildFragmentManager());
        adepter2.setFragment(new fragmentchild1(),"Friend Request");
        adepter2.setFragment(new fragmentchild2(),"Find Friend");
        viewPager2.setAdapter(adepter2);
        tabLayout2.setupWithViewPager(viewPager2);

    }
}