package com.hackny.spring.helpers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import android.util.Log;

public class TextProcessor {

	public String stime, etime, day, month, year, title, description;
	public String msg;


	public TextProcessor(String file) throws Exception{
		msg = "";

		if (file==null) {
			throw new IllegalArgumentException("null input given");
		}

		FileReader f = new FileReader(file);
		BufferedReader b = new BufferedReader(f);

		while(b.ready() == true){
			String c = b.readLine();
			c = c.trim().toLowerCase();
			c = c.trim().toLowerCase();
			msg = msg + " " + c;
		}
		msg = msg.toLowerCase();		

		String[] date = dates(msg).split("\\-|\\/|\\.");
		if (date.length != 3) 
			date = null;
		else {
			month = date[0];
			day = date[1];
			year = date[2];
		}
		
		String[] tiempo = time(msg);
		stime = tiempo[0];
		if (tiempo.length > 1)
			etime = tiempo[1];
	}

	public String[] time(String msg){
		String tiempo = "(((([0]?[1-9]|1[0-2]):[0-5][0-9])|([0-9]))( )?(AM|am|aM|Am|PM|pm|pM|Pm))|(([0]?[0-9]|1[0-9]|2[0-3]):[0-5][0-9](:[0-5][0-9])?)|([0]?[1-9](\\-))";
		Pattern timeRegex = Pattern.compile(tiempo);
		Matcher timeMatch = timeRegex.matcher(msg);
		String[] time = new String[10];
		int i = 0;
		while(timeMatch.find()){
			time[i] = timeMatch.group();
			if (time[i].charAt(time[i].length() - 1) =='-') 
				time[i] = time[i].substring(0, time[i].length()-1);
			i++;
		}
		return time;
	}

	public String dates(String msg){
		int d1 = 0, d2 = 0, d3 = 0;
		String formatted = "(0[1-9]|1[012]|[1-9])(-|\\/|\\.)(0[1-9]|[1-9]|[12][0-9]|3[01])(-|\\/|\\.)(((19|20)\\d{2})|(\\d{2}))";
		Pattern timeRegex = Pattern.compile(formatted);
		Matcher timeMatch = timeRegex.matcher(msg);
		String date = "";
		int i = 0;
		while(timeMatch.find()){
			date = timeMatch.group();
			i++;
		}
		if (i != 0) return date;
		//else is for when date is not in format 3/24/12
		else {
			//must be jan 27th '12
			String month = "january|jan|february|feb|march|mar|april|apr|may|june|jun|july|jul|" +
			"august|aug|september|sep|sept|october|oct|november|nov|december|dec";
			String day = "((0[1-9]|[1-9]|[12][0-9]|3[01])((st)|(nd)|(rd)|(th)))|" +
					"(january|jan|february|feb|march|mar|april|apr|may|june|jun|july|jul|" +
			"august|aug|september|sep|sept|october|oct|november|nov|december|dec)" +
			" (0[1-9]|[12][0-9]|3[01]|[1-9])";
			String year = "((19|20)\\d{2})|((\\')\\d{2})";
			Pattern dayReg = Pattern.compile(day);
			Pattern monthReg = Pattern.compile(month);
			Pattern yearReg = Pattern.compile(year);
			Matcher dayMatch = dayReg.matcher(msg);
			Matcher monthMatch = monthReg.matcher(msg);
			Matcher yearMatch = yearReg.matcher(msg);
			
			
			while(monthMatch.find()){
				String match = "";
				int count = 0;
				for (int j = 0; j < msg.length(); j++){
					char c = msg.charAt(j);
					if (c == monthMatch.group().charAt(0)) d1 = count; 
					count++;
					if (d1 != 0) break;
				}
				
				if (monthMatch.group().equals("january") || monthMatch.group().equals("jan")) match = "1";
				if (monthMatch.group().equals("february") || monthMatch.group().equals("feb")) match = "2";
				if (monthMatch.group().equals("march") || monthMatch.group().equals("mar")) match = "3";
				if (monthMatch.group().equals("april") || monthMatch.group().equals("apr")) match = "4";
				if (monthMatch.group().equals("may")) match = "5";
				if (monthMatch.group().equals("june") || monthMatch.group().equals("jun")) match = "6";
				if (monthMatch.group().equals("july") || monthMatch.group().equals("jul")) match = "7";
				if (monthMatch.group().equals("august") || monthMatch.group().equals("aug")) match = "8";
				if (monthMatch.group().equals("september") || monthMatch.group().equals("sept") || monthMatch.group().equals("sep")) match = "9";
				if (monthMatch.group().equals("october") || monthMatch.group().equals("oct")) match = "10";
				if (monthMatch.group().equals("november") || monthMatch.group().equals("nov")) match = "11";
				if (monthMatch.group().equals("december") || monthMatch.group().equals("dec")) match = "12";
				date = date + match;
				date = date + "/";
			}
			while (dayMatch.find()){
				int count = 0;
				for (int j = 0; j < msg.length(); j++){
					char c = msg.charAt(j);
					if (c == dayMatch.group().charAt(0)) d2 = count;
					count++;
					if (d2 != 0) break;
				}
				
				if (dayMatch.group().length() > 4)
					date = date + dayMatch.group().substring(dayMatch.group().length() - 2, dayMatch.group().length());
				else 
					date = date + dayMatch.group().substring(0, dayMatch.group().length() - 2);
				date = date + "/";
			}
			while (yearMatch.find()){
				int count = 0;
				for (int j = 0; j < msg.length(); j++){
					char c = msg.charAt(j);
					if (c == yearMatch.group().charAt(0)) d3 = count;
					count++;
					if (d3 != 0) break;
				}
				
				String yurr = yearMatch.group();
				if (yurr.length() > 2)
					yurr = yurr.substring(2);
				date = date + yurr;
			}
			if (date.charAt(date.length() - 1) == '/')
				date = date + (Calendar.getInstance().get(Calendar.YEAR) - 2000);
		
			if (d3 == 0) d3 = 10000;
			
			int d = Math.min(d1, Math.min(d2, d3));

			String titleReg = "[a-zA-Z0-9 ]+";
			Pattern titleRegex = Pattern.compile(titleReg);
			Matcher titleMatch = titleRegex.matcher(msg.substring(0, d));
			while (titleMatch.find())
				this.title = titleMatch.group();
			
			//uppercase first char of each word in title
			StringBuilder b = new StringBuilder(this.title);
			int k = 0;
			do {
			  b.replace(k, k + 1, b.substring(k, k + 1).toUpperCase());
			  k =  b.indexOf(" ", k) + 1;
			} while (k > 0 && k < b.length());
			this.title = b.toString();
			
			//output will be of format 3/24/12
			return date;
		}
	}



//	public static void main(String args[]){
//		TextProcessor t = null;
//		try {
//			t = new TextProcessor("pirates.txt");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

}