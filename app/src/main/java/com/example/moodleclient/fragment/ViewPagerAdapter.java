package com.example.moodleclient.fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new CourseFragment();
            case 1:
                return new AssignmentFragment();
            case 2:
                return new UploadFragment();
            case 3:
                return new ProfileFragment();
            default:
                return new CourseFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
