package com.immotor.batterystation.android.mycar.mycarmain;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.entity.CarListBean;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by jm on 2017/9/26 0026.
 */

public class MyCarAdapter extends RecyclerView.Adapter<MyCarAdapter.MyViewHolder> {

    private Context context;
    List<CarListBean> dataList;
    GeocodeSearch geocoderSearch;
    String address = "";
    private int mposition = 0;
    private OnItemListener mOnItemClickListener;

    public interface OnItemListener {
        void onItemClick(View view, int position);

    }

    public void setOnItemClickListener(OnItemListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public MyCarAdapter(Context context) {
        this.context = context;
        geocoderSearch = new GeocodeSearch(context);
        initListener();
    }

    public void setDataList(List<CarListBean> dataList) {
        this.dataList = dataList;
        mposition = 0;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.item_my_car_recy, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        CarListBean bean = dataList.get(position);
        if (mposition < dataList.size()) {
            ++mposition;
            getAddress(bean.getLocation());
        }
        batteryImg(holder, bean.getSoc());
        holder.caraddress.setText(address);
        holder.destance.setText( getRemailMiles(bean.getRemailMiles())+ context.getString(R.string.kilometer));
        holder.status.setText(getTravelStatus(bean.getDeviceState()));
        holder.userName.setText(bean.getNickName());
        holder.powertv.setText(bean.getSoc() + "%");

        // 如果设置了回调，则设置点击事件
        if (mOnItemClickListener != null) {
            holder.batteryLlyt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.batteryLlyt, pos);
                }
            });
            holder.traverLLyt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.traverLLyt, pos);
                }
            });
            holder.setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.setting, pos);
                }
            });
            holder.addressLlyt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.addressLlyt, pos);
                }
            });
        }
    }
    private  String  getRemailMiles(Double remailMile){
        String real = "0";
        if (remailMile == 0) {
            real = "0";
        } else {
            real =String.valueOf(new DecimalFormat(".00").format(remailMile));
        }
        return real;
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView status;
        TextView destance;
        TextView powertv;
        TextView caraddress;
        ImageView powerimg;
        LinearLayout traverLLyt;
        LinearLayout addressLlyt;
        LinearLayout batteryLlyt;
        LinearLayout setting;

        public MyViewHolder(View view) {
            super(view);
            userName = (TextView) itemView.findViewById(R.id.my_car_user_name);
            status = (TextView) itemView.findViewById(R.id.my_car_use_status);
            destance = (TextView) itemView.findViewById(R.id.my_car_travel_destance);
            powertv = (TextView) itemView.findViewById(R.id.my_car_power_tv);
            caraddress = (TextView) itemView.findViewById(R.id.my_car_address);
            powerimg = (ImageView) itemView.findViewById(R.id.my_car_power_img);
            setting = (LinearLayout) itemView.findViewById(R.id.my_car_setting_llyt);
            traverLLyt = (LinearLayout) itemView.findViewById(R.id.my_car_travel_recode);
            addressLlyt = (LinearLayout) itemView.findViewById(R.id.my_car_address_llyt);
            batteryLlyt = (LinearLayout) itemView.findViewById(R.id.item_my_car_recy);
        }
    }

    private void batteryImg(MyViewHolder holder, int mbatteryPower) {
        //  ImageView view = holder.getView(R.id.my_car_power_img);
        ImageView view = holder.powerimg;
        if (0 == mbatteryPower) {
            view.setImageResource(R.mipmap.power_0);
        } else if (0 < mbatteryPower && mbatteryPower < 10) {
            view.setImageResource(R.mipmap.power_1);
        } else if (10 <= mbatteryPower && mbatteryPower < 20) {
            view.setImageResource(R.mipmap.power_2);
        } else if (20 <= mbatteryPower && mbatteryPower < 30) {
            view.setImageResource(R.mipmap.power_3);
        } else if (30 <= mbatteryPower && mbatteryPower < 40) {
            view.setImageResource(R.mipmap.power_4);
        } else if (40 <= mbatteryPower && mbatteryPower < 50) {
            view.setImageResource(R.mipmap.power_5);
        } else if (50 <= mbatteryPower && mbatteryPower < 60) {
            view.setImageResource(R.mipmap.power_6);
        } else if (60 <= mbatteryPower && mbatteryPower < 70) {
            view.setImageResource(R.mipmap.power_7);
        } else if (70 <= mbatteryPower && mbatteryPower < 80) {
            view.setImageResource(R.mipmap.power_8);
        } else if (80 <= mbatteryPower && mbatteryPower < 90) {
            view.setImageResource(R.mipmap.power_9);
        } else if (90 <= mbatteryPower && mbatteryPower < 100) {
            view.setImageResource(R.mipmap.power_9);
        } else if (100 == mbatteryPower) {
            view.setImageResource(R.mipmap.power_10);
        }
    }

    public void getAddress(String location) {
        if (location.length() <= 0) {
            return;
        }
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(Double.parseDouble(location.substring(0, location.lastIndexOf(","))), Double.parseDouble(location.substring(location.lastIndexOf(",") + 1))), 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);

    }

    private String getTravelStatus(int status) {
        //0 代表熄火，1 代表点火
        String string =  context.getString(R.string.not_driver);
        if (status == 0) {
            string = context.getString(R.string.not_driver);
        } else if (status == 1) {
            string = context.getString(R.string.drivering);
        }
        return string;
    }

    private void initListener() {
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                if (i == 1000) {
                    address = regeocodeResult.getRegeocodeAddress().getFormatAddress();
                    notifyDataSetChanged();
                } else {
                    address = context.getString(R.string.cont_get_car_location);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });
    }


}

