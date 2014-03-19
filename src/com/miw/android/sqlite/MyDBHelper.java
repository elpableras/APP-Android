package com.miw.android.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 
 * Gestiona la creacion y actualizacion de la base de datos SQLite.
 * 
 */
public class MyDBHelper extends SQLiteOpenHelper {

	/**
	 * Nombre de la tabla
	 */
	public static final String TABLE_NOTES = "notes";

	/**
	 * Columnas de la tabla.
	 */
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NOTE = "note";
	public static final String COLUMN_FECHA = "fecha";
	public static final String COLUMN_HORA = "hora";
	public static final String COLUMN_COORDENADA = "coordenada";
	public static final String COLUMN_IMG = "img";

	/**
	 * Nombre y versión de la base de datos
	 */
	private static final String DATABASE_NAME = "notes.db";
	public static final int DATABASE_VERSION = 1;

	/**
	 * Script para crear la base datos
	 */
	private static final String DATABASE_CREATE = "create table " + TABLE_NOTES
			+ "( " + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_NOTE + " text not null, " + COLUMN_FECHA
			+ " text not null, " + COLUMN_HORA + " text not null, "
			+ COLUMN_COORDENADA + " text, " + COLUMN_IMG + " text);";

	/**
	 * Constructor. Crea el objeto.
	 * 
	 * @param context
	 *            contexto
	 */
	public MyDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Crear la base de datos.
	 * 
	 * @param db
	 *            objeto que se encarga de lanzar la consulta
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	/**
	 * Actualizar la base de datos.
	 * 
	 * @param db
	 *            objeto que se encarga de lanzar la consulta
	 * @param oldVersion
	 *            version anterior
	 * @param newVerios
	 *            nueva version
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("UPDATE", "updating database from version " + oldVersion
				+ " to version " + newVersion);
		String alter_query1 = "alter table " + TABLE_NOTES + " RENAME TO temp;";
		String alter_query2 = DATABASE_CREATE;
		String alter_query3 = "insert into " + TABLE_NOTES
				+ " select * from temp;";
		String alter_query4 = "DROP TABLE temp;";

		db.execSQL(alter_query4);
		db.execSQL(alter_query1);
		db.execSQL(alter_query2);
		db.execSQL(alter_query3);
		db.execSQL(alter_query4);
	}

}
