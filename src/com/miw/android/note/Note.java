package com.miw.android.note;

/**
 * JavaBean para las notas
 * 
 * @author Pablo
 * 
 */
public class Note {

	/**
	 * Atributos que equivalen a las columnas de la tabla
	 */
	private Long id;
	private String nota;
	private String fecha;
	private String hora;
	private String coordenadas;
	private String img;

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	/**
	 * @return the nota
	 */
	public String getNota() {
		return nota;
	}

	/**
	 * @param nota
	 *            the nota to set
	 */
	public void setNota(String nota) {
		this.nota = nota;
	}

	/**
	 * @return the fecha
	 */
	public String getFecha() {
		return fecha;
	}

	/**
	 * @param fecha
	 *            the fecha to set
	 */
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	/**
	 * @return the hora
	 */
	public String getHora() {
		return hora;
	}

	/**
	 * @param hora
	 *            the hora to set
	 */
	public void setHora(String hora) {
		this.hora = hora;
	}

	/**
	 * @return the coordenadas
	 */
	public String getCoordenadas() {
		return coordenadas;
	}

	/**
	 * @param coordenadas
	 *            the coordenadas to set
	 */
	public void setCoordenadas(String coordenadas) {
		this.coordenadas = coordenadas;
	}

	/**
	 * @return the img
	 */
	public String getImg() {
		return img;
	}

	/**
	 * @param img
	 *            the img to set
	 */
	public void setImg(String img) {
		this.img = img;
	}

	/**
	 * Sera llamado por el listview cuando lo muestre.
	 */
	@Override
	public String toString() {
		return "Fecha: " + fecha + "\nHora: " + hora + "\n" + "Nota: " + nota
				+ "\nRegistrada desde: " + coordenadas;
	}
}
