package com.yatoufang;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.psi.*;
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
 * @Date: 2021/1/28
 */
public class MethodNavigatingAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiElement data = e.getData(LangDataKeys.PSI_ELEMENT);
        PsiJavaFile file = (PsiJavaFile) e.getData(LangDataKeys.PSI_FILE);
        if (file == null || data == null) {
            return;
        }
        if (data instanceof PsiMethod) {
            String baseUrl = Psi.getProjectInfo(file);
            AppSettingsState settingsState = AppSettingsState.getInstance();
            String classUrl = "";
            Psi.setBasePackage(file.getPackageName());
            Psi.setProjectName(file.getProject().getName());
            for (PsiClass psiClass : file.getClasses()) {
                if (psiClass.hasAnnotation(SpringAnnotation.RESTCONTROLLER) || psiClass.hasAnnotation(SpringAnnotation.CONTROLLER)) {
                    PsiAnnotation classAnnotation = psiClass.getAnnotation(SpringAnnotation.REQUESTMAPPING);
                    classAnnotation = classAnnotation == null ? psiClass.getAnnotation(SpringAnnotation.CONTROLLER) : classAnnotation;
                    classUrl = Psi.getClassUrl(classAnnotation);
                }
                PsiMethod psiMethod = (PsiMethod) data;
                Method method = new Parser().action(psiMethod, baseUrl,classUrl);
                MarkdownGenerator generator = new MarkdownGenerator();
                ArrayList<Param> params = method.getParams();
                if (method.getDescription() == null) {
                    generator.addTitle(method.getName(), 3);
                } else {
                    generator.addTitle(method.getDescription(), 3);
                }
                if (method.getRequestType() != null) {
                    generator.addContent(method.getRequestMethod());
                }
                if (method.getUrl() != null) {
                    generator.addURL(method.getReviseUrl());
                }
                if (params.size() > 0) {
                    generator.addContent("Request-Parameters:");
                    generator.addTableHead();
                    for (Param param : params) {
                        generator.addTableRow(param.getALL());
                    }
                } else {
                    generator.addContent("Request-Parameters: null");
                }
                if (!settingsState.requestExample) {
                    if(method.getRequestExample() != null){
                        generator.addRequestExample(method.getRequestExample());
                    }
                }
                if (!settingsState.responseFields) {
                    if ("void".equals(method.getReturnType())) {
                        generator.addContent("Response-Fields: void");
                    } else {
                        generator.addContent("Response-Fields: ");
                        generator.addResponseTable();
                        generator.addTableRow(method.getReturnList());
                        if(!settingsState.responseExample){
                            generator.addResponseExample(method.getResponseExample());
                        }
                    }
                }
                generator.newLine();
                new ExportDialog(e.getProject(), generator.getContent(), method.getName()).show();
                break;

            }

        }

    }

}