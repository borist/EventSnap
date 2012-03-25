package com.hackny.spring;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class EventSnapActivity extends Activity {
    //Button camera_button;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    }
    
    public void onClick(View v){
    	switch (v.getId()) {
    	case R.id.camera_button:
    		Intent intent = new Intent(getApplicationContext(), SnapPictureActivity.class);
    		startActivity(intent);
    		break;
    	case R.id.calendar_button:
    		startActivity(new Intent(getApplicationContext(), CalendarActivity.class));
    		break;
    	}
    }
}