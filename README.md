AutoScrollViewPager
===================
需求：<br/>
自动轮播的viewpager，且无限循环。 但若当size==1时，不可划动		

用法：<br/>
 viewPager = (AutoScrollViewPager) findViewById(R.id.pager);<br/>
 viewPager.setInterval(3000);//设置自动轮播间隔<br/>
 viewPager.setAdapter(getSupportFragmentManager(),fragmentPagerAdapter);//必须有两个参数，第二个是FragmentPagerAdapter<br/>
 viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener())// 这个监听必须有<br/>
 viewPager.startAutoLoop();  //开始自动轮播。 一般放在onResume里<br/>
 viewPager.stopAutoLoop();   //停止自动轮播。 一般放在onPause 里<br/>

参考资料：<br/>
http://www.cnblogs.com/moqi2013/p/3547816.html<br/>
https://github.com/antonyt/InfiniteViewPager<br/>
https://github.com/Trinea/android-auto-scroll-view-pager<br/>
http://blog.csdn.net/just_sanpark/article/details/17436037<br/>
