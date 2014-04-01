package com.nappking.movietimesup;

import java.sql.SQLException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.nappking.movietimesup.database.DBActivity;
import com.nappking.movietimesup.entities.User;
import com.nappking.movietimesup.util.IabHelper;
import com.nappking.movietimesup.util.IabResult;
import com.nappking.movietimesup.util.Inventory;
import com.nappking.movietimesup.util.Purchase;

public class ShopActivity extends DBActivity{
	private IabHelper mHelper;
	private static final String SKU_SECONDS_2000 = "seconds_2000";
	private static final String SKU_SECONDS_5000 = "seconds_5000";
	private static final String SKU_SECONDS_10000 = "seconds_10000";
	private static final String SKU_SECONDS_20000 = "seconds_20000";
	private static final String[] SKUS = new String[]{SKU_SECONDS_2000, SKU_SECONDS_5000, SKU_SECONDS_10000, SKU_SECONDS_20000};
	private static final int RC_REQUEST = 101; 
    
	private String seconds2000Price ="?";
	private String seconds5000Price ="?";
	private String seconds10000Price ="?";
	private String seconds20000Price ="?";
	
	private String mUserId;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
       //setContentView(R.layout.activity_shop);
		
		String base64EncodedPublicKey = getResources().getString(R.string.app_key5) + 
				getResources().getString(R.string.app_key4) + getResources().getString(R.string.app_key3) + 
				getResources().getString(R.string.app_key2) + getResources().getString(R.string.app_key1);
        try {
			User user = getHelper().getUserDAO().queryForId(0);
			mUserId=user.getUser();
		} catch (SQLException e) {
			mUserId="";
			e.printStackTrace();
		}
		mHelper = new IabHelper(this, base64EncodedPublicKey);
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
                mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});
	}
	
	// Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");
            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;
            
            if (result.isFailure()) {
            	//handle error
            	return;
            }

            Log.d(TAG, "Query inventory was successful.");

            //Recovery prices for products
            seconds2000Price = inventory.getSkuDetails(SKU_SECONDS_2000).getPrice();
            seconds5000Price = inventory.getSkuDetails(SKU_SECONDS_5000).getPrice();
            seconds10000Price = inventory.getSkuDetails(SKU_SECONDS_10000).getPrice();
            seconds20000Price = inventory.getSkuDetails(SKU_SECONDS_20000).getPrice();            
            
            // Check for deliveries -- if we own some product, we should consume it inmediately
            for(String sku: SKUS){
            	checkPurchase(inventory,sku);
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
        String payload = mUserId+sku;
        mHelper.launchPurchaseFlow(this, sku, RC_REQUEST, mPurchaseFinishedListener, payload);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        if(payload!=null && payload.startsWith(mUserId))
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
                switch(i){
	                case 0:alert("You got 2,000 seconds more!");break;
	                case 1:alert("You got 5,000 seconds more!");break;
	                case 2:alert("You got 10,000 seconds more!");break;
	                case 3:alert("You got 20,000 seconds more!");break;
	                default:break;
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
        //findViewById(R.id.screen_main).setVisibility(set ? View.GONE : View.VISIBLE);
        //findViewById(R.id.screen_wait).setVisibility(set ? View.VISIBLE : View.GONE);
    }

    void complain(String message) {
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton(Dialog.BUTTON_NEUTRAL, null);
        bld.create().show();
    }

}
