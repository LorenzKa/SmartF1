package net.htlgrieskirchen.smartf1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
    private String jsonResponse;
    private int size;
    private static final String FILE_NAME = "tracks.json";
    private File file;
    private String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        trackList = new ArrayList<>();
        listView = findViewById(R.id.listview_track);
        adapter = new TrackAdapter(this, R.layout.track, trackList);
        listView.setAdapter(adapter);
        file = new File(Environment.getExternalStorageDirectory().toString()+"/tracks.json");
      if (!file.exists()) {
          try {
              file.createNewFile();
          } catch (IOException e) {
              e.printStackTrace();
          }
          for (int i = 1; i < 22; i++) {
              ServerTask s = new ServerTask(String.valueOf(i));
              s.execute();
          }
      }else{
           load();
       }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                trackListAsString = trackList.get(position).toString();
                location = trackList.get(position).getLocation().toString();
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
    public class ServerTask extends AsyncTask<String, Integer, String> {
        private String number;

        public ServerTask(String number) {
            this.number = number;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter.notifyDataSetChanged();
            writeFile(trackList);

        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... strings) {
                List<Track> privateTrackList = new ArrayList<>();
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL("http://ergast.com/api/f1/2019/"+number+"/circuits.json").openConnection();
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
                            JSONObject location = driverAndConstructor.getJSONObject("Location");
                            String latitude = location.getString("lat");
                            String longitude = location.getString("long");
                            String locality = location.getString("locality");
                            String country = location.getString("country");
                            TrackLocation location1 = new TrackLocation(latitude, longitude, locality, country);
                            trackClassed.setLocation(location1);
                            privateTrackList.add(trackClassed);
                        }
                     //   trackList = new ArrayList<>();
                        trackList.addAll(privateTrackList);
                        return jsonResponse;
                    } else {
                        return "ErrorCodeFromAPI";
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return jsonResponse;
            }
    }
    private void setUpIntent(){
        Intent intent = new Intent(TrackActivity.this, DetailTrack.class);
        intent.putExtra("track", trackListAsString);
        intent.putExtra("location", location);
        startActivity(intent);
    }
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
    private boolean isExternalStorageWritable(){
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
    private void writeFile(List<Track> trackArrayList){
        if(isExternalStorageWritable()){
            File textFile = new File(Environment.getExternalStorageDirectory(), FILE_NAME);
            try {

                String json = new Gson().toJson(trackArrayList);
                FileOutputStream output = new FileOutputStream(textFile);
                output.write(json.getBytes());
                output.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    private String readExternalStorage(){
        StringBuilder sb = new StringBuilder();

        if (isExternalStorageReadable()){
            try {
                Environment.getExternalStorageDirectory();
                File file = new File(Environment.getExternalStorageDirectory(), FILE_NAME);
                FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);

                String line;
                while((line = br.readLine()) != null){

                    sb.append(line + "\n");

                }
                fis.close();
                System.out.println(sb);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return String.valueOf(sb);
    }
    private void load(){
        try {
            List<Track> privateTrackList = new ArrayList<>();
            response = readExternalStorage();
            trackList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(response);
            JSONObject mrdata = jsonObject.getJSONObject("MRData");
            JSONObject circuitTable = mrdata.getJSONObject("CircuitTable");
            JSONArray circuitsArray = circuitTable.getJSONArray("Circuits");
            JsonParser parser = new JsonParser();
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            for (int i = 0; i < circuitsArray.length(); i++) {
                JSONObject driverAndConstructor = circuitsArray.getJSONObject(i);
                JsonElement driverElement = parser.parse(driverAndConstructor.toString());
                Track trackClassed = gson.fromJson(driverElement, Track.class);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        }
    }

