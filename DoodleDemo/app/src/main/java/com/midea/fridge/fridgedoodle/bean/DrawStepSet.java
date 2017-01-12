package com.midea.fridge.fridgedoodle.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 涂鸦步骤集合
 * Created by Administrator on 2016/12/29.
 */
public class DrawStepSet {
    private List<DrawStep> saveSteps;// 还在画布上的步骤
    private List<DrawStep> deleteSteps;// 已撤销的步骤

    public DrawStepSet() {
        saveSteps = new ArrayList<>();
        deleteSteps = new ArrayList<>();
    }

    public List<DrawStep> getSaveSteps() {
        return saveSteps;
    }

    public void setSaveSteps(List<DrawStep> saveSteps) {
        this.saveSteps = saveSteps;
    }

    public List<DrawStep> getDeleteSteps() {
        return deleteSteps;
    }

    public void setDeleteSteps(List<DrawStep> deleteSteps) {
        this.deleteSteps = deleteSteps;
    }
}
