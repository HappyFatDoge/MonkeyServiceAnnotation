package com.nineteenc.compile.utils;

import com.nineteenc.annotation.MonkeyServiceUtil;
import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;

import java.util.ArrayList;

import javax.lang.model.element.Element;

/**
 * Author    zhengchengbin
 * Describe:
 * Data:      2019/8/20 14:33
 * Modify by:
 * Modification date:
 * Modify content:
 */
public class MethodCompileUtil {

    private static volatile MethodCompileUtil mMethodCompile;

    private Trees mTrees;
    private TreeMaker mTreeMaker;
    private Name.Table mName;

    private static final String IS_USE_MONKEY_PATH = "android.app.ActivityManager.isUserAMonkey";

    private MethodCompileUtil(Trees trees, TreeMaker treeMaker, Name.Table mName) {
        this.mTrees = trees;
        this.mTreeMaker = treeMaker;
        this.mName = mName;
    }

    public static MethodCompileUtil getInstance(Trees trees, TreeMaker treeMaker, Name.Table name) {
        if (mMethodCompile == null) {
            synchronized (MethodCompileUtil.class) {
                if (mMethodCompile == null) {
                    mMethodCompile = new MethodCompileUtil(trees, treeMaker, name);
                }
            }
        }
        return mMethodCompile;
    }

