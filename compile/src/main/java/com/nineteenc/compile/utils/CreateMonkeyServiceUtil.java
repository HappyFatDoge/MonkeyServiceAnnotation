package com.nineteenc.compile.utils;

import com.nineteenc.annotation.MonkeyServiceUtil;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

/**
 * Author    zhengchengbin
 * Describe:
 * Data:      2019/8/19 14:39
 * Modify by:
 * Modification date:
 * Modify content:
 */
public class CreateMonkeyServiceUtil {

    private static final ClassName activityMCN =
            ClassName.get("android.app", "ActivityManager");
    private static final ClassName contextCN =
            ClassName.get("android.content", "Context");
    private static final ClassName intentCN =
            ClassName.get("android.content", "Intent");
    private static final ClassName messengerCN =
            ClassName.get("android.os", "Messenger");
    private static final ClassName logCN =
            ClassName.get("android.util", "Log");
    private static final ClassName connectionCN =
            ClassName.get("android.content", "ServiceConnection");
    private static final ClassName componentNameCN =
            ClassName.get("android.content", "ComponentName");
    private static final ClassName iBinderCN =
            ClassName.get("android.os", "IBinder");
    private static final ClassName messageCN =
            ClassName.get("android.os", "Message");
    private static final ClassName remoteExceptionCN =
            ClassName.get("android.os", "RemoteException");
    private static final ClassName superInterfaceCN =
            ClassName.bestGuess("com.nineteenc.annotation.util.IMonkeyUtil");

    public static void createServiceUtil(String appName, Filer mFiler) {
        String className = appName + "MonkeyUtil";
        TypeSpec.Builder newClassBuilder =TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(superInterfaceCN);

        // 构建成员变量
        createCommonField(newClassBuilder, className);
        if (appName.equals(IContact.COMIC_BOOK_READ_NAME)) {
            // 绘本阅读其余成员变量
            createCBField(newClassBuilder);
        } else if (appName.equals(IContact.PAPER_POINT_READ_NAME)) {
            // 纸上点读其余成员变量
            createPPRField(newClassBuilder);
        }
        // 构建类方法
        createMethod(newClassBuilder, className, appName);

        createConnection(newClassBuilder, appName);
        JavaFile javaFile = JavaFile.builder(MonkeyServiceUtil.SERVICE_PAGE_NAME, newClassBuilder.build())
                .addStaticImport(contextCN, "BIND_AUTO_CREATE")
                .build();
        try {
            javaFile.writeTo(System.out);
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
//            e.printStackTrace();
        }

    }

    private static void createCommonField(TypeSpec.Builder newClassBuilder, String className) {
        FieldSpec packageName = FieldSpec.builder(String.class, "MONKEY_SERVICE_PACKAGE_NAME")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", IContact.MONKEY_SERVICE_PACKAGE_NAME)
                .build();

        FieldSpec tag = FieldSpec.builder(String.class, "TAG")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", className)
                .build();

        FieldSpec bondMS = FieldSpec.builder(boolean.class, "mBondMS")
                .addModifiers(Modifier.PRIVATE)
                .initializer("$L", false)
                .build();

        FieldSpec start = FieldSpec.builder(boolean.class, "mStart")
                .addModifiers(Modifier.PRIVATE)
                .initializer("$L", false)
                .build();

        FieldSpec serverMessenger = FieldSpec.builder(messengerCN, "mMonkeyServerMessenger")
                .addModifiers(Modifier.PRIVATE)
                .build();

        FieldSpec context = FieldSpec.builder(contextCN, "mAppContext")
                .addModifiers(Modifier.PRIVATE)
                .build();

        FieldSpec clientMessenger = FieldSpec.builder(messengerCN, "mClientMessenger")
                .addModifiers(Modifier.PRIVATE)
                .build();

        FieldSpec monkeyUtil = FieldSpec.builder(ClassName.get(MonkeyServiceUtil.SERVICE_PAGE_NAME, className), "mMonkeyUtil")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.VOLATILE)
                .build();

        FieldSpec connection = FieldSpec.builder(connectionCN, "mMonkeyServiceConnection")
                .addModifiers(Modifier.PRIVATE)
                .build();

