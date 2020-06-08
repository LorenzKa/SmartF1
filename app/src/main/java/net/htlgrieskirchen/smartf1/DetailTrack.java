package net.htlgrieskirchen.smartf1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DetailTrack extends AppCompatActivity {


    private TextView tvCircuitName;
    private TextView tvCircuitLocality;
    private TextView latlong;
    private ImageView imageView;
    private String[] split;
    private String url;
    private String trackName;
    private String trackLocation;
    private Intent intent;
    private Bitmap bitmap;
    private String circuitName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_track);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setUpIntent();
        initializeViews();

        split = trackName.split(",");
        circuitName = split[2];
        tvCircuitName.setText(circuitName);

        if (!fileExist(circuitName)){
            if (Connection()) {
                ServerTask st = new ServerTask(split[1].substring(29));
                st.execute();
            }else{
                if (fileExist(circuitName)){
                    loadIMG();
                }else{
                    Toast.makeText(this, "Stellen Sie eine Internetverbindung her um das Fahrerbild zu sehen/downloaden!", Toast.LENGTH_LONG).show();
                }
            }
        }else{
            if (fileExist(circuitName)){
                    loadIMG();
                }
            }
        split = trackLocation.split(",");
        tvCircuitLocality.setText("Ort: "+split[2]+"\nLand: "+split[3]);
        latlong.setText("Longitude: "+split[0]+"\nLatiude: "+split[1]);
    }
    public boolean fileExist(String fileName) {
        String path = "/data/data/net.htlgrieskirchen.smartf1/app_tracks/"+fileName+".jpg";
        File file = new File(path);
        return file.exists();
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
                    HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
                    bitmap = BitmapFactory.decodeStream(con.getInputStream());
                    return jsonResponse;
                } else {
                    return "ErrorCodeFromAPI";
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return sJsonResponse;
        }
        @Override
        protected void onPostExecute(String s) {
            if (url != null) {
                imageView.setBackgroundColor(Color.WHITE);
                Picasso.with(DetailTrack.this).load(url).into(imageView);
                saveToInternalStorage(bitmap);
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(this, TrackActivity.class);
        startActivity(myIntent);
        return true;
    }

    private void initializeViews(){
        tvCircuitName = findViewById(R.id.circuitName);
        latlong = findViewById(R.id.latlong);
        tvCircuitLocality = findViewById(R.id.circuitLocality);
        imageView = findViewById(R.id.imageView);
    }
    private void setUpIntent(){
        intent = getIntent();
        trackName = intent.getStringExtra("track");
        trackLocation = intent.getStringExtra("location");
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
    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("tracks", Context.MODE_PRIVATE);
        File mypath = new File(directory, circuitName + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }
    private void loadIMG(){
        Bitmap bitmap = BitmapFactory.decodeFile("/data/data/net.htlgrieskirchen.smartf1/app_tracks/"+circuitName+".jpg");
        imageView.setBackgroundColor(Color.WHITE);
        imageView.setImageBitmap(bitmap);

    }
}