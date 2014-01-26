package com.nappking.movietimesup.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.facebook.android.friendsmash.R;
import com.google.gson.Gson;
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
	//private ProgressDialog _dialog;
	private Context _context;
	private String _path = "movies";
	boolean _service = false;
	int _filmCounter = 0;
	
	public DownloadMoviesTask(Context c){
		this._context=c;
		/**if(this._dialog==null)
			this._dialog= new ProgressDialog(_context);*/
	}
	
	public DownloadMoviesTask(Context c, boolean isService){
		Log.i(NotificationService.class.toString(), "Check if there are new movies");
		this._context=c;
		this._service = isService;
	}
	
	protected void onPreExecute(){
		/**this._dialog.setMessage("Downloading movies from WS");
		if (!_dialog.isShowing())
			_dialog.show();*/
	}
	
	@Override
	protected void onPostExecute(final Integer result){
		/**if(_dialog.isShowing()){
			_dialog.dismiss();
		}*/
		String text="";
		if(!_service){
			//If it was not called from NotificationService
			switch(result){
			case 0: text= "Downloaded new movies!";break;
			case 1: text= "You already have all the movies!";break;
			case 2: text= "Impossible to connect. Check your network!";break;
			default: break;
			}
			//When it finishes, we have to start main menu and finish the splash screen
			Toast.makeText(this._context, text, Toast.LENGTH_SHORT).show();
	  		Intent mainActivity = new Intent(this._context, HomeActivity.class);
	        _context.startActivity(mainActivity);
	        ((SplashActivity) this._context).finish();
		}
		else{
			//It was called from NotificationService
			if(result==0){ 
				//There are new movies downloaded
				text= "You got "+_filmCounter*100+" points and "+_filmCounter+" new movies to guess";
				Intent intent = new Intent(this._context, HomeActivity.class);
        		PendingIntent pIntent = PendingIntent.getActivity(this._context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        		// Construimos la notificación. Podemos también añadir acciones
        		NotificationCompat.Builder notBuilder = new NotificationCompat.Builder(this._context)
        		        .setContentTitle("Películas!!")
        		        .setContentText(text)
        		        .setLights(0xff0000ff, 100, 100)
        		        .setSmallIcon(R.drawable.movie_notification)
        		        .setContentIntent(pIntent);
        		Notification notification = notBuilder.build();            		  
        		NotificationManager notificationManager = 
        		  (NotificationManager) _context.getSystemService(NotificationService.NOTIFICATION_SERVICE);

        		// Flag para ocultar la notificación después de su selección y añadimos sonido, luz y vibración
        		notification.defaults |= Notification.DEFAULT_SOUND;
        		notification.defaults |= Notification.DEFAULT_VIBRATE;
        		notification.defaults |= Notification.DEFAULT_LIGHTS;
        		notification.flags |= Notification.FLAG_AUTO_CANCEL;
        		
        		notificationManager.notify(NotificationService.MOVIEID, notification);                
			}
			_context.stopService(new Intent(_context,NotificationService.class));
		}
	}
	
	protected Integer doInBackground(String... arg0) {
		int result=2;
		String read_movies = readMovieFeed(WebServiceTask.URL+_path);
		try{
			JSONArray jsonContent = new JSONArray(read_movies);
			int numItems = jsonContent.length();
			Gson gson = new Gson();
			DBHelper helper = OpenHelperManager.getHelper(this._context, DBHelper.class);
			Dao<Movie,Integer> movieDao = helper.getMovieDAO();
			Dao<User,Integer> userDao = helper.getUserDAO();
			int j=movieDao.queryForAll().size();
			List<User> users = userDao.queryForAll();
			if(j>=numItems){
				result= 1;
			}	
			else{
				for(int i=j; i<numItems;i++){
					JSONObject itemjson = jsonContent.getJSONObject(i);
					Movie movie = gson.fromJson(itemjson.toString(), Movie.class);
					movieDao.create(movie);	
					//If you want to download the poster
	    			new DownloadPosterTask(movie.getId(),null, this._context).execute(movie.getPoster());  
				}
				_filmCounter=numItems-j;
				result=0;		
				if(!users.isEmpty()){
					User user = users.get(0);
					user.setSeconds(user.getSeconds()+_filmCounter*100);
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

}
