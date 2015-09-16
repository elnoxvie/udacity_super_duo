package barqsoft.footballscores.dao;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public final class DatabaseContract {
    public static final String TABLE_SCORES = "scores_table";

    public static final String AUTHORITY = "barqsoft.footballscores";
    public static final String PATH_SCORES = "scores";
    public static final String PATH_LEAGUE = "league";
    public static final String PATH_ID = "_id";
    public static final String PATH_DATE = "date";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class ScoresEntry implements BaseColumns {


        //Table Columns
        public static final String LEAGUE_COL = "league";
        public static final String DATE_COL = "date";
        public static final String TIME_COL = "time";
        public static final String HOME_COL = "home";
        public static final String AWAY_COL = "away";
        public static final String HOME_GOALS_COL = "home_goals";
        public static final String AWAY_GOALS_COL = "away_goals";
        public static final String MATCH_ID = "match_id";
        public static final String MATCH_DAY = "match_day";


        //Types
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_SCORES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + PATH_SCORES;


        public static Uri buildScoreWithLeague() {
            return CONTENT_URI.buildUpon().appendPath(PATH_LEAGUE).build();
        }


        public static Uri buildScoreWithId() {
            return CONTENT_URI.buildUpon().appendPath(PATH_ID).build();
        }


        public static Uri buildScoreWithDate() {
            return CONTENT_URI.buildUpon().appendPath(PATH_DATE).build();
        }
    }


}
