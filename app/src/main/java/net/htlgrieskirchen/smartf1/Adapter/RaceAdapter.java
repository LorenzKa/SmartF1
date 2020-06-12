package net.htlgrieskirchen.smartf1.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.htlgrieskirchen.smartf1.Beans.Constructor;
import net.htlgrieskirchen.smartf1.Beans.Driver;
import net.htlgrieskirchen.smartf1.Beans.RaceResult;
import net.htlgrieskirchen.smartf1.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RaceAdapter extends BaseAdapter {
    private final int listViewItemLayoutId;
    private List<RaceResult> raceResults;
    private LayoutInflater layoutInflater;
    private TextView tvRacePosition;
    private TextView tvRaceFastestLap;
    private TextView tvRaceDriverName;
    private TextView tvRacePoints;
    private List<Driver> drivers;


    public RaceAdapter(Context context, int listViewItemLayoutId, List<RaceResult> raceResults) {
        this.listViewItemLayoutId = listViewItemLayoutId;
        this.raceResults = raceResults;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        drivers = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return raceResults.size();
    }

    @Override
    public Object getItem(int position) {
        return raceResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View givenView, ViewGroup parent) {
        View listItemView;
        if (givenView == null) {
            listItemView = this.layoutInflater.inflate(this.listViewItemLayoutId, null);
        } else {
            listItemView = givenView;
        }
        tvRaceDriverName = listItemView.findViewById(R.id.raceDrivername);
        tvRaceFastestLap = listItemView.findViewById(R.id.raceFastestLap);
        tvRacePoints = listItemView.findViewById(R.id.racePoints);
        tvRacePosition = listItemView.findViewById(R.id.racePosition);
        RaceResult result = this.raceResults.get(position);

        for (int i = 0; i < raceResults.size(); i++) {
            if (raceResults.get(i).getFastestLap().getRank().equals("1")) {
                drivers.add(new Driver(null, "0", raceResults.get(i).getDriver().getCode(), "", "", "", "", "", null, "", ""));
            }
        }
        if (result == null) {

        } else {
            tvRacePosition.setText(String.valueOf(position + 1));
            tvRacePoints.setText(result.getPoints());
            tvRaceDriverName.setText(result.getDriver().getGivenName() + " " + result.getDriver().getFamilyName().toUpperCase());

            if (drivers.get(0).getCode().equals(raceResults.get(position).getDriver().getCode())){
                tvRacePoints.setTextColor(Color.MAGENTA);
            }else{
                tvRacePoints.setTextColor(Color.GRAY);
            }

            if (result.getTime()==null) {
                if(result.getPositionText().equals("R")){
                    tvRaceFastestLap.setText("DNF");
                }else{
                    tvRaceFastestLap.setText(result.getStatus());
                }
            }else{
                tvRaceFastestLap.setText(result.getTime().getTime());
            }

        }
        return listItemView;
    }
}
