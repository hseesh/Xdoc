package com.yatoufang.generator;

import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.util.PsiUtil;
import com.yatoufang.entity.Method;
import com.yatoufang.entity.ReferenceBox;
import com.yatoufang.templet.SpringAnnotation;
import com.yatoufang.templet.Value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @Auther: hse
 * @Date: 2021/1/23
 */
public class Parser {


    public Method action(PsiMethod psiMethod, String baseUrl, String classUrl){
        HashSet<String> mapSet = new HashSet<>();
        ArrayList<String> mapKey = new ArrayList<>();
        ArrayList<PsiType> mapType = new ArrayList<>();
        ArrayList<String> paramDesc = new ArrayList<>();
        Method method = new Method();
        int paramsCount = Psi.getParamsCount(psiMethod);
        //Top-Down Navigation JavaRecursiveElementWalkingVisitor
        psiMethod.accept(new JavaRecursiveElementWalkingVisitor() {

            @Override
            public void visitDocComment(PsiDocComment comment) {
                method.setDescription(Psi.getMethodDescription(comment));
                PsiDocTag[] tags = comment.getTags();
                for (PsiDocTag tag : tags) {
                    switch (tag.getName()) {
                        case "param":
                            paramDesc.add(Psi.getParameterDescription(tag));
                            break;
                        case "return":
                            method.setReturnDescription(Psi.getParameterDescription(tag));
                            break;
                        default:break;
                    }
                }
            }

            @Override
            public void visitParameterList(PsiParameterList list) {
                method.setParams(Psi.getParamsDetail(list, paramsCount));
            }

            @Override
            public void visitAnnotation(PsiAnnotation annotation) {
               if(annotation.hasQualifiedName(SpringAnnotation.REQUESTMAPPING)
               || annotation.hasQualifiedName(SpringAnnotation.POSTMAPPING)
               || annotation.hasQualifiedName(SpringAnnotation.GETMAPPING)
               || annotation.hasQualifiedName(SpringAnnotation.DELETEMAPPING)
               || annotation.hasQualifiedName(SpringAnnotation.PUTMAPPING)){
                   String methodUrl = Psi.getMethodRequestUrl(annotation);
                   methodUrl = methodUrl.startsWith("/") ? methodUrl : "/" + methodUrl;
                   method.setUrl(classUrl +  methodUrl);
                   method.setRequestMethod(Psi.getMethodRequestType(annotation));
               }
            }


            @Override
            public void visitReturnStatement(PsiReturnStatement statement) {
                PsiExpression returnValue = statement.getReturnValue();
                if (returnValue != null) {
                        // for complex return statement
                        returnValue.accept(new JavaRecursiveElementWalkingVisitor() {
                            @Override
                            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                                String text = expression.getText();
                                if (text.indexOf(".put(") > 0) {
                                    PsiExpressionList argumentList = expression.getArgumentList();
                                    PsiExpression[] expressions = argumentList.getExpressions();
                                    if (expressions.length == 2) {
                                        if (mapSet.add(expressions[0].getText())) {
                                            mapKey.add(expressions[0].getText().replace("\"", ""));
                                            mapType.add(expressions[1].getType());
                                        }
                                    }
                                }
                            }
                        });
                    }

            }

            @Override
            public void visitMethodCallExpression(PsiMethodCallExpression expression) {
                //Determines the map type's expression is not rigorous by string comparison and parameter number matching
                String text = expression.getText();
                if (text.indexOf(".put(") > 0) {// indexOf is faster than KMP ,regular expression and contaions in this case
                    PsiExpressionList argumentList = expression.getArgumentList();
                    PsiExpression[] expressions = argumentList.getExpressions();
                    if (expressions.length == 2) {
                        if (mapSet.add(expressions[0].getText())) {
                            mapKey.add(expressions[0].getText().replace("\"", ""));
                            mapType.add(expressions[1].getType());
                        }
                    }
                }
            }
        });

        PsiType returnType = psiMethod.getReturnType();
        StringBuilder builder = new StringBuilder("{\n");
        ReferenceBox example = new ReferenceBox();

        example.setMapKey(mapKey);
        example.setMapValueType(mapType);
        if (returnType != null) {
            getDefaultValue(returnType, builder, example);
        }
        //Revised format for single return value
        // eg: {"result"}
        int lastIndex = builder.lastIndexOf(",");
        if (lastIndex == builder.length() - 2) {
            builder.deleteCharAt(lastIndex);
        }
        if (builder.length() > 1 && builder.indexOf(":") < 0) {
            builder.deleteCharAt(0);
            lastIndex = builder.indexOf("\"");
            if(lastIndex > 0){
                builder.deleteCharAt(lastIndex);
            }
            lastIndex = builder.lastIndexOf("\"");
            if(lastIndex > 0){
                builder.deleteCharAt(lastIndex);
            }
        }else{
            builder.append("}");
        }

