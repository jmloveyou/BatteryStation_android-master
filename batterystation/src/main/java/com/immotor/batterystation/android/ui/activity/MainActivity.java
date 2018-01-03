package com.immotor.batterystation.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.immotor.batterystation.android.MyApplication;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.entity.BatteryStationDetailInfo;
import com.immotor.batterystation.android.entity.BatteryStationInfo;
import com.immotor.batterystation.android.entity.MyRentInfo;
import com.immotor.batterystation.android.entity.RentQueryInfo;
import com.immotor.batterystation.android.http.HttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;
import com.immotor.batterystation.android.ui.base.BaseActivity;
import com.immotor.batterystation.android.util.LogUtil;

import java.util.Arrays;
import java.util.List;

import butterknife.OnClick;

public class MainActivity extends BaseActivity {



    private String orderNo = "";

    private final int RENT_STATUS_NOTHING = -1;
    private final int RENT_STATUS_RENT = 1;
    private final int RENT_STATUS_CANCEL = 2;
    private final int RENT_STATUS_UPDATE = 3;
    private int curRentStatus = RENT_STATUS_NOTHING;
    MyRentInfo curRentInfo = new MyRentInfo();
    BatteryStationInfo curStation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void initUIView() {

    }

    @OnClick(R.id.get_power_station)
    public void ActionGetStation(){
        httpGetStation(22.57741659165173, 113.9264765381813, 2000);
    }


    @OnClick(R.id.get_station_detail)
    public void ActionGetStationDetail(){
        List<BatteryStationInfo> list = MyApplication.getStationList();
        if(list!=null&&list.size()>0) {
            httpGetStationDetail(list.get(0).getpID());
            curStation = list.get(0);
//            curRentInfo.setStation(list.get(0));
        }
    }

    @OnClick(R.id.rent_battery)
    public void ActionRentBattery(){
        BatteryStationDetailInfo info = MyApplication.getStationDetail();
        if(info!=null) {
            for(int i = 0; i < info.getPorts().size(); i++){
                BatteryStationDetailInfo.PortInStation port = info.getPorts().get(i);
                if(port.getBattery()!=null && port.getBattery().getStatus()==0){
                    httpRentBattery(curStation.getpID(), mPreferences.getUserID()+"", 0, Arrays.asList(port.getPort()+""));
                    return;
                }
            }
        }
    }

    @OnClick(R.id.cancle_order)
    public void ActionCancelRent(){
     //   if(curRentInfo!=null && curRentInfo.getStation()!=null) {
       //     httpCancelRent(curRentInfo.getStation().getpID(), mPreferences.getUserID()+"", curRentInfo.getAuth());
      //  }
    }

    @OnClick(R.id.query_for_rent)
    public void ActionQueryForRent(){
     //   if(curRentInfo!=null && curRentInfo.getStation()!=null) {
         //   httpQueryForRent(curRentInfo.getStation().getpID(), mPreferences.getUserID()+"", orderNo);
      //  }
    }


    @OnClick(R.id.get_my_rent)
    public void ActionGetMyRent(){
        httpGetMyRent(mPreferences.getUserID()+"");
    }
    private void httpGetStation(double latitude, double longitude, double radius){
        if (!isNetworkAvaliable()) return;

        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<List<BatteryStationInfo>>() {
            @Override
            public void onError(Throwable e) {
                showSnackbar(e.getMessage());
            }

            @Override
            public void onNext(List<BatteryStationInfo> result) {
                if(result!=null){
                    MyApplication.setStationList(result);
                }
            }
        };
        HttpMethods.getInstance().getPowerStation(new ProgressSubscriber(subscriberOnNextListener, this, null), latitude, longitude, radius);
    }

    @OnClick(R.id.update_battery)
    public void ActionUpdateBattery(){
//        if(curRentInfo!=null && curRentInfo.getStation()!=null){
//            if(curRentInfo.getStation().getPorts().size()>1){
//                List<String> newPorts = Arrays.asList(curRentInfo.getStation().getPorts().get(0));
//                httpUpdateBattery(curRentInfo.getStation().getpID(),mPreferences.getUserID()+"", curRentInfo.getAuth(), newPorts);
//            }else {
//                if(MyApplication.getStationDetail()!=null && MyApplication.getStationDetail().getId().equals(curRentInfo.getStation().getpID())){
//                    List<String> own = new ArrayList<>();
//                    if(curRentInfo.getStation().getPorts().size()==1){
//                        own.add(curRentInfo.getStation().getPorts().get(0));
//                    }
//                    for(int i = 0; i < MyApplication.getStationDetail().getPorts().size(); i++){
//                        if(MyApplication.getStationDetail().getPorts().get(i).getBattery().getStatus()==0){
//                            own.add(MyApplication.getStationDetail().getPorts().get(i).getPort()+"");
//                            httpUpdateBattery(curRentInfo.getStation().getpID(),mPreferences.getUserID()+"", curRentInfo.getAuth(), own);
//                            return;
//                        }
//                    }
//                }
//            }
//
//        }

    }

