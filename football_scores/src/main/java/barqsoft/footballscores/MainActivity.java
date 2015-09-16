package barqsoft.footballscores;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import barqsoft.footballscores.activity.AboutActivity;
import barqsoft.footballscores.fragment.PagerFragment;
import barqsoft.footballscores.util.LogUtil;
import barqsoft.footballscores.util.SyncUtils;

public class MainActivity extends AppCompatActivity{
    public static final String CURRENT_PAGER_ITEM = "Pager_Current";
    public static final String SELECTED_MATCH_ID = "Selected_match";
    public static final String EXTRA_SELECTION_FROM_WIDGET = "selection_from_widget";
    public static int sSelectedMatchId;
//    public static int sCurrentFragment = 2;
    private final String SAVE_TAG = "Save Test";
    private PagerFragment myMain;
    private LogUtil mLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setIcon(R.drawable.ic_launcher);

        mLog = new LogUtil.Builder().setLogTag(MainActivity.class).build();
        mLog.d("Reached MainActivity onCreate");

        if (savedInstanceState == null) {
            myMain = new PagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, myMain)
                    .commit();

            SyncUtils.TriggerRefresh();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent start_about = new Intent(this, AboutActivity.class);
            startActivity(start_about);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
