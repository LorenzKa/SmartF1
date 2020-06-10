package net.htlgrieskirchen.smartf1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
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
import java.util.Calendar;
import java.util.List;

public class PastChampionShipActivity extends AppCompatActivity {

    private Spinner spinner;
    private ListView listView;
    private ArrayList<Integer> arrayList;
    private String jsonResponse;
    private ArrayList<Driver> driverArrayList = new ArrayList<>();
    private ArrayList<Driver> driverList;
    private Adapter adapter;
    private String year;
    private File file;
    private String path;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_champion_ship);
        spinner = findViewById(R.id.spinner);
        listView = (ListView) findViewById(R.id.listview_past);
        arrayList = new ArrayList<>();
        driverList = new ArrayList<>();


        Calendar calendar = Calendar.getInstance();
        int now = calendar.get(Calendar.YEAR);

        for (int i = 1950; i < now; i++) {
            arrayList.add(i);
        }
        adapter = new Adapter(this, R.layout.item, driverList);
        listView.setAdapter(adapter);
        spinner.setAdapter(new ArrayAdapter<Integer>(this, R.layout.spinneritem, arrayList));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                driverList.clear();
                year = spinner.getSelectedItem().toString();
                path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pastchampionships/" + year + ".json";
                file = new File(path);
                if (file.exists()) {
                    driverList.clear();
                    load();
                    adapter.notifyDataSetChanged();
                }else {
                    ServerTask st = new ServerTask(year, true);
                    st.execute();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public class ServerTask extends AsyncTask<String, Integer, String> {
        private final String baseURL = "http://ergast.com/api/f1/";
        private String year;
        private boolean driverStandings;

        public ServerTask(String year, boolean driverStandings) {
            this.year = year;
            this.driverStandings = driverStandings;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter.notifyDataSetChanged();
            writeFile(jsonResponse);
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
        @Override
        protected String doInBackground(String... strings) {
                driverArrayList.clear();
                String sJsonResponse = "";
                String typeOfStanding;
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
                            return jsonResponse;
                        } else {
                            return "ErrorCodeFromAPI";
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    return sJsonResponse;
                }
            return jsonResponse;
        }
    }
    private void writeFile(String response){
        if(isExternalStorageWritable()){
            File sd = Environment.getExternalStorageDirectory();
            String path = sd.getAbsolutePath() + "/pastchampionships";
            file = new File(path);
            file.mkdir();
            file = new File(path+"/"+year+".json");
            try {
                file.createNewFile();
                FileOutputStream output = new FileOutputStream(file);
                output.write(response.getBytes());
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void load(){
        String response = readExternalStorage();
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
        file = new File( Environment.getExternalStorageDirectory().getAbsolutePath() + "/pastchampionships/" + year + ".json");
        if (isExternalStorageReadable()){
            try {
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
    private boolean Connection() {
        boolean Wifi = false;
        boolean Mobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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