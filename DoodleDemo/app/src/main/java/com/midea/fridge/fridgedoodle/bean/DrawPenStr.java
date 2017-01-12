package com.midea.fridge.fridgedoodle.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 绘画存储-画笔路径(字符串格式，方便储存)
 * Created 2015-7-13 10:36:1
 */
public class DrawPenStr {
    /**画笔颜色*/
    private int color;
    /**画笔粗细*/
    private float strokeWidth;
    /**是否橡皮擦*/
    private boolean isEraser;
    /**移动到初始点坐标*/
    private Point moveTo;
    /**移动中A集*/
    private List<Point> quadToA;
    /**移动中B集*/
    private List<Point> quadToB;
    /**移动到终点坐标*/
    private Point lineTo;
    /**所在界面高距坐标*/
    private Point offset;

    /**
     *获得画笔颜色
     *Created 2015-7-21 18:11:48
     *@return 画笔颜色
     *@author gpy
     */
    public int getColor() {
        return color;
    }

    /**
     *设置画笔颜色
     *Created 2015-7-21 18:11:48
     *@param color 画笔颜色
     *@author gpy
     */
    public void setColor(int color) {
        this.color = color;
    }

    /**
     *获得画笔粗细
     *Created 2015-7-21 18:11:48
     *@return 画笔粗细
     *@author gpy
     */
    public float getStrokeWidth() {
        return strokeWidth;
    }

    /**
     *设置画笔粗细
     *Created 2015-7-21 18:11:48
     *@param strokeWidth 画笔粗细
     *@author gpy
     */
    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    /**
     *获得是否橡皮擦
     *Created 2015-7-21 18:11:48
     *@return 是否橡皮擦
     *@author gpy
     */
    public boolean getIsEraser() {
        return isEraser;
    }

    /**
     *设置是否橡皮擦
     *Created 2015-7-21 18:11:48
     *@param isEraser 是否橡皮擦
     *@author gpy
     */
    public void setIsEraser(boolean isEraser) {
        this.isEraser = isEraser;
    }

    /**
     *获得移动到初始点坐标
     *Created 2015-7-21 18:11:48
     *@return 移动到初始点坐标
     *@author gpy
     */
    public Point getMoveTo() {
        return moveTo;
    }

    /**
     *设置移动到初始点坐标
     *Created 2015-7-21 18:11:48
     *@param moveTo 移动到初始点坐标
     *@author gpy
     */
    public void setMoveTo(Point moveTo) {
        this.moveTo = moveTo;
    }

    /**
     *获得移动中A集
     *Created 2015-7-21 18:11:48
     *@return 移动中A集
     *@author gpy
     */
    public List<Point> getQuadToA() {
        if(null== quadToA){
            quadToA = new ArrayList<Point>();
        }
        return quadToA;
    }

    /**
     *设置移动中A集
     *Created 2015-7-21 18:11:48
     *@param quadToA 移动中A集
     *@author gpy
     */
    public void setQuadToA(List<Point> quadToA) {
        this.quadToA = quadToA;
    }

    /**
     *获得移动中B集
     *Created 2015-7-21 18:11:48
     *@return 移动中B集
     *@author gpy
     */
    public List<Point> getQuadToB() {
        if(null== quadToB){
            quadToB = new ArrayList<Point>();
        }
        return quadToB;
    }

    /**
     *设置移动中B集
     *Created 2015-7-21 18:11:48
     *@param quadToB 移动中B集
     *@author gpy
     */
    public void setQuadToB(List<Point> quadToB) {
        this.quadToB = quadToB;
    }

    /**
     *获得移动到终点坐标
     *Created 2015-7-21 18:11:48
     *@return 移动到终点坐标
     *@author gpy
     */
    public Point getLineTo() {
        return lineTo;
    }

    /**
     *设置移动到终点坐标
     *Created 2015-7-21 18:11:48
     *@param lineTo 移动到终点坐标
     *@author gpy
     */
    public void setLineTo(Point lineTo) {
        this.lineTo = lineTo;
    }

    /**
     *获得所在界面高距坐标
     *Created 2015-7-21 18:11:48
     *@return 所在界面高距坐标
     *@author gpy
     */
    public Point getOffset() {
        return offset;
    }

    /**
     *设置所在界面高距坐标
     *Created 2015-7-21 18:11:48
     *@param offset 所在界面高距坐标
     *@author gpy
     */
    public void setOffset(Point offset) {
        this.offset = offset;
    }
}
