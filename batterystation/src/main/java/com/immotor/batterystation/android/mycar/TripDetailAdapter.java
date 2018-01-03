package com.immotor.batterystation.android.mycar;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.database.Preferences;
import com.immotor.batterystation.android.entity.TripBean;
import com.immotor.batterystation.android.util.Common;
import com.immotor.batterystation.android.util.DateTimeUtil;

import java.util.List;


/**
 * Created by Ashion on 2016/6/23.
 */
public class TripDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;
    private Context context;
    List<TripBean> dataList;
    private View mHeaderView;

    private OnItemListener mOnItemClickListener;

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderView == null) return TYPE_NORMAL;
        if (position == 0) return TYPE_HEADER;
        return TYPE_NORMAL;
    }

    public interface OnItemListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public TripDetailAdapter(Context context, List<TripBean> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (mHeaderView != null && viewType == TYPE_HEADER) {
            return new MyViewHolder(mHeaderView);
        }
        RecyclerView.ViewHolder holder = new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.trip_item_layout, parent, false));

        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == TYPE_HEADER) return;

        final int pos = getRealPosition(holder);

        TripBean bean = dataList.get(pos);

        Typeface typeface = Common.getTypeFace(context);
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.mileageValue.setTypeface(typeface);
        myViewHolder.timeValue.setTypeface(typeface);

        String startTime = DateTimeUtil.getTimeString(bean.getSTime());
        String timeValueStr = DateTimeUtil.secondToShortTime(bean.getCostTime());

        myViewHolder.startTimeView.setText(startTime);
        myViewHolder.endTimeView.setText(DateTimeUtil.getTimeString(bean.getETime()));
        float milesValue = bean.getMiles();
        String format = context.getString(R.string.trip_unit);

        myViewHolder.mileageValue.setText(String.valueOf(milesValue));
        String tripDescStr = String.format(format, context.getString(R.string.km));
        myViewHolder.tripUnit.setText(tripDescStr);

        myViewHolder.timeValue.setText(timeValueStr);
        myViewHolder.startAddress.setText(bean.getSPlaceName());
        myViewHolder.endAddress.setText(bean.getEPlaceName());

        // 如果设置了回调，则设置点击事件
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mHeaderView == null ? dataList.size() : dataList.size() + 1;
    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return mHeaderView == null ? position : position - 1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout itemView;
        TextView startTimeView;
        TextView endTimeView;
        TextView mileageValue;
        TextView timeValue;
        TextView startAddress;
        TextView endAddress;
        TextView timeUnit;
        TextView tripUnit;

        public MyViewHolder(View view) {
            super(view);
            if (itemView == mHeaderView) return;
            itemView = (LinearLayout) view.findViewById(R.id.item_view);
            startTimeView = (TextView) view.findViewById(R.id.start_time_text);
            endTimeView = (TextView) view.findViewById(R.id.end_time_text);
            mileageValue = (TextView) view.findViewById(R.id.mileage_value);
            timeValue = (TextView) view.findViewById(R.id.time_value);
            startAddress = (TextView) view.findViewById(R.id.start_address);
            endAddress = (TextView) view.findViewById(R.id.end_address);
            timeUnit = (TextView) view.findViewById(R.id.time_unit);
            tripUnit = (TextView) view.findViewById(R.id.trip_unit);
        }
    }
}
