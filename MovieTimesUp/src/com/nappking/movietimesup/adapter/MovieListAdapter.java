package com.nappking.movietimesup.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
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
	private int mIdMovie=-1;
	private Context mContext;
    
    public MovieListAdapter(Context context, int textViewResourceId, List<Movie> movies) {
        super(context, textViewResourceId, movies);
        this.mMoviesList = movies;
        this.mContext = context;
    }
    
    public List<Movie> getList(){
    	return mMoviesList;
    }
    
    public void reload(List<Movie> movies, int idMovie) {
    	this.mMoviesList.clear();
    	this.mMoviesList.addAll(movies);
    	this.mIdMovie = idMovie;
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
     * Load icons depending of the characteristics, stablish actions over elements
     * in movieitem.xml
     * @param v : view where to load the properties of the task
     * @param movie : movie item in the gridview
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
        		if(this.mIdMovie==-1 || this.mIdMovie == id){
        			Bitmap bmap = loadImageFromStorage(id);
        			if(bmap!=null){
        				v.poster.setImageBitmap(bmap);
        			}
        			else{
		        		v.poster.setImageResource(R.drawable.filmstrip);
		        		new DownloadPosterTask(id,v.poster, this.mContext).execute(movie.getPoster()); 
        			}
	        		v.coin.setImageResource(R.drawable.movie_points);
        		}
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
            holder.position = position;
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
		
	private Bitmap loadImageFromStorage(int id){
		Bitmap bmap = null;
		File path = getExternalFile(id);
		if(path==null){
			path = getInternalFile(id);
		}
		if(path!=null && path.exists()){
		    try {
		        bmap = BitmapFactory.decodeStream(new FileInputStream(path));
		    } 
		    catch (FileNotFoundException e) {
		        e.printStackTrace();
		    }
		}
	    return bmap;
	}
	
	private  File getExternalFile(int id){
	    // To be safe, check if the SDCard is mounted
	    File mediaFile = null;
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
		            + "/Android/data/"
		            + mContext.getApplicationContext().getPackageName()
		            + "/Posters"); 
		    // This location works best if you want the created images to be shared
		    // between applications and persist after your app has been uninstalled
			
		    // Create the storage directory if it does not exist
		    if (! mediaStorageDir.exists()){
		        if (! mediaStorageDir.mkdirs()){
		            return null;
		        }
		    } 
		    String mImageName=id+".jpg";
		    mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);  
		}
		return mediaFile;
	} 
	
	private  File getInternalFile(int id){
		File mediaFile = null;
        ContextWrapper cw = new ContextWrapper(mContext.getApplicationContext());
        // path to /data/data/com.nappking.movietimesup/app_data/posters
        File parent_path = cw.getDir("posters", Context.MODE_PRIVATE);
        mediaFile = new File(parent_path, id+".jpg");
        
		return mediaFile;
	} 

}
