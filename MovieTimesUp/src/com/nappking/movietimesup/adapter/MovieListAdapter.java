package com.nappking.movietimesup.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import android.content.Context;
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
	
	private List<Movie> _movies;
	private Context _context;
    
    public MovieListAdapter(Context context, int textViewResourceId, List<Movie> movies) {
        super(context, textViewResourceId, movies);
        this._movies = movies;
        this._context = context;
    }
    
    public List<Movie> getList(){
    	return _movies;
    }
    
    /**
     * cargamos los iconos en la vista según las características de la tarea para su 
     * correcta visualización. Además, se establecen las diferentes acciones sobre 
     * elementos del movieitem.xml
     * @param v : Vista sobre la que se cargan las propiedades de la tarea
     * @param movie : Película visualizada
     */
    private void display(View v, final Movie movie){
    	ImageView iMovie = (ImageView) v.findViewById(R.id.movieButton);
    	ImageView iStar = (ImageView) v.findViewById(R.id.starpoints);
    	TextView txTitle = (TextView) v.findViewById(R.id.title);
    	TextView txPoints = (TextView) v.findViewById(R.id.moviepoints);
        
    	if (iMovie != null) {
    		int points = movie.getPoints();
    		txPoints.setText(points+"");
        	if(movie.isLocked(this._context)) {
        		//Movie was locked and you can see anything about that   
        		iMovie.setImageResource(R.drawable.filmstrip_locked);   
        		iStar.setImageResource(R.drawable.movie_points_grey);
        		txTitle.setVisibility(View.INVISIBLE);
        	}	
            else if (movie.isUnlocked(this._context)){
            	//Movie was unlocked so you can see the poster and see the specific data
        		int id = movie.getId();
        		Bitmap poster = getBitmapPoster(id); 
        		if(poster==null){
        			iMovie.setImageResource(R.drawable.filmstrip);
        			new DownloadPosterTask(id,iMovie, this._context).execute(movie.getPoster());      			
        		}
        		iMovie.setImageBitmap(poster);
        		iStar.setImageResource(R.drawable.movie_points);
        		txTitle.setVisibility(View.VISIBLE);
        		txTitle.setText(movie.getTitle());
            }
            else{
            	//Movie is ready to play
        		iMovie.setImageResource(R.drawable.filmstrip);
        		iStar.setImageResource(R.drawable.movie_points_green);
            	txTitle.setVisibility(View.INVISIBLE);
            }        	
    	}        
    }
    
	private Bitmap getBitmapPoster(long id) {
	    // To be safe, check if the SDCard is mounted
	    File path = null;
		Bitmap bmap = null;
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			File storageDir = new File(Environment.getExternalStorageDirectory()
		            + "/Android/data/"+ this._context.getApplicationContext().getPackageName()
		            + "/Posters"); 
		    // Create the storage directory if it does not exist
		    if (! storageDir.exists()){
		        if (! storageDir.mkdirs()){
		            return null;
		        }
		    } 
		    String mImageName=id+".jpg";
		    path = new File(storageDir.getPath() + File.separator + mImageName);  
		    
			if(path.exists()){
			    try {
			        bmap = BitmapFactory.decodeStream(new FileInputStream(path));
			    } 
			    catch (FileNotFoundException e) {
			        e.printStackTrace();
			    }
			}
		}
	    return bmap;
	}

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater layout = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layout.inflate(R.layout.movieitem, null);
        }
        Movie movie = (Movie) _movies.get(position);
        if (movie != null) {
        	display(view,movie);	            
        }
        return view;
    }    

}
