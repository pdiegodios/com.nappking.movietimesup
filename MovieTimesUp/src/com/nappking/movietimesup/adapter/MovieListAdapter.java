package com.nappking.movietimesup.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nappking.movietimesup.R;
import com.nappking.movietimesup.entities.Movie;
import com.nappking.movietimesup.loader.ImageLoader;

public class MovieListAdapter extends ArrayAdapter<Movie>{
	private List<Movie> mMovies;
	private Context mContext;
    public ImageLoader imageLoader; 
    
    public MovieListAdapter(Context context, int textViewResourceId, List<Movie> movies) {
        super(context, textViewResourceId, movies);
        this.mMovies = movies;
        this.mContext = context;
        imageLoader=new ImageLoader(mContext.getApplicationContext());
    }
    
    public List<Movie> getList(){
    	return mMovies;
    }
        
    private void display(final ViewHolder v, final Movie movie){
		v.title.setText(movie.getTitle());
		v.info.setText(movie.getCountry()+","+movie.getYear()+" | "+movie.getGenre());
		v.progress.setVisibility(View.VISIBLE);      
        imageLoader.DisplayImage(movie.getId(), movie.getPoster(), v.poster, v.progress);
        v.extra.setVisibility(View.INVISIBLE);
        if(movie.getCult()){
        	v.extra.setImageResource(R.drawable.cult_movie);
            v.extra.setVisibility(View.VISIBLE);
        }
        else if(movie.getMasterpiece()){
        	v.extra.setImageResource(R.drawable.masterpiece);
            v.extra.setVisibility(View.VISIBLE);
        }
    }  

    static class ViewHolder{
    	ProgressBar progress;
    	ImageView poster;
    	ImageView extra;
    	TextView title;
    	TextView info;
    	int position;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layout = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layout.inflate(R.layout.item_film_searchable, null);
            holder = new ViewHolder();
            holder.progress = (ProgressBar) convertView.findViewById(R.id.progress);
            holder.poster = (ImageView) convertView.findViewById(R.id.poster);
            holder.extra = (ImageView) convertView.findViewById(R.id.extra);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.info = (TextView) convertView.findViewById(R.id.info);
            convertView.setTag(holder);
        }
        else{
        	holder = (ViewHolder) convertView.getTag();
        }
        
        Movie movie = (Movie) mMovies.get(position);
        holder.position = position;
        if (movie != null) {
        	display(holder,movie);	            
        }
        return convertView;
    }    

}
