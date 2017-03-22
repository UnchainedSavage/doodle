package com.midea.fridge.fridgedoodle.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.midea.fridge.fridgedoodle.DrawHelper;
import com.midea.fridge.fridgedoodle.bean.DrawPenObj;
import com.midea.fridge.fridgedoodle.bean.DrawPenStr;
import com.midea.fridge.fridgedoodle.bean.DrawStep;
import com.midea.fridge.fridgedoodle.bean.Point;
import com.midea.fridge.fridgedoodle.eventbus.OneStepFinishEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Iterator;

/**
 * Created by Administrator on 2016/12/22.
 */
public class DrawPenView extends View {
    private static final String TAG = "DrawPenView";

    private Context mContext;
    private Canvas mCanvas = null;// 画布
    private Bitmap mBottomBitmap = null;// 画布底部的bitmap
    private Paint mBitmapPaint = null;
    private Paint mPaint = null;// 画笔
    private Path mPath;// 绘画路径
    private DrawHelper mDrawHelper;

    private static final float TOUCH_TOLERANCE = 4;

    public DrawPenView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        init(context);
    }

    public DrawPenView(Context context, AttributeSet attr) {
        super(context, attr);
        init(context);
    }

    public DrawPenView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mDrawHelper = DrawHelper.getInstance();
        initPaint();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//是否使用抗锯齿功能,会消耗较大资源，绘制图形速度会变慢
        mPaint.setDither(true);// 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
        mPaint.setColor(Color.parseColor(mDrawHelper.getPenColor().getColor()));//设置绘制的颜色
        mPaint.setStyle(Paint.Style.STROKE);//设置画笔的样式
        mPaint.setStrokeJoin(Paint.Join.ROUND);//设置绘制时各图形的结合方式，如平滑效果等
        mPaint.setStrokeCap(Paint.Cap.ROUND);//当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的图形样式，如圆形样式    Cap.ROUND,或方形样式Cap.SQUARE
        mPaint.setStrokeWidth(mDrawHelper.getPenType().getWidth());//当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的粗细度
    }

    /**
     * 切换至画笔
     */
    public void switchPen() {
        initPaint();
        postInvalidate();
    }

    /**
     * 切换至橡皮擦
     */
    public void switchEraser() {
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(mDrawHelper.getEraserType().getWidth());
        postInvalidate();
    }

    /**
     * 根据已保存的步骤重绘一遍路径
     */
    public void reDraw() {
        Log.d(TAG, "reDraw");
        int width = mCanvas.getWidth();
        int height = mCanvas.getHeight();
        mBottomBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas.setBitmap(mBottomBitmap);
        Iterator<DrawStep> iter = mDrawHelper.getDrawStepSet().getSaveSteps().iterator();
        DrawStep temp;
        while (iter.hasNext()) {
            temp = iter.next();
            if (temp.getType() == DrawHelper.DRAW_PEN) {
                mCanvas.drawPath(temp.getDrawPenObj().getPath(), temp.getDrawPenObj().getPaint());
            }
        }
        postInvalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged, w:" + w + " h:" + h + " oldw:" + oldw + " oldh:" + oldh);
        if(w > 0&& h > 0){
            Log.d("Doodle", "onSizeChanged, w:" + w + ", h:" + h);
            if(null == mBottomBitmap) {
                mBottomBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                mCanvas = new Canvas(mBottomBitmap);
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw");
        canvas.drawBitmap(mBottomBitmap, 0, 0, mBitmapPaint);
        if (mPath != null) {
            canvas.drawPath(mPath, mPaint);
        }
    }

    private float posX, posY;// 暂存坐标数据
    private DrawStep mDrawStep;// 涂鸦步骤
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mDrawHelper.isTextSelect()) {
            return super.onTouchEvent(event);
        }
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //FIXME 为了解决橡皮擦笔触为黑色的问题
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));

                mPath = new Path();
                mPath.moveTo(x, y);
                posX = x;
                posY = y;
                postInvalidate();

                initDrawStep();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - posX);
                float dy = Math.abs(y - posY);
                if (dx > TOUCH_TOLERANCE || dy > TOUCH_TOLERANCE) {
                    refreshDrawStep(x, y);
                    mPath.quadTo(posX, posY, (x + posX) / 2, (y + posY) / 2);
                    posX = x;
                    posY = y;
                }
                postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                mPath.lineTo(posX, posY);
                //FIXME 为了解决橡皮擦笔触为黑色的问题
                if(mDrawHelper.isEraserSelect()) {
                    mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));//擦除模式
                }
                mCanvas.drawPath(mPath, mPaint);// 保存这一步的画图到画布上
                mPath = null;
                postInvalidate();

                saveDrawStep(posX, posY);
                EventBus.getDefault().post(new OneStepFinishEvent());// 通知界面已完成一步绘图
                break;
        }
        return true;
    }

    /** 初始化涂鸦的步骤 */
    private void initDrawStep() {
        DrawPenObj drawPenObj = new DrawPenObj();
        drawPenObj.setPaint(new Paint(mPaint));
        drawPenObj.setPath(mPath);

        DrawPenStr drawPenStr = new DrawPenStr();
        drawPenStr.setColor(mPaint.getColor());
        drawPenStr.setStrokeWidth(mPaint.getStrokeWidth());
        drawPenStr.setMoveTo(new Point(posX, posY));
        drawPenStr.setIsEraser(mDrawHelper.isEraserSelect());

        mDrawStep = new DrawStep();
        mDrawStep.setType(DrawHelper.DRAW_PEN);
        mDrawStep.setDrawPenObj(drawPenObj);
        mDrawStep.setDrawPenStr(drawPenStr);
    }

    /** 触控中刷新涂鸦的步骤 */
    private void refreshDrawStep(float x, float y) {
        mDrawStep.getDrawPenStr().getQuadToA().add(new Point(posX, posY));
        mDrawStep.getDrawPenStr().getQuadToB().add(new Point((x + posX) / 2,(y + posY) / 2));
    }

    /** 保存涂鸦步骤 */
    private void saveDrawStep(float x, float y) {
        mDrawStep.getDrawPenStr().setLineTo(new Point(x, y));
        DrawHelper.getInstance().addDrawStep(mDrawStep);
    }
}
