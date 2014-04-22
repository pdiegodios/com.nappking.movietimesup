package com.nappking.movietimesup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

public class TutorialSlideActivity extends FragmentActivity{

	private static final int NUM_PAGES = 5;
	private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_slide);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new SlideAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }
    

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }
    
    private class SlideAdapter extends FragmentStatePagerAdapter {
        public SlideAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new TutorialSlideFragment();
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
