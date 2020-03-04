package com.example.apiexecutor2.xposed;

import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.example.apiexecutor2.util.LogWriter;
import com.example.apiexecutor2.util.TransformJsonUtil;

import de.robv.android.xposed.XC_MethodHook;

public class ViewApplyLegacyAnimation extends XC_MethodHook {
    private LogWriter logWriter = LogWriter.getInstance();
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        //false 表示动画已经完成
        boolean isFinish = (boolean) param.getResult();
        if(!isFinish){
            View view = (View) param.thisObject;
            JSONObject jsonObject = TransformJsonUtil.transformAnimation(view);
            logWriter.writeLog(jsonObject.toJSONString());
        }
    }
}
