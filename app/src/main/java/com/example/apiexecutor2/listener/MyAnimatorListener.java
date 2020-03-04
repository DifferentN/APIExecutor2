package com.example.apiexecutor2.listener;

import android.animation.Animator;
import android.view.View;
import android.view.ViewPropertyAnimator;

import com.alibaba.fastjson.JSONObject;
import com.example.apiexecutor2.dataRecord.ViewAnimationScrollRecord;
import com.example.apiexecutor2.util.LogWriter;
import com.example.apiexecutor2.util.TransformJsonUtil;


public class MyAnimatorListener implements Animator.AnimatorListener {
    private Animator.AnimatorListener originalListener;
    private int currentapiVersion=android.os.Build.VERSION.SDK_INT;
    private ViewAnimationScrollRecord record;
    private ViewPropertyAnimator viewPropertyAnimator;
    public MyAnimatorListener(ViewPropertyAnimator animator,Animator.AnimatorListener listener){
        originalListener = listener;
        viewPropertyAnimator = animator;
        record = ViewAnimationScrollRecord.getInstance();
    }
    @Override
    public void onAnimationStart(Animator animation, boolean isReverse) {
        if(originalListener==null){
            return;
        }
        if(currentapiVersion>=26){
            originalListener.onAnimationStart(animation,isReverse);
        }
    }

    @Override
    public void onAnimationEnd(Animator animation, boolean isReverse) {
        if(originalListener==null){
            return;
        }
        if(currentapiVersion>=26){
            originalListener.onAnimationEnd(animation,isReverse);
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {
        if(originalListener==null){
            return;
        }
        originalListener.onAnimationStart(animation);
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if(originalListener==null){
            return;
        }
        LogWriter logWriter = LogWriter.getInstance();
        JSONObject jsonObject = TransformJsonUtil.transformViewPropertyAnimator(viewPropertyAnimator);
        logWriter.writeLog(jsonObject.toJSONString());
        record.removeViewPropertyAnimatorRecord(viewPropertyAnimator);
        originalListener.onAnimationEnd(animation);
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        if(originalListener==null){
            return;
        }
        originalListener.onAnimationCancel(animation);
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        if(originalListener==null){
            return;
        }
        originalListener.onAnimationRepeat(animation);
    }
}
