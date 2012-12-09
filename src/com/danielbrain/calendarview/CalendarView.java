package com.danielbrain.calendarview;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class CalendarView extends Fragment {
	protected final Calendar calendar;
	private final Locale locale;
	private ViewSwitcher calendarSwitcher;
	private TextView currentMonth;
	private CalendarAdapter calendarAdapter;

	public CalendarView() {
		calendar = Calendar.getInstance();
		locale = Locale.getDefault();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final RelativeLayout calendarLayout = (RelativeLayout) inflater.inflate(R.layout.calendar, null);
		final GridView calendarDayGrid = (GridView) calendarLayout.findViewById(R.id.calendar_days_grid);
		final GestureDetector swipeDetector = new GestureDetector(getActivity(), new SwipeGesture(getActivity()));
		final GridView calendarGrid = (GridView) calendarLayout.findViewById(R.id.calendar_grid);
		calendarSwitcher = (ViewSwitcher) calendarLayout.findViewById(R.id.calendar_switcher);
		currentMonth = (TextView) calendarLayout.findViewById(R.id.current_month);

		calendarAdapter = new CalendarAdapter(getActivity(), calendar);
		updateCurrentMonth();

		final TextView nextMonth = (TextView) calendarLayout.findViewById(R.id.next_month);
		nextMonth.setOnClickListener(new NextMonthClickListener());
		final TextView prevMonth = (TextView) calendarLayout.findViewById(R.id.previous_month);
		prevMonth.setOnClickListener(new PreviousMonthClickListener());
		calendarGrid.setOnItemClickListener(new DayItemClickListener());

		calendarGrid.setAdapter(calendarAdapter);
		calendarGrid.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return swipeDetector.onTouchEvent(event);
			}
		});
		calendarDayGrid.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.day_item, getResources().getStringArray(R.array.days_array)));

		//XXX for debugging only
		currentMonth.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e("", "==========");
				Iterator<CalendarItem> selectedDaysIterator = calendarAdapter.getSelectedDaysIterator();
				while (selectedDaysIterator.hasNext()) {
					CalendarItem item = selectedDaysIterator.next();
					Log.e("", item.toString());
				}
				Log.e("", "==========");
			}
		});

		return calendarLayout;
	}

	protected void updateCurrentMonth() {
		calendarAdapter.refreshDays();
		currentMonth.setText(String.format(locale, "%tB", calendar) + " " + calendar.get(Calendar.YEAR));
	}

	private final class DayItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			CalendarItem item = (CalendarItem) view.getTag();
			calendarAdapter.toggleSelected(item);
		}
	}

	protected final void onNextMonth() {
		calendarSwitcher.setInAnimation(getActivity(), R.anim.in_from_right);
		calendarSwitcher.setOutAnimation(getActivity(), R.anim.out_to_left);
		calendarSwitcher.showNext();
		calendar.add(Calendar.MONTH, 1);
		updateCurrentMonth();
	}

	protected final void onPreviousMonth() {
		calendarSwitcher.setInAnimation(getActivity(), R.anim.in_from_left);
		calendarSwitcher.setOutAnimation(getActivity(), R.anim.out_to_right);
		calendarSwitcher.showPrevious();
		calendar.add(Calendar.MONTH, -1);
		updateCurrentMonth();
	}

	private final class NextMonthClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			onNextMonth();
		}
	}

	private final class PreviousMonthClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			onPreviousMonth();
		}
	}

	private final class SwipeGesture extends SimpleOnGestureListener {
		private final int swipeMinDistance;
		private final int swipeThresholdVelocity;

		public SwipeGesture(Context context) {
			final ViewConfiguration viewConfig = ViewConfiguration.get(context);
			swipeMinDistance = viewConfig.getScaledTouchSlop();
			swipeThresholdVelocity = viewConfig.getScaledMinimumFlingVelocity();
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (e1.getX() - e2.getX() > swipeMinDistance && Math.abs(velocityX) > swipeThresholdVelocity) {
				onNextMonth();
			} else if (e2.getX() - e1.getX() > swipeMinDistance && Math.abs(velocityX) > swipeThresholdVelocity) {
				onPreviousMonth();
			}
			return false;
		}
	}

}
