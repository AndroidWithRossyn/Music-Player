package com.demo.music.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.demo.music.fragments.MPMImageFragment;
import com.demo.music.model.MPMSongModel;
import java.util.ArrayList;


public class TestFragmentAdapter extends FragmentStatePagerAdapter {
    private ArrayList<MPMSongModel> content;
    private Context context;

    @Override 
    public int getItemPosition(Object obj) {
        return -2;
    }

    @Override 
    public float getPageWidth(int i) {
        return 0.7f;
    }

    @Override 
    public Object instantiateItem(ViewGroup viewGroup, int i) {
        return super.instantiateItem(viewGroup, i);
    }

    @Override 
    public boolean isViewFromObject(View view, Object obj) {
        return obj != null && ((Fragment) obj).getView() == view;
    }

    public TestFragmentAdapter(FragmentManager fragmentManager, Context context, ArrayList<MPMSongModel> arrayList) {
        super(fragmentManager);
        this.context = context;
        this.content = arrayList;
    }

    @Override 
    public Fragment getItem(int i) {
        return MPMImageFragment.newInstance(this.content.get(i));
    }

    @Override 
    public int getCount() {
        ArrayList<MPMSongModel> arrayList = this.content;
        if (arrayList == null) {
            return 0;
        }
        return arrayList.size();
    }

    @Override 
    public CharSequence getPageTitle(int i) {
        return this.content.get(i).getTitle();
    }
}
