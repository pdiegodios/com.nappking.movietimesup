package com.nappking.movietimesup;

import android.os.Bundle;
import android.util.Log;

import com.nappking.movietimesup.database.DBActivity;
import com.nappking.movietimesup.util.IabHelper;
import com.nappking.movietimesup.util.IabResult;

public class ShopActivity extends DBActivity{
	IabHelper mHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	   String base64EncodedPublicKey = getResources().getString(R.string.app_key5) + 
			   getResources().getString(R.string.app_key4) + getResources().getString(R.string.app_key3) + 
			   getResources().getString(R.string.app_key2) + getResources().getString(R.string.app_key1);
	   
	   mHelper = new IabHelper(this, base64EncodedPublicKey);
	   mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			@Override
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					Log.d(TAG, "Problem setting up In-app Billing: " + result);
				}            
		          	// Hooray, IAB is fully set up!  
				//TODO: Query for inventory
			}
		});
	}
	
	@Override
	public void onDestroy() {
	   super.onDestroy();
	   if (mHelper != null) mHelper.dispose();
	   mHelper = null;
	}

}
