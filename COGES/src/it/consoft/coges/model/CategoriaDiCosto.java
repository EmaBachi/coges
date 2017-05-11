package it.consoft.coges.model;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class CategoriaDiCosto {
	
	private String categoria;
	private Map<String, Costo> mappaCosti;
	private double importoContabilitaGenerale;
	private double importoRettificheGenerale;
	private double importoContabilitaCoges;
	private double importoRettifiche;
	
	public CategoriaDiCosto(String categoria){
		this.categoria = categoria;
		this.mappaCosti = new TreeMap<String, Costo>();
	}
	
	public void aggiungiCosto(Costo costo){
		this.mappaCosti.put(costo.getCodiceCosto(), costo);
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public Collection<Costo> getListaCosti() {
		return mappaCosti.values();
	}

	public double getImportoContabilitaGenerale(){
		return this.importoContabilitaGenerale;
	}
	
	public double getImportoRettificheGenerale(){
		return this.importoRettificheGenerale;
	}
	
	public double getImportoContabilitaCoges(){
		return this.importoContabilitaCoges;
	}
	
	public double getImportoRettifiche(){
		return this.importoRettifiche;
	}
	
	public void calcolaImportiContabilita(){
		for(Costo costo : this.getListaCosti()){
			this.importoContabilitaGenerale = this.importoContabilitaGenerale + costo.getImportoGenerale();
			this.importoRettificheGenerale = this.importoRettificheGenerale + costo.getImportoRettificaGenerale();
			this.importoContabilitaCoges = this.importoContabilitaCoges + costo.getImportoCoges();
			this.importoRettifiche = this.importoRettifiche + costo.getImportoRettificaCoges();
		}
	}
	
	public void setImportiContabilitaGenerale(){
		for(Costo costo : this.getListaCosti()){
			this.importoContabilitaGenerale = this.importoContabilitaGenerale + costo.getImportoGenerale();
			this.importoRettificheGenerale = this.importoRettificheGenerale + costo.getImportoRettificaGenerale();
		}
	}
	
	public void setImportiContabilitaCoges(){
		for(Costo costo : this.getListaCosti()){
			this.importoContabilitaCoges = this.importoContabilitaCoges + costo.getImportoCoges();
			this.importoRettifiche = this.importoRettifiche + costo.getImportoRettificaCoges();
		}
	}
	
	public Map<String, Costo> getMappa(){
		return this.mappaCosti;
	}
	
	public String toString(){
		return this.categoria.substring(0, 1).toUpperCase() + this.categoria.substring(1);
	}
	
	public void pulisciCategoria(){
		this.importoContabilitaGenerale = 0.0;
		this.importoContabilitaCoges = 0.0;
		this.importoRettifiche = 0.0;
		this.importoRettificheGenerale = 0.0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((categoria == null) ? 0 : categoria.hashCode());
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
		CategoriaDiCosto other = (CategoriaDiCosto) obj;
		if (categoria == null) {
			if (other.categoria != null)
				return false;
		} else if (!categoria.equals(other.categoria))
			return false;
		return true;
	}
	
	

}
