package com.immotor.batterystation.android.mycar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import com.immotor.batterystation.android.MyApplication;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.app.AppConstant;
import com.immotor.batterystation.android.entity.TripBean;
import com.immotor.batterystation.android.entity.TripDayBean;
import com.immotor.batterystation.android.ui.base.BaseFragment;
import com.immotor.batterystation.android.ui.views.TripHeadImageView;
import com.immotor.batterystation.android.util.Common;
import com.immotor.batterystation.android.util.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Ashion on 2016/7/19.
 */
public class TripHistoryFragment extends BaseFragment implements TripDetailAdapter.OnItemListener {

    @Bind(R.id.recycler_container)
    RecyclerView recyclerView;

    private List<TripBean> mDataList = new ArrayList<>();
    private TripDetailAdapter mAdapter;
    private static final String ARG_BEAN = "arg_bean";
    private TripDayBean tripDayBean;
    private int milesType;
    TripHeadImageView tripHeadImageView;

    @SuppressLint("ValidFragment")
    public static TripHistoryFragment newInstance(TripDayBean tripBean){
        TripHistoryFragment fragment = new TripHistoryFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_BEAN, tripBean);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getContentLayout() {
        return R.layout.fragment_trip_detail;
    }

    @Override
    public void initUIViews() {
        //set at newInstance
        tripDayBean = (TripDayBean) getArguments().getSerializable(ARG_BEAN);
        if (tripDayBean != null) {
            mDataList.clear();
            mDataList.addAll(tripDayBean.getData());
        }
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(mActivity));

        mAdapter = new TripDetailAdapter(mActivity, mDataList);
        mAdapter.setOnItemClickListener(this);
        //recyclerView.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(mAdapter);
        setHeader(recyclerView);

    }

    private void setHeader(RecyclerView view) {
        View header = LayoutInflater.from(getContext()).inflate(R.layout.trip_item_header, view, false);

        float totalMiles = 0;

        for (TripBean bean : mDataList){
                totalMiles = totalMiles + bean.getMiles();
        }

        int costTime = tripDayBean.getCostTime();
        costTime = 0;
        for(TripBean bean : mDataList){
            costTime += bean.getCostTime()/60*60;
        }
        String shortTime = DateTimeUtil.secondToShortTime(costTime);

        TextView tripMilesText = (TextView)header.findViewById(R.id.trip_miles_text);
        TextView tripMilesType = (TextView)header.findViewById(R.id.trip_miles_unit);
        TextView tripTimeText = (TextView)header.findViewById(R.id.trip_time_text);
//        TextView tripDesc = (TextView)header.findViewById(R.id.trip_desc);
   //     TextView tripGreenText = (TextView)header.findViewById(R.id.trip_green_text);
    //    TextView tripGreenUnit = (TextView)header.findViewById(R.id.trip_green_unit);

        tripHeadImageView = (TripHeadImageView)header.findViewById(R.id.trip_head_image);  //add for anim
        int greenVal = ((int)(totalMiles*170+0.5))*10;
        String greenS = greenVal/10+(greenVal%10==0?"":("."+greenVal%10));
    //    tripGreenText.setText(greenS);
   //     String format = getString(R.string.today_you_have_ride_trip_desc);
    //    String tripDescStr = String.format(format, greenS);

        tripMilesType.setText(getString(R.string.km));
        tripMilesText.setText(Common.formatFloat(totalMiles));

        Typeface typeface = Common.getTypeFace(getContext());
        tripMilesText.setTypeface(typeface);
        tripTimeText.setTypeface(typeface);
  //      tripGreenText.setTypeface(typeface);

        tripTimeText.setText(shortTime);
//        tripDesc.setText(tripDescStr);
        mAdapter.setHeaderView(header);
    }

    @Override
    public void onItemClick(View view, int position) {
        TripBean bean = mDataList.get(position - 1);
        Intent intent;
        if (DateTimeUtil.isInChina()) {
            intent = new Intent(mActivity, TripDetailActivity.class);
        }else {
            intent = new Intent(mActivity, TripDetailGMapActivity.class);
        }

        intent.putExtra("TRIP_BEAN", bean);
        intent.putExtra(AppConstant.KEY_ENTRY_BATTERY_CAR_TRIP_SID,tripDayBean.getSID());
        startActivity(intent);
    }

    /**
     * 开启头动画
     */
    public void startHeadAnimation(){
        if(tripHeadImageView!=null)
        tripHeadImageView.start();
    }

    /**
     * 停止头动画
     */
    public void stopHeadAnimation(){
        if(tripHeadImageView!=null)
        tripHeadImageView.stop();
    }
}
