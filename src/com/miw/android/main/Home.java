package com.miw.android.main;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import com.miw.android.note.Date;
import com.miw.android.note.ListNotes;
import com.miw.android.note.Note;
import com.miw.android.notes.R;
import com.miw.android.sqlite.NoteData;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Actividad principal desde donde se crean el resto
 * @author Pablo
 *
 */
public class Home extends FragmentActivity implements OnInitListener {

	public final static String KEY_NAME = "key.name";
	private static final int REQUEST_CAMERA = 3;
	private static final int REQUEST_SELECT_PHOTO = 2;
	public final static int CODE_SECOND_ACTIVITY = 1;
	public final static int CODE_SPEAK_ACTIVITY = 0;

	private Button btNHoy;
	private Button btNFecha;
	private Button btHablar;
	private Button btImg;
	private Button btCamara;
	private Button btGuardar;
	private Button btList;
	private EditText tfFecha;
	private EditText tfFechaH;
	private EditText tfNote;
	private EditText tfCoord;
	private TextView tfLocation;

	// GPS
	private boolean mGeocoderAvailable;
	private boolean gpsEnabled;
	private boolean askGPS = false;
	// UI handler codes.
	private Handler mHandler;
	private static final int UPDATE_ADDRESS = 1;
	private static final int UPDATE_LATLNG = 2;
	private LocationManager mLocationManager;
	private boolean mUseBoth;
	private static final int TEN_SECONDS = 10000;
	private static final int TEN_METERS = 10;
	private static final int TWO_MINUTES = 1000 * 60 * 2;

	// Cámara
	private String photoPath = "";

	// TTS
	private TextToSpeech tts;

