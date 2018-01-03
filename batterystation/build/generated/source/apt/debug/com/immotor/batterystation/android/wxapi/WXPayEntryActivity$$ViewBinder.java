// Generated code from Butter Knife. Do not modify!
package com.immotor.batterystation.android.wxapi;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class WXPayEntryActivity$$ViewBinder<T extends com.immotor.batterystation.android.wxapi.WXPayEntryActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131755392, "field 'mToolbar'");
    target.mToolbar = finder.castView(view, 2131755392, "field 'mToolbar'");
    view = finder.findRequiredView(source, 2131755296, "field 'info'");
    target.info = finder.castView(view, 2131755296, "field 'info'");
    view = finder.findRequiredView(source, 2131755245, "method 'actionOk'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.actionOk();
        }
      });
  }

  @Override public void unbind(T target) {
    target.mToolbar = null;
    target.info = null;
  }
}
