package com.midea.fridge.fridgedoodle.desktopwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;

/**
 * Created by Administrator on 2016/11/19.
 */
public class NoteWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
//        for (int i = 0; i < N; i++) {
//            int appWidgetId = appWidgetIds[i];
//
//            // Create an Intent to launch ExampleActivity
//            Intent intent = new Intent(context, MainActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//
//            // Get the layout for the App Widget and attach an on-click listener
//            // to the button
//            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_notebook_2x3);
//            views.setOnClickPendingIntent(R.id.root_view, pendingIntent);
//            views.setInt(R.id.root_view, "setBackgroundResource", R.drawable.ic_launcher);
//
//            // Tell the AppWidgetManager to perform an update on the current app widget
//            appWidgetManager.updateAppWidget(appWidgetId, views);
//        }

        NoteWidgetUpdater.getInstance().updateWidget(context);
    }
}
