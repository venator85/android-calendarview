package com.danielbrain.calendarview;

import java.util.Calendar;
import java.util.Iterator;
import java.util.NoSuchElementException;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CalendarAdapter extends BaseAdapter {
	private static final int FIRST_DAY_OF_WEEK = Calendar.MONDAY;
	private final Calendar calendar;
	private final CalendarItem today;
	private final LayoutInflater inflater;
	private CalendarItem[] days;

	public CalendarAdapter(Context context, Calendar monthCalendar) {
		calendar = monthCalendar;
		today = new CalendarItem(monthCalendar);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return days.length;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return days[position] != null;
	}

	@Override
	public Object getItem(int position) {
		return days[position];
	}

	@Override
	public long getItemId(int position) {
		final CalendarItem item = days[position];
		if (item != null) {
			return days[position].id;
		}
		return -1;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null) {
			view = inflater.inflate(R.layout.calendar_item, null);
		}
		final TextView dayView = (TextView) view;
		final CalendarItem currentItem = days[position];
		dayView.setTag(currentItem);

		if (currentItem == null) {
			dayView.setBackgroundDrawable(null);
			dayView.setText(null);
		} else {
			if (currentItem.equals(today)) {
				dayView.setBackgroundResource(R.drawable.today_background);
			} else if (currentItem.selected) {
				dayView.setBackgroundResource(R.drawable.selected_background);
			} else {
				dayView.setBackgroundResource(R.drawable.normal_background);
			}
			dayView.setText(currentItem.text);
		}

		return view;
	}

	public final void toggleSelected(CalendarItem item) {
		item.selected = !item.selected;
		notifyDataSetChanged();
	}

	public final void refreshDays() {
		final int year = calendar.get(Calendar.YEAR);
		final int month = calendar.get(Calendar.MONTH);
		final int firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK);
		final int lastDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		final int blankies;
		final CalendarItem[] days;

		if (firstDayOfMonth == FIRST_DAY_OF_WEEK) {
			blankies = 0;
		} else if (firstDayOfMonth < FIRST_DAY_OF_WEEK) {
			blankies = Calendar.SATURDAY - (FIRST_DAY_OF_WEEK - 1);
		} else {
			blankies = firstDayOfMonth - FIRST_DAY_OF_WEEK;
		}
		days = new CalendarItem[lastDayOfMonth + blankies];

		for (int day = 1, position = blankies; position < days.length; position++) {
			days[position] = new CalendarItem(year, month, day++);
		}

		this.days = days;
		notifyDataSetChanged();
	}

	public Iterator<CalendarItem> getSelectedDaysIterator() {
		return new SelectedDaysIterator();
	}

	private class SelectedDaysIterator implements Iterator<CalendarItem> {
		private int i;
		private CalendarItem next;

		public SelectedDaysIterator() {
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("This is a read-only iterator.");
		}

		@Override
		public boolean hasNext() {
			if (next != null) {
				return true;
			}
			return setNext();
		}

		private boolean setNext() {
			while (i < days.length) {
				CalendarItem object = days[i++];
				if (object != null && object.selected) {
					next = object;
					return true;
				}
			}
			return false;
		}

		@Override
		public CalendarItem next() {
			if (next == null) {
				if (!setNext()) {
					throw new NoSuchElementException();
				}
			}
			CalendarItem toRet = next;
			next = null;
			return toRet;
		}
	}

}