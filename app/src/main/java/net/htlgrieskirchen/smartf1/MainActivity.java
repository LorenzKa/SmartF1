package net.htlgrieskirchen.smartf1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
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
        ServerTask st = new ServerTask("2019", true);
        st.execute();
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


public class ServerTask extends AsyncTask<String, Integer, String> {
    private MainActivity activity;
    private ProgressBar mProgressBar;
    private final String baseURL = "http://ergast.com/api/f1/";
    private String year;
    private boolean driverStandings;
    private List<Driver> drivers;
    private boolean executed = false;

    public ServerTask(String year, boolean driverStandings) {
        this.year = year;
        this.driverStandings = driverStandings;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(String... strings) {
        String sJsonResponse = "";
        String typeOfStanding;
        ArrayList<Driver> driversList = new ArrayList<>();
        if (driverStandings) {
            typeOfStanding = "driverStandings";
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(baseURL + year + "/" + typeOfStanding + ".json").openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    String jsonResponse = stringBuilder.toString();
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
                        driversList.add(driverClassed);
                    }
                    System.out.println("done");
                    drivers = driversList;
                    return jsonResponse;
                } else {
                    return "ErrorCodeFromAPI";

                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return sJsonResponse;
        } else {
            typeOfStanding = "constructorStandings";
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(baseURL + year + "/" + typeOfStanding).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    String jsonResponse = stringBuilder.toString();
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();


                } else {
                    return "ErrorCodeFromAPI";

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return sJsonResponse;
        }

    }
}
}