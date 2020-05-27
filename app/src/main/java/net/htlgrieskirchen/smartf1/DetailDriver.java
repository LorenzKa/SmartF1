package net.htlgrieskirchen.smartf1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DetailDriver extends AppCompatActivity {


    private ArrayList<Driver> arrayList = new ArrayList<>();
    private String driver;
    private String constructor;
    private String pictureURL;
    ImageView imageView;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_driver);
        TextView tvName = findViewById(R.id.driver);
        TextView tvBiography = findViewById(R.id.biography);
        TextView tvSport = findViewById(R.id.sport);
        imageView = findViewById(R.id.imageView);
        Intent intent = getIntent();
        driver = intent.getStringExtra("driver");
        constructor = intent.getStringExtra("constructor");
        splitNadd();
        System.out.println(constructor);
        tvName.setText(arrayList.get(0).getGivenName()+" "+arrayList.get(0).getFamilyName().toUpperCase());
        tvBiography.setText("Geburtsdatum: "+arrayList.get(0).getDateOfBirth()+"\n"+"Alter: 35"+"\nNationalität: "+arrayList.get(0).getNationality());
        tvSport.setText("Konstrukteur: "+constructor+"\nCode: "+arrayList.get(0).getCode()+"\nNummer: "+arrayList.get(0).getPermanentNumber());
        ServerTask st = new ServerTask(arrayList.get(0).getUrl().substring(29));
        st.execute();
        while (url==null){
            System.out.println("waiting");
        }
        Picasso.with(DetailDriver.this).load(url).into(imageView);

    }
    private void splitNadd(){
        String [] split = driver.split(",");
        Constructor cons = new Constructor("", "", constructor, "");
        Constructor[] constructors = {cons};
        arrayList.add(new Driver(split[0], split[1], split[2], split[3], split[4], split[5], split[6], split[7], constructors));
    }
    public class ServerTask extends AsyncTask<String, Integer, String> {
        private final String baseURL = "https://en.wikipedia.org/w/api.php?action=query&titles=";
        private final String endURL = "&prop=pageimages&pithumbsize=300&format=json";
        private String title;

        public ServerTask(String title) {
            this.title = title;
        }
        @Override
        protected String doInBackground(String... strings) {
            String sJsonResponse = "";
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(baseURL +  title + endURL).openConnection();
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
                        JSONObject query = jsonObject.getJSONObject("query");
                        JSONObject pages = query.getJSONObject("pages");
                        String id = pages.keys().next();
                        JSONObject pagesWithId = pages.getJSONObject(String.valueOf(id));
                        JSONObject thumbnail = pagesWithId.getJSONObject("thumbnail");
                        url = thumbnail.getString("source");
                        return jsonResponse;
                    } else {
                        return "ErrorCodeFromAPI";

                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return sJsonResponse;

            }
        }
    }


