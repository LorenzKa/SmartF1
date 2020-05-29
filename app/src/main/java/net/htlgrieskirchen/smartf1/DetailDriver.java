package net.htlgrieskirchen.smartf1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView tvName = findViewById(R.id.driver);
        TextView tvBiography = findViewById(R.id.biography);
        TextView tvFacts = findViewById(R.id.facts);
        TextView tvSport = findViewById(R.id.sport);
        imageView = findViewById(R.id.imageView);
        Intent intent = getIntent();
        driver = intent.getStringExtra("driver");
        constructor = intent.getStringExtra("constructor");
        splitNadd();
        System.out.println(constructor);
        tvName.setText(arrayList.get(0).getGivenName()+" "+arrayList.get(0).getFamilyName().toUpperCase());
        tvBiography.setText("Geburtsdatum: "+formatDate()+"\n"+"Alter: "+calcAge()+"\nNationalität: "+arrayList.get(0).getNationality());
        tvFacts.setText("Konstrukteur: "+constructor+"\nCode: "+arrayList.get(0).getCode()+"\nNummer: "+arrayList.get(0).getPermanentNumber());
        tvSport.setText("Siege: "+arrayList.get(0).getSeasonWins()+"\nPunkte: "+arrayList.get(0).getSeasonPoints());

        if (Connection()) {
            ServerTask st = new ServerTask(arrayList.get(0).getUrl().substring(29));
            st.execute();
            while (url == null) {
                System.out.println("waiting");
            }
            Picasso.with(DetailDriver.this).load(url).into(imageView);
        }else{
            Toast.makeText(DetailDriver.this, "Stellen Sie eine Internetverbindung her um das Fahrerbild zu sehen!", Toast.LENGTH_LONG).show();
        }
    }
    private String calcAge(){
        int age = 0;
        try {
            SimpleDateFormat ourFormat = new SimpleDateFormat("dd.MM.yyyy");
            SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd");
            String reformattedStr = null;
            try {
                reformattedStr = ourFormat.format(apiFormat.parse(arrayList.get(0).getDateOfBirth()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date birth = ourFormat.parse(reformattedStr);
            Date d = new Date();

            LocalDate birthday = birth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate now = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            age = Period.between(birthday, now).getYears();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(age);

    }
    private String formatDate(){
        SimpleDateFormat ourFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd");
        String reformattedStr = null;
        try {
            reformattedStr = ourFormat.format(apiFormat.parse(arrayList.get(0).getDateOfBirth()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reformattedStr;
    }
    private void splitNadd(){
        String [] split = driver.split(",");
        Constructor cons = new Constructor("", "", constructor, "");
        Constructor[] constructors = {cons};
        arrayList.add(new Driver(split[0], split[1], split[2], split[3], split[4], split[5], split[6], split[7], constructors, split[9], split[10]));
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
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
        return true;
    }
    }


