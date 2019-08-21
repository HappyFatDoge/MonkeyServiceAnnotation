package com.nineteenc.compile;

import com.nineteenc.annotation.annotation.Bind2MonkeyService;
import com.nineteenc.annotation.annotation.MonkeyService;
import com.nineteenc.annotation.annotation.SendMsg2MService;
import com.nineteenc.annotation.annotation.StartMonkeyService;
import com.nineteenc.annotation.annotation.StopMonkeyService;
import com.nineteenc.annotation.annotation.Unbind2MonkeyService;
import com.nineteenc.annotation.util.MessageWhat;
import com.nineteenc.annotation.util.ServiceNameEnum;
import com.nineteenc.compile.utils.CreateMonkeyServiceUtil;
import com.nineteenc.compile.utils.IContact;
import com.nineteenc.compile.utils.MethodCompileUtil;
import com.sun.org.apache.bcel.internal.generic.RET;
import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

/**
 * Author    zhengchengbin
 * Describe:
 * Data:      2019/8/19 11:43
 * Modify by:
 * Modification date:
 * Modify content:
 */
public class MonkeyServiceProcessor extends AbstractProcessor {

    private Map<ServiceNameEnum, String> mModeMap = new HashMap<>();
    private Filer mFiler;
    private MethodCompileUtil mMethodCompileUtil;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        System.out.println("==========    start init    ==========");
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();

        Trees trees = Trees.instance(processingEnvironment);
        Context context = ((JavacProcessingEnvironment) processingEnvironment).getContext();
        TreeMaker treeMaker = TreeMaker.instance(context);
        Name.Table name = Names.instance(context).table;
        mMethodCompileUtil = MethodCompileUtil.getInstance(trees, treeMaker, name);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        System.out.println("==========    start process    ==========");
        Set<? extends Element> elementSet = roundEnvironment.getElementsAnnotatedWith(MonkeyService.class);
        for (Element element : elementSet) {
            if (element.getKind() == ElementKind.CLASS) {
                addMode(element);
            }
        }

        dealBind2MonkeyService(roundEnvironment);

        dealUnbind2MonkeyService(roundEnvironment);

        dealStartMonkeyService(roundEnvironment);

        dealStopMonkeyService(roundEnvironment);

        dealSendMsg2MService(roundEnvironment);

