package com.hackny.spring;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class EventSnapActivity extends Activity {
    TextView top;
    TextView bottom;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        top 		= 	((TextView) findViewById(R.id.top_text));
        bottom		= 	((TextView) findViewById(R.id.bottom_text));
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/my_font.ttf");
        top.setTypeface(tf);
        bottom.setTypeface(tf);
        
    }
    
    public void onClick(View v){
    	switch (v.getId()) {
    	case R.id.camera_button:
    		Intent intent = new Intent(getApplicationContext(), SnapPictureActivity.class);
    		startActivity(intent);
    		break;
    	}
    }
}