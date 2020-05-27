package net.htlgrieskirchen.smartf1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class DetailDriver extends AppCompatActivity {


    private ArrayList<Driver> arrayList = new ArrayList<>();
    private String driver;
    private String constructor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_driver);
        TextView tvName = findViewById(R.id.driver);
        TextView tvBiography = findViewById(R.id.biography);
        TextView tvSport = findViewById(R.id.sport);
        Intent intent = getIntent();
        driver = intent.getStringExtra("driver");
        constructor = intent.getStringExtra("constructor");
        splitNadd();
        System.out.println(constructor);
        tvName.setText(arrayList.get(0).getGivenName()+" "+arrayList.get(0).getFamilyName().toUpperCase());
        tvBiography.setText("Geburtsdatum: "+arrayList.get(0).getDateOfBirth()+"\n"+"Alter: 35"+"\nNationalität: "+arrayList.get(0).getNationality());
        tvSport.setText("Konstrukteur: "+constructor+"\nCode: "+arrayList.get(0).getCode()+"\nNummer: "+arrayList.get(0).getPermanentNumber());

    }
    private void splitNadd(){
        String [] split = driver.split(",");
        Constructor cons = new Constructor("", "", constructor, "");
        Constructor[] constructors = {cons};
        arrayList.add(new Driver(split[0], split[1], split[2], split[3], split[4], split[5], split[6], split[7], constructors));
    }
}
