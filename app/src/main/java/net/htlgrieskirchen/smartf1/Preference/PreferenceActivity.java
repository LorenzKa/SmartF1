package net.htlgrieskirchen.smartf1.Preference;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import net.htlgrieskirchen.smartf1.MainActivity;
import net.htlgrieskirchen.smartf1.R;
import net.htlgrieskirchen.smartf1.TrackActivity;

import androidx.appcompat.app.AppCompatActivity;

public class PreferenceActivity extends AppCompatActivity {

    private MenuItem Mcurrent_championships;
    private MenuItem Mpast_championships;
    private MenuItem MTracks;
    private MenuItem Msettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new
                        PreferenceFragment())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        Mcurrent_championships = menu.findItem(R.id.current_championship);
        Mcurrent_championships.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(PreferenceActivity.this, MainActivity.class);
                startActivity(intent);
                return false;
            }
        });
        Mpast_championships = menu.findItem(R.id.past_championship);
        Mpast_championships.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
        MTracks = menu.findItem(R.id.race_calendar);
        MTracks.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(PreferenceActivity.this, TrackActivity.class);
                startActivity(intent);
                return false;
            }
        });
        Msettings = menu.findItem(R.id.settings);
        Msettings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(PreferenceActivity.this, PreferenceActivity.class);
                startActivityForResult(intent, 1);
                return false;

            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
        return true;
    }

}
