package com.nappking.movietimesup.adapter;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nappking.movietimesup.R;
import com.nappking.movietimesup.entities.Cinema;

public class CinemaGridAdapter extends BaseAdapter{	
	private List<Cinema> mCinemas;
	private Context mContext;
    
    public CinemaGridAdapter(Context context, List<Cinema> cinemas) {
        super();
        this.mCinemas = cinemas;
        this.mContext = context;
    }
    
    public List<Cinema> getList(){
    	return mCinemas;
    }

	@Override
	public int getCount() {
		return mCinemas.size();
	}

	@Override
	public Cinema getItem(int position) {
		return mCinemas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return ((Cinema) mCinemas.get(position)).getId();
	}    
    
    static class ViewHolder{
    	ImageView complete;
    	ImageView icon;
    	TextView id;
    	int position;
    }
    
    private void display(final ViewHolder v, final Cinema cinema){
		int id = cinema.getId();
		v.id.setText(id+"");
		v.complete.setVisibility(View.INVISIBLE);
    	if(cinema.isUnlocked()){
    		//Movie was locked and you can see anything about that 
    		int solved = cinema.getSolvedMovies();
    		if(solved>=25){
    			v.complete.setVisibility(View.VISIBLE);
    		}
    		v.icon.setImageResource(getCinemaResource(solved));    		
    	} 
        else {
    		v.icon.setImageResource(R.drawable.cinema_unable);    		
        }    
    }
    
    private int getCinemaResource(int moviesSolved){
		int resource=R.drawable.cinema_enable0;
		switch(moviesSolved){
			case 1: resource=R.drawable.cinema_enable1;break;
			case 2: resource=R.drawable.cinema_enable2;break;
			case 3: resource=R.drawable.cinema_enable3;break;
			case 4: resource=R.drawable.cinema_enable4;break;
			case 5: resource=R.drawable.cinema_enable5;break;
			case 6: resource=R.drawable.cinema_enable6;break;
			case 7: resource=R.drawable.cinema_enable7;break;
			case 8: resource=R.drawable.cinema_enable8;break;
			case 9: resource=R.drawable.cinema_enable9;break;
			case 10: resource=R.drawable.cinema_enable10;break;
			case 11: resource=R.drawable.cinema_enable11;break;
			case 12: resource=R.drawable.cinema_enable12;break;
			case 13: resource=R.drawable.cinema_enable13;break;
			case 14: resource=R.drawable.cinema_enable14;break;
			case 15: resource=R.drawable.cinema_enable15;break;
			case 16: resource=R.drawable.cinema_enable16;break;
			case 17: resource=R.drawable.cinema_enable17;break;
			case 18: resource=R.drawable.cinema_enable18;break;
			case 19: resource=R.drawable.cinema_enable19;break;
			case 20: resource=R.drawable.cinema_enable20;break;
			case 21: resource=R.drawable.cinema_enable21;break;
			case 22: resource=R.drawable.cinema_enable22;break;
			case 23: resource=R.drawable.cinema_enable23;break;
			case 24: resource=R.drawable.cinema_enable24;break;
			case 25: resource=R.drawable.cinema_enable25;break;
			default:break;	
		}
    	
		return resource;
    }

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layout = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layout.inflate(R.layout.item_cinema, null);
            holder = new ViewHolder();
            holder.complete = (ImageView) convertView.findViewById(R.id.isComplete);
            holder.icon = (ImageView) convertView.findViewById(R.id.cinemaButton);
            holder.id = (TextView) convertView.findViewById(R.id.cinema_number);
            convertView.setTag(holder);
        }
        else{
        	holder = (ViewHolder) convertView.getTag();
        }
        
        Cinema cinema = (Cinema) mCinemas.get(position);
        holder.position = position;
        if (cinema != null) {
        	display(holder,cinema);	            
        }
        return convertView;
    }

}
