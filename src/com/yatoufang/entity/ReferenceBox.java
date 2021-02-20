package com.yatoufang.entity;

import com.intellij.psi.PsiType;

import java.util.ArrayList;

/**
 * @Auther: hse
 * @Date: 2021/1/20
 */
public class ReferenceBox {


    /**
     * record key-value type method call expression for parsing return object
     */
    private ArrayList<String> mapKey;
    private ArrayList<PsiType> mapValueType;


    public ArrayList<PsiType> getMapValueType() {
        return mapValueType;
    }

    public void setMapValueType(ArrayList<PsiType> mapValueType) {
        this.mapValueType = mapValueType;
    }


    public ArrayList<String> getMapKey() {
        return mapKey;
    }

    public void setMapKey(ArrayList<String> mapKey) {
        this.mapKey = mapKey;
    }

}
