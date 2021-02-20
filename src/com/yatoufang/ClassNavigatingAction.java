package com.yatoufang;



import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.yatoufang.config.AppSettingsState;
import com.yatoufang.entity.Method;
import com.yatoufang.entity.Param;
import com.yatoufang.generator.Parser;
import com.yatoufang.generator.Psi;
import com.yatoufang.templet.ExportDialog;
import com.yatoufang.templet.MarkdownGenerator;
import com.yatoufang.templet.SpringAnnotation;

import java.util.ArrayList;

/**
 * @Auther: hse
 * @Date: 2021/1/10
 */
public class ClassNavigatingAction extends AnAction {


    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiJavaFile file = (PsiJavaFile) e.getData(LangDataKeys.PSI_FILE);

        if (file == null) {
            return;
        }
        Psi.setBasePackage(file.getPackageName());
        Psi.setProjectName(file.getProject().getName());

        PsiClass[] psiClasses = file.getClasses();
        MarkdownGenerator markdownGenerator = new MarkdownGenerator();
        AppSettingsState settingsState = AppSettingsState.getInstance().getState();
        String baseUrl = Psi.getProjectInfo(file);
        for (PsiClass psiClass : psiClasses) {
            // remove following ifStatement for parsing all methods
            if (psiClass.hasAnnotation(SpringAnnotation.RESTCONTROLLER) || psiClass.hasAnnotation(SpringAnnotation.CONTROLLER)) {
                PsiAnnotation classAnnotation = psiClass.getAnnotation(SpringAnnotation.REQUESTMAPPING);
                classAnnotation = classAnnotation == null ? psiClass.getAnnotation(SpringAnnotation.CONTROLLER) : classAnnotation;
                String classUrl = Psi.getClassUrl(classAnnotation);
                ArrayList<Method> methods = action(psiClass, baseUrl,classUrl);
                int methodIndex = 1;
                markdownGenerator.addTitle(psiClass.getQualifiedName(), 2);
                for (Method method : methods) {
                    markdownGenerator.addTitle(methodIndex++ + ". " + method.getDescription(), 3);
                    markdownGenerator.addContent(method.getRequestMethod());
                    markdownGenerator.addURL(method.getReviseUrl());
                    ArrayList<Param> params = method.getParams();
                    if (params.size() > 0) {
                        markdownGenerator.addContent("Request-Parameters:");
                        markdownGenerator.addTableHead();
                        for (Param param : params) {
                            markdownGenerator.addTableRow(param.getALL());
                        }
                    } else {
                        markdownGenerator.addContent("Request-Parameters: null");
                    }
                    if(!settingsState.requestExample){
                        markdownGenerator.addRequestExample(method.getRequestExample());
                    }

                   if(!settingsState.responseFields){
                       if ("void".equals(method.getReturnType())) {
                           markdownGenerator.addContent("Response-Fields: void");
                       } else {
                           markdownGenerator.addContent("Response-Fields: ");
                           markdownGenerator.addResponseTable();
                           markdownGenerator.addTableRow(method.getReturnList());
                       }
                   }
                    if(!settingsState.responseExample){
                        if (method.getReturnType() != null && !"void".equals(method.getReturnType())) {
                            markdownGenerator.addResponseExample(method.getResponseExample());
                        }
                    }
                    markdownGenerator.newLine();
                }
                new ExportDialog(e.getProject(), markdownGenerator.getContent(), psiClass.getQualifiedName()).show();
            }
        }
    }


    private ArrayList<Method> action(PsiClass psiClass, String baseUrl,String classUrl) {
        PsiMethod[] allMethods = psiClass.getMethods();
        ArrayList<Method> methodArrayList = new ArrayList<>();
        for (PsiMethod psiMethod : allMethods) {
            PsiAnnotation[] annotations = psiMethod.getAnnotations();
            //only parsing method which contains Spring Annotation
            for (PsiAnnotation annotation : annotations) {
                if (annotation.hasQualifiedName(SpringAnnotation.REQUESTMAPPING)
                        || annotation.hasQualifiedName(SpringAnnotation.POSTMAPPING)
                        || annotation.hasQualifiedName(SpringAnnotation.GETMAPPING)
                        || annotation.hasQualifiedName(SpringAnnotation.PUTMAPPING)
                        || annotation.hasQualifiedName(SpringAnnotation.DELETEMAPPING)) {
                    methodArrayList.add(new Parser().action(psiMethod,baseUrl,classUrl));
                    break;
                }
            }
        }
        return methodArrayList;
    }
}
