package com.nappking.movietimesup.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.friendsmash.R;
import com.google.gson.Gson;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.nappking.movietimesup.database.DBHelper;
import com.nappking.movietimesup.entities.User;

public class LoadUserDataTask extends AsyncTask<String,Void,Integer>{
	private final static String ERROR = "ERROR";
	private final static int CONN_TIMEOUT = 7000;
	private final static int SOCKET_TIMEOUT = 10000;
	
	private ProgressDialog mDialog;
	private Context mContext;
	private DBHelper mDBHelper;
	private String mPath="users";
	private User mUser=null;
	boolean mService = false;
	boolean mCheckExtraSeconds;
	
	public LoadUserDataTask(Context c, User u, boolean showProgress){
		this.mContext=c;
		this.mCheckExtraSeconds=showProgress;
		if(this.mDialog==null)
			this.mDialog= new ProgressDialog(mContext);
		this.mUser=u;
	}
	
	@Override
	protected void onPreExecute() {
		if(mDialog!=null){
			mDialog.setMessage(mContext.getResources().getString(R.string.check_user));
			if (!mDialog.isShowing() && mCheckExtraSeconds)
				mDialog.show();
		}
	}
	
	@Override
	protected void onPostExecute(final Integer result){
		if(mDialog!=null && mDialog.isShowing()){
			mDialog.dismiss();
		}
		String text="";
		switch(result){
			case 1: 
				text= mContext.getResources().getString(R.string.uploaded_user);
				break;
			case 2: 
				text= mContext.getResources().getString(R.string.downloaded_user);
				break;
			case 3: 
				text= mContext.getResources().getString(R.string.already_updated_user);
				break;
			case 4: 
				text= mContext.getResources().getString(R.string.error_updating_user);
				break;
			default: 
				text= mContext.getResources().getString(R.string.network_error);
				break;
		}
        if (mDBHelper != null) {
            OpenHelperManager.releaseHelper();
            mDBHelper = null;
        }
		Toast.makeText(this.mContext, text, Toast.LENGTH_SHORT).show();
		
		if(mCheckExtraSeconds && mUser!=null){
			checkForSeconds();
		}
	}