        newClassBuilder.addField(packageName)
                .addField(tag)
                .addField(bondMS)
                .addField(start)
                .addField(serverMessenger)
                .addField(context)
                .addField(clientMessenger)
                .addField(monkeyUtil)
                .addField(connection);
    }

    private static void createPPRField(TypeSpec.Builder newClassBuilder) {

        FieldSpec serviceClassName = FieldSpec.builder(String.class, "MONKEY_SERVICE_CLASS_NAME")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", IContact.MONKEY_SERVICE_PPRCLASS_NAME)
                .build();

        FieldSpec typeMessage = FieldSpec.builder(int.class, "TYPE_MESSAGE")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("$L", 1)
                .build();

        newClassBuilder.addField(serviceClassName)
                .addField(typeMessage);
    }

    private static void createCBField(TypeSpec.Builder newClassBuilder) {

        FieldSpec serviceClassName = FieldSpec.builder(String.class, "MONKEY_SERVICE_CLASS_NAME")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", IContact.MONKEY_SERVICE_CBCLASS_NAME)
                .build();

        FieldSpec typeStart = FieldSpec.builder(int.class, "TYPE_START_SCAN")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("$L", -1)
                .build();

        FieldSpec typeResult = FieldSpec.builder(int.class, "TYPE_SCAN_BOOK_RESULT")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("$L", 0)
                .build();

        FieldSpec typePage = FieldSpec.builder(int.class, "TYPE_BOOK_PAGE")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("$L", 1)
                .build();

        FieldSpec typeStop = FieldSpec.builder(int.class, "TYPE_STOP_SEND_MESSAGE")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("$L", 2)
                .build();

        newClassBuilder.addField(serviceClassName)
                .addField(typeStart)
                .addField(typeResult)
                .addField(typePage)
                .addField(typeStop);
    }

    private static void createMethod(TypeSpec.Builder newClassBuilder, String className, String appName) {
        MethodSpec initMethod = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build();

        MethodSpec instance = MethodSpec.methodBuilder(IContact.GET_INSTANCE)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get(MonkeyServiceUtil.SERVICE_PAGE_NAME, className))
                .beginControlFlow("if (mMonkeyUtil == null) ")
                .beginControlFlow("synchronized (" + className + ".class) ")
                .beginControlFlow("if (mMonkeyUtil == null) ")
                .addStatement("mMonkeyUtil = new " + className + "()")
                .endControlFlow()
                .endControlFlow()
                .endControlFlow()
                .addStatement("return mMonkeyUtil")
                .build();

        MethodSpec isUseAMonkey = MethodSpec.methodBuilder(IContact.IS_USER_A_MONKEY)
                .addModifiers(Modifier.PRIVATE)
                .returns(boolean.class)
                .addStatement("return $T.isUserAMonkey()", activityMCN)
                .build();

        MethodSpec startMethod = MethodSpec.methodBuilder(IContact.START_MONKEY_SERVICE)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .beginControlFlow("if (isUseAMonkey()) ")
                .addStatement("$T intent = new $T()", intentCN, intentCN)
                .addStatement("$T componentName = new $T(MONKEY_SERVICE_PACKAGE_NAME, MONKEY_SERVICE_CLASS_NAME)", componentNameCN, componentNameCN)
                .addStatement("intent.setComponent(componentName)")
                .beginControlFlow("if (mAppContext != null) ")
                .addStatement("$T.d(TAG, \"startMonkeyService\")", logCN)
                .addStatement("mAppContext.startService(intent)")
                .addStatement("mStart = true")
                .endControlFlow()
                .endControlFlow()
                .build();

        MethodSpec stopMethod = MethodSpec.methodBuilder(IContact.STOP_MONKEY_SERVICE)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addStatement("$T intent = new $T()", intentCN, intentCN)
                .addStatement("$T componentName = new $T(MONKEY_SERVICE_PACKAGE_NAME, MONKEY_SERVICE_CLASS_NAME)", componentNameCN, componentNameCN)
                .addStatement("intent.setComponent(componentName)")
                .beginControlFlow("if (mAppContext != null && mStart) ")
                .addStatement("mAppContext.stopService(intent)")
                .addStatement("mStart = false")
                .endControlFlow()
                .build();

        MethodSpec bindMethod = MethodSpec.methodBuilder(IContact.BIND_MONKEY_SERVICE)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .beginControlFlow("if (isUseAMonkey())")
                .beginControlFlow("if (mBondMS) ")
                .addStatement("return")
                .endControlFlow()
                .addStatement("$T intent = new $T()",intentCN, intentCN)
                .addStatement("$T componentName = new $T(MONKEY_SERVICE_PACKAGE_NAME, MONKEY_SERVICE_CLASS_NAME)", componentNameCN, componentNameCN)
                .addStatement("intent.setComponent(componentName)")
                .beginControlFlow("if (mMonkeyServiceConnection == null)")
                .addStatement("mMonkeyServiceConnection = new MonkeyServiceConnection()")
                .endControlFlow()
                .beginControlFlow("if (mAppContext != null) ")
                .addStatement("$T.d(TAG, \"bindToMonkeyService\")", logCN)
                .addStatement("mAppContext.bindService(intent, mMonkeyServiceConnection, BIND_AUTO_CREATE)")
                .endControlFlow()
                .endControlFlow()
                .build();

        MethodSpec.Builder unbindMethodBuild = MethodSpec.methodBuilder(IContact.UNBIND_MONKEY_SERVICE)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .beginControlFlow("if (mBondMS && mMonkeyServiceConnection != null && mAppContext != null) ")
                .addStatement("$T.d(TAG, \"unbindMonkeyServiceIfNeed\")", logCN);

        if (appName.equals(IContact.COMIC_BOOK_READ_NAME)) {
            unbindMethodBuild.addStatement("sendMsg2MService(TYPE_STOP_SEND_MESSAGE)");
        }
        unbindMethodBuild.addStatement("mAppContext.unbindService(mMonkeyServiceConnection)")
                .addStatement("mBondMS = false")
                .addStatement("mMonkeyServerMessenger = null")
                .endControlFlow();

        MethodSpec setContext = MethodSpec.methodBuilder("setContext")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(ParameterSpec.builder(contextCN, "context").build())
                .addStatement("this.mAppContext = context")
                .build();

        MethodSpec setClientMessenger = MethodSpec.methodBuilder("setClientMessenger")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(ParameterSpec.builder(messengerCN, "messenger").build())
                .addStatement("this.mClientMessenger = messenger")
                .build();

        MethodSpec sendMessage = MethodSpec.methodBuilder(IContact.SEND_MESSAGE)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(ParameterSpec.builder(int.class, "what").build())
                .beginControlFlow("if (null == mMonkeyServerMessenger) ")
                .addStatement("$T.w(TAG, \"sendMsg2MService null == mMonkeyServerMessenger\")", logCN)
                .addStatement("return")
                .endControlFlow()
                .beginControlFlow("if (mClientMessenger != null) ")
                .addStatement("$T message = $T.obtain()", messageCN, messageCN)
                .addStatement("message.what = what")
                .addStatement("message.replyTo = mClientMessenger")
                .beginControlFlow("try ")
                .addStatement("mMonkeyServerMessenger.send(message)")
                .endControlFlow()
                .beginControlFlow("catch ($T e) ", remoteExceptionCN)
                .addStatement("$T.e(TAG, \"sendMsg2MService : \", e)", logCN)
                .endControlFlow()
                .endControlFlow()
                .build();

        newClassBuilder.addMethod(initMethod)
                .addMethod(instance)
                .addMethod(isUseAMonkey)
                .addMethod(startMethod)
                .addMethod(stopMethod)
                .addMethod(bindMethod)
                .addMethod(unbindMethodBuild.build())
                .addMethod(setContext)
                .addMethod(setClientMessenger)
                .addMethod(sendMessage);
    }

    private static void createConnection(TypeSpec.Builder newClassBuilder, String appName) {
        TypeSpec.Builder connectionBuilder = TypeSpec.classBuilder("MonkeyServiceConnection")
                .addModifiers(Modifier.PRIVATE)
                .addSuperinterface(connectionCN);

        MethodSpec.Builder connectedMethodBuilder = MethodSpec.methodBuilder("onServiceConnected")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(ParameterSpec.builder(componentNameCN, "name").build())
                .addParameter(ParameterSpec.builder(iBinderCN, "service").build())
                .addStatement("$T.d(TAG, \"onMonkeyServiceConnected\")", logCN)
                .addStatement("mMonkeyServerMessenger = new $T(service)", messengerCN)
                .addStatement("mBondMS = true");

        if (appName.equals(IContact.PAPER_POINT_READ_NAME)) {
            connectedMethodBuilder.addStatement(IContact.SEND_MESSAGE + "(TYPE_MESSAGE)");
        } else if (appName.equals(IContact.COMIC_BOOK_READ_NAME)) {
            connectedMethodBuilder.addStatement(IContact.SEND_MESSAGE + "(TYPE_BOOK_PAGE)");
        }

        MethodSpec disconnected = MethodSpec.methodBuilder("onServiceDisconnected")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(ParameterSpec.builder(componentNameCN, "name").build())
                .addStatement("$T.d(TAG, \"onMonkeyServiceDisconnected\")", logCN)
                .build();

        connectionBuilder.addMethod(connectedMethodBuilder.build())
                .addMethod(disconnected);

        newClassBuilder.addType(connectionBuilder.build());
    }
}
