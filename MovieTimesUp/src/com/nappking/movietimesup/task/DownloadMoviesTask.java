package com.nappking.movietimesup.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import com.facebook.android.friendsmash.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.nappking.movietimesup.HomeActivity;
import com.nappking.movietimesup.SplashActivity;
import com.nappking.movietimesup.database.DBHelper;
import com.nappking.movietimesup.entities.Movie;
import com.nappking.movietimesup.entities.User;
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
	private Context mContext;
	private DBHelper mDBHelper;
	private String mPath = "movies";
	private boolean mService = false;
	private int mFilmCounter = 0;
	
	public DownloadMoviesTask(Context c){
		this.mContext=c;
	}
	
	public DownloadMoviesTask(Context c, boolean isService){
		Log.i(NotificationService.class.toString(), "Check if there are new movies");
		this.mContext=c;
		this.mService = isService;
	}
	
	@Override
	protected void onPostExecute(final Integer result){
		String text="";
		if(!mService){
			//If it was not called from NotificationService
			switch(result){
				case 0: text= "Downloaded new movies!";break;
				case 1: text= "You already have all the movies!";break;
				case 2: text= "Impossible to connect. Check your network!";break;
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
				text= "You got "+mFilmCounter*100+" points and "+mFilmCounter+" new movies to guess";
				Intent intent = new Intent(this.mContext, HomeActivity.class);
        		PendingIntent pIntent = PendingIntent.getActivity(this.mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        		// Construimos la notificaci�n. Podemos tambi�n a�adir acciones
        		NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(this.mContext)
        		        .setContentTitle("Pel�culas!!")
        		        .setContentText(text)
        		        .setLights(0xff0000ff, 100, 100)
        		        .setSmallIcon(R.drawable.movie_notification)
        		        .setContentIntent(pIntent);
        		Notification notification = notBuilder.build();            		  
        		NotificationManager notificationManager = 
        		  (NotificationManager) mContext.getSystemService(NotificationService.NOTIFICATION_SERVICE);

        		// Flag para ocultar la notificaci�n despu�s de su selecci�n y a�adimos sonido, luz y vibraci�n
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
		String read_movies = readMovieFeed(WebServiceTask.URL+mPath);
		try{
			JSONArray jsonContent = new JSONArray(read_movies);
			int numItems = jsonContent.length();
			final Dao<Movie,Integer> movieDao = getHelper().getMovieDAO();
			final Dao<User,Integer> userDao = getHelper().getUserDAO();
			int j= (int) movieDao.countOf();
			User user = userDao.queryForId(0);
			if(j>=numItems){
				result= 1;
			}	
			else{
    			Type listMovieType = new TypeToken<List<Movie>>(){}.getType();
    			final List<Movie> moviesToInsert = new Gson().fromJson(jsonContent.toString(), listMovieType);
    			movieDao.callBatchTasks(new Callable<Void>() {
    			    public Void call() throws Exception {
    			        for (Movie movie : moviesToInsert) {
    			            movieDao.create(movie);
    			        }
    			        return null;
    			    }
    			});
				mFilmCounter=numItems-j;
				result=0;		
				if(user!=null){
					user.setSeconds(user.getSeconds()+mFilmCounter*100);
					userDao.update(user);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = 2;
		}
		return result;
	}
	
	private String readMovieFeed(String url){
	    StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
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
				Log.e(DownloadMoviesTask.class.toString(), "Failed to download file");
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
