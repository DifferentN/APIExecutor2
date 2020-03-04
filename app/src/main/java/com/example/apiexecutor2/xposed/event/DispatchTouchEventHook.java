package com.example.apiexecutor2.xposed.event;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.apiexecutor2.listener.TouchedView;
import com.example.apiexecutor2.util.LogWriter;
import com.example.apiexecutor2.util.ViewUtil;

import de.robv.android.xposed.XC_MethodHook;

public class DispatchTouchEventHook extends XC_MethodHook {
    private LogWriter logWriter;
    private String fileName = "methodLog.txt";
    public DispatchTouchEventHook(){
        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName;
        logWriter = LogWriter.getInstance();
    }
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        MotionEvent motionEvent = (MotionEvent) param.args[0];
        JSONObject jsonObject = null;
        Object obj =  param.thisObject;
        if(obj instanceof View){
            View view = (View) obj;
            jsonObject = writeInfo(view,motionEvent);
            writeThreadId(jsonObject);
            writeViewInfo(jsonObject,view);
            writeViewFlag(jsonObject,view);
            writeActivityID(jsonObject,view);
            logWriter.writeLog("before: "+jsonObject.toJSONString());
        }else if(obj instanceof Activity){
            Activity activity = (Activity) obj;
            jsonObject = writeActivityInfo(activity,motionEvent);
            logWriter.writeLog("before: "+jsonObject.toJSONString());
        }

    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        MotionEvent motionEvent = (MotionEvent) param.args[0];
        JSONObject json = null,resultJSON = null;
        Object obj = param.thisObject;
        //记录刚刚点击的view
        if(obj instanceof View){
            View view = (View) obj;
            sendDispatchView(view);
            json = writeInfo(view,motionEvent);
            resultJSON = writeResult(param);
            json.put("methodResult",resultJSON);
            writeThreadId(json);
            writeViewInfo(json,view);
            writeViewFlag(json,view);
            writeActivityID(json,view);
//            Log.i("LZH-Method","after: "+json.toJSONString());
            logWriter.writeLog("after: "+json.toJSONString());
        }else if(obj instanceof Activity){
            Activity activity = (Activity) obj;
            JSONObject jsonObject = writeActivityInfo(activity,motionEvent);
            logWriter.writeLog("after: "+jsonObject.toJSONString());
        }
    }
    private JSONObject writeActivityInfo(Activity activity, MotionEvent motionEvent){
        JSONObject json = new JSONObject();
        json.put("callerClassName",activity.getClass().getName());
        json.put("methodName","dispatchTouchEvent");
        JSONObject itemJSON = new JSONObject();
        itemJSON.put("parameterClassName",motionEvent.getClass().getName());
        itemJSON.put("parameterValue",writeMotionEvent(motionEvent));
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(itemJSON);
        json.put("methodParameter",jsonArray);

        long threadId = Thread.currentThread().getId();
        json.put("threadId",threadId);
        json.put("ViewFlag",false);
        json.put("ActivityID",activity.getClass().getName());
        return json;
    }
    /**
     * 记录刚刚点击的View
     * @param view
     */
    private void sendDispatchView(View view){
        TouchedView.setView(view);
    }
    /**
     * 将点击信息转化为一个JSON
     * @param view
     * @param motionEvent
     * @return
     */
    private JSONObject writeInfo(View view, MotionEvent motionEvent){
        JSONObject json = new JSONObject();
        json.put("callerClassName",view.getClass().getName());
        json.put("methodName","dispatchTouchEvent");

        JSONObject itemJSON = new JSONObject();
        itemJSON.put("parameterClassName",motionEvent.getClass().getName());
        itemJSON.put("parameterValue",writeMotionEvent(motionEvent));
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(itemJSON);
        json.put("methodParameter",jsonArray);

        long threadId = Thread.currentThread().getId();
        json.put("threadId",threadId);

        return json;
    }
    /**
     * 将MotionEvent转化为一个JSON
     * @param motionEvent
     * @return
     */
    private JSONObject writeMotionEvent(MotionEvent motionEvent){
        JSONObject json = new JSONObject();
        json.put("action",motionEvent.getAction());
        json.put("downTime",motionEvent.getDownTime());
        json.put("EventTime",motionEvent.getEventTime());
        json.put("x",(int)motionEvent.getX());
        json.put("y",(int)motionEvent.getY());
        json.put("metaState",motionEvent.getMetaState());
        return json;
    }

    /**
     * 将方法的返回值写入JSON（true/false）
     * @param param
     * @return
     */
    private JSONObject writeResult(MethodHookParam param){
        boolean res = (boolean) param.getResult();
        JSONObject resultJSON = new JSONObject();
        resultJSON.put("resultClassName",boolean.class);
        resultJSON.put("resultValue",res);
        return resultJSON;
    }
    private void writeThreadId(JSONObject jsonObject){
        long id = Thread.currentThread().getId();
        jsonObject.put("threadId",id);
    }

    /**
     * 将响应点击的view的ID和路径写入JSON
     * * @param json
     * @param view
     */
    private void writeViewInfo(JSONObject json, View view){
        JSONObject viewInfo = new JSONObject();
        viewInfo.put("viewId",view.getId());
        viewInfo.put("viewPath", ViewUtil.getViewPath(view));
        json.put("viewInfo",viewInfo);
    }
    private void writeViewFlag(JSONObject jsonObject, View view) {
        if(view==null){
            jsonObject.put("ViewFlag",false);
        }else{
            jsonObject.put("ViewFlag",true);
        }
    }
    private void writeActivityID(JSONObject json, View view){
        json.put("ActivityID",ViewUtil.getActivityNameByView(view));
    }
}
