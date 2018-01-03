// Generated code from Butter Knife. Do not modify!
package com.immotor.batterystation.android.mycar;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class TripDetailGMapActivity$$ViewBinder<T extends com.immotor.batterystation.android.mycar.TripDetailGMapActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131755392, "field 'mToolbar'");
    target.mToolbar = finder.castView(view, 2131755392, "field 'mToolbar'");
    view = finder.findRequiredView(source, 2131755344, "field 'chartView'");
    target.chartView = finder.castView(view, 2131755344, "field 'chartView'");
    view = finder.findRequiredView(source, 2131755337, "field 'mileageValue'");
    target.mileageValue = finder.castView(view, 2131755337, "field 'mileageValue'");
    view = finder.findRequiredView(source, 2131755340, "field 'timeValue'");
    target.timeValue = finder.castView(view, 2131755340, "field 'timeValue'");
    view = finder.findRequiredView(source, 2131755343, "field 'batteryValue'");
    target.batteryValue = finder.castView(view, 2131755343, "field 'batteryValue'");
    view = finder.findRequiredView(source, 2131755341, "field 'timeUnitText'");
    target.timeUnitText = finder.castView(view, 2131755341, "field 'timeUnitText'");
    view = finder.findRequiredView(source, 2131755338, "field 'tripUnit'");
    target.tripUnit = finder.castView(view, 2131755338, "field 'tripUnit'");
  }

  @Override public void unbind(T target) {
    target.mToolbar = null;
    target.chartView = null;
    target.mileageValue = null;
    target.timeValue = null;
    target.batteryValue = null;
    target.timeUnitText = null;
    target.tripUnit = null;
  }
}
