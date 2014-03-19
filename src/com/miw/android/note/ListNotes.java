package com.miw.android.note;

import java.util.List;
import java.util.Locale;

import com.miw.android.adapter.Adaptador;
import com.miw.android.notes.R;
import com.miw.android.sqlite.NoteData;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * SQLite
 * 
 */
public class ListNotes extends ListActivity implements OnInitListener {

	private ListView lv;
	private TextToSpeech tts;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Estabecemos el contenido a mostrar
		setContentView(R.layout.lista_notas);

		tts = new TextToSpeech(this, this);
		speakOut();
		// Obtenemos todas las valoraciones
		final NoteData nD = new NoteData(getApplicationContext());
		nD.open();

		final List<Note> values = nD.getAllNotes();

		lv = (ListView) findViewById(android.R.id.list);
		final Adaptador adaptador = new Adaptador(this, values);
		lv.setAdapter(adaptador);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long arg) {
				showMessage("Eliminar", "Desea eliminar está entrada", "Si",
						"No", position);
			}

			private void showMessage(final String title, final String msg,
					final String txtButtonSi, final String txtButtonNo,
					final int position) {
				new AlertDialog.Builder(ListNotes.this)
						.setTitle(title)
						.setIcon(android.R.drawable.ic_input_delete)
						.setMessage(msg)
						.setPositiveButton(txtButtonSi,
								new DialogInterface.OnClickListener() {
									public void onClick(
											final DialogInterface dialog,
											final int which) {
										final NoteData nD2 = new NoteData(
												getApplicationContext());
										nD2.open();
										String cad = nD2
												.deleteNote(values
														.get(position)
														.getFecha(), values
														.get(position)
														.getHora());
										Toast.makeText(ListNotes.this, cad,
												Toast.LENGTH_SHORT).show();
										Toast.makeText(ListNotes.this,
												"Actualizando...",
												Toast.LENGTH_LONG).show();
										final List<Note> values = nD2
												.getAllNotes();
										lv = (ListView) findViewById(android.R.id.list);
										final Adaptador adaptador = new Adaptador(
												ListNotes.this, values);
										lv.setAdapter(adaptador);
										nD2.close();
									}
								})
						.setNegativeButton(txtButtonNo,
								new DialogInterface.OnClickListener() {
									public void onClick(
											final DialogInterface dialog,
											final int which) {
										return;
									}
								}).show();
			}
		});

		// Cerramos la conexion
		nD.close();
	}

	/**
	 * Destroy TTS
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
	 * Inicializa TTS
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

		String text = "Está usted hacediendo a la visión de notas";
		tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}
}
