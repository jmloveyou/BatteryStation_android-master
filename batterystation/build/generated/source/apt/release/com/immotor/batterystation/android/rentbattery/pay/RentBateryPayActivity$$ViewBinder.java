// Generated code from Butter Knife. Do not modify!
package com.immotor.batterystation.android.rentbattery.pay;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class RentBateryPayActivity$$ViewBinder<T extends com.immotor.batterystation.android.rentbattery.pay.RentBateryPayActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131755227, "field 'wxFlag'");
    target.wxFlag = finder.castView(view, 2131755227, "field 'wxFlag'");
    view = finder.findRequiredView(source, 2131755229, "field 'zfbFlag'");
    target.zfbFlag = finder.castView(view, 2131755229, "field 'zfbFlag'");
    view = finder.findRequiredView(source, 2131755226, "method 'actionWXPay'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.actionWXPay();
        }
      });
    view = finder.findRequiredView(source, 2131755228, "method 'actionZFBPay'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.actionZFBPay();
        }
      });
    view = finder.findRequiredView(source, 2131755230, "method 'ActionPay'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.ActionPay();
        }
      });
  }

  @Override public void unbind(T target) {
    target.wxFlag = null;
    target.zfbFlag = null;
  }
}
