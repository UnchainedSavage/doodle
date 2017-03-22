package com.midea.fridge.fridgedoodle;

import com.midea.fridge.fridgedoodle.bean.DrawStep;
import com.midea.fridge.fridgedoodle.bean.DrawStepSet;

/**
 * Created by Administrator on 2016/12/29.
 */
public class DrawHelper {
    private static DrawHelper instance;
    /**
     * 操作类型-画笔
     */
    public static final int DRAW_PEN = 1;
    /**
     * 操作类型-文字
     */
    public static final int DRAW_TEXT = 2;

    private DrawStepSet mDrawStepSet;
    private PenColor mPenColor;// 当前选择的颜色
    private PenType mPenType;// 当前选择的笔类型
    private EraserType mEraserType;// 当前选择的橡皮擦类型
    private boolean mTextType;// 文本工具是否选择

    public static synchronized DrawHelper getInstance() {
        if(null == instance) {
            instance = new DrawHelper();
        }
        return instance;
    }

    private DrawHelper() {
        mDrawStepSet = new DrawStepSet();
        reset();
    }

    public void reset() {
        selectPenColor(PenColor.BLACK);
        selectPenType(PenType.MIDDLE);
        mDrawStepSet.getSaveSteps().clear();
        mDrawStepSet.getDeleteSteps().clear();
    }

    public void addDrawStep(DrawStep oneStep) {
        mDrawStepSet.getSaveSteps().add(oneStep);
    }

    public DrawStep undoDrawStep() {
        DrawStep lastDrawStep = mDrawStepSet.getSaveSteps().get(mDrawStepSet.getSaveSteps().size() - 1);
        mDrawStepSet.getDeleteSteps().add(lastDrawStep);
        mDrawStepSet.getSaveSteps().remove(lastDrawStep);
        return lastDrawStep;
    }

    public DrawStep redoDrawStep() {
        DrawStep nextDrawStep = mDrawStepSet.getDeleteSteps().get(mDrawStepSet.getDeleteSteps().size() - 1);
        mDrawStepSet.getSaveSteps().add(nextDrawStep);
        mDrawStepSet.getDeleteSteps().remove(nextDrawStep);
        return nextDrawStep;
    }

    public DrawStepSet getDrawStepSet() {
        return mDrawStepSet;
    }

    public void selectPenColor(PenColor penColor) {
        mPenColor = penColor;
    }

    public PenColor getPenColor() {
        return mPenColor;
    }

    public void selectPenType(PenType penType) {
        mEraserType = null;
        mTextType = false;
        mPenType = penType;
    }

    public PenType getPenType() {
        return mPenType;
    }

    public void selectEraserType(EraserType eraserType) {
        mPenType = null;
        mTextType = false;
        mEraserType = eraserType;
    }

    public EraserType getEraserType() {
        return mEraserType;
    }

    public void selectTextType() {
        mPenType = null;
        mEraserType = null;
        mTextType = true;
    }

    public boolean isPenSelect() {
        if(null != mPenType) {
            return true;
        }
        return false;
    }

    public boolean isEraserSelect() {
        if(null != mEraserType) {
            return true;
        }
        return false;
    }

    public boolean isTextSelect() {
        return mTextType;
    }

    public enum PenColor {
        BLACK("#252525"),RED("#FF0000"),PURPLE("#E203CD"),GREEN("#009845"),YELLOW("#FCCE43"),BLUE("#0070FF");

        private String color;

        private PenColor(String color) {
            this.color = color;
        }

        public String getColor() {
            return this.color;
        }
    }

    public enum PenType {
        SMALL(5),MIDDLE(10),LARGE(18);

        private int width;

        private PenType(int width) {
            this.width = width;
        }

        public int getWidth() {
            return width;
        }
    }

    public enum EraserType {
        SMALL(20),LARGE(40);

        private int width;

        private EraserType(int width) {
            this.width = width;
        }

        public int getWidth() {
            return width;
        }
    }
}
