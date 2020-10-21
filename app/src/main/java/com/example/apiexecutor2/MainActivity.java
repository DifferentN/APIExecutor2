package com.example.apiexecutor2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.example.apiexecutor2.ViewManager.FloatViewManager;

import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1);
            } else {
                //TODO do something you need
            }
        }
        init();
//        String name = getTopActivityName(this);
//        Log.i("LZH","activity: "+name);
    }

    private void init(){
        FloatViewManager floatViewManager = FloatViewManager.getInstance(this);
        floatViewManager.showSaveIntentViewBt();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("LZH","onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("LZH","onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private static String getTopActivityName(Context context) {
        String topActivityPackageName = null;
        ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfos = manager.getRunningTasks(1);
        if (taskInfos.size() > 0)
            topActivityPackageName = taskInfos.get(0).topActivity.getPackageName();
        Log.i("LZH","topActivityPackageName: "+topActivityPackageName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //此处要判断用户的安全权限有没有打开，如果打开了就进行获取栈顶Activity的名字的方法
            //当然，我们的要求是如果没打开就不获取了，要不然跳转会影响用户的体验
            if (isSecurityPermissionOpen(context)) {
                UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
                long endTime = System.currentTimeMillis();
                long beginTime = endTime - 1000 * 60 * 2;
                UsageStats recentStats = null;

                List<UsageStats> queryUsageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, beginTime, endTime);
                if (queryUsageStats == null || queryUsageStats.isEmpty()) {
                    return null;
                }

                for (UsageStats usageStats : queryUsageStats) {
                    if (recentStats == null || recentStats.getLastTimeUsed() < usageStats.getLastTimeUsed()) {
                        recentStats = usageStats;
                    }
                }
                topActivityPackageName = recentStats.getPackageName();
                return topActivityPackageName;
            } else {
                return null;
            }
        } else {
//            List<ActivityManager.RunningTaskInfo> taskInfos = manager.getRunningTasks(1);
//            if (taskInfos.size() > 0)
//                topActivityPackageName = taskInfos.get(0).topActivity.getPackageName();
//            else
//                return null;
            return topActivityPackageName;
        }

    }

    //判断用户对应的安全权限有没有打开
    private static boolean isSecurityPermissionOpen(Context context) {
        long endTime = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, endTime);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return false;
        }
        return true;
    }
}
