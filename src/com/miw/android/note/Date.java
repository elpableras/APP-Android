package com.miw.android.note;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.miw.android.notes.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

/**
 * Clase para obtener la fecha y la hora
 * 
 * @author Pablo
 * 
 */
public class Date extends Activity {

	public final static String CODE_SECOND_ACTIVITY = "key.second";
	private String day;
	private String month;
	private String year;
	private DatePicker datePicker;
	private TimePicker timePicker;
	private Button mButtonSet;

	/**
	 * Creación de la pantalla con los elementos
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.date);

		datePicker = (DatePicker) findViewById(R.id.datePicker);
		timePicker = (TimePicker) findViewById(R.id.timePicker);
		mButtonSet = (Button) findViewById(R.id.btSetFecha);

		mButtonSet.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				day = String.valueOf(datePicker.getDayOfMonth());
				month = String.valueOf(datePicker.getMonth() + 1);
				year = String.valueOf(datePicker.getYear());

				String s;
				Format formatter;
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
				calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());

				formatter = new SimpleDateFormat("HH:mm");
				s = formatter.format(calendar.getTime()); // 08:00

				String fecha = day + "-" + month + "-" + year + "/" + s;

				final Intent resultIntent = new Intent();
				final Bundle mBundle = new Bundle();

				mBundle.putString(CODE_SECOND_ACTIVITY, fecha);
				resultIntent.putExtras(mBundle);

				setResult(RESULT_OK, resultIntent);
				finish();
			}
		});
	}
}
