package kale.debug.log.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import kale.debug.log.LogCat;
import kale.debug.log.LogParser;
import kale.debug.log.R;

public class LogActivity extends AppCompatActivity {

    private static final String TAG = "LogActivity";

    private static final String[] TITLES = {
            LogParser.VERBOSE,
            LogParser.DEBUG,
            LogParser.INFO,
            LogParser.WARN,
            LogParser.ERROR};

    private Toolbar mToolbar;
    private ViewPager mPager;
    private PagerSlidingTabStrip mTabStrip;
    private FloatingActionButton mFab;
    private EditText tagEt;

    final List<LogListFragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kale_log_activity);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_log);
        mPager = (ViewPager) findViewById(R.id.log_vp);
        mTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mFab = (FloatingActionButton) findViewById(R.id.clear_log_btn);
        tagEt = (EditText) findViewById(R.id.log_et);

        initViews();
    }

    private void initViews() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        for (String title : TITLES) {
            fragments.add(LogListFragment.getInstance(title));
        }

        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(TITLES.length);
        mTabStrip.setViewPager(mPager);
        mTabStrip.setIndicatorHeight(10);
        mTabStrip.setIndicatorColor(getResources().getColor(R.color.colorAccent));
        mTabStrip.setOnPageChangeListener(mOnPageChangeListener);

        mFab.setOnClickListener(mFabOnClickListener);
    }

    private void updateLog(LogListFragment fragment) {
        fragment.updateLog(tagEt.getText().toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            updateLog(fragments.get(position));
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private FragmentPagerAdapter mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

        @Override
        public CharSequence getPageTitle(int position) {
            return LogParser.parseLev(TITLES[position]).toString();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    };

    private View.OnClickListener mFabOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LogCat.getInstance().clear().commit();
            int currentItem = mPager.getCurrentItem();
            updateLog(fragments.get(currentItem));
            for (int i = 0; i < mPagerAdapter.getCount(); i++) {
                if (currentItem != i) {
                    updateLog(fragments.get(i));
                }
            }
        }
    };
}
