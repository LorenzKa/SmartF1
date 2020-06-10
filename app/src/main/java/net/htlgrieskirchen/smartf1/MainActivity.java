package net.htlgrieskirchen.smartf1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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

import net.htlgrieskirchen.smartf1.Preference.PreferenceActivity;

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
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MenuItem Mcurrent_championships;
    private MenuItem Mpast_championships;
    private MenuItem MTracks;
    private MenuItem Msettings;
    private ListView currentChampionship;
    private Adapter adapter;
    private ArrayList<Driver> driverList;
    private static final String FILE_NAME = "drivers.json";
    private File textFile;
    private String jsonResponse;
    ArrayList<Driver> driverArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        driverList = new ArrayList<>();
        currentChampionship = findViewById(R.id.listview_championship);

        textFile = new File(Environment.getExternalStorageDirectory(), FILE_NAME);

            if (checkPermission()) {
                if (textFile.exists()) {
                    load();
                    System.out.println(driverList);
                    if (driverList.isEmpty()){
                        ServerTask serverTask = new ServerTask( true);
                        serverTask.execute();
                    }
                }else{
                    ServerTask serverTask = new ServerTask(true);
                    serverTask.execute();
                }
            } else {
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
                ServerTask serverTask = new ServerTask(true);
                serverTask.execute();
            }

        adapter = new Adapter(this, R.layout.item, driverList);
        currentChampionship.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        currentChampionship.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String driver = driverList.get(position).toString();
                String result = Arrays.toString(driverList.get(position).getConstructors());
                String constructor = result.replaceAll("[\\[\\]]","");
                Intent intent = new Intent(MainActivity.this, DetailDriver.class);
                intent.putExtra("driver", driver);
                intent.putExtra("constructor", constructor);
                startActivity(intent);
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
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                return false;
            }
        });
        Mpast_championships = menu.findItem(R.id.past_championship);
        Mpast_championships.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(MainActivity.this, PastChampionShipActivity.class);
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
    public class ServerTask extends AsyncTask<String, Integer, String> {
        private final String baseURL = "http://ergast.com/api/f1/";
        private boolean driverStandings;

        public ServerTask(boolean driverStandings) {

            this.driverStandings = driverStandings;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            currentChampionship.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(String... strings) {
            String sJsonResponse = "";
            String typeOfStanding;
            if (driverStandings) {
                typeOfStanding = "driverStandings";
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(baseURL + "current/" + typeOfStanding + ".json").openConnection();
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
                        JSONObject standingstable = mrdata.getJSONObject("StandingsTable");
                        JSONArray standingsList = standingstable.getJSONArray("StandingsLists");
                        JSONObject driversObject = standingsList.getJSONObject(0);
                        JSONArray driversArray = driversObject.getJSONArray("DriverStandings");

                        JsonParser parser = new JsonParser();
                        GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();
                        for (int i = 0; i < driversArray.length(); i++) {
                            JSONObject driverAndConstructor = driversArray.getJSONObject(i);

                            JSONObject driver = driverAndConstructor.getJSONObject("Driver");
                            JsonElement driverElement = parser.parse(driver.toString());

                            Driver driverClassed = gson.fromJson(driverElement, Driver.class);
                            String seasonWins = driverAndConstructor.getString("wins");
                            String seasonPoints = driverAndConstructor.getString("points");
                            driverClassed.setSeasonPoints(seasonPoints);
                            driverClassed.setSeasonWins(seasonWins);
                            JSONArray constructors = driverAndConstructor.getJSONArray("Constructors");
                            List<Constructor> constructorList = new ArrayList<>();
                            for (int j = 0; j < constructors.length(); j++) {
                                JSONObject constructorFromArray = constructors.getJSONObject(j);
                                JsonElement constructorElement = parser.parse(constructorFromArray.toString());
                                Constructor constructor = gson.fromJson(constructorElement
                                        , Constructor.class);
                                constructorList.add(constructor);
                            }

                            Constructor[] constructorsArray = new Constructor[constructorList.size()];
                            constructorList.toArray(constructorsArray);
                            driverClassed.setConstructors(constructorsArray);
                            driverArrayList.add(driverClassed);
                        }
                        driverList.addAll(driverArrayList);
                        writeFile(jsonResponse);
                        return jsonResponse;
                    } else {
                        return "ErrorCodeFromAPI";
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return sJsonResponse;
            }
            return sJsonResponse;
        }
    }

    private void writeFile(String response){
        if(isExternalStorageWritable()){
            textFile = new File(Environment.getExternalStorageDirectory(), FILE_NAME);
            try {
                textFile.createNewFile();
                FileOutputStream output = new FileOutputStream(textFile);
                output.write(response.getBytes());
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    private void load(){
        String response = readExternalStorage();
        driverList = new ArrayList<>();
        try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject mrdata = jsonObject.getJSONObject("MRData");
                JSONObject standingstable = mrdata.getJSONObject("StandingsTable");
                JSONArray standingsList = standingstable.getJSONArray("StandingsLists");
                JSONObject driversObject = standingsList.getJSONObject(0);
                JSONArray driversArray = driversObject.getJSONArray("DriverStandings");

                JsonParser parser = new JsonParser();
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                for (int i = 0; i < driversArray.length(); i++) {
                    JSONObject driverAndConstructor = driversArray.getJSONObject(i);
                    String seasonWins = driverAndConstructor.getString("wins");
                    String seasonPoints = driverAndConstructor.getString("points");
                    JSONObject driver = driverAndConstructor.getJSONObject("Driver");
                    JsonElement driverElement = parser.parse(driver.toString());
                    Driver driverClassed = gson.fromJson(driverElement, Driver.class);
                    driverClassed.setSeasonPoints(seasonPoints);
                    driverClassed.setSeasonWins(seasonWins);
                    JSONArray constructors = driverAndConstructor.getJSONArray("Constructors");
                    List<Constructor> constructorList = new ArrayList<>();
                    for (int j = 0; j < constructors.length(); j++) {
                        JSONObject constructorFromArray = constructors.getJSONObject(j);
                        JsonElement constructorElement = parser.parse(constructorFromArray.toString());
                        Constructor constructor = gson.fromJson(constructorElement
                                , Constructor.class);
                        constructorList.add(constructor);
                    }
                    Constructor[] constructorsArray = new Constructor[constructorList.size()];
                    constructorList.toArray(constructorsArray);
                    driverClassed.setConstructors(constructorsArray);
                    driverArrayList.add(driverClassed);
                }
                driverList.addAll(driverArrayList);
        } catch (JSONException e) {
            e.printStackTrace();
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
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }
    private boolean isExternalStorageWritable(){
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }
    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

}