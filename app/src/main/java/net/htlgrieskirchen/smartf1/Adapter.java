package net.htlgrieskirchen.smartf1;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.Arrays;
import java.util.List;

public class Adapter extends BaseAdapter {
    private final int listViewItemLayoutId;
    private List<Driver> driver;
    private LayoutInflater layoutInflater;
    private TextView tvNumber;
    private TextView tvPosition;
    private TextView tvDriverFirstName;
    private TextView tvConstructor;
    private String constructor;

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
        tvNumber = listItemView.findViewById(R.id.number);
        tvPosition = listItemView.findViewById(R.id.position);
        tvDriverFirstName = listItemView.findViewById(R.id.drivername);
        tvConstructor = listItemView.findViewById(R.id.constructor);

        Driver driver = this.driver.get(position);

        tvPosition.setText(String.valueOf(position+1));
        tvNumber.setText(driver.getPermanentNumber());
        tvDriverFirstName.setText(driver.getGivenName()+" "+driver.getFamilyName().toUpperCase());
        String result = Arrays.toString(driver.getConstructors());
        constructor = result.replaceAll("\\[|\\]","");
        tvConstructor.setText(constructor);
        return listItemView;
    }
}
