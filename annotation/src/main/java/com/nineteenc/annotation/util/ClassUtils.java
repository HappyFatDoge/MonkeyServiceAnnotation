package com.nineteenc.annotation.util;

import android.content.Context;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexFile;

/**
 * Author    zhengchengbin
 * Describe:
 * Data:      2019/8/14 9:49
 * Modify by:
 * Modification date:
 * Modify content:
 */
public class ClassUtils {

    public static List<DexFile> getDexFiles(Context context) {
        List<DexFile> dexFiles = new ArrayList<>();
        BaseDexClassLoader loader = (BaseDexClassLoader) context.getClassLoader();
        try {
            Field pathListField = field("dalvik.system.BaseDexClassLoader", "pathList");
            Object list = pathListField.get(loader);
            Field dexElementField = field("dalvik.system.DexPathList", "dexElements");
            Object[] dexElements = (Object[]) dexElementField.get(list);
            Field dexFileField = field("dalvik.system.DexPathList$Element", "dexFile");
            for (Object dex : dexElements) {
                DexFile dexFile = (DexFile) dexFileField.get(dex);
                if (dexFile != null) {
                    dexFiles.add(dexFile);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return dexFiles;
    }

    private static Field field(String clazz, String fieldName)
            throws ClassNotFoundException, NoSuchFieldException {
        Class cls = Class.forName(clazz);
        Field field = cls.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;

    }

    public static Set<String> getFileNameByPackageName(Context context, final String packageName) {
        final Set<String> classNames = new HashSet<>();

        List<DexFile> dexFiles = getDexFiles(context);
        for (final DexFile dexFile : dexFiles) {
            Enumeration<String> dexEntries = dexFile.entries();
            while (dexEntries.hasMoreElements()) {
                String className = dexEntries.nextElement();
                if (className.startsWith(packageName)) {
                    classNames.add(className);
                }
            }
        }

        return classNames;
    }
}
