package com.nappking.movietimesup.loader;

import java.io.File;
import android.content.Context;
import android.os.Environment;

/**
 * source--> https://github.com/thest1/LazyList/
 */
public class FileCache {
    
    private File cacheDir;
    
    public FileCache(Context context){
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
            cacheDir=new File(Environment.getExternalStorageDirectory()
		            + "/Android/data/"
		            + context.getApplicationContext().getPackageName()
		            + "/Posters"); 
        }else{
            cacheDir=context.getCacheDir();
        }    
        if(!cacheDir.exists()){
            cacheDir.mkdirs();
        }    
    }
    
    public File getFile(int id){
        String filename=String.valueOf(id);
        //String filename = URLEncoder.encode(url);
        File f = new File(cacheDir, filename);
        return f;        
    }
    
    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }

}