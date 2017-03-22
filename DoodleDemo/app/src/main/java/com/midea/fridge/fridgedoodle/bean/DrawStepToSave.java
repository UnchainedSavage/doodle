package com.midea.fridge.fridgedoodle.bean;

/**
 * Created by Administrator on 2017/1/13.
 */
public class DrawStepToSave {
    /**绘画类型*/
    private int type;
    /**画笔路径（字符形式，便于持久化）*/
    private DrawPenStr drawPenStr;
    /** 文本内容信息(便于持久化) */
    private DrawTextObj drawTextObj;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public DrawPenStr getDrawPenStr() {
        return drawPenStr;
    }

    public void setDrawPenStr(DrawPenStr drawPenStr) {
        this.drawPenStr = drawPenStr;
    }

    public DrawTextObj getDrawTextObj() {
        return drawTextObj;
    }

    public void setDrawTextObj(DrawTextObj drawTextObj) {
        this.drawTextObj = drawTextObj;
    }
}
