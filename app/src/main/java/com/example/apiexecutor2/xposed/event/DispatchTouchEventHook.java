package com.example.apiexecutor2.xposed.event;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.apiexecutor2.listener.MyTextWatcher;
import com.example.apiexecutor2.listener.TouchedView;
import com.example.apiexecutor2.util.ActivityUtil;
import com.example.apiexecutor2.util.LogWriter;
import com.example.apiexecutor2.util.ViewUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
        Context context = null;
        if(obj instanceof View){
            View view = (View) obj;
            //添加输入监听器
            if(view instanceof TextView){
                addTextWatcher((TextView) view);
            }
            context = view.getContext();
            jsonObject = writeInfo(view,motionEvent);
            writeThreadId(jsonObject);
            writeViewInfo(jsonObject,view);
            writeViewFlag(jsonObject,view);
            writeActivityID(jsonObject,view);
            if(view.getRootView().getClass().getName().contains("PopupDecorView")){
                JSONArray snapShot = ViewUtil.getSnapShotOfWindow(view.getContext());
                addSnapShot(jsonObject,snapShot);
            }
//            Log.i("LZH","view name: "+view.getClass().getName());
//            Log.i("LZH","view activity: "+ActivityUtil.getActivity(view));
//            Log.i("LZH","view context name: "+view.getContext().getClass().getName());
            logWriter.writeLog("before: "+jsonObject.toJSONString());
        }else if(obj instanceof Activity){
            Activity activity = (Activity) obj;
//            String topActivity = ActivityUtil.getTopActivityName(activity);
//            Log.i("LZH","top activity: "+ topActivity);
//            Log.i("LZH", "click activity "+activity.getClass().getName()+" rootViewHashCode: "+activity.getWindow().getDecorView().hashCode());
//            Log.i("LZH","activity view num: "+ViewUtil.getViewNum(activity.getWindow().getDecorView()));
//            printParentClass(activity.getClass());
//            context = activity.getApplicationContext();
            jsonObject = writeActivityInfo(activity,motionEvent);
            JSONArray snapShot = ViewUtil.getSnapShotOfWindow(activity);
            addSnapShot(jsonObject,snapShot);
            Log.i("LZH", "before activity dispatchTouchEvent: "+activity.getClass().getName());
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
            //如果是PopupDecorView要保存当前的视图结构，因为它的点击事件不是通过Activity传递
            if(view.getRootView().getClass().getName().contains("PopupDecorView")){
                JSONArray snapShot = ViewUtil.getSnapShotOfWindow(view.getContext());
                addSnapShot(json,snapShot);
            }
            logWriter.writeLog("after: "+json.toJSONString());
//            Log.i("LZH",view.getRootView().getClass().getName());
        }else if(obj instanceof Activity){
            Activity activity = (Activity) obj;
            JSONObject jsonObject = writeActivityInfo(activity,motionEvent);
            JSONArray snapShot = ViewUtil.getSnapShotOfWindow(activity);
            addSnapShot(jsonObject,snapShot);
            logWriter.writeLog("after: "+jsonObject.toJSONString());
            Log.i("LZH","after activity dispatchTouchEvent: "+activity.getClass().getName());
        }
    }

    private void addTextWatcher(TextView textView){
        if(!checkIsSetTextWatcher(textView)){
            textView.addTextChangedListener(new MyTextWatcher(textView));
        }
    }

    /**
     * 检查textView是否添加了MyTextWatcher
     * @param textView
     * @return true表示已经添加，false表示未添加
     */
    private boolean checkIsSetTextWatcher(TextView textView){
        Class clazz = TextView.class;
        try {
            Field listenersField = clazz.getDeclaredField("mListeners");
            ArrayList<TextWatcher> listeners = (ArrayList<TextWatcher>) listenersField.get(textView);
            for(TextWatcher textWatcher:listeners){
                if(textWatcher instanceof MyTextWatcher){
                    return true;
                }
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * add the snapshot of window to jsonObject
     * @param jsonObject
     * @param snapShot
     */
    private void addSnapShot(JSONObject jsonObject,JSONArray snapShot){
        jsonObject.put("structure",snapShot);
    }
    private JSONObject writeActivityInfo(Activity activity, MotionEvent motionEvent){
        JSONObject json = new JSONObject();
        String packageName = activity.getPackageName();
        json.put("packageName",packageName);
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
        String packageName = view.getContext().getPackageName();
        json.put("packageName",packageName);
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
        int pos[] = new int[2];
        view.getLocationInWindow(pos);
        float dpPos[] = new float[2];
        DisplayMetrics displayMetrics = view.getContext().getResources().getDisplayMetrics();
        dpPos[0] = (pos[0]/displayMetrics.density);
        dpPos[1] = (pos[1]/displayMetrics.density);
        int width = (int) (view.getWidth()/displayMetrics.density);
        int height = (int) (view.getHeight()/displayMetrics.density);
        viewInfo.put("viewX",dpPos[0]);
        viewInfo.put("viewY",dpPos[1]);
        viewInfo.put("viewWidth",width);
        viewInfo.put("viewHeight",height);
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

    private void printParentClass(Class clazz){
        Class parentClazz = clazz.getSuperclass();
        Log.i("LZH","parent: "+parentClazz.getName());
        printParentClass(parentClazz);

    }
}
