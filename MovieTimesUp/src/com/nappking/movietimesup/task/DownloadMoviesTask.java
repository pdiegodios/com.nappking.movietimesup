package com.nappking.movietimesup.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;

import com.nappking.movietimesup.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.nappking.movietimesup.HomeActivity;
import com.nappking.movietimesup.SplashActivity;
import com.nappking.movietimesup.database.DBHelper;
import com.nappking.movietimesup.entities.Movie;
import com.nappking.movietimesup.notifications.NotificationService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Task to download all the new movies when the app start or from NotificationService
 * @author Nappking - pdiego
 */
public class DownloadMoviesTask extends AsyncTask<String,Void,Integer>{
	private final static int CONN_TIMEOUT = 20000;
	private final static int SOCKET_TIMEOUT = 25000;
	private Context mContext;
	private DBHelper mDBHelper;
	private String mCountPath = "movies/count";
	private String mSincePath = "movies/since";
	private boolean mService = false;
	private int mFilmCounter = 0;
	
	public DownloadMoviesTask(Context c){
		this.mContext=c;
	}
	
	public DownloadMoviesTask(Context c, boolean isService){
		this.mContext=c;
		Log.i(NotificationService.class.toString(), mContext.getResources().getString(R.string.check_movies));
		this.mService = isService;
	}
	
	@Override
	protected void onPostExecute(final Integer result){
		String text="";
		if(!mService){
			//If it was not called from NotificationService
			switch(result){
				case 0: text= mContext.getResources().getString(R.string.movies_downloaded);break;
				case 1: text= mContext.getResources().getString(R.string.movies_downloaded_false);break;
				case 2: text= mContext.getResources().getString(R.string.network_error);break;
				default: break;
			}
			//When it finishes, we have to start main menu and finish the splash screen
			Toast.makeText(this.mContext, text, Toast.LENGTH_SHORT).show();
	  		Intent mainActivity = new Intent(this.mContext, HomeActivity.class);
	        mContext.startActivity(mainActivity);
	        ((SplashActivity) this.mContext).finish();
		}
		else{
			//It was called from NotificationService
			if(result==0){ 
				//There are new movies downloaded
				text= mContext.getResources().getString(R.string.you_got)+" "+mFilmCounter*100+" "+
						mContext.getResources().getString(R.string.points)+" "+
						mContext.getResources().getString(R.string.and)+" "+mFilmCounter+" "+
						mContext.getResources().getString(R.string.new_movies_to_guess);
				Intent intent = new Intent(this.mContext, HomeActivity.class);
        		PendingIntent pIntent = PendingIntent.getActivity(this.mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        		// Construimos la notificación. Podemos también añadir acciones
        		NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(this.mContext)
        		        .setContentTitle(mContext.getResources().getString(R.string.movies)+"!!")
        		        .setContentText(text)
        		        .setLights(0xff0000ff, 100, 100)
        		        .setSmallIcon(R.drawable.movie_notification)
        		        .setContentIntent(pIntent);
        		Notification notification = notBuilder.build();            		  
        		NotificationManager notificationManager = 
        		  (NotificationManager) mContext.getSystemService(NotificationService.NOTIFICATION_SERVICE);

        		// Flag para ocultar la notificación después de su selección y añadimos sonido, luz y vibración
        		notification.defaults |= Notification.DEFAULT_SOUND;
        		notification.defaults |= Notification.DEFAULT_VIBRATE;
        		notification.defaults |= Notification.DEFAULT_LIGHTS;
        		notification.flags |= Notification.FLAG_AUTO_CANCEL;
        		
        		notificationManager.notify(NotificationService.MOVIEID, notification);                
			}
			mContext.stopService(new Intent(mContext,NotificationService.class));
		}
        if (mDBHelper != null) {
            OpenHelperManager.releaseHelper();
            mDBHelper = null;
        }
	}
	
	protected Integer doInBackground(String... arg0) {
		int result=2;
		try{
			String counter = readMovieFeed(WebServiceTask.URL+mCountPath);
			int moviesOnlineCount = 0;
			if(counter!=null && !counter.isEmpty()){
				moviesOnlineCount = Integer.parseInt(counter);
			}
			final Dao<Movie,Integer> daoMovie = getHelper().getMovieDAO();
			int moviesDownloadedCount = (int) daoMovie.countOf();
			if(moviesDownloadedCount>=moviesOnlineCount){
				//We have all the movies downloaded
				if(moviesOnlineCount!=0){
					result= 1;
				}
			}	
			else{
				String read_movies = readMovieFeed(WebServiceTask.URL+mSincePath+"/"+moviesDownloadedCount);
				JSONArray jsonContent = new JSONArray(read_movies);
    			Type listMovieType = new TypeToken<List<Movie>>(){}.getType();
    			final List<Movie> moviesToInsert = new Gson().fromJson(jsonContent.toString(), listMovieType);
    			Collections.shuffle(moviesToInsert, new Random(System.nanoTime()));
    			daoMovie.callBatchTasks(new Callable<Void>() {
    			    public Void call() throws Exception {
    			        for (Movie movie : moviesToInsert) {
    			        	daoMovie.create(movie);
    			        }
    			        return null;
    			    }
    			});
				result=0;	
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = 2;
		}
		return result;
	}
	
   // Establish connection and socket (data retrieval) timeouts
   private HttpParams getHttpParams() {         
       HttpParams htpp = new BasicHttpParams();
       HttpConnectionParams.setConnectionTimeout(htpp, CONN_TIMEOUT);
       HttpConnectionParams.setSoTimeout(htpp, SOCKET_TIMEOUT);         
       return htpp;
   }
	
	private String readMovieFeed(String url){
	    StringBuilder builder = new StringBuilder();
		HttpGet httpGet = new HttpGet(url);
		DefaultHttpClient client = new DefaultHttpClient(getHttpParams());
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				Log.e(DownloadMoviesTask.class.toString(), mContext.getResources().getString(R.string.error_download_file));
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

	private DBHelper getHelper(){
		if(mDBHelper==null){
			mDBHelper = OpenHelperManager.getHelper(mContext, DBHelper.class);
		}
		return mDBHelper;
	}
	
}
