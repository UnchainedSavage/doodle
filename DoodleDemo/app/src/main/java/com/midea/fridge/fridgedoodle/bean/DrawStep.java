package com.midea.fridge.fridgedoodle.bean;

/**
 * 涂鸦步骤
 */
public class DrawStep {
    /**绘画类型*/
    private int mType;

    /**画笔路径（对象形式，便于撤销操作）*/
    private DrawPenObj mDrawPenObj;
    /**画笔路径（字符形式，便于持久化）*/
    private DrawPenStr mDrawPenStr;

    /**
     *获得绘画类型
     */
    public int getType() {
        return mType;
    }

    /**
     *设置绘画类型
     */
    public void setType(int type) {
        this.mType = type;
    }

    /**
     *获得画笔路径（字符形式）
     */
    public DrawPenStr getDrawPenStr() {
        return mDrawPenStr;
    }

    /**
     *设置画笔路径（字符形式）
     */
    public void setDrawPenStr(DrawPenStr DrawPenStr) {
        this.mDrawPenStr = DrawPenStr;
    }

    /**
     *获得画笔路径
     */
    public DrawPenObj getDrawPenObj() {
        return mDrawPenObj;
    }

    /**
     *设置画笔路径
     */
    public void setDrawPenObj(DrawPenObj drawPenObj) {
        this.mDrawPenObj = drawPenObj;
    }
}
