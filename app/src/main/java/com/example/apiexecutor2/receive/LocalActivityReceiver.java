package com.example.apiexecutor2.receive;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.apiexecutor2.util.FileUtil;
import com.example.apiexecutor2.util.LogWriter;
import com.example.apiexecutor2.util.ViewUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
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
                //com.example.refrigerator com.sdu.didi.psnger com.mygolbs.mybus
                //com.lianjia.beike com.mfw.roadbook com.sogou.map.android.maps
                //com.dp.android.elong com.mfw.roadbook com.tuniu.app.ui
                //com.gift.android com.dragon.read com.sina.news
                //com.kmxs.reader bubei.tingshu com.kuaikan.comic
                //com.tencent.news com.xiangha com.jnzc.shipudaquan
                //com.xiachufang.lazycook
                if(selfPackageName.contains("com.dragon.read")||
                    selfPackageName.contains("bubei.tingshu")){
                    //设置LogWriter可以写入日志
                    LogWriter.turnWriteAble();
                    if(selfActivityName.equals(showActivityName)){
                        final HashMap<String,String> pageContent = ViewUtil.capturePageContent(selfActivity);
                        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/pageContent.txt";
                        FileUtil.writePageContent(filePath,pageContent);
                    }
                }
                break;
            case "destroy":
                selfActivity.unregisterReceiver(this);
        }
    }
}
