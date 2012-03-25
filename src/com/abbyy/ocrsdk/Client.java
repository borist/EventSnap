package com.abbyy.ocrsdk;

import java.io.*;
import java.net.*;

public class Client {
	public String ApplicationId;
	public String Password;
	
	public String ServerUrl = "http://cloud.ocrsdk.com";
	
	public Task ProcessImage( String filePath, ProcessingSettings settings) throws Exception
	{
		URL url = new URL(ServerUrl + "/processImage?" + settings.AsUrlParams());
		byte[] fileContents = readDataFromFile( filePath );
		
		HttpURLConnection connection = openPostConnection(url);
		
		connection.setRequestProperty("Content-Length", Integer.toString(fileContents.length));
		connection.getOutputStream().write( fileContents );
		
		BufferedReader reader = new BufferedReader( new InputStreamReader( connection.getInputStream()));
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
