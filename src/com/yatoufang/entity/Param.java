package com.yatoufang.entity;
/**
 * @Auther: hse
 * @Date: 2021/1/13
 */
public class Param {

    private int level;
    private String paramName;
    private String paramType;
    private Boolean required;
    private String defaultValue;
    private String paramDescription = "";



    public Param(String paramName) {
        this.paramName = paramName;
        this.required = true;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getParamDescription() {
        return paramDescription;
    }

    public void setParamDescription(String paramDescription) {
        this.paramDescription = paramDescription;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getALL(){
        return "| " + paramName + " | " + paramType + " | " + required +  " | " + paramDescription +  " |";
    }

    @Override
    public String toString() {
        return "Param{" +
                "paramName='" + paramName + '\'' +
                ", paramType='" + paramType + '\'' +
                ", required=" + required +
                ", defaultValue='" + defaultValue + '\'' +
                ", paramDescription='" + paramDescription + '\'' +
                '}';
    }
}
