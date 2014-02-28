package com.nappking.movietimesup.adapter;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.android.friendsmash.R;
import com.nappking.movietimesup.entities.Movie;
import com.nappking.movietimesup.task.DownloadPosterTask;

public class MovieListAdapter extends BaseAdapter{
	
	private List<Movie> mMoviesList;
	private List<String> mLocked;
	private List<String> mUnlocked;
	private Context mContext;
    
    public MovieListAdapter(Context context, List<Movie> movies, 
    		List<String> locked, List<String> unlocked) {
        super();
        this.mMoviesList = movies;
        this.mLocked = locked;
        this.mUnlocked = unlocked;
        this.mContext = context;
    }
    
    public List<Movie> getList(){
    	return mMoviesList;
    }

	@Override
	public int getCount() {
		return mMoviesList.size();
	}

	@Override
	public Movie getItem(int position) {
		return mMoviesList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return ((Movie) mMoviesList.get(position)).getId();
	}    
    
    public void reload(List<Movie> movies, List<String> locked, List<String> unlocked) {
    	this.mMoviesList.clear();
    	this.mMoviesList.addAll(movies);
    	this.mLocked = locked;
    	this.mUnlocked = unlocked;
    	notifyDataSetChanged();
    }
    
    static class ViewHolder{
    	ProgressBar progress;
    	ImageView poster;
    	ImageView coin;
    	TextView title;
    	TextView points;
    	int position;
    }
    
    /**
     * Load icons depending of the characteristics, stablish actions over elements
     * in movieitem.xml
     * @param v : view where to load the properties of the task
     * @param movie : movie item in the gridview
     */
    private void display(final ViewHolder v, final Movie movie){    	 
    	if (v != null) {
    		int points = movie.getPoints();
    		v.points.setText(points+"");
    		v.title.setVisibility(View.INVISIBLE);
    		v.progress.setVisibility(View.INVISIBLE);
    		int id = movie.getId();
        	if(mLocked.contains(id+"")){
        		//Movie was locked and you can see anything about that  
        		v.poster.setImageResource(R.drawable.filmstrip_locked);  
        		v.coin.setImageResource(R.drawable.movie_points_grey);
        	}	
            else if(!mUnlocked.contains(id+"")){
            	//Movie is ready to play
        		v.poster.setImageResource(R.drawable.filmstrip);
        		v.coin.setImageResource(R.drawable.movie_points_green);
            }    
            else {
            	//Movie was unlocked so you can see the poster and see the specific data
        		//int id = movie.getId();    	
    			v.progress.setVisibility(View.VISIBLE);            		
        		v.poster.setImageResource(R.drawable.filmstrip);
        		new DownloadPosterTask(id,v.poster,v.progress, this.mContext).execute(movie.getPoster()); 
    			v.coin.setImageResource(R.drawable.movie_points);
        		v.title.setVisibility(View.VISIBLE);
        		v.title.setText(movie.getTitle());
            }    	
    	}        
    }

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layout = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layout.inflate(R.layout.movieitem, null);
            holder = new ViewHolder();
            holder.progress = (ProgressBar) convertView.findViewById(R.id.progress);
            holder.poster = (ImageView) convertView.findViewById(R.id.movieButton);
            holder.coin = (ImageView) convertView.findViewById(R.id.starpoints);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.points = (TextView) convertView.findViewById(R.id.moviepoints);
            convertView.setTag(holder);
        }
        else{
        	holder = (ViewHolder) convertView.getTag();
        }
        
        Movie movie = (Movie) mMoviesList.get(position);
        holder.position = position;
        if (movie != null) {
        	display(holder,movie);	            
        }
        return convertView;
    }

}
