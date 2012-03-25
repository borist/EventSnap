package com.hackny.spring;

import java.net.*;
import java.io.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class ParselyJSONQuery {
	URL query;
	String json = "";
	HttpURLConnection queryreader;
	JSONObject j;
	public ParselyJSONQuery(String url) throws IOException, ParseException{
		try {
			query = new URL(url);
			queryreader = (HttpURLConnection) query.openConnection();
			queryreader.setRequestMethod("POST");
		    queryreader.setUseCaches (false);
		    queryreader.setDoInput(true);
		    queryreader.setDoOutput(true);

		      //Send request
		      DataOutputStream wr = new DataOutputStream (queryreader.getOutputStream ());
		      wr.writeBytes ("text=Jake the dog and Finn the human are the stars of Adventure Time. wiki_filter=true");
		      wr.flush ();
		      wr.close ();
		      
		      InputStream is = queryreader.getInputStream();
		      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		      String line="";
		      while((line = rd.readLine()) != null) {
		        json+= line;
		      }
		      rd.close();
		      
			j = (JSONObject)new JSONParser().parse(json);
			String get =(String) j.get("url");
			//System.out.println(get);
			String getURL = "http://hack.parsely.com" + get;
			
			query = new URL(getURL);
			queryreader = (HttpURLConnection) query.openConnection();
			queryreader.setRequestMethod("GET");

	        BufferedReader in = new BufferedReader(new InputStreamReader(
	                                    queryreader.getInputStream()));
	        String inputLine = "";
	        while(in.ready())
	        	inputLine += in.readLine();
	        System.out.println(inputLine);
	       	j = (JSONObject)new JSONParser().parse(inputLine);
	       	queryreader.setRequestMethod("GET");
	       	in = new BufferedReader(new InputStreamReader(
                    queryreader.getInputStream()));
	       	while(((String) j.get("status")).equals("WORKING")){
	       		query = new URL(getURL);
				queryreader = (HttpURLConnection) query.openConnection();
				queryreader.setRequestMethod("GET");

		        in = new BufferedReader(new InputStreamReader(
		                                    queryreader.getInputStream()));
		        inputLine = "";
		        while(in.ready())
		        	inputLine += in.readLine();
		        System.out.println(inputLine);
		       	j = (JSONObject)new JSONParser().parse(inputLine);
		       	queryreader.setRequestMethod("GET");
	       		while(in.ready())
		        	inputLine += in.readLine();
		        System.out.println(inputLine);
		       	j = (JSONObject)new JSONParser().parse(inputLine);
		       	queryreader.setRequestMethod("GET");
		       	in = new BufferedReader(new InputStreamReader(
                        queryreader.getInputStream()));
	       	}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String getJSON(){
		return json;
	}
	
	public String getDescription(){
		return (String) j.get("data");
	}
	
	public static void main(String args[]) throws IOException, ParseException{
		ParselyJSONQuery q = new ParselyJSONQuery("http://hack.parsely.com/parse");

	}
}
