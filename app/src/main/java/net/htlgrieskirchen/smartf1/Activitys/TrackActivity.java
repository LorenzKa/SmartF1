package net.htlgrieskirchen.smartf1.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.htlgrieskirchen.smartf1.Adapter.TrackAdapter;
import net.htlgrieskirchen.smartf1.Beans.Track;
import net.htlgrieskirchen.smartf1.Beans.TrackLocation;
import net.htlgrieskirchen.smartf1.Fragments.DriverChampionShipFragment;
import net.htlgrieskirchen.smartf1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TrackActivity extends AppCompatActivity {

    private MenuItem Mcurrent_championships;
    private MenuItem Mpast_championships;
    private MenuItem MTracks;
    private MenuItem Msettings;
    private List<Track> trackList;
    private ListView listView;
    private TrackAdapter adapter;
    private String jsonResponse;
    private int size;
    private static final String FILE_NAME = "tracks.json";
    private File textFile;
    private String response;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private double lat;
    private double lon;
    private Context context;
    private SharedPreferences prefs ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = TrackActivity.this;
        trackList = new ArrayList<>();
        listView = findViewById(R.id.listview_track);
        prefs = PreferenceManager.getDefaultSharedPreferences(this );

        File file = new File("/data/data/net.htlgrieskirchen.smartf1/app_tracks/tracks.json");
        if (file.exists()){
            load();
        }else{
            if (Connection()) {
                    ServerTask st = new ServerTask();
                    st.execute();
            }else{
                Toast.makeText(TrackActivity.this, "Stellen Sie eine Internetvebindung her!", Toast.LENGTH_LONG).show();
            }
        }


        if((checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED&&prefs.getBoolean("notification", true))){
            gps();
        }
        adapter = new TrackAdapter(this, R.layout.track, trackList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String trackListAsString = trackList.get(position).toString();
                String location = trackList.get(position).getLocation().toString();
                setUpIntent(trackListAsString, location);
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
        return super.onCreateOptionsMenu(menu);
    }
    public void gps(){
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Location location = null;
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setCostAllowed(false);
        String provider = locationManager.getBestProvider(criteria, false);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
                if(prefs.getBoolean("notification", true)) {
                    ServerTask2 st2 = new ServerTask2();
                    st2.execute();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        onResume();

        try {
            location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                lon = location.getLongitude();
                lat = location.getLatitude();
            }
        } catch (SecurityException e) {
            Log.e("SecurityException", e.getMessage());
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(locationManager != null) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000000,
                        1000,
                        locationListener);
            }
        }
    }

    public class ServerTask2 extends AsyncTask<String, Integer, String> {
        String countryOfUser;
        @Override
        protected String doInBackground(String... strings) {
            try {
                String jsonResponse;
                HttpURLConnection connection = (HttpURLConnection) new URL("https://eu1.locationiq.com/v1/reverse.php?key=1465ff08166a42&lat=" + lat + "&lon="+lon+"&format=json").openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    jsonResponse = stringBuilder.toString();
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    JSONObject data = jsonObject.getJSONObject("address");
                    String country = data.getString("country");
                    return country;
                } else {
                    return "error";
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return "error";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            for (int i = 0; i < trackList.size(); i++) {
                if(trackList.get(i).getLocation().getCountry().toLowerCase().equals(s.toLowerCase())&&!trackList.get(i).isNotified()){
                    if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
                        CharSequence name = "channel";
                        String description = "channel";
                        int importance = NotificationManager.IMPORTANCE_DEFAULT;
                        NotificationChannel channel = new NotificationChannel("TrackNotification", name, importance);
                        channel. setDescription ( description );
                        NotificationManager notificationManager = getSystemService(NotificationManager. class );
                        notificationManager.createNotificationChannel(channel);
                    }
                    String trackListAsString = trackList.get(i).toString();
                    String location = trackList.get(i).getLocation().toString();
                    Intent intent = new Intent(TrackActivity.this, DetailTrack.class);

                    intent.putExtra("track", trackListAsString);
                    intent.putExtra("location", location);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent =
                            PendingIntent.getActivity(context, 0, intent, 0);
                    Notification.Builder builder = new
                            Notification.Builder(context, "TrackNotification")
                            .setSmallIcon(android.R.drawable.star_big_on)
                            .setColor(Color.RED)
                            .setContentTitle(getString(R.string.app_name))
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setContentText("Die Rennstrecke "+ trackList.get(i).getCircuitName()+" befindet sich in deinem Land")
                            .setWhen(System.currentTimeMillis());

                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    int notificationId = 1;
                    notificationManager.notify(notificationId, builder.build());
                    trackList.get(i).setNotified(true);
                    writeFile(trackList);

                }
            }
        }
    }

    public class ServerTask extends AsyncTask<String, Integer, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter.notifyDataSetChanged();
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... strings) {
            List<Track> privateTrackList = new ArrayList<>();
            try {

                HttpURLConnection connection = (HttpURLConnection) new URL("http://ergast.com/api/f1/current/circuits.json").openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    jsonResponse = stringBuilder.toString();
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    JSONObject mrdata = jsonObject.getJSONObject("MRData");
                    JSONObject circuitTable = mrdata.getJSONObject("CircuitTable");
                    JSONArray circuitsArray = circuitTable.getJSONArray("Circuits");
                    size = circuitsArray.length();
                    JsonParser parser = new JsonParser();
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    for (int i = 0; i < circuitsArray.length(); i++) {
                        JSONObject driverAndConstructor = circuitsArray.getJSONObject(i);
                        JsonElement driverElement = parser.parse(driverAndConstructor.toString());
                        Track trackClassed = gson.fromJson(driverElement, Track.class);
                        trackClassed.setNotified(false);
                        JSONObject location = driverAndConstructor.getJSONObject("Location");
                        String latitude = location.getString("lat");
                        String longitude = location.getString("long");
                        String locality = location.getString("locality");
                        String country = location.getString("country");
                        TrackLocation location1 = new TrackLocation(latitude, longitude, locality, country);
                        trackClassed.setLocation(location1);
                        privateTrackList.add(trackClassed);
                    }
                    trackList.addAll(privateTrackList);
                        writeFile(trackList);
                    return jsonResponse;
                } else {
                    return "ErrorCodeFromAPI";
                }
            } catch (IOException | JSONException e) {
                doInBackground();
            }
            return jsonResponse;
        }
    }


    private void setUpIntent(String trackListAsString, String location) {
        Intent intent = new Intent(TrackActivity.this, DetailTrack.class);
        intent.putExtra("track", trackListAsString);
        intent.putExtra("location", location);
        startActivity(intent);
    }
    private void writeFile(List<Track> trackArrayList) {

        ContextWrapper cw = new ContextWrapper(getBaseContext());
        File directory = cw.getDir("tracks", Context.MODE_PRIVATE);
        File mypath = new File(directory, "tracks.json");
        String data = new Gson().toJson(trackArrayList);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            fos.write(data.getBytes());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        }
    private String readStorageString(){
        StringBuilder sb = new StringBuilder();

        try {
            File file = new File("/data/data/net.htlgrieskirchen.smartf1/app_tracks/tracks.json");
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while((line = br.readLine()) != null){
                sb.append(line + "\n");
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.valueOf(sb);
    }

    private void load() {
        try {
            List<Track> privateTrackList = new ArrayList<>();
            response = readStorageString();
            trackList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(response);
            JsonParser parser = new JsonParser();
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject trackAndLocation = jsonArray.getJSONObject(i);
                JsonElement jsonElement = parser.parse(trackAndLocation.toString());
                Track trackClassed = gson.fromJson(jsonElement, Track.class);
                privateTrackList.add(trackClassed);
            }
            trackList.addAll(privateTrackList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if((checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED&&prefs.getBoolean("notification", true))){
            ServerTask2 st2 = new ServerTask2();
            st2.execute();
        }

    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
        return true;
    }
    private boolean Connection() {
        boolean Wifi = false;
        boolean Mobile = false;

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo NI : netInfo) {
            if (NI.getTypeName().equalsIgnoreCase("WIFI")) {
                if (NI.isConnected()) {
                    Wifi = true;
                }
            }
            if (NI.getTypeName().equalsIgnoreCase("MOBILE"))
                if (NI.isConnected()) {
                    Mobile = true;
                }
        }
        return Wifi || Mobile;
    }


}

