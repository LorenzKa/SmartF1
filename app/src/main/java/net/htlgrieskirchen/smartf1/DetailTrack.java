package net.htlgrieskirchen.smartf1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailTrack extends AppCompatActivity {

    private TextView tvCircuitName;
    private TextView tvCircuitLocality;
    private TextView latlong;
    private String[] split;
    private String trackName;
    private String trackLocation;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_track);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setUpIntent();
        initializeViews();

        split = trackName.split(",");
        String circuitName = split[2];
        tvCircuitName.setText(circuitName);

        split = trackLocation.split(",");
        tvCircuitLocality.setText("Ort: "+split[2]+"\nLand: "+split[3]);
        latlong.setText("Longitude: "+split[0]+"\nLatiude: "+split[1]);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(this, TrackActivity.class);
        startActivity(myIntent);
        return true;
    }

    private void initializeViews(){
        tvCircuitName = findViewById(R.id.circuitName);
        latlong = findViewById(R.id.latlong);
        tvCircuitLocality = findViewById(R.id.circuitLocality);
    }
    private void setUpIntent(){
        intent = getIntent();
        trackName = intent.getStringExtra("track");
        trackLocation = intent.getStringExtra("location");
    }
}
