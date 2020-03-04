package com.example.apiexecutor2.listener;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.apiexecutor2.util.LogWriter;
import com.example.apiexecutor2.util.ViewUtil;

public class MyTextWatcher implements TextWatcher {
    private String fileName = "methodLog.txt";
    private LogWriter logWriter;
    private View view;
    public MyTextWatcher(View view){
        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName;
        logWriter = LogWriter.getInstance();
        this.view = view;
    }

    /**
     * 将方法调用的调用者，方法名称，参数，线程ID，viewId/path，和ViewFlag写入JSON
     * @param view
     * @param text
     * @return
     */
    private JSONObject writeInfo(View view, String text){
        JSONObject json = new JSONObject();
        json.put("callerClassName",this.getClass().getName());

        json.put("methodName","setText");

        JSONObject itemJSON = new JSONObject();
        itemJSON.put("parameterClassName",String.class.getName());
        itemJSON.put("parameterValue",text);
        itemJSON.put("parameterId",view.getId());
        itemJSON.put("parameterViewIndex", ViewUtil.getViewIndex(view));
//        Log.i("LZH","parameterViewIndex: "+ViewUtil.getViewIndex(view));
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(itemJSON);

        json.put("methodParameter",jsonArray);

        JSONObject resultJSON = new JSONObject();
        resultJSON.put("resultClassName",null);
        resultJSON.put("resultHashCode",null);
        resultJSON.put("resultValue",null);
        json.put("methodResult",resultJSON);

        long threadId = Thread.currentThread().getId();
        json.put("threadId",threadId);

        JSONObject viewInfo = getViewInfoJSON(view);
        json.put("viewInfo",viewInfo);

        writeViewFlag(json,view);

        writeActivityID(json,view);
        return json;

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        //如果不是用户点击的View产生的文本变化，则忽略
        if(view!=TouchedView.getView()){
            return;
        }
        String info = writeInfo(view,s.toString()).toJSONString();
        if(logWriter!=null){
            logWriter.writeLog("before: "+info);
        }
        if(logWriter!=null){
            logWriter.writeLog("after: "+info);
        }
    }
    private JSONObject getViewInfoJSON(View view){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("viewId",view.getId());
        jsonObject.put("viewPath", ViewUtil.getViewPath(view));
        return jsonObject;
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
