package net.htlgrieskirchen.smartf1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrackActivity extends AppCompatActivity {

    private MenuItem Mcurrent_championships;
    private MenuItem Mpast_championships;
    private MenuItem MTracks;
    private MenuItem Msettings;
    private List<Track> trackList;
    private ListView listView;
    private TrackAdapter adapter;
    private String location;
    private String trackListAsString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeViews();

        TrackLocation[] trackLocations = {new TrackLocation("29.09", "4.5678989", "Shakir", "Bahrain")};
        trackList.add(new Track("bahr", "fd", "BahrainGP", trackLocations));

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                trackListAsString = trackList.get(position).toString();
                location = Arrays.toString(trackList.get(position).getTrackLocations()).replaceAll("\\[|\\]","");
                setUpIntent();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        Mcurrent_championships = menu.findItem(R.id.current_championship);
        Mcurrent_championships.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(TrackActivity.this, MainActivity.class);
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
                Intent intent = new Intent(TrackActivity.this, TrackActivity.class);
                startActivity(intent);
                return false;
            }
        });
        Msettings = menu.findItem(R.id.settings);
        Msettings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(this, TrackActivity.class);
        startActivity(myIntent);
        return true;
    }
    private void initializeViews(){
        trackList = new ArrayList<>();
        listView = findViewById(R.id.listview_track);
        adapter = new TrackAdapter(this, R.layout.track, trackList);
    }
    private void setUpIntent(){
        Intent intent = new Intent(TrackActivity.this, DetailTrack.class);
        intent.putExtra("track", trackListAsString);
        intent.putExtra("location", location);
        startActivity(intent);
    }
}
