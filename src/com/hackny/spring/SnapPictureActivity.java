package com.hackny.spring;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;

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
			}
		});
       
        shutterCallback = new ShutterCallback(){
        	public void onShutter() {
        		//yo face
        	}
        };
        
        rawCallback = new PictureCallback() {
        	public void onPictureTaken(byte[] data, Camera camera) {
        		//
        	}
        };
        
        jpegCallback = new PictureCallback() {
    		public void onPictureTaken(byte[] data, Camera camera) {
    			FileOutputStream outStream = null;
    			try {
    				// write to local sandbox file system
    				// outStream =
    				// CameraDemo.this.openFileOutput(String.format("%d.jpg",
    				// System.currentTimeMillis()), 0);
    				// Or write to sdcard
    				outStream = new FileOutputStream(String.format(
    						"/sdcard/%d.jpg", System.currentTimeMillis()));
    				outStream.write(data);
    				outStream.close();
    				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
    			} catch (FileNotFoundException e) {
    				e.printStackTrace();
    			} catch (IOException e) {
    				e.printStackTrace();
    			} finally {
    			}
    			Log.d(TAG, "onPictureTaken - jpeg");
    		}
    	};
    }
    
}