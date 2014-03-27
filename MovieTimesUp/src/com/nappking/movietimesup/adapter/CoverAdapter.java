package com.nappking.movietimesup.adapter;

import java.util.List;

import com.nappking.movietimesup.R;
import com.nappking.movietimesup.entities.Movie;
import com.nappking.movietimesup.loader.ImageLoader;
import com.nappking.movietimesup.widget.CoverFlow;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class CoverAdapter extends BaseAdapter {
	private List<Movie> mMovies;
	private Context mContext;
    public ImageLoader imageLoader; 
    private final int mWidth;
    private final int mHeight;

    public CoverAdapter(Context context, List<Movie> movies) {
        this.mMovies = movies;
        this.mContext = context;
        this.mWidth = (int) (mContext.getResources().getDimension(R.dimen.search_cover_width) / mContext.getResources().getDisplayMetrics().density); 
        this.mHeight = (int) (mContext.getResources().getDimension(R.dimen.search_cover_height) / mContext.getResources().getDisplayMetrics().density);
        imageLoader=new ImageLoader(mContext.getApplicationContext());
    }
    
    public void setList(List<Movie> movies){
    	this.mMovies = movies;
    }
    
	@Override
	public int getCount() {
		return mMovies.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mMovies.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return mMovies.get(arg0).getId();
	}	
		
	@Override	
    public View getView(int position, View convertView, ViewGroup parent) {
		Movie movie = mMovies.get(position);
        ImageView image = new ImageView(mContext);
        image.setLayoutParams(new CoverFlow.LayoutParams(mWidth,mHeight));
        image.setScaleType(ImageView.ScaleType.FIT_XY);
		imageLoader.DisplayImage(movie.getId(), movie.getPoster(), image, null);
        BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
        drawable.setAntiAlias(true);
        return image;
       }
     /** Returns the size (0.0f to 1.0f) of the views 
        * depending on the 'offset' to the center. */ 
    public float getScale(boolean focused, int offset) { 
        return Math.max(0, 1.0f / (float)Math.pow(2, Math.abs(offset))); 
    } 

}
