// Generated code from Butter Knife. Do not modify!
package com.immotor.batterystation.android.ui.activity;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ModifyProfileActivity$$ViewBinder<T extends com.immotor.batterystation.android.ui.activity.ModifyProfileActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131755271, "field 'nameEdit'");
    target.nameEdit = finder.castView(view, 2131755271, "field 'nameEdit'");
    view = finder.findRequiredView(source, 2131755272, "field 'genderGroup'");
    target.genderGroup = finder.castView(view, 2131755272, "field 'genderGroup'");
    view = finder.findRequiredView(source, 2131755273, "field 'selectMale'");
    target.selectMale = finder.castView(view, 2131755273, "field 'selectMale'");
    view = finder.findRequiredView(source, 2131755274, "field 'selectFemale'");
    target.selectFemale = finder.castView(view, 2131755274, "field 'selectFemale'");
    view = finder.findRequiredView(source, 2131755275, "field 'datePicker'");
    target.datePicker = finder.castView(view, 2131755275, "field 'datePicker'");
  }

  @Override public void unbind(T target) {
    target.nameEdit = null;
    target.genderGroup = null;
    target.selectMale = null;
    target.selectFemale = null;
    target.datePicker = null;
  }
}
