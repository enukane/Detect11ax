package org.glenda9.detect11ax;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class Dot11axInfoAdapter extends ArrayAdapter<Dot11axInfo> {
    private List<Dot11axInfo> items;
    private LayoutInflater inflater;
    private int resource;

    public Dot11axInfoAdapter(Context context, int resource, List<Dot11axInfo> items) {
        super(context, resource, items);
        this.resource = resource;
        this.items = items;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView != null) {
            view = convertView;
        } else {
            view = inflater.inflate(resource, null);
        }

        Dot11axInfo axInfo = items.get(position);

        setTextView(view, R.id.ap_bssid, axInfo.bssid);
        setTextView(view, R.id.ap_ssid, axInfo.ssid);

        return view;
    }

    private void setTextView(View view, int id, String text) {
        TextView tv = view.findViewById(id);
        tv.setText(text);
    }
}
