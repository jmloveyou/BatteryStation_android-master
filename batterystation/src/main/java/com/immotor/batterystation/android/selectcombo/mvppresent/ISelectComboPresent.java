package com.immotor.batterystation.android.selectcombo.mvppresent;

import android.content.Context;

/**
 * Created by jm on 2017/7/31 0031.
 */

public interface ISelectComboPresent {
   void  requestSelectCombo(String token);

   void requestUpdateCombo(String token,long id,int buycode, double comboprice);

   void requestLowerCombo(String token,long id,  int buycode, double price);

   void requestBuyCombo(String token , long id,int code,double price);

   void requestAutoExpense( String token,boolean status);

   void requestgetAutoStatus(String token);

}
