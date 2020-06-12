package net.htlgrieskirchen.smartf1.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.htlgrieskirchen.smartf1.Adapter.RaceAdapter;
import net.htlgrieskirchen.smartf1.Beans.RaceResult;
import net.htlgrieskirchen.smartf1.Beans.Track;
import net.htlgrieskirchen.smartf1.Beans.TrackLocation;
import net.htlgrieskirchen.smartf1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LastRaceFragment extends Fragment {
    RaceAdapter adapter;
    List<RaceResult> raceResults;
    ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lastrace, container, false);
        raceResults = new ArrayList<>();
        listView = view.findViewById(R.id.listview_lastrace);
        adapter = new RaceAdapter(getActivity(), R.layout.raceresult_item, raceResults);
        listView.setAdapter(adapter);
        ServerTask st = new ServerTask();
        st.execute();
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
            String jsonResponse;
            List<RaceResult> privateResultList = new ArrayList<>();
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
                    //writeFile(trackList);
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
}
