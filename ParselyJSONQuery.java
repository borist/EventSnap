package com.hackny.spring.helpers;
import java.net.*;
import java.io.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class ParselyJSONQuery {
	URL query;
	String json = "";
	HttpURLConnection queryreader;
	public ParselyJSONQuery(String text) throws IOException, ParseException, InterruptedException{
		try {
			query = new URL("http://hack.parsely.com/parse");
			queryreader = (HttpURLConnection) query.openConnection();
			queryreader.setRequestMethod("POST");
		    queryreader.setUseCaches (false);
		    queryreader.setDoInput(true);
		    queryreader.setDoOutput(true);

		      //Send request
		      DataOutputStream wr = new DataOutputStream (queryreader.getOutputStream ());
		      wr.writeBytes ("text=" + text +"&wiki_filter=false");
		      wr.flush ();
		      wr.close ();
		      
		      InputStream is = queryreader.getInputStream();
		      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		      String line="";
		      while((line = rd.readLine()) != null) {
		        json+= line;
		      }
		      rd.close();
		      
			JSONObject j = (JSONObject)new JSONParser().parse(json);
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
	       	
	       
	       	while(((String) j.get("status")).equals("WORKING")){
	       		query = new URL(getURL);
				queryreader = (HttpURLConnection) query.openConnection();
				queryreader.setRequestMethod("GET");
				inputLine = "";
				in = new BufferedReader(new InputStreamReader(
                        queryreader.getInputStream()));
				while(in.ready())
		        	inputLine += in.readLine();
		        System.out.println(inputLine);
		       	j = (JSONObject)new JSONParser().parse(inputLine);
		       	
				Thread.sleep(1000);
	       	}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String getJSON(){
		return json;
	}
	
	public static void main(String args[]) throws IOException, ParseException, InterruptedException{
		ParselyJSONQuery q = new ParselyJSONQuery("I met a traveler from an antique land Who said: Two vast and trunkless legs of stone Stand in the desert. Near them, on the sand, Half sunk, a shattered visage lies, whose frown, And wrinkled lip, and sneer of cold command, Tell that its sculptor well those passions read Which yet survive, stamped on these lifeless things, The hand that mocked them, and the heart that fed; And on the pedestal these words appear: My name is Ozymandias, king of kings: Look on my works, ye Mighty, and despair!Nothing beside remains. Round the decay Of that colossal wreck, boundless and bare The lone and level sands stretch far away.");

	}
}
