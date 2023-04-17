package com.example.hindustanhealthinsurance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class HomeActivity extends AppCompatActivity {
    private ViewPager ss_pager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout layoutDots;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private long pressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        ss_pager = findViewById(R.id.ss_pager);
        layoutDots = findViewById(R.id.layoutDots);
        btnSkip = findViewById(R.id.btn_skip);
        btnNext = findViewById(R.id.btn_next);

        layouts = new int[]{R.layout.welcome_slide1, R.layout.welcome_slide2, R.layout.welcome_slide3};

        addBottomDots(0);

        myViewPagerAdapter = new MyViewPagerAdapter();
        ss_pager.setAdapter(myViewPagerAdapter);
        ss_pager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check for the last page in the slide show
                int current = getItem(+1);
                if (current < layouts.length) {
                    startActivity(new Intent(HomeActivity.this, StartActivity.class));
                }
                ss_pager.setCurrentItem(current - 2);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check for the last page in the slide show
                int current = getItem(+1);
                if (current < layouts.length)
                    ss_pager.setCurrentItem(current);
                else
                    startActivity(new Intent(HomeActivity.this, StartActivity.class));
            }
        });
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length + 30];

        layoutDots.removeAllViews();
        layoutDots.addView(btnSkip);

        for (int i = 0; i < 15; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(" ");
            layoutDots.addView(dots[i]);
        }

        for (int i = 15; i < 18; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#9679;"));
            dots[i].setTextSize(20);
            dots[i].setTextColor(getResources().getColor(R.color.dot_light_ss));
            layoutDots.addView(dots[i]);
        }
        dots[currentPage + 15].setTextColor(getResources().getColor(R.color.dot_dark_ss));

        for (int i = 18; i < 33; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(" ");
            layoutDots.addView(dots[i]);
        }

        layoutDots.addView(btnNext);
    }

    private int getItem(int i) {
        return ss_pager.getCurrentItem() + i;
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnSkip.setText(getResources().getText(R.string.back));
                btnNext.setText(getString(R.string.start));
            } else {
                // still pages are left
                btnSkip.setText(getResources().getText(R.string.skip));
                btnNext.setText(getString(R.string.next));
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    //View Page Adapter
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}