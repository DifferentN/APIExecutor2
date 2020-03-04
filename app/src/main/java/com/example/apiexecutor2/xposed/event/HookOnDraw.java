package com.example.apiexecutor2.xposed.event;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.apiexecutor2.listener.MyTextWatcher;
import com.example.apiexecutor2.receive.LocalActivityReceiver;
import com.example.apiexecutor2.util.LogWriter;

import java.lang.reflect.Field;
import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;

public class HookOnDraw extends XC_MethodHook {
    private LogWriter logWriter;
    private String fileName = "methodLog.txt";
    public HookOnDraw(){
        fileName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+fileName;
        logWriter = LogWriter.getInstance();
    }
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        View view = (View) param.thisObject;
        Activity activity = getActivity(view);
        if(view!=null&&view instanceof TextView){
            if(!checkIsSetTextWatcher((TextView) view)){
                ((TextView) view).addTextChangedListener(new MyTextWatcher(view));
            }
        }

    }
    private Activity getActivity(View view){
        if(view!=null){
            Context context = view.getContext();
            while(context instanceof ContextWrapper){
                if(context instanceof Activity){
                    return (Activity)context;
                }
                context = ((ContextWrapper)context).getBaseContext();
            }
        }
        return null;
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
}
