package com.nappking.movietimesup.adapter;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nappking.movietimesup.R;
import com.nappking.movietimesup.entities.Movie;
import com.nappking.movietimesup.loader.ImageLoader;

public class MovieGridAdapter extends BaseAdapter{
	
	private List<Movie> mMoviesList;
	private List<String> mLocked;
	private List<String> mUnlocked;
	private Context mContext;
    public ImageLoader imageLoader; 
    
    public MovieGridAdapter(Context context, List<Movie> movies, List<String> locked, List<String> unlocked) {
        super();
        this.mMoviesList = movies;
        this.mLocked = locked;
        this.mUnlocked = unlocked;
        this.mContext = context;
        imageLoader=new ImageLoader(mContext.getApplicationContext());
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
    
    public void setValues(List<String> locked, List<String> unlocked) {
    	this.mLocked = locked;
    	this.mUnlocked = unlocked;
    }
    
    static class ViewHolder{
    	ProgressBar progress;
    	ImageView poster;
    	ImageView mark;
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
		int points = movie.getPoints();
		v.points.setText(points+"");
		v.title.setVisibility(View.INVISIBLE);
		v.progress.setVisibility(View.INVISIBLE);
		int id = movie.getId();
    	if(mLocked.contains(id+"")){
    		//Movie was locked and you can see anything about that  
    		v.poster.setImageResource(R.drawable.filmstrip_locked);  
    		v.mark.setImageResource(R.drawable.bookmark_disable);
    	}	
        else if(!mUnlocked.contains(id+"")){
        	//Movie is ready to play
    		v.poster.setImageResource(R.drawable.filmstrip);
    		v.mark.setImageResource(R.drawable.bookmark_enable);
        }    
        else {
        	//Movie was unlocked so you can see the poster and see the specific data  
			v.progress.setVisibility(View.VISIBLE);            		
            imageLoader.DisplayImage(id, movie.getPoster(), v.poster, v.progress);
    		v.mark.setImageResource(R.drawable.bookmark);
    		v.title.setVisibility(View.VISIBLE);
    		v.title.setText(movie.getTitle());
        }    
    }

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layout = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layout.inflate(R.layout.item_film, null);
            holder = new ViewHolder();
            holder.progress = (ProgressBar) convertView.findViewById(R.id.progress);
            holder.poster = (ImageView) convertView.findViewById(R.id.movieButton);
            holder.mark = (ImageView) convertView.findViewById(R.id.starpoints);
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
