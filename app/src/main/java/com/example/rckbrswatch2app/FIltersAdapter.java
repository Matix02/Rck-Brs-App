package com.example.rckbrswatch2app;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class FiltersAdapter extends FragmentPagerAdapter {

    private Filter filterUser;


    public FiltersAdapter(@NonNull FragmentManager fm, int behavior, Filter filterUser) {
        super(fm, behavior);
        this.filterUser = filterUser;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }

}
