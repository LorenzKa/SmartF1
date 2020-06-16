package net.htlgrieskirchen.smartf1.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.htlgrieskirchen.smartf1.Adapter.ConstructorAdapter;
import net.htlgrieskirchen.smartf1.Adapter.RaceAdapter;
import net.htlgrieskirchen.smartf1.Beans.Constructor;
import net.htlgrieskirchen.smartf1.Beans.ConstructorResult;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ConstructorChampionshipFragment extends Fragment {
     List<ConstructorResult> constructorResults;
     ListView listView;
     ConstructorAdapter adapter;
    private static final String FILE_NAME = "constructor.json";
    private File textFile;
    List<ConstructorResult> privateResultList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team, container, false);
        constructorResults = new ArrayList<>();
        privateResultList = new ArrayList<>();
        textFile = new File(Environment.getExternalStorageDirectory(), FILE_NAME);
        listView = view.findViewById(R.id.listview_constructor);
        adapter = new ConstructorAdapter(getActivity(), R.layout.constructor_item, constructorResults);
        listView.setAdapter(adapter);

            if (Connection()) {
                Calendar cal = Calendar.getInstance();
                int currentDayOfYear = cal.get(Calendar.DAY_OF_YEAR);
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("syncConstructorChampionship", 0);
                int dayOfYear = sharedPreferences.getInt("dayOfYear", 0);
                if (dayOfYear != currentDayOfYear) {
                    File file = new File("/data/data/net.htlgrieskirchen.smartf1/app_results/constructor.json");
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
            }else {
                load();
            }

        return view;
    }
    public class ServerTask extends AsyncTask<String, Integer, String> {
        private final String baseURL = "http://ergast.com/api/f1/";
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter.notifyDataSetChanged();
        }
        @Override
        protected String doInBackground(String... strings) {
            String jsonResponse = "";
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(baseURL + "current/" + "constructorStandings" + ".json").openConnection();
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
                        JSONObject constructorObject = standingsList.getJSONObject(0);
                        JSONArray constructorArray = constructorObject.getJSONArray("ConstructorStandings");

                        JsonParser parser = new JsonParser();
                        GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();
                        for (int i = 0; i < constructorArray.length(); i++) {
                            JSONObject constructorArrayObject = constructorArray.getJSONObject(i);
                            JsonElement driverElement = parser.parse(constructorArrayObject.toString());
                            ConstructorResult constructorResult = gson.fromJson(driverElement, ConstructorResult.class);
                            privateResultList.add(constructorResult);
                        }
                        save(jsonResponse);
                        constructorResults.addAll(privateResultList);
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

private void save(String data) {
    ContextWrapper cw = new ContextWrapper(getActivity());
    File directory = cw.getDir("results", Context.MODE_PRIVATE);
    File mypath = new File(directory, "constructor.json");

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
            JSONObject standingstable = mrdata.getJSONObject("StandingsTable");
            JSONArray standingsList = standingstable.getJSONArray("StandingsLists");
            JSONObject constructorObject = standingsList.getJSONObject(0);
            JSONArray constructorArray = constructorObject.getJSONArray("ConstructorStandings");

            JsonParser parser = new JsonParser();
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            for (int i = 0; i < constructorArray.length(); i++) {
                JSONObject constructorArrayObject = constructorArray.getJSONObject(i);
                JsonElement driverElement = parser.parse(constructorArrayObject.toString());
                ConstructorResult constructorResult = gson.fromJson(driverElement, ConstructorResult.class);
                privateResultList.add(constructorResult);
            }
            constructorResults.addAll(privateResultList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private String readStorageString(){
        StringBuilder sb = new StringBuilder();

            try {
                File file = new File("/data/data/net.htlgrieskirchen.smartf1/app_results/constructor.json");
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
