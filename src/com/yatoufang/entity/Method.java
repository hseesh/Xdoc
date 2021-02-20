package com.yatoufang.entity;

import java.util.ArrayList;

/**
 * @Auther: hse
 * @Date: 2021/1/13
 */
public class Method {

    private String description;
    private String url;
    private String requestMethod;
    private String returnType;
    private String returnValue;
    private String returnDescription="";
    private String name;
    private String requestExample;
    private String responseExample;
    private ArrayList<Param> params;


    public String getResponseExample() {
        return responseExample;
    }

    public void setResponseExample(String responseExample) {
        this.responseExample = responseExample;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public String getReviseUrl(){
        if(url.startsWith("/")){
            return  url.substring(1,url.length());
        }
        return url;
    }

    public void setUrl(String url) {
        //handle Restfull method
        int index = url.indexOf("{");
        if(index > 0){
            url = url.substring(0,index - 1);
        }
        this.url = url;
    }

    public String getRequestMethod() {
        return "Type: " + requestMethod;
    }

    public String getRequestType() {
        return  requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        if (requestMethod != null) {
            if (requestMethod.contains(".")) {
                String[] split = requestMethod.split("\\.");
                this.requestMethod =  split[1];
            }else{
                this.requestMethod = requestMethod;
            }
        }
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(String returnValue) {
        this.returnValue = returnValue;
    }

    public String getReturnDescription() {
        return returnDescription;
    }

    public void setReturnDescription(String returnDescription) {
        this.returnDescription = returnDescription;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequestExample() {
        return requestExample;
    }

    public void setRequestExample(String requestExample) {

        if ("POST".equals(requestMethod)) {
            this.requestExample = requestExample;
        } else {
            StringBuilder urlBuilder = new StringBuilder(requestExample);
            if (params.size() > 0) {
                urlBuilder.append("?");
                for (int i = 0; i < params.size(); i++) {
                    Param param = params.get(i);
                    urlBuilder.append(param.getParamName().trim());
                    urlBuilder.append("=");
                    //@RequestMapping defaultValue is "\n\t\t\n\t\t\n\uE000\uE001\uE002\n\t\t\t\t\n"
                    String defaultValue = param.getDefaultValue();
                    if(defaultValue == null || !defaultValue.startsWith("\\n")){
                        urlBuilder.append(param.getParamName());
                    }
                    if (i != params.size() - 1) {
                        urlBuilder.append("&");
                    }

                }
            }
            this.requestExample = urlBuilder.toString();
        }
    }

    public ArrayList<Param> getParams() {
        return params;
    }

    public void setParams(ArrayList<Param> params) {
        this.params = params;
    }

    public void setParamDescription(ArrayList<String> description) {
        for (int i = 0; i < params.size(); i++) {
            if(i < description.size()){
                params.get(i).setParamDescription(description.get(i));
            }
        }
    }

    public String getReturnList() {
        return "| " + returnValue + " | " + returnType + " | " + returnDescription + " |";
    }

    @Override
    public String toString() {
        return "Method{" +
                "description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", requestMethod='" + requestMethod + '\'' +
                ", returnType='" + returnType + '\'' +
                ", returnValue='" + returnValue + '\'' +
                ", returnDescription='" + returnDescription + '\'' +
                ", name='" + name + '\'' +
                ", requestExample='" + requestExample + '\'' +
                ", params=" + params.toString() +
                '}';
    }

}
