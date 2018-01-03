package com.immotor.batterystation.android.mapsearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Tip;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.immotor.batterystation.android.R;

/**
 * Created by jm on 2017/9/11 0011.
 */

public class RecomandAdapter extends BaseQuickAdapter<Tip, BaseViewHolder> {

    public RecomandAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, Tip item) {
        viewHolder.setText(R.id.search_address, item.getName())
                .setText(R.id.search_address_detail, item.getAddress());

   //     mAdddress = (TextView) itemView.findViewById(R.id.search_address);
  //      mAdddressDetail = (TextView) itemView.findViewById(R.id.search_address_detail);
    }
}

     /*
        extends RecyclerView.Adapter<RecomandAdapter.ListHolder> {

    private List<PositionEntity> mPositionEntity;
    private Context context;

    public RecomandAdapter(Context context) {
        this.context = context;
    }

    public void setPositionEntities(List<PositionEntity> positionEntity) {
        mPositionEntity = positionEntity;
    }

    @Override
    public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_search_layout, parent, false);
        return new ListHolder(view);

    }

    @Override
    public void onBindViewHolder(RecomandAdapter.ListHolder holder, int position) {
        holder.setData(position);
    }

    public PositionEntity getItem(int position) {
        return mPositionEntity.get(position);
    }
    @Override
    public int getItemCount() {
        int count = 0;
         if(mPositionEntity!=null){
             count = mPositionEntity.size();
         }else{
             count = 0;
         }
        return count;
    }

    class ListHolder extends RecyclerView.ViewHolder {

        TextView mAdddress;
        TextView mAdddressDetail;

        public ListHolder(View itemView) {
            super(itemView);
            mAdddress = (TextView) itemView.findViewById(R.id.search_address);
            mAdddressDetail = (TextView) itemView.findViewById(R.id.search_address_detail);
        }

        public void setData(int position) {
            mAdddress.setText(mPositionEntity.get(position).getCity());
            mAdddressDetail.setText(mPositionEntity.get(position).getAddress());
        }


    }
}
*/