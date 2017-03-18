package kale.debug.log.ui;

import android.app.Activity;
import android.content.res.Resources;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import kale.debug.log.R;
import kale.debug.log.model.LogBean;
import kale.debug.log.util.Level;

/**
 * @author Kale
 * @date 2016/3/25
 */
public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {

    public static final Map<Level, Integer> LEV_MAP = new ArrayMap<>();

    static {
        LEV_MAP.put(Level.VERBOSE, R.color.gray);
        LEV_MAP.put(Level.DEBUG, R.color.blue);
        LEV_MAP.put(Level.INFO, R.color.green);
        LEV_MAP.put(Level.WARN, R.color.yellow);
        LEV_MAP.put(Level.ERROR, R.color.red);
        LEV_MAP.put(Level.FATAL, R.color.red);
        LEV_MAP.put(Level.ASSERT, R.color.red);
    }

    private List<LogBean> mData;
    private Resources mResources;
    private Activity mActivity;

    public LogAdapter(Activity activity, List<LogBean> data) {
        mActivity = activity;
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mResources = parent.getContext().getResources();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kale_log_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LogBean log = mData.get(position);

        holder.tag.setText(log.tag);
        holder.tag.setTextColor(mResources.getColor(LEV_MAP.get(log.lev)));
        holder.time.setText(log.time);
        holder.time.setTextColor(mResources.getColor(LEV_MAP.get(log.lev)));
        holder.msg.setText(log.msg.trim());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tag, msg, time;

        public ViewHolder(View itemView) {
            super(itemView);
            tag = (TextView) itemView.findViewById(R.id.tag_tv);
            msg = (TextView) itemView.findViewById(R.id.msg_tv);
            time = (TextView) itemView.findViewById(R.id.time_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mActivity.startActivity(LogDetailActivity.withIntent(mActivity, mData.get(position)));
        }
    }
}
