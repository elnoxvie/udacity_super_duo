package barqsoft.footballscores.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import barqsoft.footballscores.dao.ScoresDBHelper;
import barqsoft.footballscores.dao.DatabaseContract;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class ScoresProvider extends ContentProvider {
    private static ScoresDBHelper mHelper;
    private static final int MATCHES = 100;
    private static final int MATCHES_WITH_LEAGUE = 101;
    private static final int MATCHES_WITH_ID = 102;
    private static final int MATCHES_WITH_DATE = 103;

    private static final UriMatcher sUriMatcher =  new UriMatcher(UriMatcher.NO_MATCH);

    private static final String SCORES_BY_LEAGUE = DatabaseContract.ScoresEntry.LEAGUE_COL + " = ?";
    private static final String SCORES_BY_DATE = DatabaseContract.ScoresEntry.DATE_COL + " LIKE ?";
    private static final String SCORES_BY_ID = DatabaseContract.ScoresEntry.MATCH_ID + " = ?";

    static{
        sUriMatcher.addURI(DatabaseContract.AUTHORITY, null, MATCHES);
        sUriMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.PATH_LEAGUE,MATCHES_WITH_LEAGUE);
        sUriMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.PATH_ID, MATCHES_WITH_ID);
        sUriMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.PATH_DATE, MATCHES_WITH_DATE);
    }

    @Override
    public boolean onCreate() {
        mHelper = new ScoresDBHelper(getContext());
        return true;
    }


    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MATCHES:
                return DatabaseContract.ScoresEntry.CONTENT_TYPE;
            case MATCHES_WITH_LEAGUE:
                return DatabaseContract.ScoresEntry.CONTENT_TYPE;
            case MATCHES_WITH_ID:
                return DatabaseContract.ScoresEntry.CONTENT_ITEM_TYPE;
            case MATCHES_WITH_DATE:
                return DatabaseContract.ScoresEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri :" + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(DatabaseContract.TABLE_SCORES);

        switch (sUriMatcher.match(uri)) {
            case MATCHES:
                selection = null;
                break;
            case MATCHES_WITH_DATE:
                selection = SCORES_BY_DATE;
                break;
            case MATCHES_WITH_ID:
                selection = SCORES_BY_ID;
                break;
            case MATCHES_WITH_LEAGUE:
                selection = SCORES_BY_LEAGUE;
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri" + uri);
        }
        Cursor cursor = builder.query(db,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case MATCHES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(DatabaseContract.TABLE_SCORES, null, value,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) { return null; }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) { return 0; }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }
}