    private void httpGetStationDetail(String pID){
        if (!isNetworkAvaliable()) return;
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<BatteryStationDetailInfo>() {
            @Override
            public void onError(Throwable e) {
                showSnackbar(e.getMessage());
            }

            @Override
            public void onNext(BatteryStationDetailInfo result) {
                if(result!=null){
                    MyApplication.setStationDetail(result);
                }
            }
        };
        HttpMethods.getInstance().getPowerStationDetail(new ProgressSubscriber(subscriberOnNextListener, this), pID);
    }

    private void httpRentBattery(String pID, String uID, int code, List<String> ports){
        if (!isNetworkAvaliable()) return;
        if(curRentStatus !=RENT_STATUS_NOTHING){
            return;
        }
        curRentStatus = RENT_STATUS_RENT;
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<Integer>() {
            @Override
            public void onError(Throwable e) {
                showSnackbar(e.getMessage());
                curRentStatus = RENT_STATUS_NOTHING;
            }

            @Override
            public void onNext(Integer result) {
                if(result!=null){
                    orderNo = result.toString();
                }
            }
        };
        HttpMethods.getInstance().rentBattery(new ProgressSubscriber(subscriberOnNextListener, this),mPreferences.getToken(), pID,uID,code,ports);
    }

    private void httpCancelRent(String pID, String uID, int code){
        if (!isNetworkAvaliable()) return;
        if(curRentStatus !=RENT_STATUS_NOTHING){
            return;
        }
        curRentStatus = RENT_STATUS_CANCEL;
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<Integer>() {
            @Override
            public void onError(Throwable e) {
                showSnackbar(e.getMessage());
                curRentStatus = RENT_STATUS_NOTHING;
            }

            @Override
            public void onNext(Integer result) {
                if(result!=null){
                    orderNo = result.toString();
                }
            }
        };
        HttpMethods.getInstance().cancelRent(new ProgressSubscriber(subscriberOnNextListener, this),mPreferences.getToken(), pID,uID,code);
    }



    private void httpQueryForRent(String pID, String uID, String no){
        if (!isNetworkAvaliable()) return;
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<RentQueryInfo>() {
            @Override
            public void onError(Throwable e) {
                showSnackbar(e.getMessage());
                curRentStatus = RENT_STATUS_NOTHING;
            }

            @Override
            public void onNext(RentQueryInfo result) {
                if(result!=null){
                    LogUtil.d("result:"+result);
                 //   curRentInfo.setAuth(result.getAuth());
                    curRentInfo.setExpire(result.getExpire());
                    try{
                        if(curRentStatus ==RENT_STATUS_RENT){
                            //
                        //    curRentInfo.setStation(curStation);
                        }else if(curRentStatus == RENT_STATUS_CANCEL){
                            //
                      //      curRentInfo.setAuth(0);
                        }else if(curRentStatus == RENT_STATUS_UPDATE){
                            //
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
                curRentStatus = RENT_STATUS_NOTHING;
            }
        };
        HttpMethods.getInstance().queryForRent(new ProgressSubscriber(subscriberOnNextListener, this),mPreferences.getToken(), pID,no,uID);
    }

    private void httpGetMyRent(String uID){
        if (!isNetworkAvaliable()) return;
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<MyRentInfo>() {
            @Override
            public void onError(Throwable e) {
                showSnackbar(e.getMessage());
            }

            @Override
            public void onNext(MyRentInfo result) {
                if(result!=null){
                    curRentInfo = result;
                    if(curStation==null){
                  //      curStation = curRentInfo.getStation();
                    }
//                    MyApplication.setMyRentInfo(result);
                }
            }
        };
     //   HttpMethods.getInstance().getMyRent(new ProgressSubscriber(subscriberOnNextListener, this),mPreferences.getToken(), uID);
    }

    private void httpUpdateBattery(String pID, String uID, int code, List<String> ports){
        if (!isNetworkAvaliable()) return;
        if(curRentStatus !=RENT_STATUS_NOTHING){
            return;
        }
        curRentStatus = RENT_STATUS_UPDATE;
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<Integer>() {
            @Override
            public void onError(Throwable e) {
                showSnackbar(e.getMessage());
                curRentStatus = RENT_STATUS_NOTHING;
            }

            @Override
            public void onNext(Integer result) {
                if(result!=null){
                    orderNo = result.toString();
//                    curRentInfo = result;
//                    if(curStation==null){
//                        curStation = curRentInfo.getStation();
//                    }
//                    MyApplication.setMyRentInfo(result);
                }
            }
        };
        HttpMethods.getInstance().updateBattery(new ProgressSubscriber(subscriberOnNextListener, this),mPreferences.getToken(), pID, uID, code, ports);
    }

    @OnClick(R.id.start_map)
    public void ActionStartMap(){
        startActivity(new Intent(this, HomeActivity.class));
    }

}
