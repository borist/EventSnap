package com.abbyy.ocrsdk;

import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.util.Log;

public class Task {
	public enum TaskStatus {
		Unknown, Submitted, Queued, InProgress, Completed, ProcessingFailed, Deleted, NotEnoughCredits 
	}
	
	public Task( Reader reader ) throws Exception
	{
		// Read all text into string
		//String data = new Scanner(reader).useDelimiter("\\A").next();
		// Read full task information from xml
		InputSource source = new InputSource();
		source.setCharacterStream(reader);
		
		Log.e("WTF IN TASK IS THIS", source.toString());
		
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = null;
		
		try {
			doc = builder.parse(source);
		}
		
		catch (Exception e) {
			final Writer result = new StringWriter();
	        final PrintWriter printWriter = new PrintWriter(result);
	        e.printStackTrace(printWriter);
			Log.e("TASK DIED", result.toString());
			Log.e("TASK DIED..", e.getClass() + ", " + e.getMessage());
		}		
		
		NodeList taskNodes = doc.getElementsByTagName("task");
		Element task = (Element)taskNodes.item(0);
		
		parseTask(task);
	}
	
	
	
	public TaskStatus Status = TaskStatus.Unknown;
	public String Id;
	public String DownloadUrl;
	
	public Boolean IsTaskActive()
	{
		if( Status == TaskStatus.Queued || Status == TaskStatus.InProgress )
			return true;
		
		return false;
	}
	
	private void parseTask( Element taskElement )
	{
		Id = taskElement.getAttribute("id");
		Status = parseTaskStatus( taskElement.getAttribute( "status" ) );
		if( Status == TaskStatus.Completed )
			DownloadUrl = taskElement.getAttribute("resultUrl");
	}
	
	private TaskStatus parseTaskStatus( String status )
	{
		if( status.equals( "Submitted") )
			return TaskStatus.Submitted;
		else if( status.equals( "Queued" ) )
			return TaskStatus.Queued;
		else if( status.equals( "InProgress" ) )
			return TaskStatus.InProgress;
		else if( status.equals( "Completed") )
			return TaskStatus.Completed;
		else if (status.equals( "ProcessingFailed") )
			return TaskStatus.ProcessingFailed;
		else if (status.equals( "Deleted") )
			return TaskStatus.Deleted;
		else if (status.equals( "NotEnoughCredits") )
			return TaskStatus.NotEnoughCredits;
		else
			return TaskStatus.Unknown;
	}
	
}
