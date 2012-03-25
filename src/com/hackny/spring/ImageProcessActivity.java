//package com.hackny.spring;
//
//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//
//import android.app.Activity;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.os.Environment;
//import android.util.Log;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.abbyy.ocrsdk.Client;
//import com.abbyy.ocrsdk.ProcessingSettings;
//import com.abbyy.ocrsdk.Task;
//
//
//public class ImageProcessActivity extends Activity {
//	TextView tv;
//	Bitmap image;
//	
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		tv = new TextView(this);
//		tv.setText("Hello, cloud ocr\n");
//		setContentView(tv);
//
//		//getImage();
//		
//		
//		new Thread(new Worker()).start();
//	}
//
//	
//	
//
//
//	class Worker implements Runnable {
//
//
//		public void run() {
//			try {
//				Thread.sleep(1000);
//				displayMessage( "Starting.." );
//				Client restClient = new Client();
//				//restClient.ApplicationId = "EventSnap";
//				//restClient.Password = "eDKaa5wH8j01g81VIsthssEO";
//				
//				String outputFile = "/sdcard/result.txt";
//				
//				ProcessingSettings settings = new ProcessingSettings();
//				settings.setOutputFormat( ProcessingSettings.OutputFormat.txt );
//				
//				displayMessage( "Uploading.." );
//				Task task = null;// restClient.ProcessImage(filePath, settings);
//				
//				while( task.IsTaskActive() ) {
//					Thread.sleep(2000);
//					
//					displayMessage( "Waiting.." );
//					task = restClient.GetTaskStatus(task.Id);
//				}
//				
//				if( task.Status == Task.TaskStatus.Completed ) {
//					displayMessage( "Downloading.." );
//					restClient.DownloadResult(task, outputFile);
//				} else {
//					displayMessage( "Task failed" );
//				}
//				
//				displayMessage( "Ready" );
//
//				
//				StringBuffer contents = new StringBuffer(); 
//				BufferedReader reader = new BufferedReader(new FileReader(outputFile)); 
//				String text = null; 
//				while ((text = reader.readLine()) != null) { 
//					contents.append(text) 
//					.append(System.getProperty( 
//							"line.separator")); 
//				}
//				
//				displayMessage( contents.toString() );
//				
//			} catch ( Exception e ) {
//				displayMessage( "Error: " + e.getMessage() );
//			}
//		}
//
//		private void displayMessage( String text )
//		{
//			tv.post( new MessagePoster( text ) );
//		}
//
//		class MessagePoster implements Runnable {
//			public MessagePoster( String message )
//			{
//				_message = message;
//			}
//
//			public void run() {
//				tv.append( _message + "\n" );
//				setContentView( tv );
//			}
//
//			private final String _message;
//		}
//	}
//}
