package com.example.apiexecutor2.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ViewUtil {
    public static final String VIEW_X = "viewX";
    public static final String VIEW_Y = "viewY";
    public static final String VIEW_WIDTH = "viewWidth";
    public static final String VIEW_HEIGHT = "viewHeight";
    public static final String VIEW_PATH = "viewPath";
    public static final String VIEW_CLASS_NAME = "viewName";
    public static final String VIEW_CHILD_INDEX = "viewIndex";
    public static final String CHILDS = "childs";

    public static int getViewIndex(View target){
        View decorView = target.getRootView();
        ArrayList<View> list = new ArrayList<>();
        list.add(decorView);
        ViewGroup viewGroup;
        View view;
        int index = 0;
        while(!list.isEmpty()){
            view = list.remove(0);
            index++;
            if(view==target){
                return index;
            }else if(view instanceof ViewGroup){
                viewGroup = (ViewGroup) view;
                List<View> childViews = obtainChildViews(viewGroup);
                for(int i=0;i<childViews.size();i++){
                    view = childViews.get(i);
                    list.add(view);
                }
            }
        }
        return -1;
    }

    public static String getViewPath(View view){
        if(view==null){
            return "";
        }
        View decorView = view.getRootView();
        ViewGroup viewGroup;
        View child;
        ViewNode childNode;
        ViewNode temp;
        List<ViewNode> list = new ArrayList<>();
        list.add(new ViewNode(decorView,decorView.getClass().getName()));
        while(!list.isEmpty()){
            temp = list.remove(0);
            if(temp.view==view){
                return temp.path;
            }else if(temp.view instanceof ViewGroup){
                viewGroup = (ViewGroup) temp.view;
                List<View> childViews = obtainChildViews(viewGroup);
                for(int i=0;i<childViews.size();i++){
                    child = childViews.get(i);
                    childNode = new ViewNode(child,temp.path+"/"+child.getClass()+":"+i);
                    list.add(childNode);
                }
            }
        }
        return "";
    }
    public static String getActivityNameByView(View view){
        Context context = null;
        String activityName = "";
        if(view!=null){
            context = view.getContext();
            while(context instanceof ContextWrapper){
                if(context instanceof Activity){
                    activityName = ((Activity)context).getComponentName().getClassName();
                    break;
                }
                context = ((ContextWrapper)context).getBaseContext();
            }
        }
        return activityName;
    }
    /**
     * 获取activity页面中的内容
     * 返回结果中的每一个Item格式： xpath:text
     * @param activity
     * @return
     */
    public static HashMap<String,String> capturePageContent(Activity activity){
        HashMap<String,String> hash = new HashMap<>();
        View decorView = activity.getWindow().getDecorView();
        ViewGroup viewGroup;
        View child;
        ViewNode childNode;
        ViewNode temp;
        List<ViewNode> list = new ArrayList<>();
        list.add(new ViewNode(decorView,decorView.getClass().getName()));
        while(!list.isEmpty()){
            temp = list.remove(0);
            if(temp.view instanceof TextView){
                hash.put(temp.path,((TextView) temp.view).getText().toString());
            }else if(temp.view instanceof ViewGroup){
                viewGroup = (ViewGroup) temp.view;
                List<View> childViews = obtainChildViews(viewGroup);
                for(int i=0;i<childViews.size();i++){
                    child = childViews.get(i);
                    childNode = new ViewNode(child,temp.path+"/"+child.getClass()+":"+i);
                    list.add(childNode);
                }
            }
        }
        return hash;
    }
    public static JSONArray getSnapShotOfWindow(Activity activity){
        Context context = activity.getApplicationContext();
        return getSnapShotOfWindow(context);
    }
    /**
     * obtain window snapShot
     * @param context
     * @return
     */
    public static JSONArray getSnapShotOfWindow(Context context){
        Object windowManagerImpl = context.getSystemService(Context.WINDOW_SERVICE);
        Class windowManagerImplClass = windowManagerImpl.getClass();

        Object windowManagerGlobal = null;
        Class windowManagerGlobalClass = null;

        ArrayList<View> listViews = null;
        try {
            Field field = windowManagerImplClass.getDeclaredField("mGlobal");
            field.setAccessible(true);
            windowManagerGlobal = field.get(windowManagerImpl);
            windowManagerGlobalClass = windowManagerGlobal.getClass();
            Field viewField = windowManagerGlobalClass.getDeclaredField("mViews");
            viewField.setAccessible(true);
            listViews = (ArrayList<View>) viewField.get(windowManagerGlobal);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        JSONArray res = new JSONArray();
        if(listViews==null){
            return res;
        }
        for(int i=0;i<listViews.size();i++){
            View view = listViews.get(i);
            if(!isVisible(view)){
                continue;
            }
            JSONObject viewInfoJSON = getViewInfoJSON(view,"",0);
            res.add(viewInfoJSON);
        }
        return res;
    }
    /**
     * obtain the information(JSON form) of view
     * @param view
     * @return
     */
    private static JSONObject getViewInfoJSON(View view,String parentxPath,int curIndex){
        JSONObject info = new JSONObject();
        info.put(VIEW_X,obtainX(view));
        info.put(VIEW_Y,obtainY(view));
        info.put(VIEW_WIDTH,obtainWidth(view));
        info.put(VIEW_HEIGHT,obtainHeight(view));
        info.put(VIEW_CLASS_NAME,view.getClass().getName());
        info.put(VIEW_CHILD_INDEX,curIndex);
        String xpath = parentxPath+"/"+view.getClass().getName()+":"+curIndex;
        info.put(VIEW_PATH,xpath);

        if(view instanceof ViewGroup){
            ViewGroup viewGroup = (ViewGroup) view;
            JSONArray childInfos = new JSONArray();
            List<View> childViews = obtainChildViews(viewGroup);
            for(int i=0;i<childViews.size();i++){
//                if(!isVisible(childViews.get(i))){
//                    continue;
//                }
                JSONObject childInfo = getViewInfoJSON(childViews.get(i),xpath,i);
                childInfos.add(childInfo);
            }
            info.put(CHILDS,childInfos);
        }
        return info;
    }
    public static boolean isVisible(View view){
        while(view!=null){
            if(view.getVisibility()==View.INVISIBLE||
                    view.getVisibility()==View.GONE||view.getAlpha()==0){
                return  false;
            }
            ViewParent parent = view.getParent();
            if(parent instanceof View){
                view = (View) parent;
            }else view = null;
        }
        return true;
    }
    public static float obtainX(View view){
        int pos[] = new int[2];
        view.getLocationInWindow(pos);
        float density = view.getResources().getDisplayMetrics().density;
        //transform to dp value
        return pos[0]/density;
    }
    public static float obtainY(View view){
        int pos[] = new int[2];
        view.getLocationInWindow(pos);
        float density = view.getResources().getDisplayMetrics().density;
        //transform to dp value
        return pos[1]/density;
    }
    public static float obtainWidth(View view){
        int widthpx = view.getWidth();
        float density = view.getResources().getDisplayMetrics().density;
        //transform to dp value
        return widthpx/density;
    }
    public static float obtainHeight(View view){
        int heightpx = view.getHeight();
        float density = view.getResources().getDisplayMetrics().density;
        //transform to dp value
        return heightpx/density;
    }
    private static List<View> obtainChildViews(ViewGroup parentView){
        List<View> childs = new ArrayList<>();
        for(int i=0;i<parentView.getChildCount();i++){
            childs.add( parentView.getChildAt(i) );
        }
        Collections.sort(childs, new Comparator<View>() {
            @Override
            public int compare(View o1, View o2) {
                int res = 0;
                float val = o1.getX() - o2.getX();
                res = (int) (val/Math.abs(val));
                if(res!=0){
                    return res;
                }
                val = o1.getY() - o2.getY();
                res = (int) (val / Math.abs(val));
                if(res!=0){
                    return res;
                }
                res = o1.getWidth() - o2.getWidth();
                if(res!=0){
                    return res;
                }
                res = o1.getHeight() - o2.getHeight();
                if(res!=0){
                    return res;
                }
                return o1.getClass().getName().compareTo(o2.getClass().getName());
            }
        });
        return childs;
    }
    public static int getViewNum(View rootView){
        List<View> queue = new ArrayList<>();
        queue.add(rootView);
        int num = 0;
        while(!queue.isEmpty()){
            num++;
            View curView = queue.remove(0);
            if(curView instanceof ViewGroup){
                ViewGroup vg = (ViewGroup) curView;
                for(int i=0;i<vg.getChildCount();i++){
                    queue.add(vg.getChildAt(i));
                }
            }
        }
        return num;
    }
    static class ViewNode{
        public View view;
        public String path;
        public ViewNode(View view,String path){
            this.view = view;
            this.path = path;
        }
    }
}
