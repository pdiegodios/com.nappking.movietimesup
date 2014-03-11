package com.nappking.movietimesup.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	private final static int TIMEOUT_CONNECTION = 15000;
	private final static int TIMEOUT_SOCKET = 15000;
	private final static int DAY_PRIZE = 20;
	private Context mContext;
	private DBHelper mDBHelper;
	private String mPath="users";
	private User mUser=null;
	private int mPointsWon = 0;
	boolean mService = false;
	boolean mIsFirst;
	
	public LoadUserDataTask(Context c, User u, boolean isFirst){
		this.mContext=c;
		this.mUser=u;
		this.mIsFirst=isFirst;
	}
	
	@Override
	protected void onPostExecute(final Integer result){
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
				text= mContext.getResources().getString(R.string.error_updating_user);
				break;
		}
        if (mDBHelper != null) {
            OpenHelperManager.releaseHelper();
            mDBHelper = null;
        }
		Toast.makeText(this.mContext, text, Toast.LENGTH_SHORT).show();
		if(mPointsWon>0){
			showDialog();
		}
	}

	protected Integer doInBackground(String... arg0) {
		int result = 0;
		String read_user = readUserFeed(WebServiceTask.URL+mPath);
		try{
			WebServiceTask wsUser = new WebServiceTask(WebServiceTask.POST_TASK);
			if(read_user==null || read_user.equals("") && mUser!=null){
				//If there is not user associated in the server we have to create a new user in the WS
				List<User> users = new ArrayList<User>();
				users.add(this.mUser);
				try {							
					JSONArray jsonArray = new JSONArray(new Gson().toJson(users));
					wsUser.addNameValuePair("users", jsonArray.toString());
					Log.i(this.toString(), jsonArray.toString());
			        wsUser.addNameValuePair("action", "SAVE");        
			        wsUser.execute(new String[] {WebServiceTask.URL+mPath});	
					result=1;				
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			else{
				//Read the user from WS
				JSONObject itemjson = new JSONObject(read_user);
				Dao<User,Integer> userDao = getHelper().getUserDAO();
				Log.i("User updated: ", itemjson.toString());
				User user = new Gson().fromJson(itemjson.toString(), User.class);
				if(mUser!=null && user!=null && user.getLastUpdate()>mUser.getLastUpdate()){
					Log.i("UPDATE", "ACTUALIZADO!!");
					//If user from WS was updated after user from Database
					mUser.setUnlockedMovies(user.getUnlockedMovies());
					mUser.setLockedMovies(user.getLockedMovies());
					mUser.setScore(user.getScore());
					mUser.setSeconds(user.getSeconds());
					mUser.setMovies(user.getMovies());
					if(!mIsFirst){
						updateDays(user.getLastUpdate(), user.getDays());
					}
					else{
						mUser.setDays(user.getDays());
						mUser.setLastUpdate(user.getLastUpdate());
					}
					userDao.update(mUser);
					result = 2;
				}
				else if (mUser!=null && user!=null && user.getLastUpdate()<mUser.getLastUpdate()){
					Log.i("UPDATE", "Subimos a WebService");
					//If user from Local DB was updated after user from WS
					List<User> users = new ArrayList<User>();
					if(!mIsFirst){
						updateDays(mUser.getLastUpdate(), mUser.getDays());
					}
					userDao.update(mUser);
					users.add(mUser);
					JSONArray jsonArray = new JSONArray(new Gson().toJson(users));
					wsUser.addNameValuePair("users", jsonArray.toString());
					Log.i(this.toString(), jsonArray.toString());
			        wsUser.addNameValuePair("action", "UPDATE");        
			        wsUser.execute(new String[] {WebServiceTask.URL+"users"});	
					result = 1;
				}
				else if(mUser!=null && user!=null){
					result = 3;
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
	
	private void updateDays(long milliseconds, int daysBefore){
		Calendar today = GregorianCalendar.getInstance();
		Calendar lastEntry=GregorianCalendar.getInstance();
		lastEntry.setTimeInMillis(milliseconds);
		Log.i("DATE", lastEntry.get(Calendar.DATE)+"/"+lastEntry.get(Calendar.MONTH));
		if(lastEntry.get(Calendar.DAY_OF_YEAR) != today.get(Calendar.DAY_OF_YEAR) ||
				lastEntry.get(Calendar.YEAR) != today.get(Calendar.YEAR)){
			//If it's a different day
			lastEntry.add(Calendar.DATE, 1);
			if(today.get(Calendar.DAY_OF_YEAR) == lastEntry.get(Calendar.DAY_OF_YEAR) &&
					today.get(Calendar.YEAR) == lastEntry.get(Calendar.YEAR)){
				Log.i("LAST_ENTRY", "YESTERDAY");
				//The user entered yesterday to the game
				mUser.setDays(daysBefore+1);
				if(mUser.getDays()>0){
					if(mUser.getDays()>=3){
						mPointsWon = 5*DAY_PRIZE;
					}
					else{
						mPointsWon = (mUser.getDays()+1)*DAY_PRIZE;
					}
				}
			}
			else{
				Log.i("LAST_ENTRY", "NOT TODAY & NOT YESTERDAY");
				//The user didn't enter yesterday or today before now, so the days in row will be reset
				mUser.setDays(0);
				mPointsWon = DAY_PRIZE;
			}					
			mUser.setSeconds(mUser.getSeconds()+DAY_PRIZE);
		}
		mUser.setLastUpdate(System.currentTimeMillis());		
	}
	
	private void showDialog() {
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
		if(mPointsWon>=40){
			day2.setBackgroundColor(mContext.getResources().getColor(R.color.greenlight));
			day2.setTextColor(mContext.getResources().getColor(R.color.black));	
			if(mPointsWon>=60){
				day3.setBackgroundColor(mContext.getResources().getColor(R.color.greenlight));
				day3.setTextColor(mContext.getResources().getColor(R.color.black));	
				if(mPointsWon>=100){
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
		points.setText(" "+mPointsWon+" ");
		okButton.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
        dialog.show();
	}
	
	private String readUserFeed(String url){
	    StringBuilder builder = new StringBuilder();
		HttpGet httpGet = new HttpGet(url+"/"+this.mUser.getUser());
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used. 
		HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT_CONNECTION);
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_SOCKET);
		DefaultHttpClient client = new DefaultHttpClient(httpParameters);
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
		return builder.toString();
	}

	private DBHelper getHelper(){
		if(mDBHelper==null){
			mDBHelper = OpenHelperManager.getHelper(mContext, DBHelper.class);
		}
		return mDBHelper;
	}

}