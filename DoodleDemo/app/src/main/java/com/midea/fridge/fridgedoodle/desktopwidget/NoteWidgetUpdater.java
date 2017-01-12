package com.midea.fridge.fridgedoodle.desktopwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.midea.fridge.fridgedoodle.DrawActivity;
import com.midea.fridge.fridgedoodle.MainActivity;
import com.midea.fridge.fridgedoodle.R;
import com.midea.fridge.fridgedoodle.StoreUtil;
import com.midea.fridge.fridgedoodle.bean.DoodleInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/1/3.
 */
public class NoteWidgetUpdater {
    private static final String TAG = "NoteWidgetUpdater";
    private static NoteWidgetUpdater instance;

    public static synchronized NoteWidgetUpdater getInstance() {
        if(instance == null) {
            instance = new NoteWidgetUpdater();
        }
        return instance;
    }

    private NoteWidgetUpdater() {
    }

    public void updateWidget(final Context context) {
        Intent intent1 = new Intent(context, MainActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent1 = PendingIntent.getActivity(context, 0, intent1, 0);

        Intent intent2 = new Intent(context, DrawActivity.class);
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intent2, 0);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_notebook_2x3);
        views.setOnClickPendingIntent(R.id.new_doodle_btn, pendingIntent2);
        List<DoodleInfo> doodleInfos = StoreUtil.loadDoodleImages();
        Log.d(TAG, "updateWidget size:" + doodleInfos.size());
        if(doodleInfos.size() > 0) {
            Bitmap bitmap = BitmapFactory.decodeFile(doodleInfos.get(0).getImagePath());
            views.setImageViewBitmap(R.id.doodle_desktop_view, bitmap);
            views.setViewVisibility(R.id.doodle_empty_view, View.GONE);
            views.setViewVisibility(R.id.doodle_desktop_view, View.VISIBLE);
            views.setOnClickPendingIntent(R.id.doodle_desktop_view, pendingIntent1);
        } else {
            views.setViewVisibility(R.id.doodle_empty_view, View.VISIBLE);
            views.setViewVisibility(R.id.doodle_desktop_view, View.GONE);
            views.setOnClickPendingIntent(R.id.doodle_empty_view, pendingIntent1);
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(new ComponentName(context, NoteWidgetProvider.class), views);
    }
}
