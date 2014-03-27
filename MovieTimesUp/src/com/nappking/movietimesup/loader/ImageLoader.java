package com.nappking.movietimesup.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.nappking.movietimesup.R;


import android.os.Handler;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * source--> https://github.com/thest1/LazyList/
 */
public class ImageLoader {
    
    MemoryCache memoryCache=new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, Integer> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, Integer>());
    ExecutorService executorService;
    Handler handler=new Handler();//handler to display images in UI thread
    
    public ImageLoader(Context context){
        fileCache=new FileCache(context);
        executorService=Executors.newFixedThreadPool(5);
    }
    
    final int default_image=R.drawable.filmstripnoimage;
    public void DisplayImage(int id, String url, ImageView imageView, ProgressBar progress){
        imageViews.put(imageView, id);
        Bitmap bitmap=memoryCache.get(id);
        if(bitmap!=null){
            imageView.setImageBitmap(bitmap);
            if(progress!=null){
            	progress.setVisibility(View.INVISIBLE);
            }
        }    
        else{
            queuePhoto(id, url, imageView, progress);
            imageView.setImageResource(default_image);
        }
    }
    
    public Bitmap getImage(int id){
    	Bitmap bitmap=memoryCache.get(id);
        return bitmap;
    }
        
    private void queuePhoto(int id, String url, ImageView imageView, ProgressBar progress){
        PhotoToLoad p=new PhotoToLoad(id, url, imageView, progress);
        executorService.submit(new PhotosLoader(p));
    }
    
    private Bitmap getBitmap(int id, String url) {
        File f=fileCache.getFile(id);
        
        //from SD cache
        Bitmap b = decodeFile(f);
        if(b!=null)
            return b;
        
        //from web
        try {
            Bitmap bitmap=null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            conn.disconnect();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Throwable ex){
           ex.printStackTrace();
           if(ex instanceof OutOfMemoryError)
               memoryCache.clear();
           return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1=new FileInputStream(f);
            BitmapFactory.decodeStream(stream1,null,o);
            stream1.close();
            
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=160;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }
            
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            FileInputStream stream2=new FileInputStream(f);
            Bitmap bitmap=BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //Task for the queue
    private class PhotoToLoad{
    	private int mId;
    	private String mUrl;
    	private ImageView mImage;
    	private ProgressBar mProgress;
        
    	public PhotoToLoad(int id, String url, ImageView image, ProgressBar progress){
        	mId = id;
            mUrl = url; 
            mImage = image;
            mProgress = progress;
        }
    	
    	public int getId(){
    		return mId;
    	}
    	
    	public String getUrl(){
    		return mUrl;
    	}
    	
    	public ImageView getImage(){
    		return mImage;
    	}
    	
    	public ProgressBar getProgress(){
    		return mProgress;
    	}
    }
    
    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
        
        @Override
        public void run() {
            try{
                if(imageViewReused(photoToLoad))
                    return;
                Bitmap bmp=getBitmap(photoToLoad.getId(),photoToLoad.getUrl());
                memoryCache.put(photoToLoad.getId(), bmp);
                if(imageViewReused(photoToLoad))
                    return;
                BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
                handler.post(bd);
            }catch(Throwable th){
                th.printStackTrace();
            }
        }
    }
    
    boolean imageViewReused(PhotoToLoad photoToLoad){
        int tag=imageViews.get(photoToLoad.getImage());
        if(tag!=photoToLoad.getId())
            return true;
        return false;
    }
    
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable{
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        ProgressBar progress;
        ImageView image;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
        public void run(){
        	progress = photoToLoad.getProgress();
        	image = photoToLoad.getImage();
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap!=null){
                image.setImageBitmap(bitmap);
                if(progress!=null){
                	progress.setVisibility(View.INVISIBLE);
                }
            }
            else{
                image.setImageResource(default_image);
                if(progress!=null){
                	progress.setVisibility(View.INVISIBLE);
                }
        	}	
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

}