package com.nappking.movietimesup.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.nappking.movietimesup.database.DBHelper;
import com.nappking.movietimesup.entities.User;

public class LoadUserDataTask extends AsyncTask<String,Void,Boolean>{
	private ProgressDialog _dialog;
	private Context _context;
	private String _path="users";
	private User _user=null;
	boolean _service = false;
	
	public LoadUserDataTask(Context c, User u){
		this._context=c;
		this._user=u;
		if(this._dialog==null)
			this._dialog= new ProgressDialog(_context);
	}
	
	protected void onPreExecute(){
		this._dialog.setMessage("Downloading user data from WS");
		if (!_dialog.isShowing())
			_dialog.show();
	}
	
	@Override
	protected void onPostExecute(final Boolean success){
		if(_dialog.isShowing()){
			_dialog.dismiss();
		}
		String text="";
		//If it was not called from NotificationService
		if(success){
			text= "Updated user data";
		}
		else {
			text= "Impossible to update user data. Check your network connection.";
		}
		//When it finishes, we have to start main menu and finish the splash screen
		Toast.makeText(this._context, text, Toast.LENGTH_SHORT).show();
	}
	
	protected Boolean doInBackground(String... arg0) {
		boolean success = false;
		String read_user = readUserFeed(WebServiceTask.URL+_path);
		try{
			if(read_user==null || read_user.equals("")){
				//If there is not user associated in the server we have to create a new user in the WS
				WebServiceTask wsUser = new WebServiceTask(WebServiceTask.POST_TASK);
				Gson gson = new Gson();
				List<User> users = new ArrayList<User>();
				users.add(this._user);
				try {							
					JSONArray jsonArray = new JSONArray(gson.toJson(users));
					wsUser.addNameValuePair("users", jsonArray.toString());
					Log.i(this.toString(), jsonArray.toString());
			        wsUser.addNameValuePair("action", "SAVE");        
			        wsUser.execute(new String[] {WebServiceTask.URL+_path});	
					success=true;				
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			else{
				JSONObject itemjson = new JSONObject(read_user);
				Gson gson = new Gson();
				DBHelper helper = OpenHelperManager.getHelper(this._context, DBHelper.class);
				Dao<User,Integer> userDao = helper.getUserDAO();
				Log.i("User updated: ", itemjson.toString());
				User user = gson.fromJson(itemjson.toString(), User.class);
				UpdateBuilder<User, Integer> updateBuilder = userDao.updateBuilder();
				// set the criteria like you would a QueryBuilder
				updateBuilder.where().eq(User.USER, this._user.getUser());
				// update the value of your field(s)
				updateBuilder.updateColumnValue(User.UNLOCKED, user.getUnlockedMovies());
				updateBuilder.updateColumnValue(User.LOCKED, user.getLockedMovies());
				updateBuilder.updateColumnValue(User.SECONDS, user.getSeconds());
				updateBuilder.updateColumnValue(User.SCORE, user.getScore());
				updateBuilder.update();
				success = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return success;
	}
	
	private String readUserFeed(String url){
	    StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url+"/"+this._user.getUser());
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
				Log.e(LoadUserDataTask.class.toString(), "IdUser not found");
			} else {
				Log.e(LoadUserDataTask.class.toString(), "Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

}