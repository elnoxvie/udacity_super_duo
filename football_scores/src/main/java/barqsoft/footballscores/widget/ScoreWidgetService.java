package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.util.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.R;
import barqsoft.footballscores.dao.DatabaseContract;
import barqsoft.footballscores.util.Utilities;

import static barqsoft.footballscores.adapter.RecyclerScoresAdapter.*;
import static barqsoft.footballscores.adapter.RecyclerScoresAdapter.COL_MATCHTIME;

/**
 * Created by elnoxvie on 26/8/15.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ScoreWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ScoreWidgetFactory(getApplicationContext(), intent);
    }

    private class ScoreWidgetFactory implements RemoteViewsFactory{

        private Cursor mCursor;
        private Context mContext;
        private int mAppWidgetId;

        public ScoreWidgetFactory(Context context, Intent intent){
            mContext = context;
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {

            int mode = Utilities.getDateModeFromPreference(mContext, mAppWidgetId);
            String currentDate = Utilities.getDateByMode(mode);

            mCursor = getContentResolver().query(DatabaseContract.ScoresEntry.buildScoreWithDate(), null, null, new String[]{currentDate}, null);
        }

        @Override
        public void onDataSetChanged() {
            if (mCursor != null){
                mCursor.close();
            }

            int mode = Utilities.getDateModeFromPreference(mContext, mAppWidgetId);
            String currentDate = Utilities.getDateByMode(mode);

            mCursor = getContentResolver().query(DatabaseContract.ScoresEntry.buildScoreWithDate(), null, null, new String[]{currentDate}, null);
        }

        @Override
        public void onDestroy() {
            if (mCursor != null){
                mCursor.close();
            }
        }

        @Override
        public int getCount() {
            return mCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            mCursor.moveToPosition(position);

            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.scores_list_item_base_content);
            rv.setTextViewText(R.id.home_name, mCursor.getString(COL_HOME));
            rv.setTextViewText(R.id.away_name, mCursor.getString(COL_AWAY));
            rv.setTextViewText(R.id.date_textview, mCursor.getString(COL_MATCHTIME));
            rv.setTextViewText(R.id.score_textview, Utilities.getScores(mCursor.getInt(COL_HOME_GOALS), mCursor.getInt(COL_AWAY_GOALS)));
            rv.setImageViewResource(R.id.home_crest, Utilities.getTeamCrestByTeamName(mCursor.getString(COL_HOME)));
            rv.setImageViewResource(R.id.away_crest, Utilities.getTeamCrestByTeamName(mCursor.getString(COL_AWAY)));

            return rv;
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
}
