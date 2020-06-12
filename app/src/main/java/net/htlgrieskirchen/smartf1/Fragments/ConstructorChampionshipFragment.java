package net.htlgrieskirchen.smartf1.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
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
import net.htlgrieskirchen.smartf1.Beans.Track;
import net.htlgrieskirchen.smartf1.Beans.TrackLocation;
import net.htlgrieskirchen.smartf1.R;

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

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ConstructorChampionshipFragment extends Fragment {
     List<ConstructorResult> constructorResults;
     ListView listView;
     ConstructorAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team, container, false);
        constructorResults = new ArrayList<>();
        listView = view.findViewById(R.id.listview_constructor);
        adapter = new ConstructorAdapter(getActivity(), R.layout.constructor_item, constructorResults);
        listView.setAdapter(adapter);
        ServerTask st = new ServerTask();
        st.execute();
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
            List<ConstructorResult> privateResultList = new ArrayList<>();
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

                        constructorResults.addAll(privateResultList);
                        //writeFile(jsonResponse);
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
