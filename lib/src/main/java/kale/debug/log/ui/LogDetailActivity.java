package kale.debug.log.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import kale.debug.log.R;
import kale.debug.log.model.LogBean;


/**
 * @author Kale
 * @date 2015/12/18
 */
public class LogDetailActivity extends AppCompatActivity {

    public static final String KEY_MESSAGE = "key_message";

    private Toolbar mToolbarDetail;
    private TextView mTvTitle;
    private TextView mTvTime;
    private TextView mTvTag;
    private TextView mTvMsg;

    private LogBean mLogBean;

    public static Intent withIntent(Activity activity, LogBean str) {
        return new Intent(activity, LogDetailActivity.class)
                .putExtra(KEY_MESSAGE, str);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kale_log_detail_activity);
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }

        mLogBean = (LogBean) intent.getSerializableExtra(KEY_MESSAGE);

        mToolbarDetail = (Toolbar) findViewById(R.id.toolbar_detail);
        mTvTitle = (TextView) findViewById(R.id.tv_title_detail);
        mTvTime = (TextView) findViewById(R.id.tv_time);
        mTvTag = (TextView) findViewById(R.id.tv_tag);
        mTvMsg = (TextView) findViewById(R.id.tv_msg);

        initViews();
    }

    @SuppressLint("SetTextI18n")
    private void initViews() {
        setSupportActionBar(mToolbarDetail);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        mTvTitle.setText(mLogBean.lev.toString());

        mTvTime.setText(mLogBean.time);
        mTvTag.setText(mLogBean.tag);
        mTvMsg.setText(mLogBean.msg);
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
}
