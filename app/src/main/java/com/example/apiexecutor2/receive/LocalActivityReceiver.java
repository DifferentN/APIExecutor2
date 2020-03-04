package com.example.apiexecutor2.receive;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.apiexecutor2.util.LogWriter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 主要用来播放Intent，输入，点击事件。
 * 抽取，传递页面信息
 */
public class LocalActivityReceiver extends BroadcastReceiver{
    private Activity selfActivity;

    public static final String WRITE_LOG = "WRITE_LOG";
    public static final String ON_RESUME = "ON_RESUME";
    public static final String RESUME_ACTIVITY = "RESUME_ACTIVITY";

    private String selfActivityName = "";
    private String showActivityName = "";
    private String selfPackageName;
    public LocalActivityReceiver(Activity activity){
        selfActivity = activity;
        selfActivityName = activity.getComponentName().getClassName();
        selfPackageName = activity.getPackageName();

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action){
            case ON_RESUME:
                showActivityName = intent.getStringExtra(RESUME_ACTIVITY);
//                imitateExecution();
                break;
            case LocalActivityReceiver.WRITE_LOG:
                //com.douban.movie com.tencent.qqmusic
                //com.jnzc.shipudaquan com.yongche.android
                //com.dangdang.buy2 cn.cuco com.zhangshangjianzhi.newapp
                //com.ss.android.ugc.aweme  yst.apk com.cqrenyi.huanyubrowser
                //com.yr.qmzs com.jrtd.mfxszq com.netease.pris com.wondertek.paper
                //com.infzm.ireader com.ifeng.news2 com.duxiaoman.umoney
                //com.boohee.food com.boohee.one com.starbucks.cn
                if(selfPackageName.contains("com.starbucks.cn")){
                    //设置LogWriter可以写入日志
                    LogWriter.turnWriteAble();
                }
                break;
            case "destroy":
                selfActivity.unregisterReceiver(this);
        }
    }

}
