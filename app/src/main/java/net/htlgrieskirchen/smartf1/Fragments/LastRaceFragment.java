package net.htlgrieskirchen.smartf1.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.htlgrieskirchen.smartf1.Adapter.DriverAdapter;
import net.htlgrieskirchen.smartf1.Adapter.RaceAdapter;
import net.htlgrieskirchen.smartf1.Beans.Constructor;
import net.htlgrieskirchen.smartf1.Beans.Driver;
import net.htlgrieskirchen.smartf1.Beans.RaceResult;
import net.htlgrieskirchen.smartf1.Beans.Track;
import net.htlgrieskirchen.smartf1.Beans.TrackLocation;
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
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LastRaceFragment extends Fragment {
    RaceAdapter adapter;
    List<RaceResult> raceResults;
    ListView listView;
    private String jsonResponse;
    List<RaceResult> privateResultList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lastrace, container, false);
        raceResults = new ArrayList<>();
        listView = view.findViewById(R.id.listview_lastrace);
        adapter = new RaceAdapter(getActivity(), R.layout.raceresult_item, raceResults);
        listView.setAdapter(adapter);

            if (Connection()){
                Calendar cal = Calendar.getInstance();
                int currentDayOfYear = cal.get(Calendar.DAY_OF_YEAR);
                SharedPreferences sharedPreferences= getActivity().getSharedPreferences("syncLastRace", 0);
                int dayOfYear = sharedPreferences.getInt("dayOfYear", 0);
                if(dayOfYear != currentDayOfYear){
                    File file = new File("/data/data/net.htlgrieskirchen.smartf1/app_results/lastrace.json");
                    if (file.exists()){
                        file.delete();
                    }else {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("dayOfYear", currentDayOfYear);
                        editor.commit();
                        ServerTask st = new ServerTask();
                        st.execute();
                    }
                }else{
                    load();
                }
            }else{
                load();
            }
        return view;
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
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("http://ergast.com/api/f1/current/last/results.json").openConnection();
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
                    JSONObject raceTable = mrdata.getJSONObject("RaceTable");
                    JSONArray races = raceTable.getJSONArray("Races");
                    JSONObject race = races.getJSONObject(0);
                    JSONArray resultsArray = race.getJSONArray("Results");
                    JsonParser parser = new JsonParser();
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject result = resultsArray.getJSONObject(i);
                        JsonElement resultString = parser.parse(result.toString());
                        RaceResult trackClassed = gson.fromJson(resultString, RaceResult.class);
                        privateResultList.add(trackClassed);
                    }
                    raceResults.addAll(privateResultList);
                        save(jsonResponse);
                    return jsonResponse;
                } else {
                    return "ErrorCodeFromAPI";
                }
            } catch (IOException | JSONException e) {
                doInBackground();
            }
            return "jsonResponse";
        }
    }
    private void save(String data) {
        ContextWrapper cw = new ContextWrapper(getActivity());
        File directory = cw.getDir("results", Context.MODE_PRIVATE);
        File mypath = new File(directory, "lastrace.json");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            fos.write(data.getBytes());
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
    private void load(){
        String response = readStorageString();

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject mrdata = jsonObject.getJSONObject("MRData");
            JSONObject raceTable = mrdata.getJSONObject("RaceTable");
            JSONArray races = raceTable.getJSONArray("Races");
            JSONObject race = races.getJSONObject(0);
            JSONArray resultsArray = race.getJSONArray("Results");
            JsonParser parser = new JsonParser();
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject result = resultsArray.getJSONObject(i);
                JsonElement resultString = parser.parse(result.toString());
                RaceResult trackClassed = gson.fromJson(resultString, RaceResult.class);
                privateResultList.add(trackClassed);
            }
            raceResults.addAll(privateResultList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private String readStorageString(){
        StringBuilder sb = new StringBuilder();
        try {
            File file = new File("/data/data/net.htlgrieskirchen.smartf1/app_results/lastrace.json");
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
