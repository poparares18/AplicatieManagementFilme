package com.example.aplicatiemanagementfilme.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.aplicatiemanagementfilme.database.model.Movie;
import com.example.aplicatiemanagementfilme.database.model.UserAccount;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private static final int NUM_INTEMS = 3;
    List<Movie> movieList = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentManager fm, List<Movie> movieList) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return MovieBrowserFragment.newInstance(movieList);
            case 1: return new WatchListFragment();
            default: return  new ProfileFragment();
        }
    }

    @Override
    public int getCount() {
        return NUM_INTEMS;
    }
}