	protected Integer doInBackground(String... arg0) {
		int result = 0;
		String read_user = readUserFeed(WebServiceTask.URL+mPath);
		try{
			if((read_user==null || read_user.equals("")) && mUser!=null){
				//If there is not user associated in the server we have to create a new user in the WS
				List<User> users = new ArrayList<User>();
				users.add(this.mUser);
				sendUser("SAVE");	
				result=1;		
			}
			else{
				//Read the user from WS
				JSONObject itemjson = new JSONObject(read_user);
				Log.i("User from WS: ", itemjson.toString());
				User user = new Gson().fromJson(itemjson.toString(), User.class);
				if(mUser!=null && user!=null){
					if(user.getLastUpdate()==mUser.getLastUpdate()){
						result = 3;
					}
					else if(user.getLastUpdate()>mUser.getLastUpdate()){
						Log.i("UPDATE", "ACTUALIZADO!!");
						//If user from WS was updated after user from Database
						mUser.setUnlockedMovies(user.getUnlockedMovies());
						mUser.setLockedMovies(user.getLockedMovies());
						mUser.setScore(user.getScore());
						mUser.setSeconds(user.getSeconds());
						mUser.setMovies(user.getMovies());
						mUser.setDays(user.getDays());
						mUser.setLastUpdate(user.getLastUpdate());
						mUser.setLastForeground(user.getLastForeground());
						getHelper().getUserDAO().update(mUser);
						result = 2;
					}
					else{
						Log.i("UPDATE", "Subimos a WebService");
						//If user from Local DB was updated after user from WS
						sendUser("UPDATE");
						result = 1;
					}
				}
				else{
					result = 4;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
    
	private void sendUser(String action){
		try {
			WebServiceTask wsUser = new WebServiceTask(WebServiceTask.POST_TASK);
			List<User> users = new ArrayList<User>();
			users.add(mUser);
			JSONArray jsonArray = new JSONArray(new Gson().toJson(users));
			wsUser.addNameValuePair("users", jsonArray.toString());
			Log.i(action, jsonArray.toString());
	        wsUser.addNameValuePair("action", action);        
	        wsUser.execute(new String[] {WebServiceTask.URL+mPath});	   
		}catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	// Establish connection and socket (data retrieval) timeouts
	private HttpParams getHttpParams() {         
		HttpParams htpp = new BasicHttpParams();
       	HttpConnectionParams.setConnectionTimeout(htpp, CONN_TIMEOUT);
       	HttpConnectionParams.setSoTimeout(htpp, SOCKET_TIMEOUT);         
       	return htpp;
	}
	
	private String readUserFeed(String url){
	    StringBuilder builder = new StringBuilder();
		HttpGet httpGet = new HttpGet(url+"/"+this.mUser.getUser());
		DefaultHttpClient client = new DefaultHttpClient(getHttpParams());
        HttpResponse response = null;
		try {
			response = client.execute(httpGet);
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
				return builder.toString();
			} else if(statusCode == 500){
				Log.e(LoadUserDataTask.class.toString(), mContext.getResources().getString(R.string.error_user_not_found));
			} else {
				Log.e(LoadUserDataTask.class.toString(), mContext.getResources().getString(R.string.error_download_file));
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ERROR;
	}

	private void checkForSeconds(){
		try{
			Calendar now = GregorianCalendar.getInstance();
			Calendar lastForeground=GregorianCalendar.getInstance();
			lastForeground.setTimeInMillis(mUser.getLastForeground());
			Log.i("DATE", lastForeground.get(Calendar.DATE)+"/"+lastForeground.get(Calendar.MONTH));
			boolean updated=false;
			
			//EXTRA SECONDS FOR FIDELITY
			if(lastForeground.get(Calendar.DAY_OF_YEAR) != now.get(Calendar.DAY_OF_YEAR) || lastForeground.get(Calendar.YEAR) != now.get(Calendar.YEAR)){
				Log.i("LAST_ENTRY", "DIFFERENT DAY");
				//If it's a different day
				lastForeground.add(Calendar.DATE, 1);
				if(lastForeground.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) && lastForeground.get(Calendar.YEAR) == now.get(Calendar.YEAR)){
					Log.i("LAST_ENTRY", "YESTERDAY");
					//The user entered yesterday to the game
					mUser.setDays(mUser.getDays()+1);
				}
				else{
					Log.i("LAST_ENTRY", "NOT TODAY & NOT YESTERDAY");
					//The user didn't enter yesterday or today before now, so the days in row will be reset
					mUser.setDays(0);
				}					
				int days = mUser.getDays();
				int secondsExtra=0;
				switch(days){
					case 0: secondsExtra = 20; break;
					case 1: secondsExtra = 40; break;
					case 2: secondsExtra = 60; break;
					default: secondsExtra = 100; break;
				}
				mUser.setSeconds(mUser.getSeconds()+secondsExtra);
				mUser.setLastUpdate(System.currentTimeMillis());
				mUser.setLastForeground(System.currentTimeMillis());
				showExtra(secondsExtra);
				updated=true;
			}

			//SECONDS FOR NEW MOVIES
			int totalMovies=(int) getHelper().getMovieDAO().countOf();
			if(totalMovies>mUser.getMovies()){
				int secondsForMovies = (totalMovies - mUser.getMovies())*100;
				mUser.setSeconds(mUser.getSeconds()+secondsForMovies);
				mUser.setMovies(totalMovies);
				updated=true;
			}
			
			if(updated){
				getHelper().getUserDAO().update(mUser);
				sendUser("UPDATE");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void showExtra(int seconds){    	
        final Dialog dialog = new Dialog(mContext, R.style.SlideDialog);
        dialog.setContentView(R.layout.clapperdialogbonus);
        dialog.setCancelable(false);
        //instantiate elements in the dialog
        Button okButton = (Button) dialog.findViewById(R.id.actionButton);
		TextView points = (TextView) dialog.findViewById(R.id.points);	
		TextView day1 = (TextView) dialog.findViewById(R.id.day1);	
		TextView day2 = (TextView) dialog.findViewById(R.id.day2);	
		TextView day3 = (TextView) dialog.findViewById(R.id.day3);	
		TextView day4 = (TextView) dialog.findViewById(R.id.day4);	
		ImageView fidelity = (ImageView) dialog.findViewById(R.id.imagebonus);	
		day1.setBackgroundColor(mContext.getResources().getColor(R.color.greenlight));
		day1.setTextColor(mContext.getResources().getColor(R.color.black));	
		day2.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
		day2.setTextColor(mContext.getResources().getColor(R.color.greydark));
		day3.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
		day3.setTextColor(mContext.getResources().getColor(R.color.greydark));
		day4.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
		day4.setTextColor(mContext.getResources().getColor(R.color.greydark));
		if(seconds>=40){
			day2.setBackgroundColor(mContext.getResources().getColor(R.color.greenlight));
			day2.setTextColor(mContext.getResources().getColor(R.color.black));	
			if(seconds>=60){
				day3.setBackgroundColor(mContext.getResources().getColor(R.color.greenlight));
				day3.setTextColor(mContext.getResources().getColor(R.color.black));	
				if(seconds>=100){
					day4.setBackgroundColor(mContext.getResources().getColor(R.color.greenlight));
					day4.setTextColor(mContext.getResources().getColor(R.color.black));	
					fidelity.setImageDrawable(mContext.getResources().getDrawable(R.drawable.fidelity_4));
				}
				else{
					fidelity.setImageDrawable(mContext.getResources().getDrawable(R.drawable.fidelity_3));
				}
			}
			else{
				fidelity.setImageDrawable(mContext.getResources().getDrawable(R.drawable.fidelity_2));
			}
		}
		else{
			fidelity.setImageDrawable(mContext.getResources().getDrawable(R.drawable.fidelity_1));
		}
		//set values & actions
		points.setText(" "+seconds+" ");
		okButton.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
    }
	
	private DBHelper getHelper(){
		if(mDBHelper==null){
			mDBHelper = OpenHelperManager.getHelper(mContext, DBHelper.class);
		}
		return mDBHelper;
	}

}