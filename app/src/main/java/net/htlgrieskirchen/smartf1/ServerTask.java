package net.htlgrieskirchen.smartf1;

import android.accounts.NetworkErrorException;
import android.icu.text.IDNA;
import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.htlgrieskirchen.smartf1.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ServerTask extends AsyncTask<String, Integer, String> {
    private MainActivity activity ;
    private ProgressBar mProgressBar;
    private final String baseURL = "http://ergast.com/api/f1/";
    private String year;
    private boolean driverStandings;
    private List<Driver> drivers;
    private boolean executed = false;


    public ServerTask(MainActivity activity, String year, boolean driverStandings) {
        this.activity = activity;
        this.year = year;
        this.driverStandings = driverStandings;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected void onProgressUpdate(Integer ... values ) {
        super.onProgressUpdate(values);
    }
    @Override
    protected String doInBackground(String ... strings ) {
        String sJsonResponse = "";
        String typeOfStanding;
        ArrayList<Driver> driversList = new ArrayList<>();
        if(driverStandings){
            typeOfStanding = "driverStandings";
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(baseURL+year+"/"+typeOfStanding+".json").openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = "";
                    while ( (line=reader.readLine()) != null) {
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
                        JsonElement driverElement =  parser.parse(driver.toString());
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
        }
        else{
            typeOfStanding = "constructorStandings";
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(baseURL+year+"/"+typeOfStanding).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = "";
                    while ( (line=reader.readLine()) != null) {
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
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        executed=true;
    }
    public List<Driver> getDrivers() {
        return drivers;
    }

    public boolean isExecuted() {
        return executed;
    }
}