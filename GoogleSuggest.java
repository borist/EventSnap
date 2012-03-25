import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

//requires query without newlines
public class GoogleSuggest {

	  String url
	   = "http://google.com/complete/search?output=toolbar&q=";
	  String output;
	  public GoogleSuggest(String input) {
	    try {
	      String[] process = input.split(" ");
	      output = "";
	      for(int i = 0; i < process.length - 1; i = i + 2)
	      {
	    	  url = "http://google.com/complete/search?output=toolbar&q=" + process[i] + "+" + process[i+1];
	    	  System.out.println(url);
	    	  URL u = new URL(url);
	    	  URLConnection uc = u.openConnection();
	    	  HttpURLConnection connection = (HttpURLConnection) uc;
	    	  connection.setDoInput(true); 
	    	  connection.setRequestMethod("GET");
	    	  InputStream in = connection.getInputStream();
	    	  output = output + parse(in);
		      in.close();
		      connection.disconnect();
	      }
	    }
	    catch (IOException e) {
	      System.err.println(e); 
	    }
	  
	  }
	  
	  public String fuqs(){
		  return output;
	  }

	  private static String parse(
	   InputStream in) throws IOException, NumberFormatException, 
	   StringIndexOutOfBoundsException {
	    
	    StringBuffer sb = new StringBuffer();
	    Reader reader = new InputStreamReader(in, "UTF-8");
	    int c;
	    while ((c = in.read()) != -1) sb.append((char) c);
	    String document = sb.toString();
	    String startTag = "<suggestion data=";
	    String endTag = "/>";
	    int start = document.indexOf(startTag) + startTag.length();
	    int end = document.indexOf(endTag);
	    String result = document.substring(start, end);
	    return result;
	    
	  }  

}
