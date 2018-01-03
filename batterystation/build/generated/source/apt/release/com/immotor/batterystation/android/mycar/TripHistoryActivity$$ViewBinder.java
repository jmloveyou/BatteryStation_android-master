// Generated code from Butter Knife. Do not modify!
package com.immotor.batterystation.android.mycar;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class TripHistoryActivity$$ViewBinder<T extends com.immotor.batterystation.android.mycar.TripHistoryActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131755392, "field 'mToolbar'");
    target.mToolbar = finder.castView(view, 2131755392, "field 'mToolbar'");
    view = finder.findRequiredView(source, 2131755352, "field 'mTabLayout'");
    target.mTabLayout = finder.castView(view, 2131755352, "field 'mTabLayout'");
    view = finder.findRequiredView(source, 2131755345, "field 'mViewPager'");
    target.mViewPager = finder.castView(view, 2131755345, "field 'mViewPager'");
    view = finder.findRequiredView(source, 2131755243, "field 'noDataLayout'");
    target.noDataLayout = finder.castView(view, 2131755243, "field 'noDataLayout'");
  }

  @Override public void unbind(T target) {
    target.mToolbar = null;
    target.mTabLayout = null;
    target.mViewPager = null;
    target.noDataLayout = null;
  }
}
