package it.consoft.coges.model;

import java.sql.Timestamp;

public class RettificaGenerale {
	
	private Timestamp data;
	private String codiceCosto;
	private double importo;
	
	public RettificaGenerale(Timestamp data, String codiceCosto, double importo) {
		super();
		this.data = data;
		this.codiceCosto = codiceCosto;
		this.importo = importo;
	}
	
	public Timestamp getData() {
		return data;
	}
	public void setData(Timestamp data) {
		this.data = data;
	}
	public String getCodiceCosto() {
		return codiceCosto;
	}
	public void setCodiceCosto(String codiceCosto) {
		this.codiceCosto = codiceCosto;
	}
	public double getImporto() {
		return importo;
	}
	public void setImporto(double importo) {
		this.importo = importo;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codiceCosto == null) ? 0 : codiceCosto.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}
	
	@Override
	public String toString() {
		return "RettificaGenerale [data=" + data + ", codiceCosto=" + codiceCosto + ", importo=" + importo + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RettificaGenerale other = (RettificaGenerale) obj;
		if (codiceCosto == null) {
			if (other.codiceCosto != null)
				return false;
		} else if (!codiceCosto.equals(other.codiceCosto))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		return true;
	}
	
	

}
