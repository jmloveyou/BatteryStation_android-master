// Generated code from Butter Knife. Do not modify!
package com.immotor.batterystation.android.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class FirstEntryRentPayActivity$$ViewBinder<T extends com.immotor.batterystation.android.ui.activity.FirstEntryRentPayActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131755227, "field 'wxFlag'");
    target.wxFlag = finder.castView(view, 2131755227, "field 'wxFlag'");
    view = finder.findRequiredView(source, 2131755229, "field 'zfbFlag'");
    target.zfbFlag = finder.castView(view, 2131755229, "field 'zfbFlag'");
    view = finder.findRequiredView(source, 2131755236, "field 'oneFlag'");
    target.oneFlag = finder.castView(view, 2131755236, "field 'oneFlag'");
    view = finder.findRequiredView(source, 2131755239, "field 'twoFlag'");
    target.twoFlag = finder.castView(view, 2131755239, "field 'twoFlag'");
    view = finder.findRequiredView(source, 2131755238, "field 'twoTittle'");
    target.twoTittle = finder.castView(view, 2131755238, "field 'twoTittle'");
    view = finder.findRequiredView(source, 2131755235, "field 'oneTittle'");
    target.oneTittle = finder.castView(view, 2131755235, "field 'oneTittle'");
    view = finder.findRequiredView(source, 2131755234, "field 'oneLlyt' and method 'actionOneSelect'");
    target.oneLlyt = finder.castView(view, 2131755234, "field 'oneLlyt'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.actionOneSelect();
        }
      });
    view = finder.findRequiredView(source, 2131755237, "field 'twoLlyt' and method 'actionTWOSelect'");
    target.twoLlyt = finder.castView(view, 2131755237, "field 'twoLlyt'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.actionTWOSelect();
        }
      });
    view = finder.findRequiredView(source, 2131755241, "field 'numSelectMsg'");
    target.numSelectMsg = finder.castView(view, 2131755241, "field 'numSelectMsg'");
    view = finder.findRequiredView(source, 2131755243, "field 'noDataLayout'");
    target.noDataLayout = finder.castView(view, 2131755243, "field 'noDataLayout'");
    view = finder.findRequiredView(source, 2131755463, "field 'noNetLayout'");
    target.noNetLayout = finder.castView(view, 2131755463, "field 'noNetLayout'");
    view = finder.findRequiredView(source, 2131755233, "field 'upLlyt'");
    target.upLlyt = finder.castView(view, 2131755233, "field 'upLlyt'");
    view = finder.findRequiredView(source, 2131755240, "field 'downLlyt'");
    target.downLlyt = finder.castView(view, 2131755240, "field 'downLlyt'");
    view = finder.findRequiredView(source, 2131755464, "method 'noNetTry'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.noNetTry();
        }
      });
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
    target.oneFlag = null;
    target.twoFlag = null;
    target.twoTittle = null;
    target.oneTittle = null;
    target.oneLlyt = null;
    target.twoLlyt = null;
    target.numSelectMsg = null;
    target.noDataLayout = null;
    target.noNetLayout = null;
    target.upLlyt = null;
    target.downLlyt = null;
  }
}
