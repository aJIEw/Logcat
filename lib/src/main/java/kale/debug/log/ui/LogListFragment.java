package kale.debug.log.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.LinkedList;

import kale.debug.log.LogCat;
import kale.debug.log.LogLoader;
import kale.debug.log.LogParser;
import kale.debug.log.R;
import kale.debug.log.model.LogBean;
import kale.debug.log.util.Level;
import kale.debug.log.util.Options;

/**
 * @author Jack Tony
 * @date 2015/12/7
 */
public class LogListFragment extends Fragment {

    private static final String TAG = "LogListFragment";

    public static final String KEY_LEV = "key_lev";

    private RecyclerView mLogRecycler;
    private ProgressBar loadingPb;
    private LinearLayout mEmptyView;

    private LinkedList<LogBean> mData;
    private String tag;
    private Level lev;
    private LogAdapter mLogAdapter;

    public static LogListFragment getInstance(String lev) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_LEV, lev);
        LogListFragment fragment = new LogListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData = new LinkedList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.kale_log_fragment, container, false);
        lev = LogParser.parseLev(getArguments().getString(KEY_LEV, LogParser.VERBOSE));
        mLogAdapter = new LogAdapter(getActivity(), mData);

        loadingPb = (ProgressBar) root.findViewById(R.id.loading_pb);
        mEmptyView = (LinearLayout) root.findViewById(R.id.empty_view);
        mLogRecycler = (RecyclerView) root.findViewById(R.id.rv_log);

        mLogRecycler.setAdapter(mLogAdapter);
        mLogRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLogRecycler.setItemAnimator(new DefaultItemAnimator());
        mLogRecycler.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

        updateLog(null);
        return root;
    }

    /**
     * 注意：此方法会被频繁调用，需要注意效率问题
     */
    public void updateLog(@Nullable String tag) {
        if (getActivity() == null) {
            Log.e(TAG, "updateLog: activity is null!");
            return;
        }
        this.tag = tag;
        startLoading();
        mData.clear();
        Process process = LogCat.getInstance()
                .options(Options.DUMP)
                .withTime()
                .recentLines(1000)
                .filter(tag, lev)
                .commit();

        LogLoader.load(process, handler);
    }

    private LogLoader.LoadHandler handler = new LogLoader.LoadHandler() {
        @Override
        public void handLine(String line) {
            LogBean logBean = getLogBean(line);
            LogBean previous = null;
            if (!mData.isEmpty()) {
                previous = mData.getLast();
            }

            if (logBean != null) {
                /*
                * Append this log bean's message to the previous's
                * message if they are equal.
                * */
                if (previous != null && previous.equals(logBean)) {
                    previous.msg += "\n" + logBean.msg;
                    return;
                }
                mData.add(logBean);
            }
        }

        @Override
        public void onComplete() {
            mLogAdapter.notifyDataSetChanged();
            stopLoading(mLogAdapter.getItemCount() > 0);
        }
    };

    @Nullable
    private LogBean getLogBean(String line) {
        LogBean logBean = new LogBean();
        int tagStart = line.indexOf("/");
        int msgStart = line.indexOf("):");

        if (msgStart == -1 || tagStart == -1) {
            return null;
        }

        logBean.tag = line.substring(tagStart + 1, msgStart + 1);
        logBean.msg = line.substring(msgStart + 2);
        String lev = line.substring(tagStart - 1, tagStart);
        /*
        * Only show the log that match the level
        * */
        if (!lev.equals(getArguments().getString(KEY_LEV))) {
            return null;
        }
        String tag = line.substring(tagStart + 1, msgStart + 1);
        String msg = line.substring(msgStart + 2);
        String time = line.substring(0, tagStart - 2);

        logBean.msg = msg;
        logBean.tag = tag;
        logBean.lev = LogParser.parseLev(lev);
        logBean.time = time;
        return logBean;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mData = null;
    }

    private void startLoading() {
        loadingPb.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.INVISIBLE);
    }

    private void stopLoading(boolean hasData) {
        loadingPb.setVisibility(View.INVISIBLE);
        if (!hasData) {
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(Context context) {
            mDivider = context.getResources().getDrawable(R.drawable.line_divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
