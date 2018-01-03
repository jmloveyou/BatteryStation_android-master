// Generated code from Butter Knife. Do not modify!
package com.immotor.batterystation.android.mycar;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class TripHistoryFragment$$ViewBinder<T extends com.immotor.batterystation.android.mycar.TripHistoryFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131755244, "field 'recyclerView'");
    target.recyclerView = finder.castView(view, 2131755244, "field 'recyclerView'");
  }

  @Override public void unbind(T target) {
    target.recyclerView = null;
  }
}
