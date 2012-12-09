package com.danielbrain.calendarview;

import java.util.Calendar;

public class CalendarItem {
	public int year;
	public int month;
	public int day;
	public String text;
	public long id;
	public boolean selected;

	public CalendarItem(Calendar calendar) {
		this(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
	}

	public CalendarItem(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.text = String.valueOf(day);
		this.id = Long.valueOf(year + "" + month + "" + day);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + day;
		result = prime * result + month;
		result = prime * result + year;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CalendarItem other = (CalendarItem) obj;
		if (day != other.day) {
			return false;
		}
		if (month != other.month) {
			return false;
		}
		if (year != other.year) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "CalendarItem [" + day + "/" + month + "/" + year + ", selected=" + selected + "]";
	}
}