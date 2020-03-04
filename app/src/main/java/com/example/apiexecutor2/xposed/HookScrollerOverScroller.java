package com.example.apiexecutor2.xposed;

import com.alibaba.fastjson.JSONObject;
import com.example.apiexecutor2.util.LogWriter;
import com.example.apiexecutor2.util.TransformJsonUtil;

import de.robv.android.xposed.XC_MethodHook;

public class HookScrollerOverScroller extends XC_MethodHook {
    private LogWriter logWriter = LogWriter.getInstance();
    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        //false代表滑动完成
        boolean isFinish = (boolean) param.getResult();
        if(isFinish){
            return;
        }
        Object obj = param.thisObject;
        JSONObject jsonObject = TransformJsonUtil.transformScroll(obj);
        logWriter.writeLog(jsonObject.toJSONString());
    }
}
