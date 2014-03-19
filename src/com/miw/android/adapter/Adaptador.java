package com.miw.android.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.miw.android.note.Note;
import com.miw.android.notes.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ImageView;

/**
 * Adaptador para la lista con la notas y sus imagenes
 * 
 * @author Pablo
 * 
 */
public class Adaptador extends ArrayAdapter<Note> {

	private Context contexto;
	private ArrayList<Note> datos;

	/**
	 * Constructor
	 * 
	 * @param contexto
	 * @param values
	 */
	public Adaptador(Context contexto, List<Note> values) {
		super(contexto, R.layout.list_item_nota, values);
		this.contexto = contexto;
		this.datos = (ArrayList<Note>) values;
	}

	/**
	 * Método para visualizar las notas
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View item = convertView;
		ViewHolder holder;

		if (item == null) {
			LayoutInflater inflater = ((Activity) contexto).getLayoutInflater();
			item = inflater.inflate(R.layout.list_item_nota, null);
			holder = new ViewHolder();
			holder.nombre = (TextView) item.findViewById(R.id.lista_nota);
			holder.image = (ImageView) item.findViewById(R.id.lista_img);

			item.setTag(holder);
		} else {
			holder = (ViewHolder) item.getTag();
		}
		File imgFile = new File(datos.get(position).getImg());
		if (imgFile.exists()) {
			Bitmap myBitmap = BitmapFactory.decodeFile(imgFile
					.getAbsolutePath());
			holder.image.setImageBitmap(myBitmap);
			holder.nombre.setText(datos.get(position).toString());
		} else {
			holder.nombre.setText(datos.get(position).toString());
		}
		return item;
	}
}
