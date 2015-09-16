package barqsoft.footballscores.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import barqsoft.footballscores.R;
import barqsoft.footballscores.util.Utilities;
import barqsoft.footballscores.view.CursorRecyclerAdapter;
import barqsoft.footballscores.view.MyRecyclerView;
import barqsoft.footballscores.view.MyRecyclerViewHolder;

/**
 * Created by elnoxvie on 24/8/15.
 */
public class RecyclerScoresAdapter extends CursorRecyclerAdapter<RecyclerScoresAdapter.ViewHolder> {
    private String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";

    public static final int COL_DATE = 1;
    public static final int COL_MATCHTIME = 2;
    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_LEAGUE = 5;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_ID = 8;
    public static final int COL_MATCHDAY = 9;

    public double detailMatchId = 0;
    private MyRecyclerView.MyRecylerCallbacks mCallbacks;


    public RecyclerScoresAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scores_list_item, parent, false);
        return new ViewHolder(view);
    }

    public void setCallbacks(MyRecyclerView.MyRecylerCallbacks callbacks){
        mCallbacks =  callbacks;
    }

    @Override
    public void onBindViewHolderCursor(final ViewHolder holder, Cursor cursor) {

        String homeTeam = cursor.getString(COL_HOME);
        String awayTeam = cursor.getString(COL_AWAY);
        String matchTime= cursor.getString(COL_MATCHTIME);
        int homeGoals = cursor.getInt(COL_HOME_GOALS);
        int awayGoals = cursor.getInt(COL_AWAY_GOALS);
        double matchId = cursor.getDouble(COL_ID);

        holder.homeName.setText(homeTeam);
        holder.homeName.setContentDescription(getContext().getString(R.string.home_team_description, homeTeam));
        holder.awayName.setText(awayTeam);
        holder.awayName.setContentDescription(getContext().getString(R.string.away_team_description, awayTeam));
        holder.date.setText(matchTime);
        holder.date.setContentDescription(getContext().getString(R.string.match_time_description, matchTime));
        holder.score.setText(Utilities.getScores(homeGoals, awayGoals));
        holder.score.setContentDescription(getContext().getString(R.string.match_results_description, homeTeam, homeGoals, awayTeam, awayGoals));
        holder.matchId = matchId;
        holder.homeCrest.setImageResource(Utilities.getTeamCrestByTeamName(cursor.getString(COL_HOME)));
        holder.homeCrest.setContentDescription(getContext().getString(R.string.home_team_description, homeTeam) + getContext().getString(R.string.crest));
        holder.awayCrest.setImageResource(Utilities.getTeamCrestByTeamName(cursor.getString(COL_AWAY)));
        holder.awayCrest.setContentDescription(getContext().getString(R.string.away_team_description, awayTeam) + getContext().getString(R.string.crest));

        ViewGroup container = (ViewGroup) holder.mView.findViewById(R.id.details_fragment_container);

        if (holder.matchId == detailMatchId) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.detail_fragment, null);

            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));
            TextView match_day = (TextView) v.findViewById(R.id.matchday_textview);
            match_day.setText(Utilities.getMatchDay(getContext(), cursor.getInt(COL_MATCHDAY), cursor.getInt(COL_LEAGUE)));
            TextView league = (TextView) v.findViewById(R.id.league_textview);
            league.setText(Utilities.getLeague(getContext(), cursor.getInt(COL_LEAGUE)));
            Button shareButton = (Button) v.findViewById(R.id.share_button);
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //add Share Action
                    getContext().startActivity(createShareForecastIntent(holder.homeName.getText() + " "
                            + holder.score.getText() + " " + holder.awayName.getText() + " "));
                }
            });
        } else {
            container.removeAllViews();
        }
    }

    public Intent createShareForecastIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }

    public class ViewHolder extends MyRecyclerViewHolder {
        public TextView homeName;
        public TextView awayName;
        public TextView score;
        public TextView date;
        public ImageView homeCrest;
        public ImageView awayCrest;

        public double matchId;
        public View mView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mView.setTag(this);
            homeName = (TextView) view.findViewById(R.id.home_name);
            awayName = (TextView) view.findViewById(R.id.away_name);
            score = (TextView) view.findViewById(R.id.score_textview);
            date = (TextView) view.findViewById(R.id.date_textview);
            homeCrest = (ImageView) view.findViewById(R.id.home_crest);
            awayCrest = (ImageView) view.findViewById(R.id.away_crest);
        }

        @Override
        public void onClick(View view) {
            super.onClick(view);
            if (mCallbacks != null){
                mCallbacks.OnItemClick(view, getAdapterPosition());
            }
        }
    }
}
