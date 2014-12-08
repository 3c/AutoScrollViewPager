AutoScrollViewPager
===================
需求：自动轮播的viewpager，且无限循环。 但若当size==1时，不可划动		

用法：
 viewPager = (AutoScrollViewPager) findViewById(R.id.pager);
 viewPager.setInterval(3000);//设置自动轮播间隔
 viewPager.setAdapter(getSupportFragmentManager(),fragmentPagerAdapter);//必须有两个参数，第二个是FragmentPagerAdapter
 viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener())// 这个监听必须有
 viewPager.startAutoLoop();  //开始自动轮播。 一般放在onResume里
 viewPager.stopAutoLoop();   //停止自动轮播。 一般放在onPause 里

参考资料：
http://www.cnblogs.com/moqi2013/p/3547816.html
https://github.com/antonyt/InfiniteViewPager
https://github.com/Trinea/android-auto-scroll-view-pager
http://blog.csdn.net/just_sanpark/article/details/17436037
