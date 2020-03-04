package com.example.apiexecutor2.util;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.Scroller;

import com.alibaba.fastjson.JSONObject;
import com.example.apiexecutor2.dataRecord.ViewAnimationScrollRecord;

import java.util.ArrayList;
import java.util.List;

public class TransformJsonUtil {
    private static final String VIEW_PATH = "viewPath";
    private static final String ANIMATOR_TYPE = "animatorType";
    private static final String ObjectAnimatorType = "ObjectAnimator";
    private static final String ValueAnimatorType = "ValueAnimatorType";
    private static final String AnimationType = "AnimationType";
    private static final String ScrollerType = "ScrollerType";
    private static final String OverScrollerType = "OverScroller";
    private static final String ViewPropertyAnimatorType = "ViewPropertyAnimator";
    public static JSONObject transformObjectAnimator(ObjectAnimator objectAnimator){
        Object target = objectAnimator.getTarget();
        if(target instanceof View){
            View view = (View) target;
            String viewPath = ViewUtil.getViewPath(view);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(VIEW_PATH,viewPath);
            jsonObject.put(ANIMATOR_TYPE,ObjectAnimatorType);
            return jsonObject;
        }
        return null;
    }
    public static List<JSONObject> transformValueAnimator(ValueAnimator valueAnimator){
        ViewAnimationScrollRecord record = ViewAnimationScrollRecord.getInstance();
        List<View> viewList = record.getViewByValueAnimator(valueAnimator);
        List<JSONObject> jsonObjectList = new ArrayList<>();
        while(!viewList.isEmpty()){
            View view = viewList.remove(0);
            String viewPath = ViewUtil.getViewPath(view);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(VIEW_PATH,viewPath);
            jsonObject.put(ANIMATOR_TYPE,ValueAnimatorType);
            jsonObjectList.add(jsonObject);
        }
        return jsonObjectList;
    }
    public static JSONObject transformAnimation(View view){
        String viewPath = ViewUtil.getViewPath(view);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(VIEW_PATH,viewPath);
        jsonObject.put(ANIMATOR_TYPE,AnimationType);
        return jsonObject;
    }
    public static JSONObject transformScroll(Object scroll){
        ViewAnimationScrollRecord record = ViewAnimationScrollRecord.getInstance();
        View view = record.getViewByScrollOrOverScroll(scroll);
        String viewPath = ViewUtil.getViewPath(view);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(VIEW_PATH,viewPath);
        if(scroll instanceof Scroller){
            jsonObject.put(ANIMATOR_TYPE,ScrollerType);
        }else{
            jsonObject.put(ANIMATOR_TYPE,OverScrollerType);
        }
        return jsonObject;
    }
    public static JSONObject transformViewPropertyAnimator(ViewPropertyAnimator animator){
        ViewAnimationScrollRecord record = ViewAnimationScrollRecord.getInstance();
        View view = record.getViewByViewPropertyAnimator(animator);
        String viewPath = ViewUtil.getViewPath(view);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(VIEW_PATH,viewPath);
        jsonObject.put(ANIMATOR_TYPE,ViewPropertyAnimatorType);
        return jsonObject;
    }
}