        for (String appName : mModeMap.values()) {
            CreateMonkeyServiceUtil.createServiceUtil(appName, mFiler);
        }
        return true;
    }

    private void addMode(Element element) {
        MonkeyService monkeyService = element.getAnnotation(MonkeyService.class);
        ServiceNameEnum serviceName = monkeyService.serviceName();
        if (serviceName.equals(ServiceNameEnum.COMIC_BOOK_READ_SERVICE)) {
            mModeMap.put(serviceName, IContact.COMIC_BOOK_READ_NAME);
            System.out.println("==========    appName : " + IContact.COMIC_BOOK_READ_NAME + "    ==========");
        } else if (serviceName.equals(ServiceNameEnum.PAPER_POINT_READ_SERVICE)) {
            mModeMap.put(serviceName, IContact.PAPER_POINT_READ_NAME);
            System.out.println("==========    appName : " + IContact.PAPER_POINT_READ_NAME + "    ==========");
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(MonkeyService.class.getCanonicalName());
        annotations.add(Bind2MonkeyService.class.getCanonicalName());
        annotations.add(Unbind2MonkeyService.class.getCanonicalName());
        annotations.add(StartMonkeyService.class.getCanonicalName());
        annotations.add(StopMonkeyService.class.getCanonicalName());
        annotations.add(SendMsg2MService.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    private void dealBind2MonkeyService(RoundEnvironment roundEnvironment) {
        Set<? extends Element> elementSet = roundEnvironment.getElementsAnnotatedWith(Bind2MonkeyService.class);
        for (Element element : elementSet) {
            if (element.getKind() == ElementKind.METHOD) {
                if (mMethodCompileUtil != null) {
                    Bind2MonkeyService bind = element.getAnnotation(Bind2MonkeyService.class);
                    ServiceNameEnum serviceName = bind.serviceName();
                    if (serviceName.equals(ServiceNameEnum.COMIC_BOOK_READ_SERVICE)) {
                        mMethodCompileUtil.compileBind2Service(
                                IContact.COMIC_BOOK_READ_NAME + "MonkeyUtil", element);
                    } else if (serviceName.equals(ServiceNameEnum.PAPER_POINT_READ_SERVICE)) {
                        mMethodCompileUtil.compileBind2Service(
                                IContact.PAPER_POINT_READ_NAME + "MonkeyUtil", element);
                    }
                }
            }
        }
    }

    private void dealUnbind2MonkeyService(RoundEnvironment roundEnvironment) {
        Set<? extends Element> elementSet = roundEnvironment.getElementsAnnotatedWith(Unbind2MonkeyService.class);
        for (Element element : elementSet) {
            if (element.getKind() == ElementKind.METHOD) {
                if (mMethodCompileUtil != null) {
                    Unbind2MonkeyService unbind = element.getAnnotation(Unbind2MonkeyService.class);
                    ServiceNameEnum serviceName = unbind.serviceName();
                    if (serviceName.equals(ServiceNameEnum.COMIC_BOOK_READ_SERVICE)) {
                        mMethodCompileUtil.compileUnbind2Service(
                                IContact.COMIC_BOOK_READ_NAME + "MonkeyUtil", element);
                    } else if (serviceName.equals(ServiceNameEnum.PAPER_POINT_READ_SERVICE)) {
                        mMethodCompileUtil.compileUnbind2Service(
                                IContact.PAPER_POINT_READ_NAME + "MonkeyUtil", element);
                    }
                }
            }
        }
    }

    private void dealStartMonkeyService(RoundEnvironment roundEnvironment) {
        Set<? extends Element> elementSet = roundEnvironment.getElementsAnnotatedWith(StartMonkeyService.class);
        for (Element element : elementSet) {
            if (element.getKind() == ElementKind.METHOD) {
                if (mMethodCompileUtil != null) {
                    StartMonkeyService start = element.getAnnotation(StartMonkeyService.class);
                    ServiceNameEnum serviceName = start.serviceName();
                    if (serviceName.equals(ServiceNameEnum.COMIC_BOOK_READ_SERVICE)) {
                        mMethodCompileUtil.compileStartService(
                                IContact.COMIC_BOOK_READ_NAME + "MonkeyUtil", element);
                    } else if (serviceName.equals(ServiceNameEnum.PAPER_POINT_READ_SERVICE)) {
                        mMethodCompileUtil.compileStartService(
                                IContact.PAPER_POINT_READ_NAME + "MonkeyUtil", element);
                    }
                }
            }
        }
    }

    private void dealStopMonkeyService(RoundEnvironment roundEnvironment) {
        Set<? extends Element> elementSet = roundEnvironment.getElementsAnnotatedWith(StopMonkeyService.class);
        for (Element element : elementSet) {
            if (element.getKind() == ElementKind.METHOD) {
                if (mMethodCompileUtil != null) {
                    StopMonkeyService stop = element.getAnnotation(StopMonkeyService.class);
                    ServiceNameEnum serviceName = stop.serviceName();
                    if (serviceName.equals(ServiceNameEnum.COMIC_BOOK_READ_SERVICE)) {
                        mMethodCompileUtil.compileStopService(
                                IContact.COMIC_BOOK_READ_NAME + "MonkeyUtil", element);
                    } else if (serviceName.equals(ServiceNameEnum.PAPER_POINT_READ_SERVICE)) {
                        mMethodCompileUtil.compileStopService(
                                IContact.PAPER_POINT_READ_NAME + "MonkeyUtil", element);
                    }
                }
            }
        }
    }

    private void dealSendMsg2MService(RoundEnvironment roundEnvironment) {
        Set<? extends Element> elementSet = roundEnvironment.getElementsAnnotatedWith(SendMsg2MService.class);
        for (Element element : elementSet) {
            if (element.getKind() == ElementKind.METHOD) {
                if (mMethodCompileUtil != null) {
                    SendMsg2MService sendMsg = element.getAnnotation(SendMsg2MService.class);
                    ServiceNameEnum serviceName = sendMsg.serviceName();
                    int what = getMessageWhat(sendMsg.what());
                    if (serviceName.equals(ServiceNameEnum.COMIC_BOOK_READ_SERVICE)) {
                        mMethodCompileUtil.compileSendMsg2Service(
                                IContact.COMIC_BOOK_READ_NAME + "MonkeyUtil", element, what);
                    } else if (serviceName.equals(ServiceNameEnum.PAPER_POINT_READ_SERVICE)) {
                        mMethodCompileUtil.compileSendMsg2Service(
                                IContact.PAPER_POINT_READ_NAME + "MonkeyUtil", element, what);
                    }
                }
            }
        }
    }


    private int getMessageWhat (MessageWhat what) {
        switch (what) {
            case PPR_TYPE_MESSAGE:
                return IContact.PPR_TYPE_MESSAGE;
            case CB_TYPE_START_SCAN:
                return IContact.CB_TYPE_START_SCAN;
            case CB_TYPE_SCAN_BOOK_RESULT:
                return IContact.CB_TYPE_SCAN_BOOK_RESULT;
            case CB_TYPE_BOOK_PAGE:
                return IContact.CB_TYPE_BOOK_PAGE;
            case CB_TYPE_STOP_SEND_MESSAGE:
                return IContact.CB_TYPE_STOP_SEND_MESSAGE;
            default:
                return -100;
        }
    }
}
