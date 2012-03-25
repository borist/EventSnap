package com.hackny.spring;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CalendarActivity extends Activity {
	private Button m_button_getEvents;
	private TextView m_text_event;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);
		getCalendars();
	    populateGetEventsBtn();
	}
	
  
    private void populateGetEventsBtn() {
    	m_button_getEvents = (Button) findViewById(R.id.button_get_events);
    	m_button_getEvents.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getLastThreeEvents();
			}
		});
    }
    
    /****************************************************************
     * Data part
     */
    /*retrieve a list of available calendars*/
    private MyCalendar m_calendars[];
    private String m_selectedCalendarId = "0";
    private void getCalendars() {
    	String[] l_projection = new String[]{"_id", "displayName"};
    	Uri l_calendars;
    	if (Build.VERSION.SDK_INT >= 8) {
    		l_calendars = Uri.parse("content://com.android.calendar/calendars");
    	} else {
    		l_calendars = Uri.parse("content://calendar/calendars");
    	}
    	Cursor l_managedCursor = this.managedQuery(l_calendars, l_projection, null, null, null);	//all calendars
    	//Cursor l_managedCursor = this.managedQuery(l_calendars, l_projection, "selected=1", null, null);   //active calendars
    	if (l_managedCursor.moveToFirst()) {
    		m_calendars = new MyCalendar[l_managedCursor.getCount()];
    		String l_calName;
    		String l_calId;
    		int l_cnt = 0;
    		int l_nameCol = l_managedCursor.getColumnIndex(l_projection[1]);
    		int l_idCol = l_managedCursor.getColumnIndex(l_projection[0]);
    		do {
    			l_calName = l_managedCursor.getString(l_nameCol);
    			l_calId = l_managedCursor.getString(l_idCol);
    			m_calendars[l_cnt] = new MyCalendar(l_calName, l_calId);
    			++l_cnt;
    		} while (l_managedCursor.moveToNext());
    	}
    }

    /*get a list of events
     * http://jimblackler.net/blog/?p=151*/
    private void getLastThreeEvents() {
    	Uri l_eventUri;
    	if (Build.VERSION.SDK_INT >= 8) {
    		l_eventUri = Uri.parse("content://com.android.calendar/events");
    	} else {
    		l_eventUri = Uri.parse("content://calendar/events");
    	}
    	String[] l_projection = new String[]{"title", "dtstart", "dtend"};
    	Cursor l_managedCursor = this.managedQuery(l_eventUri, l_projection, "calendar_id=" + m_selectedCalendarId, null, "dtstart DESC, dtend DESC");
    	//Cursor l_managedCursor = this.managedQuery(l_eventUri, l_projection, null, null, null);
    	if (l_managedCursor.moveToFirst()) {
    		int l_cnt = 0;
    		String l_title;
    		String l_begin;
    		String l_end;
    		StringBuilder l_displayText = new StringBuilder();
    		int l_colTitle = l_managedCursor.getColumnIndex(l_projection[0]);
    		int l_colBegin = l_managedCursor.getColumnIndex(l_projection[1]);
    		int l_colEnd = l_managedCursor.getColumnIndex(l_projection[1]);
    		do {
    			l_title = l_managedCursor.getString(l_colTitle);
    			l_begin = getDateTimeStr(l_managedCursor.getString(l_colBegin));
    			l_end = getDateTimeStr(l_managedCursor.getString(l_colEnd));
    			l_displayText.append(l_title + "\n" + l_begin + "\n" + l_end + "\n----------------\n");
    			++l_cnt;
    		} while (l_managedCursor.moveToNext() && l_cnt < 3);
    		m_text_event.setText(l_displayText.toString());
    	}
    }
    /************************************************
     * utility part
     */
    private static final String DATE_TIME_FORMAT = "yyyy MMM dd, HH:mm:ss";
    public static String getDateTimeStr(int p_delay_min) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
		if (p_delay_min == 0) {
			return sdf.format(cal.getTime());
		} else {
			Date l_time = cal.getTime();
			l_time.setMinutes(l_time.getMinutes() + p_delay_min);
			return sdf.format(l_time);
		}
	}
    public static String getDateTimeStr(String p_time_in_millis) {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
    	Date l_time = new Date(Long.parseLong(p_time_in_millis));
    	return sdf.format(l_time);
    }

}

class MyCalendar {
	public String name;
	public String id;
	
	public MyCalendar(String name, String id) {
		this.name = name;
		this.id = id;
	}
	
	@Override
	public String toString() {
		return name;
	}
}