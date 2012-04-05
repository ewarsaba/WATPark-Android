package com.watpark.android.uielements;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.watpark.android.LotData;
import com.watpark.android.R;
import com.watpark.android.Util;
import com.watpark.android.models.Lot;

/*
 * List Adapter for main page
 */
public class LotListAdapter extends ArrayAdapter<Integer>
{
	private Activity context;
	private Integer[] lotIDs;

	public LotListAdapter(Activity context, Integer[] lotIDs)
	{
		super(context, R.layout.rowlayout, lotIDs);
		this.context = context;
		this.lotIDs = lotIDs;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{

		// get lot from session map
		Lot lot = LotData.getLots().get(lotIDs[position]);

		// create row view
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.rowlayout, null, true);

		// set lot name
		TextView textView = (TextView) rowView.findViewById(R.id.label);
		textView.setText("Lot " + lot.getName());

		// set lot icon
		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		imageView.setImageDrawable(Util.getImage(lot.getName(), context));

		// set lot count progress bar
		TextProgressBar progressBar = (TextProgressBar) rowView
				.findViewById(R.id.progress);

		if (lot.getLatestCount() == -1)
		{
			progressBar.setVisibility(View.GONE);
		}
		else
		{
			progressBar.setLot(lot);
			progressBar.setVisibility(View.VISIBLE);
		}
		return rowView;
	}

}