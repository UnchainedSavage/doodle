package com.midea.fridge.fridgedoodle;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.midea.fridge.fridgedoodle.bean.DoodleInfo;
import com.midea.fridge.fridgedoodle.bean.DrawPenObj;
import com.midea.fridge.fridgedoodle.bean.DrawStep;
import com.midea.fridge.fridgedoodle.bean.DrawStepToSave;
import com.midea.fridge.fridgedoodle.desktopwidget.NoteWidgetUpdater;
import com.midea.fridge.fridgedoodle.eventbus.OneStepFinishEvent;
import com.midea.fridge.fridgedoodle.eventbus.SaveFinishEvent;
import com.midea.fridge.fridgedoodle.widget.DrawPenView;
import com.midea.fridge.fridgedoodle.widget.DrawTextView;
import com.midea.fridge.fridgedoodle.widget.OnSingleClickListener;
import com.midea.fridge.fridgedoodle.widget.TwoButtonDialogFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/26.
 */
public class DrawActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "DrawActivity";

    private View mPrevBtn;
    private ImageView mPrevBtnImage;
    private View mNextBtn;
    private ImageView mNextBtnImage;
    private ImageView mDrawPenSmallImage, mDrawPenMidImage, mDrawPenLargeImage;
    private ImageView mDrawEraserSmallImage, mDrawEraserLargeImage;
    private ImageView mRedBtn,mYellowBtn,mPurpleBtn,mBlackBtn,mGreenBtn,mBlueBtn;
    private ImageView mDrawTextBtn;
    private View mDrawViewWrapper;
    private DrawPenView mDrawPenView;
    private DrawTextView mDrawTextView;
    private TwoButtonDialogFragment mCancelDialog;

    private DrawHelper mDrawHelper;
    private DoodleInfo mDoodleInfo;

    private static final String TEXT_EDIT_BEFORE_SAVE = "保存前请先编辑内容";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        mDrawHelper = DrawHelper.getInstance();
        mDrawHelper.reset();
        handleIntent();
        EventBus.getDefault().register(this);
        initView();
        refreshView();

        if(null != mDoodleInfo) {
            new LoadDoodleTask().execute(mDoodleInfo);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.doodle_cancel_btn:
                showCancelDialog();
                break;
            case R.id.doodle_prev_btn:
                DrawStep lastDrawStep = mDrawHelper.undoDrawStep();
                if(lastDrawStep.getType() == DrawHelper.DRAW_PEN) {
                    mDrawPenView.reDraw();
                } else if(lastDrawStep.getType() == DrawHelper.DRAW_TEXT) {
                    mDrawTextView.undoDrawStep(lastDrawStep);
                }
                refreshView();
                break;
            case R.id.doodle_next_btn:
                DrawStep nextDrawStep = mDrawHelper.redoDrawStep();
                if(nextDrawStep.getType() == DrawHelper.DRAW_PEN) {
                    mDrawPenView.reDraw();
                } else if(nextDrawStep.getType() == DrawHelper.DRAW_TEXT) {
                    mDrawTextView.redoDrawStep(nextDrawStep);
                }
                refreshView();
                break;
            case R.id.drawpen_small_image:
                mDrawHelper.selectPenType(DrawHelper.PenType.SMALL);
                mDrawPenView.switchPen();
                refreshView();
                break;
            case R.id.drawpen_middle_image:
                mDrawHelper.selectPenType(DrawHelper.PenType.MIDDLE);
                mDrawPenView.switchPen();
                refreshView();
                break;
            case R.id.drawpen_large_image:
                mDrawHelper.selectPenType(DrawHelper.PenType.LARGE);
                mDrawPenView.switchPen();
                refreshView();
                break;
            case R.id.draweraser_small_image:
                mDrawHelper.selectEraserType(DrawHelper.EraserType.SMALL);
                mDrawPenView.switchEraser();
                refreshView();
                break;
            case R.id.draweraser_large_image:
                mDrawHelper.selectEraserType(DrawHelper.EraserType.LARGE);
                mDrawPenView.switchEraser();
                refreshView();
                break;
            case R.id.color_black_btn:
                selectColor(DrawHelper.PenColor.BLACK);
                break;
            case R.id.color_blue_btn:
                selectColor(DrawHelper.PenColor.BLUE);
                break;
            case R.id.color_green_btn:
                selectColor(DrawHelper.PenColor.GREEN);
                break;
            case R.id.color_purple_btn:
                selectColor(DrawHelper.PenColor.PURPLE);
                break;
            case R.id.color_red_btn:
                selectColor(DrawHelper.PenColor.RED);
                break;
            case R.id.color_yellow_btn:
                selectColor(DrawHelper.PenColor.YELLOW);
                break;
            case R.id.drawtext_btn:
                mDrawHelper.selectTextType();
                refreshView();
                break;
        }
    }

    // 防止重复点击
    private OnSingleClickListener onSingleClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            Log.i(TAG, "onClick");
            switch (v.getId()) {
                case R.id.doodle_confirm_btn:
                    if(mDrawHelper.getDrawStepSet().getSaveSteps().size() <= 0) {
                        showToast(TEXT_EDIT_BEFORE_SAVE);
                        return;
                    } else {
                        showProgressDialog("正在保存", null);
                        new SaveDoodleTask().execute(mDrawViewWrapper);
                    }
                    break;
            }
        }
    };

    private void handleIntent() {
        mDoodleInfo = getIntent().getParcelableExtra("DoodleInfo");
    }

    private void initView() {
        findViewById(R.id.doodle_cancel_btn).setOnClickListener(this);
        findViewById(R.id.doodle_confirm_btn).setOnClickListener(onSingleClickListener);
        mDrawViewWrapper = findViewById(R.id.draw_view_wrapper);
        mDrawPenView = (DrawPenView) findViewById(R.id.draw_pen_view);
        mDrawTextView = (DrawTextView) findViewById(R.id.draw_text_view);

        mPrevBtn = findViewById(R.id.doodle_prev_btn);
        mPrevBtn.setOnClickListener(this);
        mNextBtn = findViewById(R.id.doodle_next_btn);
        mNextBtn.setOnClickListener(this);
        mPrevBtnImage = (ImageView) findViewById(R.id.doodle_prev_btn_image);
        mNextBtnImage = (ImageView) findViewById(R.id.doodle_next_btn_image);

        mDrawPenSmallImage = (ImageView) findViewById(R.id.drawpen_small_image);
        mDrawPenSmallImage.setOnClickListener(this);
        mDrawPenMidImage = (ImageView) findViewById(R.id.drawpen_middle_image);
        mDrawPenMidImage.setOnClickListener(this);
        mDrawPenLargeImage = (ImageView) findViewById(R.id.drawpen_large_image);
        mDrawPenLargeImage.setOnClickListener(this);
        mDrawEraserSmallImage = (ImageView) findViewById(R.id.draweraser_small_image);
        mDrawEraserSmallImage.setOnClickListener(this);
        mDrawEraserLargeImage = (ImageView) findViewById(R.id.draweraser_large_image);
        mDrawEraserLargeImage.setOnClickListener(this);

        mRedBtn = (ImageView) findViewById(R.id.color_red_btn);
        mRedBtn.setOnClickListener(this);
        mYellowBtn = (ImageView) findViewById(R.id.color_yellow_btn);
        mYellowBtn.setOnClickListener(this);
        mPurpleBtn = (ImageView) findViewById(R.id.color_purple_btn);
        mPurpleBtn.setOnClickListener(this);
        mBlackBtn = (ImageView) findViewById(R.id.color_black_btn);
        mBlackBtn.setOnClickListener(this);
        mGreenBtn = (ImageView) findViewById(R.id.color_green_btn);
        mGreenBtn.setOnClickListener(this);
        mBlueBtn = (ImageView) findViewById(R.id.color_blue_btn);
        mBlueBtn.setOnClickListener(this);

        mDrawTextBtn = (ImageView) findViewById(R.id.drawtext_btn);
        mDrawTextBtn.setOnClickListener(this);
    }

    private void refreshView() {
        if(mDrawHelper.getDrawStepSet().getSaveSteps().size() > 0) {
            mPrevBtnImage.setImageResource(R.drawable.doodle_prev_btn_bg);
            mPrevBtn.setClickable(true);
        } else {
            mPrevBtnImage.setImageResource(R.drawable.doodle_prev_btn_pressed);
            mPrevBtn.setClickable(false);
        }
        if(mDrawHelper.getDrawStepSet().getDeleteSteps().size() > 0) {
            mNextBtnImage.setImageResource(R.drawable.doodle_next_btn_bg);
            mNextBtn.setClickable(true);
        } else {
            mNextBtnImage.setImageResource(R.drawable.doodle_next_btn_pressed);
            mNextBtn.setClickable(false);
        }
        DrawHelper.PenType penType = mDrawHelper.getPenType();
        if(penType == DrawHelper.PenType.SMALL) {
            mDrawPenSmallImage.setImageResource(getDrawPenImageByColor());
            mDrawPenMidImage.setImageResource(R.drawable.drawpen_unselected);
            mDrawPenLargeImage.setImageResource(R.drawable.drawpen_unselected);
        } else if(penType == DrawHelper.PenType.MIDDLE) {
            mDrawPenSmallImage.setImageResource(R.drawable.drawpen_unselected);
            mDrawPenMidImage.setImageResource(getDrawPenImageByColor());
            mDrawPenLargeImage.setImageResource(R.drawable.drawpen_unselected);
        } else if(penType == DrawHelper.PenType.LARGE) {
            mDrawPenSmallImage.setImageResource(R.drawable.drawpen_unselected);
            mDrawPenMidImage.setImageResource(R.drawable.drawpen_unselected);
            mDrawPenLargeImage.setImageResource(getDrawPenImageByColor());
        } else {
            mDrawPenSmallImage.setImageResource(R.drawable.drawpen_unselected);
            mDrawPenMidImage.setImageResource(R.drawable.drawpen_unselected);
            mDrawPenLargeImage.setImageResource(R.drawable.drawpen_unselected);
        }
        DrawHelper.EraserType eraserType = mDrawHelper.getEraserType();
        if(eraserType == DrawHelper.EraserType.SMALL) {
            mDrawEraserSmallImage.setImageResource(R.drawable.draweraser_selected);
            mDrawEraserLargeImage.setImageResource(R.drawable.draweraser_unselected);
        } else if(eraserType == DrawHelper.EraserType.LARGE) {
            mDrawEraserSmallImage.setImageResource(R.drawable.draweraser_unselected);
            mDrawEraserLargeImage.setImageResource(R.drawable.draweraser_selected);
        } else {
            mDrawEraserSmallImage.setImageResource(R.drawable.draweraser_unselected);
            mDrawEraserLargeImage.setImageResource(R.drawable.draweraser_unselected);
        }
        DrawHelper.PenColor penColor = mDrawHelper.getPenColor();
        if(penColor == DrawHelper.PenColor.BLACK) {
            mBlackBtn.setImageResource(R.drawable.color_black_selected);
            mRedBtn.setImageResource(R.drawable.color_red_normal);
            mYellowBtn.setImageResource(R.drawable.color_yellow_normal);
            mGreenBtn.setImageResource(R.drawable.color_green_normal);
            mBlueBtn.setImageResource(R.drawable.color_blue_normal);
            mPurpleBtn.setImageResource(R.drawable.color_purple_normal);
        } else if(penColor == DrawHelper.PenColor.RED) {
            mBlackBtn.setImageResource(R.drawable.color_black_normal);
            mRedBtn.setImageResource(R.drawable.color_red_selected);
            mYellowBtn.setImageResource(R.drawable.color_yellow_normal);
            mGreenBtn.setImageResource(R.drawable.color_green_normal);
            mBlueBtn.setImageResource(R.drawable.color_blue_normal);
            mPurpleBtn.setImageResource(R.drawable.color_purple_normal);
        } else if(penColor == DrawHelper.PenColor.YELLOW) {
            mBlackBtn.setImageResource(R.drawable.color_black_normal);
            mRedBtn.setImageResource(R.drawable.color_red_normal);
            mYellowBtn.setImageResource(R.drawable.color_yellow_selected);
            mGreenBtn.setImageResource(R.drawable.color_green_normal);
            mBlueBtn.setImageResource(R.drawable.color_blue_normal);
            mPurpleBtn.setImageResource(R.drawable.color_purple_normal);
        } else if(penColor == DrawHelper.PenColor.GREEN) {
            mBlackBtn.setImageResource(R.drawable.color_black_normal);
            mRedBtn.setImageResource(R.drawable.color_red_normal);
            mYellowBtn.setImageResource(R.drawable.color_yellow_normal);
            mGreenBtn.setImageResource(R.drawable.color_green_selected);
            mBlueBtn.setImageResource(R.drawable.color_blue_normal);
            mPurpleBtn.setImageResource(R.drawable.color_purple_normal);
        } else if(penColor == DrawHelper.PenColor.BLUE) {
            mBlackBtn.setImageResource(R.drawable.color_black_normal);
            mRedBtn.setImageResource(R.drawable.color_red_normal);
            mYellowBtn.setImageResource(R.drawable.color_yellow_normal);
            mGreenBtn.setImageResource(R.drawable.color_green_normal);
            mBlueBtn.setImageResource(R.drawable.color_blue_selected);
            mPurpleBtn.setImageResource(R.drawable.color_purple_normal);
        } else if(penColor == DrawHelper.PenColor.PURPLE) {
            mBlackBtn.setImageResource(R.drawable.color_black_normal);
            mRedBtn.setImageResource(R.drawable.color_red_normal);
            mYellowBtn.setImageResource(R.drawable.color_yellow_normal);
            mGreenBtn.setImageResource(R.drawable.color_green_normal);
            mBlueBtn.setImageResource(R.drawable.color_blue_normal);
            mPurpleBtn.setImageResource(R.drawable.color_purple_selected);
        }
        if(mDrawHelper.isTextSelect()) {
            mDrawTextBtn.setImageResource(getDrawTextImageByColor());
        } else {
            mDrawTextBtn.setImageResource(R.drawable.doodle_text_none);
        }
    }

    private void selectColor(DrawHelper.PenColor penColor) {
        mDrawHelper.selectPenColor(penColor);
        if(mDrawHelper.isPenSelect()) {
            mDrawPenView.switchPen();
        }
        refreshView();
    }

    private int getDrawPenImageByColor() {
        DrawHelper.PenColor penColor = mDrawHelper.getPenColor();
        if(penColor == DrawHelper.PenColor.BLACK) {
            return R.drawable.drawpen_black;
        } else if(penColor == DrawHelper.PenColor.BLUE) {
            return R.drawable.drawpen_blue;
        } else if(penColor == DrawHelper.PenColor.YELLOW) {
            return R.drawable.drawpen_yellow;
        } else if(penColor == DrawHelper.PenColor.GREEN) {
            return R.drawable.drawpen_green;
        } else if(penColor == DrawHelper.PenColor.PURPLE) {
            return R.drawable.drawpen_purple;
        } else if(penColor == DrawHelper.PenColor.RED) {
            return R.drawable.drawpen_red;
        }
        return R.drawable.drawpen_unselected;
    }

    private int getDrawTextImageByColor() {
        DrawHelper.PenColor penColor = mDrawHelper.getPenColor();
        if(penColor == DrawHelper.PenColor.BLACK) {
            return R.drawable.doodle_text_black;
        } else if(penColor == DrawHelper.PenColor.BLUE) {
            return R.drawable.doodle_text_blue;
        } else if(penColor == DrawHelper.PenColor.YELLOW) {
            return R.drawable.doodle_text_yellow;
        } else if(penColor == DrawHelper.PenColor.GREEN) {
            return R.drawable.doodle_text_green;
        } else if(penColor == DrawHelper.PenColor.PURPLE) {
            return R.drawable.doodle_text_purple;
        } else if(penColor == DrawHelper.PenColor.RED) {
            return R.drawable.doodle_text_red;
        }
        return R.drawable.doodle_text_none;
    }

    private void showCancelDialog() {
        if(null == mCancelDialog) {
            mCancelDialog = new TwoButtonDialogFragment();
            mCancelDialog.setContent("确定要放弃编辑吗");
            mCancelDialog.setOnClickWatcher(new TwoButtonDialogFragment.OnClickWatcher() {
                @Override
                public void onClickConfirm() {
                    finish();
                }

                @Override
                public void onClickCancel() {
                    dismissCancelDialog();
                }
            });
        }
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, mCancelDialog).commit();
    }

    private void dismissCancelDialog() {
        if (mCancelDialog != null) {
            getSupportFragmentManager().beginTransaction().remove(mCancelDialog).commit();
            mCancelDialog = null;
        }
    }

    private class LoadDoodleTask extends AsyncTask<DoodleInfo, Void, List<DrawStep>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog2("正在加载涂鸦资源", null, false);
        }

        @Override
        protected List<DrawStep> doInBackground(DoodleInfo... params) {
            DoodleInfo doodleInfo = params[0];
            String drawStepJson = StoreUtil.read(doodleInfo.getSavePath());
            Log.d(TAG, "drawStepJson:" + drawStepJson);
            List<DrawStep> result = new ArrayList<>();
            List<DrawStepToSave> drawSteps = new Gson().fromJson(drawStepJson, new TypeToken<List<DrawStepToSave>>() {}.getType());
            for(DrawStepToSave drawStepToSave:drawSteps) {
                DrawStep drawStep = new DrawStep();
                drawStep.setType(drawStepToSave.getType());
                drawStep.setDrawPenStr(drawStepToSave.getDrawPenStr());
                drawStep.setDrawTextObj(drawStepToSave.getDrawTextObj());
                if(drawStepToSave.getType() == DrawHelper.DRAW_PEN) {
                    DrawPenObj drawPenObj = StoreUtil.convertDrawPenObj(drawStep.getDrawPenStr());
                    drawStep.setDrawPenObj(drawPenObj);
                }
                result.add(drawStep);
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<DrawStep> result) {
            mDrawHelper.getDrawStepSet().setSaveSteps(result);
            mDrawPenView.reDraw();
            mDrawTextView.reDraw();
            refreshView();
            dismissProgressDialog();
        }
    }

    private class SaveDoodleTask extends AsyncTask<View, Void, Void> {
        @Override
        protected Void doInBackground(View... params) {
            View view = params[0];
            if(null != mDoodleInfo) {
                StoreUtil.saveDoodle(view, mDoodleInfo.getName());
            } else {
                StoreUtil.saveDoodle(view);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dismissProgressDialog();
            showToast("保存成功");
            EventBus.getDefault().post(new SaveFinishEvent());
            // 更新widget
            NoteWidgetUpdater.getInstance().updateWidget(DrawActivity.this);
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OneStepFinishEvent event) {
        refreshView();
    }
}
