package net.htlgrieskirchen.smartf1;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class Adapter extends BaseAdapter {
    private final int listViewItemLayoutId;
    private List<Driver> driver;
    private LayoutInflater layoutInflater;
    public Adapter(Context context, int listViewItemLayoutId, List<Driver> driver) {
        this.listViewItemLayoutId = listViewItemLayoutId;
        this.driver = driver;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return driver.size();
    }
    @Override
    public Object getItem(int position) {
        return driver.get(position);
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(int position, View givenView, ViewGroup parent) {
        View listItemView;
        if(givenView == null) {
            listItemView = this.layoutInflater.inflate(this.listViewItemLayoutId, null);
        } else {
            listItemView = givenView;
        }
        TextView tvnumber = listItemView.findViewById(R.id.number);
        TextView tvposition = listItemView.findViewById(R.id.position);
        TextView tvdriverfirstname = listItemView.findViewById(R.id.drivername);
        TextView tvconstructor = listItemView.findViewById(R.id.constructor);
        Driver driver = this.driver.get(position);
        int pos = position + 1;
        tvposition.setText(String.valueOf(pos));
        tvnumber.setText(driver.getPermanentNumber());
        tvdriverfirstname.setText(driver.getGivenName()+" "+driver.getFamilyName().toUpperCase());
        String res = Arrays.toString(driver.getConstructors());
        String rep = res.replaceAll("\\[|\\]","");
        tvconstructor.setText(rep);
        return listItemView;
    }
}
