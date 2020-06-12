package net.htlgrieskirchen.smartf1.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import net.htlgrieskirchen.smartf1.Beans.ConstructorResult;
import net.htlgrieskirchen.smartf1.R;

import java.util.List;

public class ConstructorAdapter extends BaseAdapter {
    private final int listViewItemLayoutId;
    private List<ConstructorResult> constructorResults;
    private LayoutInflater layoutInflater;
    public ConstructorAdapter(Context context, int listViewItemLayoutId, List<ConstructorResult> constructorResults) {
        this.listViewItemLayoutId = listViewItemLayoutId;
        this.constructorResults = constructorResults;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return constructorResults.size();
    }
    @Override
    public Object getItem(int position) {
        return constructorResults.get(position);
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
        TextView tvConstructorName = listItemView.findViewById(R.id.constructorName);
        TextView tvConstructorWins = listItemView.findViewById(R.id.constructorWins);
        TextView tvConstructorPoints = listItemView.findViewById(R.id.constructorPoints);
        TextView tvConstructorPosition = listItemView.findViewById(R.id.constructorPositon);
        tvConstructorName.setText(constructorResults.get(position).getConstructor().getName());
        tvConstructorPoints.setText(constructorResults.get(position).getPoints()+" Punkte");
        tvConstructorWins.setText(constructorResults.get(position).getWins()+" Siege");
        tvConstructorPosition.setText(String.valueOf(position+1));
        return listItemView;
    }


}
