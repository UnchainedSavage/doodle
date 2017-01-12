package com.midea.fridge.fridgedoodle;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.midea.fridge.fridgedoodle.bean.DoodleInfo;
import com.midea.fridge.fridgedoodle.desktopwidget.NoteWidgetUpdater;
import com.midea.fridge.fridgedoodle.eventbus.SaveFinishEvent;
import com.midea.fridge.fridgedoodle.widget.SpaceItemDecoration;
import com.midea.fridge.fridgedoodle.widget.TwoButtonDialogFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = "MainActivity";

    private RecyclerView mDoodleList;
    private DoodleListAdapter mDoodleListAdapter;
    private ImageView mSelectDoodleImage;// 预览大图
    private TwoButtonDialogFragment mDeleteDialog;
    private ImageView mDoodleEditBtn;
    private ImageView mDoodleDeleteBtn;
    private View mDoodleEmptyView;

    private Context mContext;
    private List<DoodleInfo> mDoodleInfos;

    private static final int LIMIT_DOODLES = 200;// 限制最多添加200个涂鸦
    private static final String TEXT_LIMIT_DOODLES = "涂鸦数量太多，请先删除再添加";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        initView();
        refreshView();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.doodle_add_btn:
            case R.id.doodle_add_big_btn:
                if(mDoodleInfos.size() >= LIMIT_DOODLES) {
                    showToast(TEXT_LIMIT_DOODLES);
                } else {
                    startActivity(new Intent(mContext, DrawActivity.class));
                }
                break;
            case R.id.doodle_edit_btn:
                if(null != mDoodleListAdapter.getSelectDoodle()) {
                    Intent intent = new Intent(mContext, DrawActivity.class);
                    intent.putExtra("DoodleInfo", mDoodleListAdapter.getSelectDoodle());
                    startActivity(intent);
                }
                break;
            case R.id.doodle_delete_btn:
                if(null != mDoodleListAdapter.getSelectDoodle()) {
                    showDeleteDialog();
                }
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    public void onSelectDoodle(DoodleInfo doodleInfo) {
        mSelectDoodleImage.setImageBitmap(BitmapFactory.decodeFile(doodleInfo.getImagePath()));
    }

    private void initView() {
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.doodle_add_btn).setOnClickListener(this);
        findViewById(R.id.doodle_add_big_btn).setOnClickListener(this);
        mDoodleEditBtn = (ImageView) findViewById(R.id.doodle_edit_btn);
        mDoodleEditBtn.setOnClickListener(this);
        mDoodleDeleteBtn = (ImageView) findViewById(R.id.doodle_delete_btn);
        mDoodleDeleteBtn.setOnClickListener(this);

        mDoodleEmptyView = findViewById(R.id.doodle_empty_view);
        mSelectDoodleImage = (ImageView) findViewById(R.id.doodle_select_image);

        mDoodleList = (RecyclerView) findViewById(R.id.doodle_preview_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mDoodleList.setLayoutManager(linearLayoutManager);
        mDoodleList.addItemDecoration(new SpaceItemDecoration(15));
        mDoodleListAdapter = new DoodleListAdapter(this);
        mDoodleList.setAdapter(mDoodleListAdapter);
    }

    private void refreshView() {
        mDoodleInfos = StoreUtil.loadDoodleImages();
        mDoodleListAdapter.setData(mDoodleInfos);
        if(mDoodleInfos.size() > 0) {
            mDoodleListAdapter.selectDoodle(mDoodleInfos.get(0));
            onSelectDoodle(mDoodleInfos.get(0));
            mDoodleEmptyView.setVisibility(View.GONE);
            mDoodleEditBtn.setImageResource(R.drawable.doodle_edit_btn_bg);
            mDoodleEditBtn.setClickable(true);
            mDoodleDeleteBtn.setImageResource(R.drawable.doodle_delete_btn_bg);
            mDoodleDeleteBtn.setClickable(true);
        } else {
            mDoodleListAdapter.selectDoodle(null);
            mSelectDoodleImage.setImageResource(android.R.color.transparent);
            mDoodleEmptyView.setVisibility(View.VISIBLE);
            mDoodleEditBtn.setImageResource(R.drawable.edit_btn_pressed);
            mDoodleEditBtn.setClickable(false);
            mDoodleDeleteBtn.setImageResource(R.drawable.delete_btn_pressed);
            mDoodleDeleteBtn.setClickable(false);
        }
    }

    private void showDeleteDialog() {
        if(null == mDeleteDialog) {
            mDeleteDialog = new TwoButtonDialogFragment();
            mDeleteDialog.setContent("确定要删除吗，删除后不可恢复哦。");
            mDeleteDialog.setOnClickWatcher(new TwoButtonDialogFragment.OnClickWatcher() {
                @Override
                public void onClickConfirm() {
                    DoodleInfo doodleInfo = mDoodleListAdapter.getSelectDoodle();
                    if(null != doodleInfo) {
                        File saveFile = new File(doodleInfo.getSavePath());
                        File imgFile = new File(doodleInfo.getImagePath());
                        saveFile.delete();
                        imgFile.delete();
                        // 通知更新界面
                        EventBus.getDefault().post(new SaveFinishEvent());

                        // 更新widget
                        NoteWidgetUpdater.getInstance().updateWidget(mContext);

                        dismissDeleteDialog();
                    }
                }

                @Override
                public void onClickCancel() {
                    dismissDeleteDialog();
                }
            });
        }
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, mDeleteDialog).commitAllowingStateLoss();
    }

    private void dismissDeleteDialog() {
        if (mDeleteDialog != null) {
            getSupportFragmentManager().beginTransaction().remove(mDeleteDialog).commitAllowingStateLoss();
            mDeleteDialog = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SaveFinishEvent event) {
        refreshView();
    }
}
