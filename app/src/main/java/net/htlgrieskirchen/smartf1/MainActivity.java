package net.htlgrieskirchen.smartf1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MenuItem Mcurrent_championships;
    private MenuItem Mpast_championships;
    private MenuItem Mrace_calendar;
    private MenuItem Msettings;
    private ListView currentChampionship;
    private ArrayAdapter<Driver> adapter;
    private ArrayList<Driver> driverList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentChampionship = (ListView) findViewById(R.id.listview_championship);
        driverList = new ArrayList<>();
        adapter = new ArrayAdapter<Driver>(this, android.R.layout.simple_list_item_1, driverList);
        currentChampionship.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        Mcurrent_championships = menu.findItem(R.id.current_championship);
        Mpast_championships = menu.findItem(R.id.past_championship);
        Mrace_calendar = menu.findItem(R.id.race_calendar);
        Msettings = menu.findItem(R.id.settings);
        return super.onCreateOptionsMenu(menu);
    }

}