        PsiType returnValueType = psiMethod.getReturnType();
        if(returnValueType != null){
            method.setReturnValue(returnValueType.getPresentableText());
            method.setReturnType(returnValueType.getCanonicalText());
        }
        method.setName(psiMethod.getName());
        method.setParamDescription(paramDesc);
        method.setResponseExample(builder.toString());
        method.setRequestExample(baseUrl + method.getUrl());
        return method;
    }


    /**
     *  Recursive parsing  return object for response example
     * @param psiType return object value type
     * @param builder markdown string builder
     * @param example method call expression record box for key-value type
     */
    private void getDefaultValue(PsiType psiType, StringBuilder builder, ReferenceBox example) {
        PsiType result = PsiUtil.extractIterableTypeParameter(psiType, true);
        if (result != null) {
            getDefaultValue(result, builder, example);
        }
        String type = psiType.getPresentableText();

        int index = type.indexOf("<");
        if (index > 0) {
            type = type.substring(0, index);
        }
        ArrayList<String> mapCase = example.getMapKey();
        switch (type) {
            case "int":
            case "long":
            case "short":
            case "Integer":
            case "Long":
            case "Short":
            case "BigDecimal":
                builder.append(Value.getInteger());
                builder.append(",\n");
                break;
            case "double":
            case "float":
            case "Double":
            case "Float":
                builder.append(Value.getDecimal());
                builder.append(",\n");
                break;
            case "boolean":
            case "Boolean":
                builder.append(true);
                builder.append(",\n");
                break;
            case "byte":
            case "Byte":
                builder.append(1);
                builder.append(",\n");
                break;
            case "char":
            case "Char":
            case "String":
                builder.append("\"");
                builder.append(Value.getWord());
                builder.append("\"");
                builder.append(",\n");
                break;
            // we can not parser object,grammatically the real type of object is known only when the program is running
            // eg:List<Object>,  List<User> only latter can be parsed
            // Actually before compiling,  parsing source code can also calculate the real type of object, it's very very complex
            // I've tried to use Reference to fix it,bur it not work ,return reference always be null
            case "Object":
                builder.append("\"object\"");
                builder.append(",\n");
                break;
            case "Date":
                builder.append("2021-01-01");
                builder.append(",\n");
                break;
            case "Map":
            case "HashMap":
            case "TreeMap":
            case "Hashtable":
                if (mapCase != null) {
                    Iterator<String> keys = mapCase.iterator();
                    Iterator<PsiType> types = example.getMapValueType().iterator();
                    while (keys.hasNext()) {
                        PsiType tem = types.next();
                        String key = keys.next();
                        keys.remove();
                        types.remove();
                        builder.append("    \"");
                        builder.append(key);
                        builder.append("\" : ");
                        getDefaultValue(tem, builder, example);
                    }
                }
                break;
            default:
                if (type.contains("[")) {
                    builder.append("[\"Author\", \"hse\"]");
                    builder.append(",\n");
                    break;
                }
                // for parsing custom entity
                PsiClass entity = PsiUtil.resolveClassInClassTypeOnly(psiType);
                if (entity != null) {
                    String targetClass = entity.getQualifiedName();
                    if (targetClass != null && targetClass.startsWith(Psi.getBasePackage())) {
                        PsiField[] fields = entity.getFields();
                        ArrayList<PsiField> classFields = new ArrayList<>();
                        for (PsiField field : fields) {
                            //remove serialVersionUID field
                            if (!"serialVersionUID".equals(field.getName())) {
                                classFields.add(field);
                            }
                        }
                        if (classFields.size() > 0) {
                            builder.append("  {\n");
                            for (PsiField psiField : classFields) {
                                builder.append("    \"");
                                builder.append(psiField.getName());
                                builder.append("\": ");
                                getDefaultValue(psiField.getType(), builder, example);
                            }
                            builder.deleteCharAt(builder.length() - 1);
                            builder.deleteCharAt(builder.length() - 1);
                            builder.append("},\n");
                        }
                    }
                }
                // parsing custom  key-value type return object
                // all map's method call expression will be record in reference box ,it's a bug in a way
                if (mapCase != null) {
                    Iterator<String> keys = mapCase.iterator();
                    Iterator<PsiType> types = example.getMapValueType().iterator();
                    while (keys.hasNext()) {
                        PsiType tem = types.next();
                        String key = keys.next();
                        keys.remove();
                        types.remove();
                        builder.append("    \"");
                        builder.append(key);
                        builder.append("\" : ");
                        getDefaultValue(tem, builder, example);
                    }
                }
                break;
        }
    }
}
