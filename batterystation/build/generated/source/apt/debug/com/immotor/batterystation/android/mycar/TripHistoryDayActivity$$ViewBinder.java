// Generated code from Butter Knife. Do not modify!
package com.immotor.batterystation.android.mycar;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class TripHistoryDayActivity$$ViewBinder<T extends com.immotor.batterystation.android.mycar.TripHistoryDayActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131755392, "field 'mToolbar'");
    target.mToolbar = finder.castView(view, 2131755392, "field 'mToolbar'");
  }

  @Override public void unbind(T target) {
    target.mToolbar = null;
  }
}
