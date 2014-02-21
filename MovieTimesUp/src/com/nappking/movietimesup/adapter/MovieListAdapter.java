package com.nappking.movietimesup.adapter;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.android.friendsmash.R;
import com.nappking.movietimesup.entities.Movie;
import com.nappking.movietimesup.task.DownloadPosterTask;

public class MovieListAdapter extends ArrayAdapter<Movie>{
	
	private List<Movie> mMoviesList;
	private Context mContext;
    
    public MovieListAdapter(Context context, int textViewResourceId, List<Movie> movies) {
        super(context, textViewResourceId, movies);
        this.mMoviesList = movies;
        this.mContext = context;
    }
    
    public List<Movie> getList(){
    	return mMoviesList;
    }
    
    public void reload(List<Movie> movies) {
    	this.mMoviesList.clear();
    	this.mMoviesList.addAll(movies);
    	notifyDataSetChanged();
    }
    
    static class ViewHolder{
    	ImageView poster;
    	ImageView coin;
    	TextView title;
    	TextView points;
    	int position;
    }
    
    /**
     * cargamos los iconos en la vista según las características de la tarea para su 
     * correcta visualización. Además, se establecen las diferentes acciones sobre 
     * elementos del movieitem.xml
     * @param v : Vista sobre la que se cargan las propiedades de la tarea
     * @param movie : Película visualizada
     */
    private void display(ViewHolder v, final Movie movie){
    	 
    	if (v.poster != null) {
    		int points = movie.getPoints();
    		v.points.setText(points+"");
        	if(movie.isLocked(this.mContext)) {
        		//Movie was locked and you can see anything about that   
        		v.poster.setImageResource(R.drawable.filmstrip_locked);   
        		v.coin.setImageResource(R.drawable.movie_points_grey);
        		v.title.setVisibility(View.INVISIBLE);
        	}	
            else if (movie.isUnlocked(this.mContext)){
            	//Movie was unlocked so you can see the poster and see the specific data
        		int id = movie.getId();
        		v.poster.setImageResource(R.drawable.filmstrip);
        		new DownloadPosterTask(id,v.poster, this.mContext).execute(movie.getPoster()); 
        		v.coin.setImageResource(R.drawable.movie_points);
        		v.title.setVisibility(View.VISIBLE);
        		v.title.setText(movie.getTitle());
            }
            else{
            	//Movie is ready to play
        		v.poster.setImageResource(R.drawable.filmstrip);
        		v.coin.setImageResource(R.drawable.movie_points_green);
            	v.title.setVisibility(View.INVISIBLE);
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
        if (movie != null) {
        	display(holder,movie);	            
        }
        return convertView;
    }    

}
