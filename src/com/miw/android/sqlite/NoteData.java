package com.miw.android.sqlite;

import java.util.ArrayList;
import java.util.List;

import com.miw.android.note.Note;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Acciones que se pueden hacer con las notas en la base de datos
 * 
 * @author Pablo
 * 
 */
public class NoteData {

	/**
	 * Referencia para manejar la base de datos.
	 */
	private SQLiteDatabase database;

	/**
	 * Referencia al helper que se encarga de crear y actualizar la base de
	 * datos.
	 */
	private final MyDBHelper dbHelper;

	/**
	 * Columnas de la tabla
	 */
	private final String[] allColumns = { MyDBHelper.COLUMN_ID,
			MyDBHelper.COLUMN_NOTE, MyDBHelper.COLUMN_FECHA,
			MyDBHelper.COLUMN_HORA, MyDBHelper.COLUMN_COORDENADA,
			MyDBHelper.COLUMN_IMG };

	/**
	 * Constructor.
	 * 
	 * @param context
	 */
	public NoteData(final Context context) {
		dbHelper = new MyDBHelper(context);
	}

	/**
	 * Abre una conexion para escritura con la base de datos.
	 * 
	 * @throws SQLException
	 */
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	/**
	 * Cierra la conexion con la base de datos
	 */
	public void close() {
		dbHelper.close();
	}

	/**
	 * Añade una nueva nota a la tabla.
	 * 
	 * @param note
	 *            Objeto de tipo <code>Note</code> que se insertara en bd
	 * 
	 * @return identificador obtenido en la consulta.
	 */
	public long createNote(final Note note) {
		// Establecemos los valores que se insertaran
		final ContentValues values = new ContentValues();
		values.put(MyDBHelper.COLUMN_NOTE, note.getNota());
		values.put(MyDBHelper.COLUMN_FECHA, note.getFecha());
		values.put(MyDBHelper.COLUMN_HORA, note.getHora());
		values.put(MyDBHelper.COLUMN_COORDENADA, note.getCoordenadas());
		values.put(MyDBHelper.COLUMN_IMG, note.getImg());

		// Insertamos la nota
		final long insertId = database.insert(MyDBHelper.TABLE_NOTES, null,
				values);

		return insertId;
	}

	/**
	 * Elimina una nota de la tabla.
	 * 
	 * @param fecha
	 *            Objeto de tipo string con la fecha
	 ** @param hora
	 *            Objeto de tipo string con la hora
	 * @return identificador obtenido en la consulta.
	 */
	public String deleteNote(String fecha, String hora) {
		String cad = "";
		// Borramos la nota
		database.delete(MyDBHelper.TABLE_NOTES, MyDBHelper.COLUMN_FECHA
				+ "=? and " + MyDBHelper.COLUMN_HORA + "=?", new String[] {
				fecha, hora });
		cad = "Eliminada nota con fecha: " + fecha + " y hora: " + hora;
		return cad;
	}

	public String updateNote() {
		String cad = "";
		// Actualizamos la nota
		int versionOld = MyDBHelper.DATABASE_VERSION;
		dbHelper.onUpgrade(database, versionOld, versionOld + 1);
		cad = "Actualizando";
		return cad;
	}

	/**
	 * Obtiene todas las notas añadidas.
	 * 
	 * @return Lista de objetos de tipo <code>Valoration</code>
	 */
	public List<Note> getAllNotes() {
		// Lista que almacenara el resultado
		final List<Note> noteList = new ArrayList<Note>();

		// Hacemos la consulta sin filtros, ya que queremos obtener todos los
		// datos. La consulta devuelve los datos en un cursor. Debemos recorrer
		// el cursor y obtener los datos a traves de la posicion de la columna.
		final Cursor cursor = database.query(MyDBHelper.TABLE_NOTES,
				allColumns, null, null, null, null, MyDBHelper.COLUMN_FECHA);
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			final Note note = new Note();
			note.setNota(cursor.getString(1));
			note.setFecha(cursor.getString(2));
			note.setHora(cursor.getString(3));
			note.setCoordenadas(cursor.getString(4));
			note.setImg(cursor.getString(5));

			noteList.add(note);
			cursor.moveToNext();
		}

		cursor.close();
		// Una vez obtenidos todos los datos y cerrado el cursor, devolvemos la
		// lista.
		return noteList;
	}

	/**
	 * Obtiene todas las notas añadidas.
	 * 
	 * @return Lista de objetos de tipo <code>Valoration</code>
	 */
	public List<Note> getAllNotesByDate(String fecha) {
		// Lista que almacenara el resultado
		final List<Note> noteList = new ArrayList<Note>();

		// Hacemos la consulta sin filtros, ya que queremos obtener todos los
		// datos. La consulta devuelve los datos en un cursor. Debemos recorrer
		// el cursor y obtener los datos a traves de la posicion de la columna.
		String[] args = new String[] { fecha };
		String sql = " select * from " + MyDBHelper.TABLE_NOTES + " where "
				+ MyDBHelper.COLUMN_FECHA + "=? ";
		final Cursor cursor = database.rawQuery(sql, args);
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			final Note note = new Note();
			note.setNota(cursor.getString(1));
			note.setFecha(cursor.getString(2));
			note.setHora(cursor.getString(3));
			note.setCoordenadas(cursor.getString(4));
			note.setImg(cursor.getString(5));

			noteList.add(note);
			cursor.moveToNext();
		}

		cursor.close();
		// Una vez obtenidos todos los datos y cerrado el cursor, devolvemos la
		// lista.
		return noteList;
	}

}
