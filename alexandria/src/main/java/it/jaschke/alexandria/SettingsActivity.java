package it.jaschke.alexandria;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by saj on 27/01/15.
 */
public class SettingsActivity extends AppCompatPreferenceActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preference_activity);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        addPreferencesFromResource(R.xml.preferences);
    }
}
