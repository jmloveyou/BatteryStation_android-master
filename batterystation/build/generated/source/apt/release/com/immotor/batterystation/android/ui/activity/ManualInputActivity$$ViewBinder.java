// Generated code from Butter Knife. Do not modify!
package com.immotor.batterystation.android.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ManualInputActivity$$ViewBinder<T extends com.immotor.batterystation.android.ui.activity.ManualInputActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131755270, "field 'inputCodeEdit'");
    target.inputCodeEdit = finder.castView(view, 2131755270, "field 'inputCodeEdit'");
    view = finder.findRequiredView(source, 2131755245, "method 'actionOK'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.actionOK();
        }
      });
  }

  @Override public void unbind(T target) {
    target.inputCodeEdit = null;
  }
}
