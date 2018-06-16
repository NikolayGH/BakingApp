package ru.prog_edu.bakingapp;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

public class WidgetUpdateService extends IntentService {

    private static final String ACTION_UPDATE_LIST_VIEW = "widgetupdateservice.update_app_widget_list";

    public WidgetUpdateService() {
        super("WidgetUpdateService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            handleActionUpdateListView();
        }
    }

    private void handleActionUpdateListView() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, IngredientsWidget.class));
        IngredientsWidget.updateAllAppWidget(this, appWidgetManager,appWidgetIds);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listView);
    }

    public static void startActionUpdateAppWidgets(Context context) {
        Intent intent = new Intent(context, WidgetUpdateService.class);
            intent.setAction(ACTION_UPDATE_LIST_VIEW);
            context.startService(intent);
    }
}
