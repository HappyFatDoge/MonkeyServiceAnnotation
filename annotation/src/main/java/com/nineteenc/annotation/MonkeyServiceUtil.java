package com.nineteenc.annotation;

import android.content.Context;
import android.os.Messenger;
import android.util.Log;
import com.nineteenc.annotation.util.ClassUtils;
import com.nineteenc.annotation.util.IMonkeyUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Author    zhengchengbin
 * Describe:
 * Data:      2019/8/20 12:08
 * Modify by:
 * Modification date:
 * Modify content:
 */
public class MonkeyServiceUtil {

    private static volatile MonkeyServiceUtil mMonkeyServiceUtil;

    public static final String SERVICE_PAGE_NAME = "com.nineteenc.services";

    private MonkeyServiceUtil(){}

    public static MonkeyServiceUtil getInstance() {
        if (mMonkeyServiceUtil == null) {
            synchronized (MonkeyServiceUtil.class) {
                if (mMonkeyServiceUtil == null) {
                    mMonkeyServiceUtil = new MonkeyServiceUtil();
                }
            }
        }
        return mMonkeyServiceUtil;
    }

    public void init(Context context, Messenger messenger) {
        Set<String> names = ClassUtils.getFileNameByPackageName(context, SERVICE_PAGE_NAME);
        Log.d("MonkeyServiceUtil", String.valueOf(names.size()));
        try {
            initVar(names, context, messenger);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initVar(Set<String> names, Context context, Messenger messenger)
            throws ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        for (String name : names) {
            Class clazz = Class.forName(name);
            Method method = clazz.getDeclaredMethod("getInstance");
            method.setAccessible(true);
            Object object = method.invoke(null);
            if (object instanceof IMonkeyUtil) {
                IMonkeyUtil iMonkeyUtil = (IMonkeyUtil) object;
                iMonkeyUtil.setContext(context);
                iMonkeyUtil.setClientMessenger(messenger);
                Log.d("MonkeyServiceUtil", String.valueOf(context == null));
            }
        }
    }

    public void addStatement2Bind(String statement) {}

    public void addStatement2Unbind(String statement) {}

}
