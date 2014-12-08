package com.cx.autoscrollviewpager;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;


public class DemoActivity extends FragmentActivity {
    AutoScrollViewPager viewPager;
    int[] colours = new int[]{Color.CYAN, Color.BLUE, Color.MAGENTA};

    private class TestAdapter extends FragmentPagerAdapter {

        public TestAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return colours.length;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new ColourFragment();
            Bundle args = new Bundle();
            args.putInt("colour", colours[position]);
            args.putInt("identifier", position);
            fragment.setArguments(args);
            return fragment;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (AutoScrollViewPager) findViewById(R.id.vp);
        viewPager.setInterval(3000);
        viewPager.setAdapter(getSupportFragmentManager(), new TestAdapter(getSupportFragmentManager()));
        //这个OnPageChangeListener是必须的
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                System.out.println("onpageSelected " + i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewPager.startAutoLoop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewPager.stopAutoLoop();
    }
}