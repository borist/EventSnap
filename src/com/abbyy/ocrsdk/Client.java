package com.abbyy.ocrsdk;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.os.Environment;
import android.util.Log;

public class Client {
	public String ApplicationId;
	public String Password;
	
	public String ServerUrl = "http://cloud.ocrsdk.com";
	
	public Task ProcessImage( byte[] in, ProcessingSettings settings) throws Exception 
	{
		BufferedReader reader = null;
		try {
			URL url = new URL(ServerUrl + "/processImage?" + settings.AsUrlParams());
		//byte[] fileContents = in;//readDataFromFile( filePath );
		byte[] fileContents = readDataFromFile(Environment.getExternalStorageDirectory() + "/pirates.jpg");
		
		HttpURLConnection connection = openPostConnection(url);
		Log.e("wtffff", "it made it after openPost");
		connection.setRequestProperty("Content-Length", Integer.toString(fileContents.length));
		connection.getOutputStream().write(fileContents);
		Log.e("WTF", "it made it after getOutputStream");
		
		reader = new BufferedReader( new InputStreamReader(connection.getInputStream()));
		}
		catch (Exception e) {
			final Writer result = new StringWriter();
	        final PrintWriter printWriter = new PrintWriter(result);
	        e.printStackTrace(printWriter);
			Log.e("MOTHER FUCKER", result.toString());
			//displayMessage( "Error: " + e.getClass() + ", " + e.getStackTrace().toString() + ", " + e.getMessage() );
		}
		return new Task(reader);
	}
	
	public Task GetTaskStatus( String taskId ) throws Exception
	{
		URL url = new URL( ServerUrl + "/getTaskStatus?taskId=" + taskId );
		
		URLConnection connection = openGetConnection( url );
		BufferedReader reader = new BufferedReader( new InputStreamReader( connection.getInputStream()));
		return new Task(reader);
	}
	
	public void DownloadResult( Task task, String outputFile ) throws Exception
	{
		if( task.Status != Task.TaskStatus.Completed ) {
			throw new IllegalArgumentException("Invalid task status");
		}
		
		if( task.DownloadUrl == null ) {
			throw new IllegalArgumentException( "Cannot download result without url" );
		}
		
		URL url = new URL( task.DownloadUrl );
		URLConnection connection = url.openConnection(); // do not use authenticated connection
		
		BufferedInputStream reader = new BufferedInputStream( connection.getInputStream());
				
		FileOutputStream out = new FileOutputStream(outputFile);

        byte data[] = new byte[1024];
        int count;
        while ((count = reader.read(data, 0, 1024)) != -1)
        {
                out.write(data, 0, count);
        }
	}
	
	private HttpURLConnection openPostConnection( URL url ) throws Exception
	{
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setRequestMethod("POST");
		setupAuthorization( connection );
		connection.setRequestProperty("Content-Type", "applicaton/octet-stream" );
		
		return connection;
	}
	
	private URLConnection openGetConnection( URL url ) throws Exception
	{
		URLConnection connection = url.openConnection();
		//connection.setRequestMethod("GET");
		setupAuthorization( connection );
		return connection;
	}
	
	private void setupAuthorization( URLConnection connection )
	{
		connection.addRequestProperty( "Authorization", "Basic: " + encodeUserPassword());	
	}
	
	private byte[] readDataFromFile( String filePath ) throws Exception
	{
		Log.e("Client test", filePath);
		File file = new File( filePath );
		InputStream inputStream = new FileInputStream( file );
		long fileLength = file.length();
		byte[] dataBuffer = new byte[(int)fileLength];
		
		int offset = 0;
		int numRead = 0;
		while( true ) {
			if( offset >= dataBuffer.length ) {
				break;
			}
			numRead = inputStream.read( dataBuffer, offset, dataBuffer.length - offset );
			if( numRead < 0 ) {
				break;
			}
			offset += numRead;
		}
		if( offset < dataBuffer.length ) {
			throw new IOException( "Could not completely read file " + file.getName() );
		}
		return dataBuffer;
	}
	
	private String encodeUserPassword()
	{
		String toEncode = ApplicationId + ":" + Password;
		return Base64.encode( toEncode );
	}
	
}
