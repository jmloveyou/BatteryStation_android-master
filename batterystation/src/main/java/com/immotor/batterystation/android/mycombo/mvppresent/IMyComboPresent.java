package com.immotor.batterystation.android.mycombo.mvppresent;

/**
 * Created by jm on 2017/7/31 0031.
 */

public interface IMyComboPresent {
   void  requestMyCombo(String token);
   void requestCancleLowerCombo(String token);

   void requestAutoExpense( String token,boolean status);

   void requestgetAutoStatus(String token);
}