	/**
	 * Creación de la nota con sus campos
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		// Obtenemos los componentes para trabajar con ellos
		btNFecha = (Button) findViewById(R.id.btNotasFecha);
		btNHoy = (Button) findViewById(R.id.btNotasHoy);
		btHablar = (Button) findViewById(R.id.btSpeak);
		btImg = (Button) findViewById(R.id.btLoadImg);
		btCamara = (Button) findViewById(R.id.btCamara);
		btGuardar = (Button) findViewById(R.id.btSave);
		btList = (Button) findViewById(R.id.btList);
		tfFecha = (EditText) findViewById(R.id.editTextFecha);
		tfFechaH = (EditText) findViewById(R.id.editTextFechaH);
		tfNote = (EditText) findViewById(R.id.editTextNote);
		tfCoord = (EditText) findViewById(R.id.editTextCoord);
		tfLocation = (TextView) findViewById(R.id.textViewLocation);

		// GPS
		// The isPresent() helper method is only available on Gingerbread or
		// above.
		mGeocoderAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
				&& Geocoder.isPresent();

		// Get a reference to the LocationManager object.
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// Handler for updating text fields on the UI like the lat/long and
		// address.
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case UPDATE_ADDRESS:
					tfLocation.setText((String) msg.obj);
					break;
				case UPDATE_LATLNG:
					tfCoord.setText((String) msg.obj);
					break;
				}
			}
		};

		// Trabajamos con ellos
		btNFecha.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View arg0) {
				btNHoy.setEnabled(false);
				final Intent mIntent = new Intent(Home.this, Date.class);
				startActivityForResult(mIntent, CODE_SECOND_ACTIVITY);
			}
		});

		btNHoy.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View arg0) {
				btNFecha.setEnabled(false);
				fechaHoy();
			}

			@SuppressLint("SimpleDateFormat")
			private void fechaHoy() {
				Calendar c = Calendar.getInstance();

				SimpleDateFormat df3 = new SimpleDateFormat("dd-M-yyyy/HH:mm");
				String fecha = df3.format(c.getTime());
				// tfFechaH.setVisibility(View.VISIBLE);
				tfFechaH.setText("");
				tfFechaH.setText(tfFechaH.getText().toString() + fecha);
			}
		});

		// ARS
		btHablar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(
						RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
						"Android Speech Recognition example");
				startActivityForResult(intent, 0);
			}
		});

		// Cámara

		// Botón Seleccionar
		btImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intentC = new Intent(Intent.ACTION_PICK);
				intentC.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						MediaStore.Images.Media.CONTENT_TYPE);
				startActivityForResult(intentC, REQUEST_SELECT_PHOTO);
			}
		});

		// Botón de la Cámara
		btCamara.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showMessageCamara(
						"Information",
						"Baje la resolución de la cámara, para poder manejar la imagen.",
						getString(R.string.message_header_button));
			}

			private void showMessageCamara(final String title,
					final String msg, final String txtButton) {
				new AlertDialog.Builder(Home.this)
						.setTitle(title)
						.setIcon(android.R.drawable.ic_dialog_info)
						.setMessage(msg)
						.setNeutralButton(txtButton,
								new DialogInterface.OnClickListener() {
									public void onClick(
											final DialogInterface dialog,
											final int which) {
										String state = Environment
												.getExternalStorageState();
										if (Environment.MEDIA_MOUNTED
												.equals(state)) {
											long captureTime = System
													.currentTimeMillis();
											photoPath = Environment
													.getExternalStorageDirectory()
													+ "/DCIM/Camera/Point"
													+ captureTime + ".jpg";
											try {
												Intent intent = new Intent(
														"android.media.action.IMAGE_CAPTURE");
												File photo = new File(photoPath);
												intent.putExtra(
														MediaStore.EXTRA_OUTPUT,
														Uri.fromFile(photo));
												startActivityForResult(
														Intent.createChooser(
																intent,
																"Capture Image"),
														REQUEST_CAMERA);
											} catch (Exception e) {
												// ERROR
												Toast.makeText(
														getApplicationContext(),
														"Error boton de la cámara",
														Toast.LENGTH_LONG)
														.show();
											}
										}
									}
								}).show();
			}
		});

		// Guardar
		btGuardar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View arg0) {
				// Bean que se guardara en bd
				final Note note = new Note();
				// Para el split de la fecha
				String[] lista;

				final String nota = tfNote.getText().toString();
				if (nota == null || nota.length() == 0) {
					Toast.makeText(Home.this, R.string.errorDataNote,
							Toast.LENGTH_LONG).show();
					return;
				} else {
					note.setNota(nota);
				}

				if (tfFechaH.getText().toString().compareTo("") == 0) {
					final String fecha = tfFecha.getText().toString();
					if (fecha == null || fecha.length() == 0) {
						Toast.makeText(Home.this, R.string.errorDataDate,
								Toast.LENGTH_LONG).show();
						return;
					} else {
						lista = fecha.split("/");
						note.setFecha(lista[0]);
						note.setHora(lista[1]);
					}
				} else {
					final String fecha = tfFechaH.getText().toString();
					if (fecha == null || fecha.length() == 0) {
						Toast.makeText(Home.this, R.string.errorDataDate,
								Toast.LENGTH_LONG).show();
						return;
					} else {
						lista = fecha.split("/");
						note.setFecha(lista[0]);
						note.setHora(lista[1]);
					}
				}
				note.setCoordenadas(tfLocation.getText().toString());
				note.setImg(photoPath);

				// Abrimos conexion con la base de datos
				final NoteData nD = new NoteData(getApplicationContext());
				nD.open();

				// Guardamos la valoracion
				nD.createNote(note);
				nD.close();

				Toast.makeText(getApplicationContext(),
						getString(R.string.notaGuardadaCorrectamente),
						Toast.LENGTH_SHORT).show();
				tts = new TextToSpeech(Home.this, Home.this);
				speakOut();
				tfNote.setText("");
				tfFecha.setText("");
				tfFechaH.setText("");
				tfLocation.setText("");
				tfCoord.setText("");
				btNFecha.setEnabled(true);
				btNHoy.setEnabled(true);
				photoPath = "";
			}
		});

		// Mostrar Notas
		btList.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent myIntentAllNotes = new Intent(Home.this,
						ListNotes.class);
				startActivity(myIntentAllNotes);
				tfNote.setText("");
				tfFecha.setText("");
				tfFechaH.setText("");
				tfLocation.setText("");
				tfCoord.setText("");
				btNFecha.setEnabled(true);
				btNHoy.setEnabled(true);
				photoPath = "";
			}
		});
	}

	/**
	 * Menu
	 */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.help, menu);
		return true;
	}

	/**
	 * Opciones del Menu
	 */
	public boolean onOptionsItemSelected(final MenuItem item) {

		switch (item.getItemId()) {
		// case R.id.back:
		// final Intent resultIntent = new Intent();
		// final Bundle mBundle = new Bundle();
		//
		// mBundle.putInt(CODE_SECOND_ACTIVITY, mName.length());
		// resultIntent.putExtras(mBundle);
		//
		// setResult(RESULT_OK, resultIntent);
		// finish();
		// break;
		case R.id.help:
			showMessage(getString(R.string.message_header_help),
					getString(R.string.message_header_text),
					getString(R.string.message_header_button));
			break;
		case R.id.exit:
			finish();
			break;
		}
		return true;
	}

	/**
	 * Mensaje donde muestro mi nombre, apra la opción del menu
	 * @param title
	 * @param msg
	 * @param txtButton
	 */
	private void showMessage(final String title, final String msg,
			final String txtButton) {
		new AlertDialog.Builder(this)
				.setTitle(title)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setMessage(msg)
				.setNeutralButton(txtButton,
						new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog,
									final int which) {
								// do nothing
							}
						}).show();
	}

	/**
	 * Los diferentes datos que se llegan de las otras actividades
	 */
	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case CODE_SECOND_ACTIVITY:
			if (resultCode == RESULT_OK) {
				final Bundle mBundle = data.getExtras();
				final String fecha = mBundle
						.getString(Date.CODE_SECOND_ACTIVITY);
				tfFecha.setText("");
				tfFecha.setText(tfFecha.getText().toString() + fecha);
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, R.string.cancelActivity, Toast.LENGTH_LONG)
						.show();
			}
			break;
		case CODE_SPEAK_ACTIVITY:
			if (resultCode == RESULT_OK) {
				ArrayList<String> matches = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				tfNote.setText(matches.get(0).toString());
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, R.string.cancelActivity, Toast.LENGTH_LONG)
						.show();
			}
			break;
		case REQUEST_CAMERA:
			if (!photoPath.equals("") && (photoPath != null)) {
				// oPunto.setPath(photoPath);
				// TODO mensaje de foto seleccionada
				Toast.makeText(this, "Foto almacenada en: " + photoPath,
						Toast.LENGTH_LONG).show();
			}
			break;
		case REQUEST_SELECT_PHOTO:
			// Log.i("LOGTAG", "Mensaje de informacion Photo: "+requestCode);
			if (resultCode != 0) {
				Cursor c = getContentResolver().query(data.getData(), null,
						null, null, null);
				if (c.moveToFirst()) {
					photoPath = c.getString(1);
					// oPunto.setPath(photoPath);
					// TODO mensaje de foto seleccionada
				}
			}
		default:
			break;
		}
	}

	/**
	 * GPS
	 */
	@Override
	protected void onStart() {
		super.onStart();

		// Check if the GPS setting is currently enabled on the device.
		// This verification should be done during onStart() because the system
		// calls this method
		// when the user returns to the activity, which ensures the desired
		// location provider is
		// enabled each time the activity resumes from the stopped state.
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		gpsEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!gpsEnabled && !askGPS) {
			// Build an alert dialog here that requests that the user enable
			// the location services, then when the user clicks the "OK" button,
			// call enableLocationSettings()
			new EnableGpsDialogFragment().show(getSupportFragmentManager(),
					"enableGpsDialog");
		}
		mUseBoth = true;
		setup();
	}

	/**
	 *  Método que lanza los settings
	 */
	private void enableLocationSettings() {
		Intent settingsIntent = new Intent(
				Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(settingsIntent);
	}


	/**
	 * Set up fine and/or coarse location providers depending on whether the
	 * fine provider or
	 * both providers button is pressed.
	 */
	private void setup() {
		Location gpsLocation = null;
		Location networkLocation = null;
		mLocationManager.removeUpdates(listener);
		tfLocation.setText(R.string.unknown);
		tfCoord.setText(R.string.unknown);
		// Get fine location updates only.
		if (mUseBoth) {
			// Request updates from both fine (gps) and coarse (network)
			// providers.
			gpsLocation = requestUpdatesFromProvider(
					LocationManager.GPS_PROVIDER, R.string.not_support_gps);
			networkLocation = requestUpdatesFromProvider(
					LocationManager.NETWORK_PROVIDER,
					R.string.not_support_network);

			// If both providers return last known locations, compare the two
			// and use the better
			// one to update the UI. If only one provider returns a location,
			// use it.
			if (gpsLocation == null && networkLocation == null) {
				Toast.makeText(this, "Red y GPS no se encuentran disponibles",
						Toast.LENGTH_LONG).show();
			} else if (gpsLocation != null && networkLocation != null) {
				updateUILocation(getBetterLocation(gpsLocation, networkLocation));
			} else if (gpsLocation != null) {
				updateUILocation(gpsLocation);
			} else if (networkLocation != null) {
				updateUILocation(networkLocation);
			}
		}
	}

	/**
	 * Method to register location updates with a desired location provider. If
	 * the requested provider is not available on the device, the app displays a
	 * Toast with a message referenced by a resource id.
	 * 
	 * @param provider
	 *            Name of the requested provider.
	 * @param errorResId
	 *            Resource id for the string message to be displayed if the
	 *            provider does not exist on the device.
	 * @return A previously returned {@link android.location.Location} from the
	 *         requested provider, if exists.
	 */
	private Location requestUpdatesFromProvider(final String provider,
			final int errorResId) {
		Location location = null;
		if (mLocationManager.isProviderEnabled(provider)) {
			mLocationManager.requestLocationUpdates(provider, TEN_SECONDS,
					TEN_METERS, listener);
			location = mLocationManager.getLastKnownLocation(provider);
		} else {
			Toast.makeText(this, errorResId, Toast.LENGTH_LONG).show();
		}
		return location;
	}

	/**
	 * Geolocalización al reves
	 * @param location
	 */
	private void doReverseGeocoding(Location location) {
		// Since the geocoding API is synchronous and may take a while. You
		// don't want to lock
		// up the UI thread. Invoking reverse geocoding in an AsyncTask.
		(new ReverseGeocodingTask(this)).execute(new Location[] { location });
	}

	/**
	 * Puntos de latitud y longitud
	 * @param location
	 */
	private void updateUILocation(Location location) {
		// We're sending the update to a handler which then updates the UI with
		// the new
		// location.
		Message.obtain(mHandler, UPDATE_LATLNG,
				location.getLatitude() + ", " + location.getLongitude())
				.sendToTarget();

		// Bypass reverse-geocoding only if the Geocoder service is available on
		// the device.
		if (mGeocoderAvailable)
			doReverseGeocoding(location);
	}

	/**
	 * Actualización de la localización
	 */
	private final LocationListener listener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			// A new location update is received. Do something useful with it.
			// Update the UI with
			// the location update.
			updateUILocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix. Code taken from
	 * http://developer.android.com/guide/topics/location
	 * /obtaining-user-location.html
	 * 
	 * @param newLocation
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 * @return The better Location object based on recency and accuracy.
	 */
	protected Location getBetterLocation(Location newLocation,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return newLocation;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved.
		if (isSignificantlyNewer) {
			return newLocation;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return currentBestLocation;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(newLocation.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return newLocation;
		} else if (isNewer && !isLessAccurate) {
			return newLocation;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return newLocation;
		}
		return currentBestLocation;
	}

	/**
	 * Checks whether two providers are the same 
	 * @param provider1
	 * @param provider2
	 * @return
	 */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}


	/**
	 * AsyncTask encapsulating the reverse-geocoding API. Since the geocoder API
	 * is blocked, we do not want to invoke it from the UI thread. 
	 * @author Pablo
	 *
	 */
	private class ReverseGeocodingTask extends AsyncTask<Location, Void, Void> {
		Context mContext;

		public ReverseGeocodingTask(Context context) {
			super();
			mContext = context;
		}

		@Override
		protected Void doInBackground(Location... params) {
			Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

			Location loc = params[0];
			List<Address> addresses = null;
			try {
				addresses = geocoder.getFromLocation(loc.getLatitude(),
						loc.getLongitude(), 1);
			} catch (IOException e) {
				Log.i("*****LOGTAG*****", e.toString());
			}
			if (addresses != null && addresses.size() > 0) {
				Address address = addresses.get(0);
				// Format the first line of address (if available), city, and
				// country name.
				String addressText = String.format(
						"%s, %s, %s",
						address.getMaxAddressLineIndex() > 0 ? address
								.getAddressLine(0) : "", address.getLocality(),
						address.getCountryName());
				// Update address field on UI.
				Message.obtain(mHandler, UPDATE_ADDRESS, addressText)
						.sendToTarget();
			}
			return null;
		}
	}

	/**
	 * Dialog to prompt users to enable GPS on the device.
	 */
	@SuppressLint("ValidFragment")
	private class EnableGpsDialogFragment extends DialogFragment {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return new AlertDialog.Builder(getActivity())
					.setTitle(R.string.gps)
					.setMessage(R.string.enable_gps_dialog)
					.setPositiveButton(R.string.enable,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									enableLocationSettings();
								}
							})
					.setNegativeButton(R.string.disable,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// Que hace si se pulsa no
									askGPS = true;
								}
							}).create();
		}
	}

	/**
	 * Destroy del TTS
	 */
	@Override
	public void onDestroy() {
		// Don't forget to shutdown tts!
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}

	/**
	 * Inicialización del TTS
	 */
	public void onInit(int status) {
		// TTS engine initialises here, and method speak works from this moment
		if (status == TextToSpeech.SUCCESS) {

			int result = tts.setLanguage(Locale.getDefault());

			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "This Language is not supported");
				Toast.makeText(getApplicationContext(),
						"Este idioma no está soportado", Toast.LENGTH_LONG)
						.show();
			} else {
				speakOut();
			}

		} else {
			Log.e("TTS", "Initilization Failed!");
		}
	}

	/**
	 * SpeakOut
	 */
	private void speakOut() {

		String text = "Los datos han sido guardados";
		tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}
}
