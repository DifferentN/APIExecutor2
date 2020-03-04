package com.example.apiexecutor2.xposed;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.example.apiexecutor2.util.LogWriter;
import com.example.apiexecutor2.util.TransformJsonUtil;
import com.example.apiexecutor2.util.ViewUtil;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;

public class ValueAnimatorEndAnimation extends XC_MethodHook {
    private LogWriter logWriter = LogWriter.getInstance();
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        Object obj = param.thisObject;
        if(obj instanceof ObjectAnimator){
            JSONObject jsonObject = TransformJsonUtil.transformObjectAnimator((ObjectAnimator) obj);
            if(jsonObject!=null){
                logWriter.writeLog(jsonObject.toJSONString());
            }
        }else{
            ValueAnimator valueAnimator = (ValueAnimator) obj;
            List<JSONObject> jsonObjectList = TransformJsonUtil.transformValueAnimator(valueAnimator);
            for(JSONObject jsonObject:jsonObjectList){
                logWriter.writeLog(jsonObject.toJSONString());
            }
        }
    }
}
