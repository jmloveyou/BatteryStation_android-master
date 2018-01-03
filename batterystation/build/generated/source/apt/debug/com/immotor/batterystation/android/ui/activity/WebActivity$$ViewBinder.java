// Generated code from Butter Knife. Do not modify!
package com.immotor.batterystation.android.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class WebActivity$$ViewBinder<T extends com.immotor.batterystation.android.ui.activity.WebActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131755392, "field 'mToolbar'");
    target.mToolbar = finder.castView(view, 2131755392, "field 'mToolbar'");
    view = finder.findRequiredView(source, 2131755354, "field 'webView'");
    target.webView = finder.castView(view, 2131755354, "field 'webView'");
    view = finder.findRequiredView(source, 2131755353, "field 'loadingProgress'");
    target.loadingProgress = finder.castView(view, 2131755353, "field 'loadingProgress'");
  }

  @Override public void unbind(T target) {
    target.mToolbar = null;
    target.webView = null;
    target.loadingProgress = null;
  }
}
