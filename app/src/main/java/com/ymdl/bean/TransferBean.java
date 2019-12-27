package com.ymdl.bean;

import java.io.Serializable;

/**
 * Created by LENOVO on 2019/12/9.
 */

public class TransferBean implements Serializable {
    private String factoryName;
    private String proNo;
    private String proNum;
    private String proUnit;
    private String equNo;
    private String equName;
    private String proName;
    private String proMasterCode;
    private String equipMasterCode;
    //类型
    private String jumpType;

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public String getProNo() {
        return proNo;
    }

    public void setProNo(String proNo) {
        this.proNo = proNo;
    }

    public String getProNum() {
        return proNum;
    }

    public void setProNum(String proNum) {
        this.proNum = proNum;
    }

    public String getProUnit() {
        return proUnit;
    }

    public void setProUnit(String proUnit) {
        this.proUnit = proUnit;
    }

    public String getEquNo() {
        return equNo;
    }

    public void setEquNo(String equNo) {
        this.equNo = equNo;
    }

    public String getEquName() {
        return equName;
    }

    public void setEquName(String equName) {
        this.equName = equName;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getProMasterCode() {
        return proMasterCode;
    }

    public void setProMasterCode(String proMasterCode) {
        this.proMasterCode = proMasterCode;
    }

    public String getEquipMasterCode() {
        return equipMasterCode;
    }

    public void setEquipMasterCode(String equipMasterCode) {
        this.equipMasterCode = equipMasterCode;
    }

    public String getJumpType() {
        return jumpType;
    }

    public void setJumpType(String jumpType) {
        this.jumpType = jumpType;
    }
}
