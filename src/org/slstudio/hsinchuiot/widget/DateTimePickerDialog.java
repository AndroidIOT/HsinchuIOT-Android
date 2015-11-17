package org.slstudio.hsinchuiot.widget;

import java.util.Calendar;

import org.slstudio.hsinchuiot.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.content.DialogInterface.OnClickListener;

public class DateTimePickerDialog extends AlertDialog implements
		OnClickListener, OnDateChangedListener, OnTimeChangedListener {
	private final OnDateTimeChangedListener mCallback;
	private final DatePicker mDatePicker;
	private final TimePicker mTimePicker;
	private static final String HOUR = "hour";
	private static final String MINUTE = "minute";
	private static final String IS_24_HOUR = "is24hour";
	private static final String YEAR = "year";
	private static final String MONTH = "month";
	private static final String DAY = "day";
	private final Calendar mCalendar;

	public DateTimePickerDialog(final Context context, final int theme,
			final OnDateTimeChangedListener callBack) {
		super(context, theme);
		this.mCallback = callBack;
		this.mCalendar = Calendar.getInstance();
		this.updateTitle();
		this.setButton(BUTTON_POSITIVE, context.getText(android.R.string.ok),
				this);
		this.setButton(BUTTON_NEGATIVE,
				context.getText(android.R.string.cancel),
				(OnClickListener) null);
		this.mDatePicker = new DatePicker(context);
		this.mDatePicker.init(this.mCalendar.get(Calendar.YEAR),
				this.mCalendar.get(Calendar.MONTH),
				this.mCalendar.get(Calendar.DAY_OF_MONTH), this);
		mDatePicker.setCalendarViewShown(false);
		this.mTimePicker = new TimePicker(context);
		this.mTimePicker.setIs24HourView(true);
		this.mTimePicker.setCurrentHour(this.mCalendar
				.get(Calendar.HOUR_OF_DAY));
		this.mTimePicker.setCurrentMinute(this.mCalendar.get(Calendar.MINUTE));
		this.mTimePicker.setOnTimeChangedListener(this);
		final LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setGravity(Gravity.CENTER);

		// LinearLayout.LayoutParams lp = new
		// LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		// lp.leftMargin = 50;

		TextView dateText = new TextView(context);
		dateText.setText("日期");
		// dateText.setLayoutParams(lp);
		dateText.setTextSize(context.getResources().getDimension(
				R.dimen.dialog_subtittle_text_size));
		dateText.setGravity(Gravity.CENTER);

		TextView timeText = new TextView(context);
		timeText.setText("时间");
		// timeText.setLayoutParams(lp);
		timeText.setTextSize(context.getResources().getDimension(
				R.dimen.dialog_subtittle_text_size));
		timeText.setGravity(Gravity.CENTER);

		linearLayout.addView(dateText);
		linearLayout.addView(this.mDatePicker);
		linearLayout.addView(timeText);
		linearLayout.addView(this.mTimePicker);

		this.setView(linearLayout);
	}

	public DateTimePickerDialog(final Context context,
			final OnDateTimeChangedListener callBack) {
		this(context, 0, callBack);
	}

	/**
	 * * Gets the {@link DatePicker} contained in this dialog. * * @return The
	 * DatePicker view.
	 */
	public DatePicker getDatePicker() {
		return this.mDatePicker;
	}

	/**
	 * * Gets the {@link TimePicker} contained in this dialog. * * @return The
	 * TimePicker view.
	 */
	public TimePicker getTimePicker() {
		return this.mTimePicker;
	}

	@Override
	public void onClick(final DialogInterface dialog, final int which) {
		this.tryNotifyDateTimeSet();
	}

	@Override
	public void onDateChanged(final DatePicker view, final int year,
			final int month, final int day) {
		this.mDatePicker.init(year, month, day, this);
		this.mCalendar.set(Calendar.YEAR, year);
		this.mCalendar.set(Calendar.MONTH, month);
		this.mCalendar.set(Calendar.DAY_OF_MONTH, day);
		this.updateTitle();
	}

	@Override
	public void onRestoreInstanceState(final Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		final int year = savedInstanceState.getInt(YEAR);
		final int month = savedInstanceState.getInt(MONTH);
		final int day = savedInstanceState.getInt(DAY);
		this.mDatePicker.init(year, month, day, this);
		final int hour = savedInstanceState.getInt(HOUR);
		final int minute = savedInstanceState.getInt(MINUTE);
		this.mTimePicker.setIs24HourView(savedInstanceState
				.getBoolean(IS_24_HOUR));
		this.mTimePicker.setCurrentHour(hour);
		this.mTimePicker.setCurrentMinute(minute);
	}

	@Override
	public Bundle onSaveInstanceState() {
		final Bundle state = super.onSaveInstanceState();
		state.putInt(YEAR, this.mDatePicker.getYear());
		state.putInt(MONTH, this.mDatePicker.getMonth());
		state.putInt(DAY, this.mDatePicker.getDayOfMonth());
		state.putInt(HOUR, this.mTimePicker.getCurrentHour());
		state.putInt(MINUTE, this.mTimePicker.getCurrentMinute());
		state.putBoolean(IS_24_HOUR, this.mTimePicker.is24HourView());
		return state;
	}

	@Override
	public void onTimeChanged(final TimePicker view, final int hourOfDay,
			final int minute) {
		this.mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		this.mCalendar.set(Calendar.MINUTE, minute);
	}

	public void setDate(final int year, final int monthOfYear,
			final int dayOfMonth) {
		this.mDatePicker.updateDate(year, monthOfYear, dayOfMonth);
	}

	public void setDateAndTime(final int year, final int monthOfYear,
			final int dayOfMonth, final int hour, final int minute,
			final boolean is24HourView) {
		this.setDate(year, monthOfYear, dayOfMonth);
		this.setTime(hour, minute, is24HourView);
	}

	public void setTime(final int hour, final int minute,
			final boolean is24HourView) {
		this.mTimePicker.setIs24HourView(is24HourView);
		this.mTimePicker.setCurrentHour(hour);
		this.mTimePicker.setCurrentMinute(minute);
	}

	private void tryNotifyDateTimeSet() {
		if (this.mCallback != null) {
			this.mDatePicker.clearFocus();
			this.mTimePicker.clearFocus();
			this.mCallback.onDateTimeChanged(this.mDatePicker,
					this.mTimePicker, this.mDatePicker.getYear(),
					this.mDatePicker.getMonth(),
					this.mDatePicker.getDayOfMonth(),
					this.mTimePicker.getCurrentHour(),
					this.mTimePicker.getCurrentMinute());
		}
	}

	private void updateTitle() {
		final String title = DateUtils.formatDateTime(this.getContext(),
				this.mCalendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_NO_YEAR
						| DateUtils.FORMAT_SHOW_WEEKDAY);
		this.setTitle(title);
	}

	public interface OnDateTimeChangedListener {
		void onDateTimeChanged(DatePicker dateView, TimePicker timeView,
				int year, int monthOfYear, int dayOfMonth, int hourOfDay,
				int minute);
	}

}
