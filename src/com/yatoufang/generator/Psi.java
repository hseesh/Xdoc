package com.yatoufang.generator;

import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocTagValue;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.yatoufang.entity.Param;
import com.yatoufang.templet.SpringAnnotation;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @Auther: hse
 * @Date: 2021/1/15 0003
 * <p>
 * this plugin is base on IntelliJ PSI,The Program Structure Interface, the layer
 * in the IntelliJ Platform
 * <p>
 * Consider using PsiViewer plugin , This plugin will show you the PSI tree structure
 * or see https://plugins.jetbrains.com/docs/intellij/psi.html to learn more about PSI
 * <p>
 * The main content of the API　Document comes from class  file's  Javadoc comments
 * and SpringAnnotation
 * for a method description, it comes from PsiDocComment object
 * for a method param's description .it comes from  docTags @Param
 * <p>
 * An entire class parsing process is done from top to bottom, one line after another,
 * one character after another, its necessary to use many for loops
 * <p>
 * Avoid using too many PsiElement methods which are expensive  deep trees.
 * it could consume a lot of cpu or memory
 * <p>
 * I do suggest making the parsing process as simple as possible in logical
 * <p>
 * Readers are encouraged to review the Javadoc comments or IntelliJ Platform Plugin SDK
 * about PsiElement to lear more
 */
public class Psi {

    private static String basePackage;
    private static String projectName;

    public static String getBasePackage() {
        return basePackage;
    }

    public static void setBasePackage(String basePackage) {
        if (basePackage.contains(".")) {
            String[] strings = basePackage.split("\\.");
            if (strings.length > 2) {
                basePackage = strings[0] + "." + strings[1];
            }
        }
        Psi.basePackage = basePackage;
    }

    public static String getProjectName() {
        return projectName;
    }

    public static void setProjectName(String projectName) {
        Psi.projectName = projectName;
    }

    /**
     * get Params Details for method from  annotations (@RequestParam @RequestBody @PathVariable)
     *
     * @param list        PsiParameterList
     * @param paramsCount paramsCount
     * @return ArrayList<Param>
     */
    public static ArrayList<Param> getParamsDetail(PsiParameterList list, int paramsCount) {
        ArrayList<Param> params = new ArrayList<>();
        PsiParameter[] parameters = list.getParameters();
        for (int i = 0; i < paramsCount; i++) {
            String paraName = parameters[i].getName();
            Param param = new Param(trims(paraName));
            param.setParamType(parameters[i].getType().getPresentableText());
            PsiAnnotation[] parameterAnnotations = parameters[i].getAnnotations();
            for (PsiAnnotation psiAnnotation : parameterAnnotations) {
                if (psiAnnotation.hasQualifiedName(SpringAnnotation.REQUESTPARAM)) {
                    PsiAnnotationMemberValue value = psiAnnotation.findAttributeValue("value");
                    PsiAnnotationMemberValue required = psiAnnotation.findAttributeValue("required");
                    PsiAnnotationMemberValue defaultValue = psiAnnotation.findAttributeValue("defaultValue");
                    // ifStatement is unnecessary because these annotation params has default value
                    if (defaultValue != null) {
                        param.setDefaultValue(trims(defaultValue.getText()));
                    }
                    if (required != null) {
                        param.setRequired(Boolean.valueOf(required.getText()));
                    }
                    if (value != null && "".equals(value.getText())) {
                        param.setParamName(value.getText());
                    }
                } else if (psiAnnotation.hasQualifiedName(SpringAnnotation.REQUESTBODY)) {
                    PsiAnnotationMemberValue required = psiAnnotation.findAttributeValue("required");
                    param.setRequired(Boolean.valueOf(required != null ? required.getText() : null));
                } else if (psiAnnotation.hasQualifiedName(SpringAnnotation.PATHVARIABLE)) {
                    PsiAnnotationMemberValue value = psiAnnotation.findAttributeValue("value");
                    PsiAnnotationMemberValue required = psiAnnotation.findAttributeValue("required");
                    if (required != null) {
                        param.setRequired(Boolean.valueOf(required.getText()));
                    }
                    if (value != null && !"".equals(value.getText())) {
                        param.setParamName(trims(value.getText()));
                    }
                }
            }
            params.add(param);
        }
        return params;
    }


    public static String getParameterDescription(PsiDocTag tag) {
        String paramName = "";
        StringBuilder builder = new StringBuilder();
        PsiDocTagValue valueElement = tag.getValueElement();
        if (valueElement != null) {
            paramName = valueElement.getText();
        }
        PsiElement[] dataElements = tag.getDataElements();
        for (PsiElement psiElement : dataElements) {
            String paramDesc = psiElement.getText().trim();
            if (!paramDesc.equals(paramName) && !"".equals(paramDesc)) {
                builder.append(paramDesc);
            }
        }
        return builder.toString();
    }


