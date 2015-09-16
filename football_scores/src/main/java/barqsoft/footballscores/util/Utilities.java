package barqsoft.footballscores.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.Time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import barqsoft.footballscores.R;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilities {
    public static final int SERIE_A = 357;
    public static final int PREMIER_LEGAUE = 354;
    public static final int CHAMPIONS_LEAGUE = 362;
    public static final int PRIMERA_DIVISION = 358;
    public static final int BUNDESLIGA = 351;
    public static final int SERIA_A_1516 = 401;

    public static final int DAY_BEFORE_YESTERDAY = 0;
    public static final int YESTERDAY = 1;
    public static final int TODAY = 2;
    public static final int TOMORROW = 3;
    public static final int DAY_AFTER_TOMORROW = 4;
    public static int[] groups = {DAY_BEFORE_YESTERDAY, YESTERDAY, TODAY, TOMORROW, DAY_AFTER_TOMORROW};

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String PREF_DATE_MODE = "barqsoft.footballscores.scoreswidget.datemode";

    public static int getNextMode(int currMode) {
        if (currMode >= DAY_AFTER_TOMORROW){
            return currMode;
        }else{
            return  currMode + 1;
        }
    }

    public static int getPrevMode(int currMode) {
        if (currMode <= DAY_BEFORE_YESTERDAY){
            return currMode;
        }else{
            return currMode - 1;
        }
    }

    public static int getDateModeFromPreference(Context context, int appWidgetId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(PREF_DATE_MODE + appWidgetId, TODAY);
    }

    public static void setDateModeToPreference(Context context, int appWidgetId, int mode) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putInt(PREF_DATE_MODE + appWidgetId, mode).commit();
    }

    public static void removeDateModeFromPreference(Context context, int appWidgetId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().remove(PREF_DATE_MODE + appWidgetId).commit();
    }

    public static String getDayName(Context context, long dateInMillis) {

        // If the date is today, return the localized version of "Today" instead of the actual
        // day name.

        Time t = new Time();
        t.setToNow();
        int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
        if (julianDay == currentJulianDay) {
            return context.getString(R.string.today);
        } else if (julianDay == currentJulianDay + 1) {
            return context.getString(R.string.tomorrow);
        } else if (julianDay == currentJulianDay - 1) {
            return context.getString(R.string.yesterday);
        } else {
            Time time = new Time();
            time.setToNow();


            // Otherwise, the format is just the day of the week (e.g "Wednesday".
            //We don't localize this since we could get the localize version of this directly from Simple Date Format
            //The only thing that we need are those above, today, tomorrow, yesterday.
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            return dayFormat.format(dateInMillis);
        }
    }

    public static String getDateByMode(int mode) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        return dateFormat.format(getCalendarByMode(mode).getTime());
    }

    public static Calendar getCalendarByMode(int mode) {
        Calendar calendar = Calendar.getInstance(Locale.US);
        switch (mode) {
            case TODAY:
                //do nothing;
                break;
            case YESTERDAY:
                calendar.add(Calendar.DATE, -1);
                break;
            case DAY_BEFORE_YESTERDAY:
                calendar.add(Calendar.DATE, -2);
                break;
            case TOMORROW:
                calendar.add(Calendar.DATE, 1);
                break;
            case DAY_AFTER_TOMORROW:
                calendar.add(Calendar.DATE, 2);
                break;
            default:
                throw new IllegalArgumentException("No Such Mode: " + mode);
        }

        return calendar;
    }

    public static String getLeague(Context context, int league_num) {
        switch (league_num) {
            case SERIE_A:
                return context.getString(R.string.seriaa);
            case PREMIER_LEGAUE:
                return context.getString(R.string.premierleague);
            case CHAMPIONS_LEAGUE:
                return context.getString(R.string.champions_league);
            case PRIMERA_DIVISION:
                return context.getString(R.string.primeradivison);
            case BUNDESLIGA:
                return context.getString(R.string.bundesliga);
            case SERIA_A_1516:
                return context.getString(R.string.seriaa_1516);
            default:
                return context.getString(R.string.msg_unknown_league);
        }
    }

    public static String getMatchDay(Context context, int match_day, int league_num) {
        if (league_num == CHAMPIONS_LEAGUE) {
            if (match_day <= 6) {
                //return "Group Stages, Matchday : 6";
                return context.getString(R.string.group_stage_text) + ", " + context.getString(R.string.matchday_text) + " : 6";
            } else if (match_day == 7 || match_day == 8) {
                return context.getString(R.string.first_knockout_round);
            } else if (match_day == 9 || match_day == 10) {
                return context.getString(R.string.quarter_final);
            } else if (match_day == 11 || match_day == 12) {
                return context.getString(R.string.semi_final);
            } else {
                return context.getString(R.string.final_text);
            }
        } else {
            return context.getString(R.string.matchday_text) + " : " + String.valueOf(match_day);
        }
    }

    public static String getScores(int home_goals, int awaygoals) {
        if (home_goals < 0 || awaygoals < 0) {
            return " - ";
        } else {
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }

    public static int getTeamCrestByTeamName(String teamname) {
        if (teamname == null) {
            return R.drawable.no_icon;
        }
        switch (teamname) { //This is the set of icons that are currently in the app. Feel free to find and add more
            //as you go.
            case "Arsenal London FC":
                return R.drawable.arsenal;
            case "Manchester United FC":
                return R.drawable.manchester_united;
            case "Swansea City":
                return R.drawable.swansea_city_afc;
            case "Leicester City":
                return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC":
                return R.drawable.everton_fc_logo1;
            case "West Ham United FC":
                return R.drawable.west_ham;
            case "Tottenham Hotspur FC":
                return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion":
                return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC":
                return R.drawable.sunderland;
            case "Stoke City FC":
                return R.drawable.stoke_city;
            default:
                return R.drawable.no_icon;
        }
    }
}
