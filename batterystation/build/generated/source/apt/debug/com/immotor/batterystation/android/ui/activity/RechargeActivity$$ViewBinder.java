// Generated code from Butter Knife. Do not modify!
package com.immotor.batterystation.android.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class RechargeActivity$$ViewBinder<T extends com.immotor.batterystation.android.ui.activity.RechargeActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131755244, "field 'mRecyclerContainer'");
    target.mRecyclerContainer = finder.castView(view, 2131755244, "field 'mRecyclerContainer'");
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
    view = finder.findRequiredView(source, 2131755317, "method 'actionRechargeProtocol'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.actionRechargeProtocol();
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
    target.mRecyclerContainer = null;
    target.wxFlag = null;
    target.zfbFlag = null;
  }
}
