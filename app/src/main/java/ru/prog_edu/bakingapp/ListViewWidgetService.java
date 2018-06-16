package ru.prog_edu.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.Set;

public class ListViewWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new AppWidgetListView (this.getApplicationContext(),
                WidgetDataModel.getDataFromSharedPrefs(getApplicationContext()));
    }
}

class AppWidgetListView implements RemoteViewsService.RemoteViewsFactory {

    private final Set<String> dataList;
    private final Context context;

    public AppWidgetListView(Context applicationContext, Set<String> dataList) {
        this.context=applicationContext;
        this.dataList=dataList;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.list_item_widget);
        String[]strings = dataList.toArray(new String[dataList.size()]);
        views.setTextViewText(R.id.ingredient_TextView, strings[position]);

        Intent fillInIntent = new Intent();
        fillInIntent.putExtra("ItemTitle",strings[position]);
        views.setOnClickFillInIntent(R.id.parentView, fillInIntent);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

