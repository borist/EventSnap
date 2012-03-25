package com.hackny.spring;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import com.hackny.spring.helpers.ParselyJSONQuery;
import com.hackny.spring.helpers.Preview;
import com.hackny.spring.helpers.TextProcessor;

public class SnapPictureActivity extends Activity {
	Camera camera;
    Preview preview;
    Button photoButton;
    ShutterCallback shutterCallback;
    PictureCallback rawCallback;
    PictureCallback jpegCallback;
    FrameLayout layout;
    byte[] imageBytes;
    ProgressDialog loadingDialog;
    final Activity mActivity = this;
    TextView tv;
	
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
    			startImageProcessing();
    		}
    	};
    }
     
    private void startImageProcessing() {
    	tv = new TextView(this);
		tv.setText("Hello, cloud ocr\n");
		setContentView(tv);
    	loadingDialog = ProgressDialog.show(this, "", "Decoding Image...");
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
				//loadingDialog = ProgressDialog.show(mActivity, "", "Uploading Image...");
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
				loadingDialog.dismiss();
				displayMessage( "Ready" );

				TextProcessor tp = new TextProcessor(outputFile);
				
				String result = "Event Title: " + tp.title + ", time:" + tp.stime + " - " + tp.etime + 
					", Date: " + tp.month + "/" + tp.day + "/" + tp.year;
				tp.description = "visit www.talklikeapirate.com for details. Yarr.";
				displayMessage( result.toString() );
				boolean allDay = false;
				
				GregorianCalendar gc = new GregorianCalendar(2000+Integer.parseInt(tp.year), Integer.parseInt(tp.month)-1, Integer.parseInt(tp.day));
				
				if (tp.stime == null && tp.etime == null)
					allDay = true;
				
				String summary = " Parsely Tags: ";
				
				Intent calIntent = new Intent(Intent.ACTION_EDIT);
				calIntent.setType("vnd.android.cursor.item/event");
				calIntent.putExtra("title", tp.title);
				calIntent.putExtra("beginTime", gc.getTime().getTime());
				calIntent.putExtra("description", tp.description + summary + "Talk like");// + summary + parsely.substring(parsely.indexOf(">"), parsely.indexOf("<")));

				if (allDay) {  
					calIntent.putExtra("allDay", true);
				   
				}
				else {
					calIntent.putExtra("allDay", false);
	     				calIntent.putExtra("beginTime", tp.stime);
					calIntent.putExtra("endTime", tp.etime);
				}
				startActivity(calIntent);
				String parsely = new ParselyJSONQuery(tp.title + ", " + tp.description).getData();
			} catch ( Exception e ) {
				final Writer result = new StringWriter();
		        final PrintWriter printWriter = new PrintWriter(result);
		        e.printStackTrace(printWriter);
				Log.e("MOTHER FUCKER", result.toString());
				//displayMessage( "Error: " + e.getClass() + ", " + e.getStackTrace().toString() + ", " + e.getMessage() );
			}
		}

		private void displayMessage( String text ) {
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