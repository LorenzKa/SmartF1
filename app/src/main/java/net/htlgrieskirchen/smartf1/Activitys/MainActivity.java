package net.htlgrieskirchen.smartf1.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import net.htlgrieskirchen.smartf1.Fragments.ConstructorChampionshipFragment;
import net.htlgrieskirchen.smartf1.Fragments.DriverChampionShipFragment;
import net.htlgrieskirchen.smartf1.Fragments.LastRaceFragment;
import net.htlgrieskirchen.smartf1.Preferences.PreferenceActivity;
import net.htlgrieskirchen.smartf1.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private MenuItem Mcurrent_championships;
    private MenuItem Mpast_championships;
    private MenuItem MTracks;
    private MenuItem Msettings;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private double lat;
    private double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new
                    String[]{Manifest.permission.ACCESS_FINE_LOCATION},2);
        }



        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new DriverChampionShipFragment()).commit();
        }

    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.driverChampionship:
                            selectedFragment = new DriverChampionShipFragment();
                            break;
                        case R.id.constructorChampionship:
                            selectedFragment = new ConstructorChampionshipFragment();
                            break;
                        case R.id.raceResult:
                            selectedFragment = new LastRaceFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        Mcurrent_championships = menu.findItem(R.id.current_championship);
        Mcurrent_championships.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                return false;
            }
        });
        Mpast_championships = menu.findItem(R.id.past_championship);
        Mpast_championships.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(MainActivity.this, PastChampionshipActivity.class);
                startActivity(intent);
                return false;

            }
        });
        MTracks = menu.findItem(R.id.race_calendar);
        MTracks.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(MainActivity.this, TrackActivity.class);
                startActivity(intent);
                return false;
            }
        });
        Msettings = menu.findItem(R.id.settings);
        Msettings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(MainActivity.this, PreferenceActivity.class);
                startActivityForResult(intent, 1);
                return false;

            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void onRequestPermissionsResult( int requestCode,
                                            @NonNull String[] permissions ,
                                            @NonNull int[] grantResults ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this,
                        "GPS Permission wurde verweigert!",
                        Toast.LENGTH_LONG).show();
            }
        }
        else if (requestCode == 1) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new DriverChampionShipFragment()).commit();
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this,
                        "SD Card Permission wurde verweigert!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}