    public void compileBind2Service(final String className, Element element) {
        JCTree jcTree = (JCTree) mTrees.getTree(element);
        jcTree.accept(new TreeTranslator(){
            @Override
            public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
                super.visitMethodDef(jcMethodDecl);
                System.out.println("==========    visitMethodDef--method: " + jcMethodDecl.name.toString() + "    ==========");
                System.out.println(jcMethodDecl.toString());

                ArrayList<JCTree.JCStatement> statementList = new ArrayList<>();
                ArrayList<JCTree.JCStatement> oldStatements = new ArrayList<>();

                JCTree.JCExpression isUseMonkey = mTreeMaker.Exec(mTreeMaker.Apply(List.<JCTree.JCExpression>nil(),
                        memberAccess(IS_USE_MONKEY_PATH),
                        List.<JCTree.JCExpression>nil())).expr;

                JCTree.JCVariableDecl monkeyUtil = mTreeMaker.VarDef(mTreeMaker.Modifiers(0), mName.fromString("monkeyUtil"),
                        memberAccess(MonkeyServiceUtil.SERVICE_PAGE_NAME + "." + className),
                        mTreeMaker.Apply(List.<JCTree.JCExpression>nil(),
                                memberAccess(MonkeyServiceUtil.SERVICE_PAGE_NAME + "." + className + ".getInstance"),
                                List.<JCTree.JCExpression>nil()));

                JCTree.JCExpressionStatement thenSta = mTreeMaker.Exec(mTreeMaker.Apply(
                        List.<JCTree.JCExpression>nil(),
                        mTreeMaker.Select(mTreeMaker.Ident(monkeyUtil.name), mName.fromString(IContact.BIND_MONKEY_SERVICE)),
                        List.<JCTree.JCExpression>nil()));

                ArrayList<JCTree.JCStatement> thenBody = new ArrayList<>();
                thenBody.add(monkeyUtil);
                thenBody.add(thenSta);

                for (JCTree.JCStatement statement : jcMethodDecl.body.stats) {
                    oldStatements.add(statement);
                    String statementStr = statement.toString();
                    if (statementStr.contains("addStatement2Bind")) {
                        String addSt[] = statementStr.substring(statementStr.indexOf("\"") + 1,
                                statementStr.lastIndexOf("\"")).split(" = ");
                        JCTree.JCExpressionStatement jcStatement = mTreeMaker.Exec(mTreeMaker.Assign(
                                mTreeMaker.Ident(getNameFromString(addSt[0])),
                                mTreeMaker.Literal(Boolean.valueOf(addSt[1]))));
                        thenBody.add(jcStatement);
                        oldStatements.remove(statement);
                    }
                }

                JCTree.JCIf ifStatement = mTreeMaker.If(
                        mTreeMaker.Binary(JCTree.Tag.EQ, isUseMonkey, mTreeMaker.Literal(true)),
                        mTreeMaker.Block(0, List.from(thenBody)),
                        mTreeMaker.Block(0, List.from(oldStatements))
                );

                statementList.add(ifStatement);
                result = mTreeMaker.MethodDef(jcMethodDecl.mods,
                        jcMethodDecl.name,
                        jcMethodDecl.restype,
                        jcMethodDecl.typarams,
                        jcMethodDecl.params,
                        jcMethodDecl.thrown,
                        mTreeMaker.Block(0, List.from(statementList)),
                        jcMethodDecl.defaultValue);
                jcMethodDecl.body = mTreeMaker.Block(0, List.from(statementList));
                super.visitMethodDef(jcMethodDecl);
                System.out.println("==========    end visitMethodDef    ==========");
                System.out.println(jcMethodDecl.toString());
            }
        });
    }


    public void compileUnbind2Service(final String className, Element element) {
        JCTree jcTree = (JCTree) mTrees.getTree(element);
        jcTree.accept(new TreeTranslator(){
            @Override
            public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
                System.out.println("==========    visitMethodDef--method: " + jcMethodDecl.name.toString() + "    ==========");
                System.out.println(jcMethodDecl.toString());

                ArrayList<JCTree.JCStatement> statementList = new ArrayList<>();
                ArrayList<JCTree.JCStatement> oldStatements = new ArrayList<>();

                JCTree.JCExpression isUseMonkey = mTreeMaker.Exec(mTreeMaker.Apply(List.<JCTree.JCExpression>nil(),
                        memberAccess(IS_USE_MONKEY_PATH),
                        List.<JCTree.JCExpression>nil())).expr;

                JCTree.JCVariableDecl monkeyUtil = mTreeMaker.VarDef(mTreeMaker.Modifiers(0), mName.fromString("monkeyUtil"),
                        memberAccess(MonkeyServiceUtil.SERVICE_PAGE_NAME + "." + className),
                        mTreeMaker.Apply(List.<JCTree.JCExpression>nil(),
                                memberAccess(MonkeyServiceUtil.SERVICE_PAGE_NAME + "." + className + ".getInstance"),
                                List.<JCTree.JCExpression>nil()));

                JCTree.JCExpressionStatement thenSta = mTreeMaker.Exec(mTreeMaker.Apply(
                        List.<JCTree.JCExpression>nil(),
                        mTreeMaker.Select(mTreeMaker.Ident(monkeyUtil.name), mName.fromString(IContact.UNBIND_MONKEY_SERVICE)),
                        List.<JCTree.JCExpression>nil()));

                ArrayList<JCTree.JCStatement> thenBody = new ArrayList<>();
                thenBody.add(monkeyUtil);
                thenBody.add(thenSta);

                for (JCTree.JCStatement statement : jcMethodDecl.body.stats) {
                    oldStatements.add(statement);
                    String statementStr = statement.toString();
                    if (statementStr.contains("addStatement2Unbind")) {
                        String addSt[] = statementStr.substring(statementStr.indexOf("\"") + 1,
                                statementStr.lastIndexOf("\"")).split(" = ");
                        JCTree.JCExpressionStatement jcStatement = mTreeMaker.Exec(mTreeMaker.Assign(
                                mTreeMaker.Ident(getNameFromString(addSt[0])),
                                mTreeMaker.Literal(Boolean.valueOf(addSt[1]))));
                        thenBody.add(jcStatement);
                        oldStatements.remove(statement);
                    }
                }

                JCTree.JCIf ifStatement = mTreeMaker.If(
                        mTreeMaker.Binary(JCTree.Tag.EQ, isUseMonkey, mTreeMaker.Literal(true)),
                        mTreeMaker.Block(0, List.from(thenBody)),
                        mTreeMaker.Block(0, List.from(oldStatements))
                );

                statementList.add(ifStatement);
                result = mTreeMaker.MethodDef(jcMethodDecl.mods,
                        jcMethodDecl.name,
                        jcMethodDecl.restype,
                        jcMethodDecl.typarams,
                        jcMethodDecl.params,
                        jcMethodDecl.thrown,
                        mTreeMaker.Block(0, List.from(statementList)),
                        jcMethodDecl.defaultValue);
                jcMethodDecl.body = mTreeMaker.Block(0, List.from(statementList));
                super.visitMethodDef(jcMethodDecl);
                System.out.println("==========    end visitMethodDef    ==========");
                System.out.println(jcMethodDecl.toString());
            }
        });
    }


    public void compileStartService(final String className, Element element) {
        JCTree jcTree = (JCTree) mTrees.getTree(element);
        jcTree.accept(new TreeTranslator(){
            @Override
            public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
                System.out.println("==========    visitMethodDef--method: " + jcMethodDecl.name.toString() + "    ==========");
                System.out.println(jcMethodDecl.toString());

                ArrayList<JCTree.JCStatement> statementList = new ArrayList<>();

                JCTree.JCVariableDecl monkeyUtil = mTreeMaker.VarDef(mTreeMaker.Modifiers(0), mName.fromString("monkeyUtil"),
                        memberAccess(MonkeyServiceUtil.SERVICE_PAGE_NAME + "." + className),
                        mTreeMaker.Apply(List.<JCTree.JCExpression>nil(),
                                memberAccess(MonkeyServiceUtil.SERVICE_PAGE_NAME + "." + className + ".getInstance"),
                                List.<JCTree.JCExpression>nil()));

                JCTree.JCExpressionStatement startStatement = mTreeMaker.Exec(mTreeMaker.Apply(
                        List.<JCTree.JCExpression>nil(),
                        mTreeMaker.Select(mTreeMaker.Ident(monkeyUtil.name), mName.fromString(IContact.START_MONKEY_SERVICE)),
                        List.<JCTree.JCExpression>nil()));

                for (JCTree.JCStatement statement : jcMethodDecl.body.stats) {
                    statementList.add(statement);
                }

                statementList.add(monkeyUtil);
                statementList.add(startStatement);
                result = mTreeMaker.MethodDef(jcMethodDecl.mods,
                        jcMethodDecl.name,
                        jcMethodDecl.restype,
                        jcMethodDecl.typarams,
                        jcMethodDecl.params,
                        jcMethodDecl.thrown,
                        mTreeMaker.Block(0, List.from(statementList)),
                        jcMethodDecl.defaultValue);
                jcMethodDecl.body = mTreeMaker.Block(0, List.from(statementList));
                super.visitMethodDef(jcMethodDecl);
                System.out.println("==========    end visitMethodDef    ==========");
                System.out.println(jcMethodDecl.toString());
            }
        });
    }

    public void compileStopService(final String className, Element element) {
        JCTree jcTree = (JCTree) mTrees.getTree(element);
        jcTree.accept(new TreeTranslator(){
            @Override
            public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
                System.out.println("==========    visitMethodDef--method: " + jcMethodDecl.name.toString() + "    ==========");
                System.out.println(jcMethodDecl.toString());

                ArrayList<JCTree.JCStatement> statementList = new ArrayList<>();


                JCTree.JCVariableDecl monkeyUtil = mTreeMaker.VarDef(mTreeMaker.Modifiers(0), mName.fromString("monkeyUtil"),
                        memberAccess(MonkeyServiceUtil.SERVICE_PAGE_NAME + "." + className),
                        mTreeMaker.Apply(List.<JCTree.JCExpression>nil(),
                                memberAccess(MonkeyServiceUtil.SERVICE_PAGE_NAME + "." + className + ".getInstance"),
                                List.<JCTree.JCExpression>nil()));

                JCTree.JCExpressionStatement startStatement = mTreeMaker.Exec(mTreeMaker.Apply(
                        List.<JCTree.JCExpression>nil(),
                        mTreeMaker.Select(mTreeMaker.Ident(monkeyUtil.name), mName.fromString(IContact.STOP_MONKEY_SERVICE)),
                        List.<JCTree.JCExpression>nil()));

                for (JCTree.JCStatement statement : jcMethodDecl.body.stats) {
                    statementList.add(statement);
                }

                statementList.add(monkeyUtil);
                statementList.add(startStatement);
                result = mTreeMaker.MethodDef(jcMethodDecl.mods,
                        jcMethodDecl.name,
                        jcMethodDecl.restype,
                        jcMethodDecl.typarams,
                        jcMethodDecl.params,
                        jcMethodDecl.thrown,
                        mTreeMaker.Block(0, List.from(statementList)),
                        jcMethodDecl.defaultValue);
                jcMethodDecl.body = mTreeMaker.Block(0, List.from(statementList));
                super.visitMethodDef(jcMethodDecl);
                System.out.println("==========    end visitMethodDef    ==========");
                System.out.println(jcMethodDecl.toString());
            }
        });
    }

    public void compileSendMsg2Service(final String className, Element element, final int what) {
        JCTree jcTree = (JCTree) mTrees.getTree(element);
        jcTree.accept(new TreeTranslator(){
            @Override
            public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
                System.out.println("==========    visitMethodDef--method: " + jcMethodDecl.name.toString() + "    ==========");
                System.out.println(jcMethodDecl.toString());

                ArrayList<JCTree.JCStatement> statementList = new ArrayList<>();


                JCTree.JCVariableDecl monkeyUtil = mTreeMaker.VarDef(mTreeMaker.Modifiers(0), mName.fromString("monkeyUtil"),
                        memberAccess(MonkeyServiceUtil.SERVICE_PAGE_NAME + "." + className),
                        mTreeMaker.Apply(List.<JCTree.JCExpression>nil(),
                                memberAccess(MonkeyServiceUtil.SERVICE_PAGE_NAME + "." + className + ".getInstance"),
                                List.<JCTree.JCExpression>nil()));

                JCTree.JCExpressionStatement startStatement = mTreeMaker.Exec(mTreeMaker.Apply(
                        List.of(memberAccess("java.lang.Integer")),
                        mTreeMaker.Select(mTreeMaker.Ident(monkeyUtil.name), mName.fromString(IContact.SEND_MESSAGE)),
                        List.<JCTree.JCExpression>of(mTreeMaker.Literal(what))));

                for (JCTree.JCStatement statement : jcMethodDecl.body.stats) {
                    statementList.add(statement);
                }

                statementList.add(monkeyUtil);
                statementList.add(startStatement);
                result = mTreeMaker.MethodDef(jcMethodDecl.mods,
                        jcMethodDecl.name,
                        jcMethodDecl.restype,
                        jcMethodDecl.typarams,
                        jcMethodDecl.params,
                        jcMethodDecl.thrown,
                        mTreeMaker.Block(0, List.from(statementList)),
                        jcMethodDecl.defaultValue);
                jcMethodDecl.body = mTreeMaker.Block(0, List.from(statementList));
                super.visitMethodDef(jcMethodDecl);
                System.out.println("==========    end visitMethodDef    ==========");
                System.out.println(jcMethodDecl.toString());
            }
        });
    }


    private JCTree.JCExpression memberAccess(String components) {
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = mTreeMaker.Ident(getNameFromString(componentArray[0]));
        for (int i = 1; i < componentArray.length; i++) {
            expr = mTreeMaker.Select(expr, getNameFromString(componentArray[i]));
        }
        return expr;
    }

    private Name getNameFromString (String name) {
        return mName.fromString(name);
    }
}
