// Generated code from Butter Knife. Do not modify!
package com.immotor.batterystation.android.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class LoginActivity$$ViewBinder<T extends com.immotor.batterystation.android.ui.activity.LoginActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131755246, "field 'editPhone'");
    target.editPhone = finder.castView(view, 2131755246, "field 'editPhone'");
    view = finder.findRequiredView(source, 2131755247, "field 'editCode'");
    target.editCode = finder.castView(view, 2131755247, "field 'editCode'");
    view = finder.findRequiredView(source, 2131755248, "field 'btnCode' and method 'ActionGetCode'");
    target.btnCode = finder.castView(view, 2131755248, "field 'btnCode'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.ActionGetCode();
        }
      });
    view = finder.findRequiredView(source, 2131755250, "field 'btnLogin' and method 'ActionLogin'");
    target.btnLogin = finder.castView(view, 2131755250, "field 'btnLogin'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.ActionLogin();
        }
      });
    view = finder.findRequiredView(source, 2131755249, "field 'protocol' and method 'ActionUserProtocol'");
    target.protocol = finder.castView(view, 2131755249, "field 'protocol'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.ActionUserProtocol();
        }
      });
  }

  @Override public void unbind(T target) {
    target.editPhone = null;
    target.editCode = null;
    target.btnCode = null;
    target.btnLogin = null;
    target.protocol = null;
  }
}
