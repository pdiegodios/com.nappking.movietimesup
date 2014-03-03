package com.nappking.movietimesup.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * Task to download a poster to SDCard or Internal Storage from a specified url
 * @author Nappking - pdiego
 */
public class DownloadPosterTask extends AsyncTask<String, Void, Bitmap> {
	final int IMAGE_MAX_SIZE = 500;
	private int mId;
	private ProgressBar mProgress;
	private ImageView mView;
	private Context mContext;
	private File mPath;

	public DownloadPosterTask(int id, ImageView view, ProgressBar progress, Context context) {
		mId = id;
		mView = view;
		mProgress = progress;
		mContext = context;
        mPath = getExternalFile();
	}

	protected Bitmap doInBackground(String... urls) {
		String urldisplay = urls[0];
		Bitmap bmap = loadImageFromStorage();
		if(bmap==null){
			//TODO: Do this with a Service!!!
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				bmap = BitmapFactory.decodeStream(in);
				saveImageToStorage(bmap);
				bmap = loadImageFromStorage();
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
		}
		return bmap;
	}

	protected void onPostExecute(Bitmap result) {
		if(result!=null){
			if(mView!=null)
				mView.setImageBitmap(result);
			if(mProgress!=null)
				mProgress.setVisibility(View.INVISIBLE);
		}
	}    	
	
	private void saveImageToStorage(Bitmap bitmapImage){
        FileOutputStream fos = null;
        try {           
            fos = new FileOutputStream(mPath);
            //compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 10, fos);
            fos.close();
			Log.i(this.toString(), "new poster: "+mPath.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	private Bitmap loadImageFromStorage(){
		BitmapFactory.Options opts;
		FileInputStream fis;
		int resizeScale;
		Bitmap bmap = null;
		if(mPath!=null && mPath.exists()){
		    try {
		    	opts = new BitmapFactory.Options();
		    	opts.inJustDecodeBounds = true;
		    	fis = new FileInputStream(mPath);
		    	BitmapFactory.decodeStream(fis, null, opts);
				fis.close();
				resizeScale = 1;
				if (opts.outHeight > IMAGE_MAX_SIZE || opts.outWidth > IMAGE_MAX_SIZE) {
					resizeScale = (int)Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(opts.outHeight, opts.outWidth)) / Math.log(0.5)));
				}
				// Load pre-scaled bitmap
				opts = new BitmapFactory.Options();
				opts.inSampleSize = resizeScale;
				fis = new FileInputStream(mPath);
				bmap = BitmapFactory.decodeStream(fis, null, opts);
				fis.close();
		    } 
		    catch (FileNotFoundException e) {
		        e.printStackTrace();
		    }
		    catch (IOException e) {
				e.printStackTrace();
			}
		}
	    return bmap;
	}
	
	/** Create a File for saving in the SDCard */
	private  File getExternalFile(){
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
		    String mImageName=mId+".jpg";
		    mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);  
		}
		return mediaFile;
	} 
	
	/** Create a File for saving in internal storage */
	private  File getInternalFile(){
		File mediaFile = null;
        ContextWrapper cw = new ContextWrapper(mContext.getApplicationContext());
        // path to /data/data/com.nappking.movietimesup/app_data/posters
        File parent_path = cw.getDir("posters", Context.MODE_PRIVATE);
        mediaFile = new File(parent_path ,mId+".jpg");
        
		return mediaFile;
	} 
		
}