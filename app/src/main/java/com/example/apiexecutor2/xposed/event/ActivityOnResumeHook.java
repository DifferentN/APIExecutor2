package com.example.apiexecutor2.xposed.event;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import com.example.apiexecutor2.receive.LocalActivityReceiver;

import de.robv.android.xposed.XC_MethodHook;

public class ActivityOnResumeHook extends XC_MethodHook {

    public ActivityOnResumeHook() {
        super();
    }

    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//        super.beforeHookedMethod(param);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        Activity activity = (Activity) param.thisObject;
        ComponentName componentName = activity.getComponentName();
        String activityName = componentName.getClassName();
        Log.i("LZH","after resume "+componentName.getClassName());

        Intent intent = new Intent();
        intent.setAction(LocalActivityReceiver.ON_RESUME);
        intent.putExtra(LocalActivityReceiver.RESUME_ACTIVITY,activityName);
        activity.sendBroadcast(intent);
    }

}
