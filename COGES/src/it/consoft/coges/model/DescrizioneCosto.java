package it.consoft.coges.model;

public class DescrizioneCosto {
	
	private String codiceCosto;
	private int tipologiaCosto;
	private String descrizione;
	
	public DescrizioneCosto(String codiceCosto, int tipologiaCosto, String descrizione) {
		super();
		this.codiceCosto = codiceCosto;
		this.tipologiaCosto = tipologiaCosto;
		this.descrizione = descrizione;
	}

	public String getCodiceCosto() {
		return codiceCosto;
	}

	public void setCodiceCosto(String codiceCosto) {
		this.codiceCosto = codiceCosto;
	}

	public int getTipologiaCosto() {
		return tipologiaCosto;
	}

	public void setTipologiaCosto(int tipologiaCosto) {
		this.tipologiaCosto = tipologiaCosto;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codiceCosto == null) ? 0 : codiceCosto.hashCode());
		result = prime * result + tipologiaCosto;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DescrizioneCosto other = (DescrizioneCosto) obj;
		if (codiceCosto == null) {
			if (other.codiceCosto != null)
				return false;
		} else if (!codiceCosto.equals(other.codiceCosto))
			return false;
		if (tipologiaCosto != other.tipologiaCosto)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DescrizioneCosto [codiceCosto=" + codiceCosto + ", tipologiaCosto=" + tipologiaCosto + ", descrizione="
				+ descrizione + "]";
	}
	
	
	
	

}
