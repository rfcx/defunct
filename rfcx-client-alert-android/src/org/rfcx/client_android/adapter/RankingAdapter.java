package org.rfcx.client_android.adapter;

import java.util.ArrayList;

import org.rfcx.client_android.R;
import org.rfcx.client_android.model.Event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class RankingAdapter extends ArrayAdapter<Event> {

	public RankingAdapter(Context context, int resource, ArrayList<Event> radios) {
		super(context, resource, radios);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	
		LayoutInflater inflater = (LayoutInflater) this.getContext()
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View rowView = inflater.inflate(R.layout.adapter_radio_rankingitem, parent, false);
		
		Event radio = getItem(position);
		return rowView;
	}
	
	public void addAll(ArrayList<Event> radios) {
		for(int i=0; i<radios.size(); i++) {
			insert(radios.get(i), getCount());
		}
	}
}
