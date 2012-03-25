package com.hackny.spring;

import java.io.ByteArrayInputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.ImageView;

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
				Intent intent = new Intent(getApplicationContext(), demoActivity.class); 
	    		startActivity(intent);
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
    			ByteArrayInputStream bytes = new ByteArrayInputStream(data);
    			BitmapDrawable bmd = new BitmapDrawable(bytes);
    			Bitmap bm = bmd.getBitmap();
    			ImageView im = new ImageView(getApplicationContext());
    			im.setImageBitmap(bm);
    			setContentView(im);
    			
    			Log.d(TAG, "onPictureTaken - jpeg");
    		}
    	};
    }
     
}