package com.nappking.movietimesup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.nappking.movietimesup.database.DBActivity;
import com.nappking.movietimesup.entities.User;
import com.nappking.movietimesup.task.WebServiceTask;
import com.nappking.movietimesup.util.IabHelper;
import com.nappking.movietimesup.util.IabResult;
import com.nappking.movietimesup.util.Inventory;
import com.nappking.movietimesup.util.Purchase;

public class ShopActivity extends DBActivity{
	private IabHelper mHelper;
	private static final String TAG="ShopFragment";
	private static final String SKU_SECONDS_2000 = "seconds_2000";
	private static final String SKU_SECONDS_5000 = "seconds_5000";
	private static final String SKU_SECONDS_10000 = "seconds_10000";
	private static final String SKU_SECONDS_20000 = "seconds_20000";
	private static final List<String> SKUS = new ArrayList<String>(Arrays.asList(SKU_SECONDS_2000, SKU_SECONDS_5000, SKU_SECONDS_10000, SKU_SECONDS_20000));
	private static final int RC_REQUEST = 101; 
    
	/*
	private String seconds2000Price ="?";
	private String seconds5000Price ="?";
	private String seconds10000Price ="?";
	private String seconds20000Price ="?";*/
	
	private Button buy2000;
	private Button buy5000;
	private Button buy10000;
	private Button buy20000;
	
