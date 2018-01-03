// Generated code from Butter Knife. Do not modify!
package com.immotor.batterystation.android.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MainActivity$$ViewBinder<T extends com.immotor.batterystation.android.ui.activity.MainActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131755251, "method 'ActionGetStation'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.ActionGetStation();
        }
      });
    view = finder.findRequiredView(source, 2131755252, "method 'ActionGetStationDetail'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.ActionGetStationDetail();
        }
      });
    view = finder.findRequiredView(source, 2131755253, "method 'ActionRentBattery'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.ActionRentBattery();
        }
      });
    view = finder.findRequiredView(source, 2131755255, "method 'ActionCancelRent'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.ActionCancelRent();
        }
      });
    view = finder.findRequiredView(source, 2131755257, "method 'ActionQueryForRent'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.ActionQueryForRent();
        }
      });
    view = finder.findRequiredView(source, 2131755256, "method 'ActionGetMyRent'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.ActionGetMyRent();
        }
      });
    view = finder.findRequiredView(source, 2131755254, "method 'ActionUpdateBattery'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.ActionUpdateBattery();
        }
      });
    view = finder.findRequiredView(source, 2131755258, "method 'ActionStartMap'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.ActionStartMap();
        }
      });
  }

  @Override public void unbind(T target) {
  }
}
