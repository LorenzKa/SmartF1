package net.htlgrieskirchen.smartf1.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.htlgrieskirchen.smartf1.Beans.Track;
import net.htlgrieskirchen.smartf1.R;

import java.util.List;

public class TrackAdapter extends BaseAdapter {
    private final int listViewItemLayoutId;
    private List<Track> track;
    private LayoutInflater layoutInflater;
    public TrackAdapter(Context context, int listViewItemLayoutId, List<Track> track) {
        this.listViewItemLayoutId = listViewItemLayoutId;
        this.track = track;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return track.size();
    }
    @Override
    public Object getItem(int position) {
        return track.get(position);
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
        TextView tvCountry = listItemView.findViewById(R.id.circuitCountry);
        TextView tvName = listItemView.findViewById(R.id.circuitName);
        tvName.setText(track.get(position).getCircuitName());
        tvCountry.setText(track.get(position).getLocation().getCountry());
        return listItemView;
    }
}
