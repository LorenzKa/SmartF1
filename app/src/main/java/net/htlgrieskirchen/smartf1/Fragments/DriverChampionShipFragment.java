package net.htlgrieskirchen.smartf1.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import net.htlgrieskirchen.smartf1.Adapter.DriverAdapter;
import net.htlgrieskirchen.smartf1.Beans.Constructor;
import net.htlgrieskirchen.smartf1.Beans.Driver;
import net.htlgrieskirchen.smartf1.Activitys.DetailDriver;
import net.htlgrieskirchen.smartf1.R;

public class DriverChampionShipFragment extends Fragment {
    private ListView listView;
    private List<Driver> driverList;
    private List<Driver> driverArrayList;
    private static final String FILE_NAME = "drivers.json";
    private File textFile;
    private DriverAdapter driverAdapter;
    private String jsonResponse;
    private boolean exception;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_championship, container, false);
        driverArrayList = new ArrayList<>();
        driverList = new ArrayList<>();
        textFile = new File(Environment.getExternalStorageDirectory(), FILE_NAME);
        driverAdapter = new DriverAdapter(getActivity(), R.layout.championship_item, driverList);
        listView = (ListView) view.findViewById(R.id.listview_championship);
        listView.setAdapter(driverAdapter);



            if (Connection()){
                ServerTask serverTask = new ServerTask(true);
                serverTask.execute();
            }else if(!Connection() && checkPermission()){
                load();
                Toast.makeText(getActivity(), "Sie befinden sich im offline Modus!", Toast.LENGTH_LONG).show();
            }
            if (!checkPermission() && !Connection()){
                Toast.makeText(getActivity(), "Sie müssen SD-Card Berechtigung hergeben oder eine Internetvebindung herstellen!", Toast.LENGTH_LONG).show();
            }
            if (!driverAdapter.isEmpty()){
                load();
            }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String driver = driverList.get(position).toString();
                String result = Arrays.toString(driverList.get(position).getConstructors());
                String constructor = result.replaceAll("[\\[\\]]","");
                Intent intent = new Intent(getActivity(), DetailDriver.class);
                intent.putExtra("driver", driver);
                intent.putExtra("constructor", constructor);
                startActivity(intent);
            }
        });
        return view;
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
            if (exception){
                load();
            }
            listView.setAdapter(driverAdapter);
            driverAdapter.notifyDataSetChanged();
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
                    exception = true;
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
            driverAdapter.notifyDataSetChanged();
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
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }
    private boolean Connection() {
        boolean Wifi = false;
        boolean Mobile = false;

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
