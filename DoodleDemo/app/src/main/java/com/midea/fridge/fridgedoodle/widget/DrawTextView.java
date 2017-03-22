package com.midea.fridge.fridgedoodle.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.midea.fridge.fridgedoodle.DrawHelper;
import com.midea.fridge.fridgedoodle.R;
import com.midea.fridge.fridgedoodle.bean.DrawStep;
import com.midea.fridge.fridgedoodle.bean.DrawTextObj;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 添加文字的层
 * Created by Administrator on 2017/1/13.
 */
public class DrawTextView extends FrameLayout {
    private static final String TAG = "DrawTextView";

    private Context mContext;
    private DrawHelper mDrawHelper;

    private List<View> mTextViews;

    public DrawTextView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        init(context);
    }

    public DrawTextView(Context context, AttributeSet attr) {
        super(context, attr);
        init(context);
    }

    public DrawTextView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mDrawHelper = DrawHelper.getInstance();
        mTextViews = new ArrayList<>();
    }

    public void undoDrawStep(DrawStep drawStep) {
        Log.i(TAG, "undoDrawStep");
        removeView(drawStep.getDrawTextView());
    }

    public void redoDrawStep(DrawStep drawStep) {
        Log.i(TAG, "redoDrawStep");
        addView(drawStep.getDrawTextView());
    }

    public void reDraw() {
        Log.i(TAG, "reDraw");
        removeAllViews();
        Iterator<DrawStep> iter = mDrawHelper.getDrawStepSet().getSaveSteps().iterator();
        DrawStep temp;
        while (iter.hasNext()) {
            temp = iter.next();
            if (temp.getType() == DrawHelper.DRAW_TEXT) {
                DrawTextObj drawTextObj = temp.getDrawTextObj();
                View view = addShowText(drawTextObj.getX(), drawTextObj.getY(), drawTextObj.getContent(), drawTextObj.getTextColor());
                temp.setDrawTextView(view);
            }
        }
    }

    // FIXME 当前编辑的文本控件，以后可以写成自定义View，逻辑更清晰
    private View mCurrentTextView;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(null != mCurrentTextView) {
                    confirmTextViewsEdit(mCurrentTextView);
                } else {
                    if(mDrawHelper.isTextSelect()) {
                        float moveX =  event.getX();
                        float moveY =  event.getY();
                        addEditText(moveX, moveY);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void addEditText(float x, float y) {
        final View textView = LayoutInflater.from(mContext).inflate(R.layout.doodle_custom_textview, this, false);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = (int) (x - 20);// 文本插入位置在点击处偏左上一点
        lp.topMargin = (int) (y - 20);
        textView.setLayoutParams(lp);

        EditText textEdit = (EditText) textView.findViewById(R.id.text_edit);
        int maxWidth = getWidth() - (int) x - 20;// 最大宽度等于总宽度减去左边距减去右边距
        textEdit.setTextColor(Color.parseColor(mDrawHelper.getPenColor().getColor()));
        textEdit.setMaxWidth(maxWidth);

        TextView textShow = (TextView) textView.findViewById(R.id.text_show);
        textShow.setTextColor(Color.parseColor(mDrawHelper.getPenColor().getColor()));
        textShow.setMaxWidth(maxWidth);
        textShow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onTextViewClicked(textView);
            }
        });

        mCurrentTextView = textView;
        addView(textView);
        textView.requestFocus();
    }

    private View addShowText(int x, int y, String content, int textColor) {
        final View textView = LayoutInflater.from(mContext).inflate(R.layout.doodle_custom_textview, this, false);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = x;
        lp.topMargin = y;
        textView.setLayoutParams(lp);

        int maxWidth = getWidth() - x - 20;// 最大宽度等于总宽度减去左边距减去右边距
        EditText textEdit = (EditText) textView.findViewById(R.id.text_edit);
        textEdit.setTextColor(textColor);
        textEdit.setMaxWidth(maxWidth);
        textEdit.setText(content);
        textEdit.setVisibility(GONE);

        TextView textShow = (TextView) textView.findViewById(R.id.text_show);
        textShow.setTextColor(textColor);
        textShow.setMaxWidth(maxWidth);
        textShow.setText(content);
        textShow.setVisibility(VISIBLE);
        textShow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onTextViewClicked(textView);
            }
        });

        mTextViews.add(textView);
        addView(textView);
        textView.requestFocus();
        return textView;
    }

    private void onTextViewClicked(View thatTextView) {
        // 首先恢复其他textView中textShow的显示
        for(View textView:mTextViews) {
            switchTextShow(textView);
        }

        // 然后显示本次点击的textEdit
        switchTextEdit(thatTextView);
    }

    /**
     * 切换到TextView控件
     * @param textView
     */
    private void switchTextShow(View textView) {
        EditText textEdit = (EditText) textView.findViewById(R.id.text_edit);
        TextView textShow = (TextView) textView.findViewById(R.id.text_show);
        textEdit.clearFocus();
        textEdit.setVisibility(GONE);
        String content = textEdit.getText().toString().trim();
        textShow.setText(content);
        textShow.setVisibility(VISIBLE);

        mCurrentTextView = null;
    }

    /**
     * 切换到EditText控件
     * @param textView
     */
    private void switchTextEdit(View textView) {
        EditText textEdit = (EditText) textView.findViewById(R.id.text_edit);
        TextView textShow = (TextView) textView.findViewById(R.id.text_show);
        textShow.setVisibility(GONE);
        String content = textShow.getText().toString().trim();
        textEdit.setText(content);
        textEdit.setVisibility(VISIBLE);
        textEdit.requestFocus();

        mCurrentTextView = textView;
    }

    /**
     * 是否有textview处于编辑状态
     */
    private boolean isTextViewsFocused() {
        for(View textView:mTextViews) {
            EditText textEdit = (EditText) textView.findViewById(R.id.text_edit);
            Log.d(TAG, "textEdit:" + textEdit.isFocused());
            if(textEdit.isFocused()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 确认当前所有textview的编辑内容
     */
    private void confirmTextViewsEdit(View textView) {
        EditText textEdit = (EditText) textView.findViewById(R.id.text_edit);
        if(textEdit.getVisibility() == VISIBLE && textEdit.isFocused()) {
            String content = textEdit.getText().toString().trim();
            // 如果无内容移除当前textview，有内容则显示并添加此次画图步骤
            if(!TextUtils.isEmpty(content)) {
                switchTextShow(textView);

                if(!mTextViews.contains(textView)) {
                    mTextViews.add(textView);

                    // 添加绘画步骤到步骤集合
                    DrawTextObj drawTextObj = new DrawTextObj();
                    drawTextObj.setTextColor(Color.parseColor(mDrawHelper.getPenColor().getColor()));
                    drawTextObj.setContent(content);
                    LayoutParams lp = (FrameLayout.LayoutParams) textView.getLayoutParams();
                    drawTextObj.setX(lp.leftMargin);
                    drawTextObj.setY(lp.topMargin);

                    DrawStep drawStep = new DrawStep();
                    drawStep.setDrawTextView(textView);
                    drawStep.setType(DrawHelper.DRAW_TEXT);
                    drawStep.setDrawTextObj(drawTextObj);
                    mDrawHelper.addDrawStep(drawStep);
                }
            } else {
                removeView(textView);
            }
        }
    }
}
