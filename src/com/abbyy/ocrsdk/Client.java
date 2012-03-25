package com.abbyy.ocrsdk;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;

public class Client {
	public String ApplicationId;
	public String Password;
	
	public String ServerUrl = "http://cloud.ocrsdk.com";
	
	public Task ProcessImage( byte[] fileContents, ProcessingSettings settings)
	{
		URL url = null;
		try {
			url = new URL(ServerUrl + "/processImage?" + settings.AsUrlParams());
		} catch (MalformedURLException e) {
			Log.e("Client fail", e.getLocalizedMessage());
		}
		Log.e("URL::::::", url.toString());
		
		//byte[] fileContents = readDataFromFile( filePath );
		//Log.e("file contents::::::", fileContents.toString());
		HttpURLConnection connection = openPostConnection(url);
		
		connection.setRequestProperty("Content-Length", Integer.toString(fileContents.length));
		
		try {
			connection.getOutputStream().write(fileContents);
		} catch (IOException e) {
			Log.e("Client fail connection request prop", (fileContents == null) + ", " + e.getMessage());
		}
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader( new InputStreamReader( connection.getInputStream()));
		} catch (IOException e1) {
			Log.e("Client fail new buffered reader", e1.getLocalizedMessage());
		}
		try {
			return new Task(reader);
		} catch (Exception e) {
			Log.e("Client fail new task", e.getLocalizedMessage());
		}
		return null;
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
	
	private HttpURLConnection openPostConnection( URL url ) 
	{
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			Log.e("Client openPostConnection openConnection", e.getMessage());
		}
		connection.setDoOutput(true);
		connection.setDoInput(true);
		
		try {
			connection.setRequestMethod("POST");
		} catch (ProtocolException e) {
			Log.e("Client openPost protocol exception", e.getMessage());
		}
		
		setupAuthorization(connection);
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
