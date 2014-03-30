package com.nappking.movietimesup.adapter;

import java.util.List;

import com.facebook.widget.ProfilePictureView;
import com.nappking.movietimesup.R;
import com.nappking.movietimesup.ScoreboardEntry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ScoreboardEntryAdapter extends BaseAdapter {
	private List<ScoreboardEntry> mEntries;
	private String mUserId;
	private Context mContext;

    public ScoreboardEntryAdapter(Context context, List<ScoreboardEntry> entries, String userId) {
        this.mEntries = entries;
        this.mUserId = userId;
        this.mContext = context;
    }
    
	@Override
	public int getCount() {
		return mEntries.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mEntries.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return Long.valueOf(mEntries.get(arg0).getId());
	}	

    static class ViewHolder{
    	ProfilePictureView picture;
    	TextView name;
    	TextView points;
    	int position;
    }
    
	@Override	
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layout = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layout.inflate(R.layout.scoreboard_entry, null);
            holder = new ViewHolder();
            holder.picture = (ProfilePictureView) convertView.findViewById(R.id.userImage);
            holder.name = (TextView) convertView.findViewById(R.id.userName);
            holder.points = (TextView) convertView.findViewById(R.id.userPoints);
            convertView.setTag(holder);
        }
        else{
        	holder = (ViewHolder) convertView.getTag();
        }
        
        ScoreboardEntry entry = (ScoreboardEntry) mEntries.get(position);
        holder.position = position;
        if (entry != null) {
        	if(entry.getId().equals(mUserId)){
        		convertView.setBackgroundResource(android.R.drawable.editbox_dropdown_light_frame);
        		int white = mContext.getResources().getColor(R.color.white);
        		holder.name.setTextColor(white);
        		holder.points.setText(white);
        	}
        	holder.picture.setProfileId(entry.getId());
        	holder.name.setText(position+2+". "+entry.getName());
        	holder.points.setText(mContext.getResources().getString(R.string.points)+": "+ entry.getScore());
        }
        return convertView;
    }

}
