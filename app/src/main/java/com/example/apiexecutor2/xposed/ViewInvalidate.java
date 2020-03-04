package com.example.apiexecutor2.xposed;

import android.view.View;

import com.example.apiexecutor2.dataRecord.AnimViewRelationRecog;

import de.robv.android.xposed.XC_MethodHook;

public class ViewInvalidate extends XC_MethodHook {
    private AnimViewRelationRecog relationRecog = AnimViewRelationRecog.getInstance();
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        View view = (View) param.thisObject;
        relationRecog.recordAnimatorView(view);
    }
}