    public static String getClassUrl(PsiAnnotation psiAnnotation) {
        if (psiAnnotation != null) {
            PsiAnnotationMemberValue classUrl = psiAnnotation.findAttributeValue("value");
            if (classUrl != null) {
                String replace = classUrl.getText().replace("\"", "");
                if (!"".equals(replace)) {
                    replace = replace.startsWith("/") ? replace : "/" + replace;
                    return replace;
                }
            }
        }
        return "";
    }

    public static int getParamsCount(PsiMethod method) {
        PsiParameterList parameterList = method.getParameterList();
        PsiParameter[] parameters = parameterList.getParameters();
        return parameters.length;
    }

    public static String getMethodDescription(PsiDocComment comment) {
        if (comment != null) {
            PsiElement[] descriptionElements = comment.getDescriptionElements();
            StringBuilder methodDescription = new StringBuilder();
            for (PsiElement element : descriptionElements) {
                if (!"".equals(element.getText().trim())) {
                    methodDescription.append(element.getText().trim());
                }
            }
            return methodDescription.toString();
        }
        return "";
    }

    public static String getMethodRequestType(PsiAnnotation annotation) {
        String methodAnnotation = annotation.getQualifiedName();
        String type = "";
        if (methodAnnotation != null) {
            switch (methodAnnotation) {
                case SpringAnnotation.REQUESTMAPPING:
                    PsiAnnotationMemberValue requestMethod = annotation.findAttributeValue("method");
                    if (requestMethod != null) {
                        type = requestMethod.getText();
                    }
                    break;
                case SpringAnnotation.POSTMAPPING:
                    type = "POST";
                    break;
                case SpringAnnotation.GETMAPPING:
                    type = "GET";
                    break;
                case SpringAnnotation.DELETEMAPPING:
                    type = "DELETE";
                    break;
                case SpringAnnotation.PUTMAPPING:
                    type = "PUT";
                    break;
                default:
                    break;
            }
        }
        return type;
    }

    public static String getMethodRequestUrl(PsiAnnotation annotation) {
        PsiAnnotationMemberValue value = annotation.findAttributeValue("value");
        if (value != null && !"{}".equals(value.getText())) {
            return trims(value.getText().trim());
        } else {
            value = annotation.findAttributeValue("path");
            if (value != null) {
                return trims(value.getText().trim());
            }
        }
        return "";
    }

    @SuppressWarnings("rawtypes")
    public static String getProjectInfo(PsiJavaFile file) {
        String port = "8080", path = file.getProject().getName() + "_war_exploded";
        PsiFile[] contextFiles = FilenameIndex.getFilesByName(file.getProject(), "application.yml", GlobalSearchScope.allScope(file.getProject()));
        if (contextFiles.length != 0) {
            for (PsiFile psiFile : contextFiles) {
                if (psiFile.getName().contains(".yml")) {
                    Yaml yaml = new Yaml();
                    Map map = null;
                    try {
                        map = yaml.loadAs(psiFile.getVirtualFile().getInputStream(), Map.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (map != null) {
                        LinkedHashMap server = (LinkedHashMap) map.get("server");
                        port = String.valueOf(server.get("port"));
                        LinkedHashMap servlet = (LinkedHashMap) server.get("servlet");
                        path = (String) servlet.get("context-path");
                        if (port == null && path == null) {
                            return readProperties(file);
                        }
                        if (path == null) path = "";
                        if (port == null) port = "8080";
                    }
                }
            }
        } else {
            return readProperties(file);
        }
        path = path.startsWith("/") ? path : "/" + path;
        return "http://localhost:" + port + path;
    }

    private static String readProperties(PsiJavaFile file) {
        String port = "8080", path = file.getProject().getName() + "_war_exploded";
        PsiFile[] contextFiles = FilenameIndex.getFilesByName(file.getProject(), "application.properties", GlobalSearchScope.allScope(file.getProject()));
        for (PsiFile contextFile : contextFiles) {
            if (contextFile.getName().contains(".properties")) {
                Properties properties = new Properties();
                try {
                    properties.load(contextFile.getVirtualFile().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                path = properties.getProperty("server.servlet.context-path", "");
                port = properties.getProperty("server.port", "8080");
                break;
            }
        }
        path = path.startsWith("/") ? path : "/" + path;
        return "http://localhost:" + port + path;
    }

    private static String trims(String str) {
        return str.replace("\"", "");
    }


}
