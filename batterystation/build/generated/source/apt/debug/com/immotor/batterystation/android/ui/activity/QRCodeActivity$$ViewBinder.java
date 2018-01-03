// Generated code from Butter Knife. Do not modify!
package com.immotor.batterystation.android.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class QRCodeActivity$$ViewBinder<T extends com.immotor.batterystation.android.ui.activity.QRCodeActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131755310, "field 'mViewPreview'");
    target.mViewPreview = finder.castView(view, 2131755310, "field 'mViewPreview'");
    view = finder.findRequiredView(source, 2131755311, "field 'mViewFinder'");
    target.mViewFinder = finder.castView(view, 2131755311, "field 'mViewFinder'");
    view = finder.findRequiredView(source, 2131755314, "field 'buttonSwitch' and method 'swichLight'");
    target.buttonSwitch = view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.swichLight();
        }
      });
    view = finder.findRequiredView(source, 2131755313, "field 'manualInput' and method 'gotoManualInput'");
    target.manualInput = view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.gotoManualInput();
        }
      });
    view = finder.findRequiredView(source, 2131755315, "field 'flashImage'");
    target.flashImage = finder.castView(view, 2131755315, "field 'flashImage'");
  }

  @Override public void unbind(T target) {
    target.mViewPreview = null;
    target.mViewFinder = null;
    target.buttonSwitch = null;
    target.manualInput = null;
    target.flashImage = null;
  }
}
