package com.hackny.spring;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.hackny.spring.helpers.Preview;

public class SnapPictureActivity extends Activity {
    private static final String TAG = "SnapPictureActivity";
	Camera camera;
    Preview preview;
    Button photoButton;
    ShutterCallback shutterCallback;
    PictureCallback rawCallback;
    PictureCallback jpegCallback;
    FrameLayout layout;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_picture);
        
        preview = new Preview(this);
        layout = ((FrameLayout) findViewById(R.id.preview_layout));
        Log.e(TAG, layout.toString());
        layout.addView(preview);
        
        photoButton = (Button) findViewById(R.id.buttonClick);
        RotateAnimation rotanim = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.camera_text_anim);
        rotanim.setFillAfter(true);
        
        photoButton.setAnimation(rotanim);
        photoButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
				//Intent intent = new Intent(getApplicationContext(), demoActivity.class); 
	    		//startActivity(intent);
			}
		});
       
        shutterCallback = new ShutterCallback(){
        	public void onShutter() {
        	}
        };
        
        rawCallback = new PictureCallback() {
        	public void onPictureTaken(byte[] data, Camera camera) {
        	}
        };
        
        jpegCallback = new PictureCallback() {
    		public void onPictureTaken(byte[] data, Camera camera) {
    			new SavePhotoTask().execute(data);
    			//preview.camera.startPreview();
    			Log.d(TAG, "onPictureTaken - jpeg");
    		}
    	};
    }
    
    private class SavePhotoTask extends AsyncTask<byte[], String, String> {

		@Override
		protected String doInBackground(byte[]... params) {
			File photo = new File(Environment.getExternalStorageDirectory(), "motherfucker.jpeg"); 
			
			Log.e("EXTERNAL STORAGE DIR", Environment.getExternalStorageState().toString());
			
			//File photo = Environment.getExternalStorageDirectory().get
			
			if (photo.exists()) {
				photo.delete();
			}
			
			try {
				FileOutputStream fos = new FileOutputStream (photo.getPath());
				        
				fos.write(params[0]);
				fos.close();
			}
			catch (IOException e) {
				Log.e("SnapPictureAcivity Async", "Caught Exception in photoCallback", e);
				//Toast.makeText(getApplicationContext(), "Unable to capture photo", Toast.LENGTH_SHORT).show();
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, 1);
			
	     }
    	
    }
    
}