package com.midea.fridge.fridgedoodle.bean;

/**
 * 绘画存储-文字
 * Created by Administrator on 2017/1/13.
 */
public class DrawTextObj {
    /**
     * 文字x坐标
     */
    private int x;
    /**
     * 文字y坐标
     */
    private int y;
    /**
     * 文字
     */
    private String content;
    /**
     * 文本颜色
     */
    private int textColor;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
