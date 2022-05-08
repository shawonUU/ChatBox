package com.example.chatbox;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class viewPageAdepter extends FragmentPagerAdapter {
    private final ArrayList<Fragment> FragmentArray = new ArrayList<>();
    private final ArrayList<String> TitleArray = new ArrayList<>();
    public viewPageAdepter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return FragmentArray.get(position);
    }
    @Override
    public int getCount() {
        return FragmentArray.size();
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return TitleArray.get(position);
    }

    public void setFragment(Fragment fragment, String string){
        FragmentArray.add(fragment);
        TitleArray.add(string);
    }
}