	private User mUser;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop);
		
		String base64EncodedPublicKey = getResources().getString(R.string.app_key5) + 
				getResources().getString(R.string.app_key4) + getResources().getString(R.string.app_key3) + 
				getResources().getString(R.string.app_key2) + getResources().getString(R.string.app_key1);

		try {
			mUser = getHelper().getUserDAO().queryForId(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		mHelper = new IabHelper(this, base64EncodedPublicKey);
		Log.d(TAG, "base64EncodedPublicKey: "+base64EncodedPublicKey);
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			@Override
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					Log.d(TAG, "Problem setting up In-app Billing: " + result);
				}            
				//TODO: Query for inventory
                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(true, SKUS, mGotInventoryListener);
			}
		});		

		initialize();
		setListeners();		
	}
	
	private void initialize(){
		buy2000 = (Button) findViewById(R.id.buy2000);
		buy5000 = (Button) findViewById(R.id.buy5000);
		buy10000 = (Button) findViewById(R.id.buy10000);
		buy20000 = (Button) findViewById(R.id.buy20000);
	}
	
	private void setListeners(){
		buy2000.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				onBuyButtonClicked(v, SKU_SECONDS_2000);
			}
		});
		buy5000.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				onBuyButtonClicked(v, SKU_SECONDS_5000);
			}
		});
		buy10000.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				onBuyButtonClicked(v, SKU_SECONDS_10000);
			}
		});
		buy20000.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				onBuyButtonClicked(v, SKU_SECONDS_20000);
			}
		});
	}
	
	// Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");
            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            Log.d(TAG, "Query inventory was successful.");
            
            if (result.isFailure()) {
            	Log.i(TAG, result.getMessage());
            	//handle error
            	//return;
            }
            if(inventory!=null){
	            //Recovery prices for products
	            /*if(inventory.getSkuDetails(SKU_SECONDS_2000)!=null)
	            	seconds2000Price = inventory.getSkuDetails(SKU_SECONDS_2000).getPrice();
	            if(inventory.getSkuDetails(SKU_SECONDS_5000)!=null)
	            	seconds5000Price = inventory.getSkuDetails(SKU_SECONDS_5000).getPrice();
	            if(inventory.getSkuDetails(SKU_SECONDS_10000)!=null)
	            	seconds10000Price = inventory.getSkuDetails(SKU_SECONDS_10000).getPrice();
	            if(inventory.getSkuDetails(SKU_SECONDS_20000)!=null)
	            	seconds20000Price = inventory.getSkuDetails(SKU_SECONDS_20000).getPrice();           
            */
	            // Check for deliveries -- if we own some product, we should consume it inmediately
	            for(String sku: SKUS){
	            	checkPurchase(inventory,sku);
	            } 
            }
            
            updateUi();
            setWaitScreen(false);
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };
    
    private void checkPurchase(Inventory inventory, String sku){
        Purchase purchase = inventory.getPurchase(sku);
        if (purchase != null && verifyDeveloperPayload(purchase)) {
            Log.d(TAG, "We have a purchase made. Consuming "+sku);
            mHelper.consumeAsync(inventory.getPurchase(sku), mConsumeFinishedListener);
            return;
        }
    }
    
    // User clicked the "Buy" button
    private void onBuyButtonClicked(View arg0, String sku) {
        Log.d(TAG, "Buy button clicked for "+sku);
        // launch the gas purchase UI flow.
        setWaitScreen(true);
        String payload = sku;
        if(mUser!=null){
        	payload=mUser.getUser()+payload;
        }        
        mHelper.launchPurchaseFlow(this, sku, RC_REQUEST, mPurchaseFinishedListener, payload);
    }

    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    private boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
    	Log.i(TAG, payload);
        if(payload!=null && mUser!=null && payload.startsWith(mUser.getUser()))
        	return true;
        else
        	return false;
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                setWaitScreen(false);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                setWaitScreen(false);
                return;
            }
            Log.d(TAG, "Purchase successful.");
            //Consume purchase
            mHelper.consumeAsync(purchase, mConsumeFinishedListener);
        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d(TAG, "Consumption successful. Provisioning.");
                //Update values
                String purchase_sku = purchase.getSku();
                int i = 0;
                for(String sku:SKUS){
                	if(sku.equals(purchase_sku)){
                		break;
                	}
                	i++;
                }                
                int seconds = 0;
                switch(i){
	                case 0:
	                	seconds=2000;
	                	alert("You got 2,000 seconds more!");
	                	break;
	                case 1:
	                	seconds=5000;
	                	alert("You got 5,000 seconds more!");
	                	break;
	                case 2:
	                	seconds=10000;
	                	alert("You got 10,000 seconds more!");
	                	break;
	                case 3:
	                	seconds=20000;
	                	alert("You got 20,000 seconds more!");
	                	break;
	                default:break;
                }
                if(seconds>0){
                	uploadUser(seconds);
                }
            }
            else {
                complain("Error while consuming: " + result);
            }
            updateUi();
            setWaitScreen(false);
            Log.d(TAG, "End consumption flow.");
        }
    };
    
	//BACKGROUND METHODS
	/**
	 * Method to send updated user to WS
	 * @param locked: the movie is now locked or if it is false you've won the points
	 */
	private void uploadUser(int seconds){
		try {
			List<User> users = new ArrayList<User>();
			Dao<User,Integer> daoUser = getHelper().getUserDAO();
			if(mUser==null){
				mUser = daoUser.queryForId(1);
			}
			mUser.setSeconds(mUser.getSeconds()+seconds);
			mUser.setLastUpdate(System.currentTimeMillis());
			daoUser.update(mUser);
			users.add(mUser);			
			WebServiceTask wsUser = new WebServiceTask(WebServiceTask.POST_TASK);			
			JSONArray jsonArray = new JSONArray(new Gson().toJson(users));
			wsUser.addNameValuePair("users", jsonArray.toString());
			Log.i(this.toString(), jsonArray.toString());
	        wsUser.addNameValuePair("action", "UPDATE");        
	        wsUser.execute(new String[] {MovieTimesUpApplication.URL+"users"});	
		} catch (SQLException e) {
			e.printStackTrace();	
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mHelper != null) mHelper.dispose();
		mHelper = null;
	}
	
    // updates UI to reflect model
    public void updateUi() {
    }

    // Enables or disables the "please wait" screen.
    void setWaitScreen(boolean set) {
    	findViewById(R.id.screen_main).setVisibility(set ? View.GONE : View.VISIBLE);
        findViewById(R.id.screen_wait).setVisibility(set ? View.VISIBLE : View.GONE);
    }

    void complain(String message) {
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        bld.create().show();
    }

}
