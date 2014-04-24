package org.schoolsfirstfcu.mobile.plugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;


public class Camera extends CordovaPlugin {

    private CallbackContext callbackContext;

	@Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
	    if (!hasRearFacingCamera()) {
	        callbackContext.error("No rear camera detected");
	        return false;
	    }
	    this.callbackContext = callbackContext;
	    Context context = cordova.getActivity().getApplicationContext();
	    Intent intent = new Intent(context, CameraActivity.class);
	    intent.putExtra(CameraActivity.TITLE, args.getString(0));
	    intent.putExtra(CameraActivity.QUALITY, args.getInt(1));
	    intent.putExtra(CameraActivity.TARGET_WIDTH, args.getInt(2));
	    intent.putExtra(CameraActivity.TARGET_HEIGHT, args.getInt(3));
	    cordova.startActivityForResult(this, intent, 0);
        return true;
    }

	private boolean hasRearFacingCamera() {
	    Context context = cordova.getActivity().getApplicationContext();
	    return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    if (resultCode == Activity.RESULT_OK) {
	        callbackContext.success(intent.getExtras().getString(CameraActivity.IMAGE_DATA));
	    } else if (resultCode == CameraActivity.RESULT_ERROR) {
	        String errorMessage = intent.getExtras().getString(CameraActivity.ERROR_MESSAGE);
	        if (errorMessage != null) {
	            callbackContext.error(errorMessage);
	        } else {
	            callbackContext.error("Failed to take picture");
	        }
	    }
    }

}
