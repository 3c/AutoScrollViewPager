package com.cx.autoscrollviewpager;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by CX on 2014/12/5.
 * <p/>
 * 示例：
 * viewPager = (AutoScrollViewPager) findViewById(R.id.pager);
 * viewPager.setInterval(3000);//设置自动轮播间隔
 * viewPager.setAdapter(getSupportFragmentManager(),fragmentPagerAdapter);//必须有两个参数，第二个是FragmentPagerAdapter
 * viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener())// 这个监听必须有
 * viewPager.startAutoLoop();  //开始自动轮播。 一般放在onResume里
 * viewPager.stopAutoLoop();   //停止自动轮播。 一般放在onPause 里
 */
public class AutoScrollViewPager extends ViewPager {


    public static final int DEFAULT_INTERVAL = 3000;

    /**
     * auto scroll time in milliseconds, default is {@link #DEFAULT_INTERVAL} *
     */
    private long interval = DEFAULT_INTERVAL;

    private Context mContext;
    private FragmentManager mFragmentManager;
    private ScheduledExecutorService scheduledExecutorService;
    /**
     * 手指是否放在上面
     */
    public static boolean sIsTouch = false;

    public AutoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }


    private PagerAdapter mRealPagerAdapter;


    private PagerAdapter mAutoScrollPagerAdapter;


    // 包装setOnPageChangeListener的数据
    private class MyOnPageChangeListener implements OnPageChangeListener {
        private OnPageChangeListener listener;
        // 是否已经提前触发了OnPageSelected事件
        private boolean alreadyTriggerOnPageSelected;

        public MyOnPageChangeListener(OnPageChangeListener listener) {
            this.listener = listener;
        }

        @Override
        // 关键之三:
        public void onPageScrollStateChanged(int arg0) {
            if (arg0 == SCROLL_STATE_IDLE) {
                //这里我做了小修改
                if (getCurrentItem() == 0) {
                    setCurrentItem(mRealPagerAdapter.getCount(), false);
                } else if (getCurrentItem() == mRealPagerAdapter.getCount() + 1) {
                    setCurrentItem(1, false);
                }
            }
            listener.onPageScrollStateChanged(arg0);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            listener.onPageScrolled(arg0, arg1, arg2);
        }

        @Override
        // 关键四:
        public void onPageSelected(int arg0) {
            if (arg0 == 0) {
                listener.onPageSelected(mRealPagerAdapter.getCount() - 1);
                alreadyTriggerOnPageSelected = true;
            } else if (arg0 == mRealPagerAdapter.getCount() + 1) {
                listener.onPageSelected(0);
                alreadyTriggerOnPageSelected = true;
            } else {
                if (!alreadyTriggerOnPageSelected) {
                    listener.onPageSelected(arg0 - 1);
                }
                alreadyTriggerOnPageSelected = false;
            }

        }

    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        super.setOnPageChangeListener(listener == null ? null : new MyOnPageChangeListener(listener));
    }

    public void setAdapter(FragmentManager _fragmentManager, FragmentPagerAdapter arg0) {
        mFragmentManager = _fragmentManager;
        mAutoScrollPagerAdapter = (arg0 == null) ? null : new AutoScrollLoogPagerAdater(mFragmentManager, arg0);
        super.setAdapter(mAutoScrollPagerAdapter);
        if (arg0 != null && arg0.getCount() != 0) {
            if (arg0.getCount() > 1) {
                setCurrentItem(1, false);
            } else {
                setCurrentItem(0, false);
            }
        }
        this.mRealPagerAdapter = arg0;
    }


    /**
     * set auto scroll time in milliseconds, default is {@link #DEFAULT_INTERVAL}
     *
     * @param interval the interval to set
     */
    public void setInterval(long interval) {
        this.interval = interval;
    }


    // 切换当前显示的图片
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int currentItem = getCurrentItem();
            currentItem++;
            setCurrentItem(currentItem, true);
        }


    };

    private class ScrollTask implements Runnable {

        public void run() {
            synchronized (AutoScrollViewPager.this) {
                if (!sIsTouch) {// 触摸则停止更新图片
                    handler.obtainMessage().sendToTarget(); // 通过Handler切换图片
                }
            }
        }
    }

    /**
     * 启动轮播图。在OnResume()中调用
     */
    public void startAutoLoop() {

        //保证只跑一个
        if (mRealPagerAdapter != null && mRealPagerAdapter.getCount() > 1) {
            stopAutoLoop();
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            // 当Activity显示出来后，每5秒钟切换一次图片显示
            scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), interval, interval, TimeUnit.MILLISECONDS);
        }


    }


    /**
     * 停止轮播图。在 OnPause()中调用
     */
    public void stopAutoLoop() {
        if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdown();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            this.sIsTouch = true;
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            this.sIsTouch = false;
        }
        return super.onTouchEvent(event);
    }


    /**
     * 自定义的fragmentPagerAdapter
     */
    private class AutoScrollLoogPagerAdater extends FragmentPagerAdapter {

        private FragmentPagerAdapter mFragmentPagerAdapter;

        public AutoScrollLoogPagerAdater(FragmentManager fm, FragmentPagerAdapter _fragmentPagerAdapter) {
            super(fm);
            mFragmentPagerAdapter = _fragmentPagerAdapter;
        }

        @Override
        public int getCount() {
            int realCount = mFragmentPagerAdapter.getCount();
            //如果adapter size 大于1，那么就+2，做成一个可以循环的
            return realCount > 1 ? realCount + 2 : realCount;
        }


        @Override
        public Fragment getItem(int position) {
            int realCount = mFragmentPagerAdapter.getCount();
            if (position == 0) {
                position = realCount - 1;
            } else if (position == realCount + 1) {
                position = 0;
            } else {
                position--;
            }
            return mFragmentPagerAdapter.getItem(position);
        }
    }


}
