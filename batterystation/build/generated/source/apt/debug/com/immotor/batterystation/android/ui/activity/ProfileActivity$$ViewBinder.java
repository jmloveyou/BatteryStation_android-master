// Generated code from Butter Knife. Do not modify!
package com.immotor.batterystation.android.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ProfileActivity$$ViewBinder<T extends com.immotor.batterystation.android.ui.activity.ProfileActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131755298, "field 'avatarView' and method 'ActionAvatar'");
    target.avatarView = finder.castView(view, 2131755298, "field 'avatarView'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.ActionAvatar();
        }
      });
    view = finder.findRequiredView(source, 2131755301, "field 'modifyNameView' and method 'actionNamePanel'");
    target.modifyNameView = view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.actionNamePanel();
        }
      });
    view = finder.findRequiredView(source, 2131755303, "field 'modifyGenderView' and method 'actionGenderPanel'");
    target.modifyGenderView = view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.actionGenderPanel();
        }
      });
    view = finder.findRequiredView(source, 2131755305, "field 'modifyBirthdayView' and method 'actionBirthdayPanel'");
    target.modifyBirthdayView = view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.actionBirthdayPanel();
        }
      });
    view = finder.findRequiredView(source, 2131755299, "field 'nameShowView'");
    target.nameShowView = finder.castView(view, 2131755299, "field 'nameShowView'");
    view = finder.findRequiredView(source, 2131755300, "field 'creditView'");
    target.creditView = finder.castView(view, 2131755300, "field 'creditView'");
    view = finder.findRequiredView(source, 2131755302, "field 'nameView'");
    target.nameView = finder.castView(view, 2131755302, "field 'nameView'");
    view = finder.findRequiredView(source, 2131755304, "field 'sexView'");
    target.sexView = finder.castView(view, 2131755304, "field 'sexView'");
    view = finder.findRequiredView(source, 2131755306, "field 'birthdayView'");
    target.birthdayView = finder.castView(view, 2131755306, "field 'birthdayView'");
    view = finder.findRequiredView(source, 2131755308, "field 'phoneView'");
    target.phoneView = finder.castView(view, 2131755308, "field 'phoneView'");
    view = finder.findRequiredView(source, 2131755309, "method 'actionLogout'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.actionLogout();
        }
      });
  }

  @Override public void unbind(T target) {
    target.avatarView = null;
    target.modifyNameView = null;
    target.modifyGenderView = null;
    target.modifyBirthdayView = null;
    target.nameShowView = null;
    target.creditView = null;
    target.nameView = null;
    target.sexView = null;
    target.birthdayView = null;
    target.phoneView = null;
  }
}
