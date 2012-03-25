package com.hackny.spring;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.abbyy.ocrsdk.Client;
import com.abbyy.ocrsdk.ProcessingSettings;
import com.abbyy.ocrsdk.Task;
import com.hackny.spring.helpers.GoogleSuggest;
import com.hackny.spring.helpers.Preview;
import com.hackny.spring.helpers.TextProcessor;

public class SnapPictureActivity extends Activity {
    private static final String TAG = "SnapPictureActivity";
	Camera camera;
    Preview preview;
    Button photoButton;
    ShutterCallback shutterCallback;
    PictureCallback rawCallback;
    PictureCallback jpegCallback;
    FrameLayout layout;
    TextView tv;
    byte[] imageBytes;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_picture);
        
        preview = new Preview(this);
        layout = ((FrameLayout) findViewById(R.id.preview_layout));
        layout.addView(preview);
        
        photoButton = (Button) findViewById(R.id.buttonClick);
        RotateAnimation rotanim = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.camera_text_anim);
        rotanim.setFillAfter(true);
        
        photoButton.setAnimation(rotanim);
        photoButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
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
    			imageBytes = data;
    			
    			
//				Log.e("SnapPictureActivity BEFORE", imageBytes.toString());
//				
//    			//code to set taken picture to be Bitmap
//    			ByteArrayInputStream bytes = new ByteArrayInputStream(data);
//    			BitmapDrawable bmd = new BitmapDrawable(bytes);
//    			Bitmap bm = bmd.getBitmap();
//
//    			String fileName = "00000001.jpg";
//    			FileOutputStream out = null;
//    			try {
//    				out = new FileOutputStream(Environment.getExternalStorageDirectory()+"/"+fileName);
//    				bm.compress(Bitmap.CompressFormat.JPEG, 20, out);
//    				out.close();
//    			} catch (IOException e) {
//    				Log.e("SNAPs", e.getMessage());
//    			}
//    			Log.e("SnapPictureActivity FOS:", Environment.getExternalStorageDirectory().toString());       
 
//				Intent intent = new Intent(getApplicationContext(), ImageProcessActivity.class);
//				startActivity(intent);
//    			Log.d(TAG, "onPictureTaken - jpeg");
    			
    			startImageProcessing();
    		}
    	};
    }
     
    private void startImageProcessing() {
    	tv = new TextView(this);
		tv.setText("Hello, cloud ocr\n");
		setContentView(tv);

		new Thread( new Worker() ).start();
    }
    
    class Worker implements Runnable {


		public void run() {
			try {
				Thread.sleep(1000);
				displayMessage( "Starting.." );
				Client restClient = new Client();
				restClient.ApplicationId = "PhotoGrabber";
				restClient.Password = "VLybwFqO/FDQCILgEPabzx7D";
				
				//File file = new File (Environment.getExternalStorageDirectory(), "result.txt");
				FileWriter fw = new FileWriter(Environment.getExternalStorageDirectory()+"/result.txt");
				fw.close();
				String outputFile = Environment.getExternalStorageDirectory() + "/result.txt";
				
				ProcessingSettings settings = new ProcessingSettings();
				settings.setOutputFormat( ProcessingSettings.OutputFormat.txt );
				
				displayMessage( "Uploading.." );
				Task task = restClient.ProcessImage(imageBytes, settings);
				
				while( task.IsTaskActive() ) {
					Thread.sleep(2000);
					
					displayMessage( "Waiting.." );
					task = restClient.GetTaskStatus(task.Id);
				}
				
				if( task.Status == Task.TaskStatus.Completed ) {
					displayMessage( "Downloading.." );
					restClient.DownloadResult(task, outputFile);
				} else {
					displayMessage( "Task failed" );
				}
				
				displayMessage( "Ready" );

				TextProcessor tp = new TextProcessor(outputFile);
				
				//Log.e("TEXTING BRO:", tp.title + "!!!!! " + tp.stime + " - " + tp.etime + " on " + tp.month + "/" + tp.day + "/" + tp.year);
				
				/*StringBuffer contents = new StringBuffer(); 
				BufferedReader reader = new BufferedReader(new FileReader(outputFile)); 
				String text = null; 
				while ((text = reader.readLine()) != null) { 
					Log.e("READER", "i read? " + text);
					contents.append(text) ;
				}*/
				
				//String result = 
					//new GoogleSuggest(contents.toString()).fuqs();
				//Log.e("FUQS results", result);
				String result = "Event Title: " + tp.title + ", time:" + tp.stime + " - " + tp.etime + 
					", Date: " + tp.month + "/" + tp.day + "/" + tp.year;
				
				displayMessage( result.toString() );
				
			} catch ( Exception e ) {
				final Writer result = new StringWriter();
		        final PrintWriter printWriter = new PrintWriter(result);
		        e.printStackTrace(printWriter);
				Log.e("MOTHER FUCKER", result.toString());
				displayMessage( "Error: " + e.getClass() + ", " + e.getStackTrace().toString() + ", " + e.getMessage() );
			}
		}

		private void displayMessage( String text )
		{
			tv.post( new MessagePoster( text ) );
		}

		class MessagePoster implements Runnable {
			public MessagePoster( String message )
			{
				_message = message;
			}

			public void run() {
				tv.append( _message + "\n" );
				setContentView( tv );
			}

			private final String _message;
		}
	}


}