package com.immotor.batterystation.android.mapsearch;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.app.AppConstant;
import com.immotor.batterystation.android.ui.activity.HomeActivity;
import com.immotor.batterystation.android.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jm on 2017/9/11 0011.
 */

public class MapSearchActivity extends BaseActivity implements TextWatcher, View.OnClickListener, PoiSearch.OnPoiSearchListener, Inputtips.InputtipsListener {

    private RecyclerView mRecyView;
    private RecomandAdapter mRecomandAdapter;
    private ImageView mClear;
    private EditText mEditText;
    private ImageView mBack;
    private PoiSearch.Query query;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search);

    }

    @Override
    public void initUIView() {
        mClear = (ImageView) findViewById(R.id.search_clear_view);
        mClear.setOnClickListener(this);
        mBack = (ImageView) findViewById(R.id.search_action_bar_btn_return);
        mBack.setOnClickListener(this);
        mEditText = (EditText) findViewById(R.id.search_edit_view);
        mEditText.addTextChangedListener(this);
        mRecyView = (RecyclerView) findViewById(R.id.search_recyView);
        mRecyView.setLayoutManager(new LinearLayoutManager(this));
        mRecomandAdapter = new RecomandAdapter(R.layout.item_search_layout);
        mRecyView.setAdapter(mRecomandAdapter);
        initInputListener();
        initRecyListener();
    }

    private void initRecyListener() {
        mRecomandAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mRecomandAdapter.getData().get(position).getPoint()==null || mRecomandAdapter.getData().get(position).getPoint().getLatitude()==0 || mRecomandAdapter.getData().get(position).getPoint().getLongitude()==0) {
                    Toast.makeText(MapSearchActivity.this, R.string.cont_get_point, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MapSearchActivity.this,HomeActivity.class);
                intent.putExtra(AppConstant.KEY_LATITUDE_TYPE,mRecomandAdapter.getData().get(position).getPoint().getLatitude());
                intent.putExtra(AppConstant.KEY_LONGITUDE_TYPE,mRecomandAdapter.getData().get(position).getPoint().getLongitude());
                startActivity(intent);
                finish();
            }
        });
    }

    private void initInputListener() {
        mEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (mEditText.getText().toString().trim().isEmpty()) {
                        Toast.makeText(MapSearchActivity.this, R.string.import_search_address, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    query = new PoiSearch.Query(mEditText.getText().toString().trim(), "", null);
                    //keyWord表示搜索字符串，
                    //第二个参数表示POI搜索类型，二者选填其一，选用POI搜索类型时建议填写类型代码，码表可以参考下方（而非文字）
                    //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
                    query.setPageSize(100);// 设置每页最多返回多少条poiitem
                    query.setPageNum(0);//设置查询页码
                    PoiSearch poiSearch = new PoiSearch(MapSearchActivity.this, query);
                    poiSearch.setOnPoiSearchListener(MapSearchActivity.this);
                    poiSearch.searchPOIAsyn();
                    return true;
                } else {
                    return false;
                }

            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //第二个参数传入null或者“”代表在全国进行检索，否则按照传入的city进行检索
        InputtipsQuery inputquery = new InputtipsQuery(s.toString(), null);
        //  inputquery.setCityLimit(true);//限制在当前城市

        Inputtips inputTips = new Inputtips(MapSearchActivity.this, inputquery);
        inputTips.setInputtipsListener(this);
        inputTips.requestInputtipsAsyn();
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mEditText.getText().toString().isEmpty()) {
            mEditText.setHint(R.string.import_address);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.search_clear_view:
                mEditText.setText("");
                mEditText.setHint(R.string.import_address);
                break;

            case R.id.search_action_bar_btn_return:
                Intent intent = new Intent(MapSearchActivity.this,HomeActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MapSearchActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEditText.removeTextChangedListener(this);
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int code) {
        if (code == 1000) {
            if (poiResult.getPois().size() == 0) {
                Toast.makeText(this, R.string.import_crrect_address, Toast.LENGTH_SHORT).show();
                return;
            }
            if (poiResult != null && poiResult.getPois() != null) {
                List<Tip> list = new ArrayList();
                for (int i = 0; i < poiResult.getPois().size(); i++) {
                    if (poiResult.getPois().get(i).getLatLonPoint() != null) {
                        Tip tip = new Tip();
                        tip.setAddress(poiResult.getPois().get(i).getSnippet());
                        tip.setName(poiResult.getPois().get(i).getTitle());
                        tip.setPostion(poiResult.getPois().get(i).getLatLonPoint());
                        list.add(tip);
                    }
                }
                mRecomandAdapter.replaceData(list);

            } else {
                Toast.makeText(this, R.string.query_fail, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this,  R.string.query_fail, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onGetInputtips(List<Tip> list, int code) {
    /*a 、由于提示中会出现相同的关键字，但是这些关键字所在区域不同，使用时可以通过 tipList.get(i).getDistrict() 获得区域，也可以在提示时在关键字后加上区域。
    b、当 Tip 的 getPoiID() 返回空，并且 getPoint() 也返回空时，表示该提示词不是一个真实存在的 POI，这时区域、经纬度参数都是空的，此时可根据该提示词进行POI关键词搜索
    c、当 Tip 的 getPoiID() 返回不为空，但 getPoint() 返回空时，表示该提示词是一个公交线路名称，此时用这个id进行公交线路查询。
    d、当 Tip 的 getPoiID() 返回不为空，且 getPoint() 也不为空时，表示该提示词一个真实存在的POI，可直接显示在地图上。*/
        if (code == 1000) {
            if (list.size() == 0) {
                Toast.makeText(this,R.string.import_crrect_address, Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPoiID() == null || list.get(i).getPoint() == null) {
                    list.remove(i);
                    break;
                }
            }
            mRecomandAdapter.replaceData(list);
        } else {
            if (!mEditText.getText().toString().trim().isEmpty()) {
                Toast.makeText(this,  R.string.query_fail, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
