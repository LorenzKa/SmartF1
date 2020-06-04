package net.htlgrieskirchen.smartf1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TrackActivity extends AppCompatActivity {

    private MenuItem Mcurrent_championships;
    private MenuItem Mpast_championships;
    private MenuItem MTracks;
    private MenuItem Msettings;
    private List<Track> trackList;
    private ListView listView;
    private TrackAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        trackList = new ArrayList<>();
        listView = findViewById(R.id.listview_track);
        adapter = new TrackAdapter(this, R.layout.track, trackList);
        listView.setAdapter(adapter);
        ServerTask st = new ServerTask("2019");
        st.execute();
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
        private final String baseURL = "http://ergast.com/api/f1/";
        private String year;
        private boolean driverStandings;
        private String jsonResponse;

        public ServerTask(String year) {
            this.year = year;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter.notifyDataSetChanged();
            //writeFile();
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... strings) {
                List<Track> privateTrackList = new ArrayList<>();
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(baseURL + year + "/" + "circuits" + ".json").openConnection();
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
                        JsonParser parser = new JsonParser();
                        GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();
                        for (int i = 0; i < circuitsArray.length(); i++) {
                            JSONObject driverAndConstructor = circuitsArray.getJSONObject(i);
                            JsonElement driverElement = parser.parse(driverAndConstructor.toString());
                            Track trackClassed = gson.fromJson(driverElement, Track.class);
                            privateTrackList.add(trackClassed);
                        }
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
}
