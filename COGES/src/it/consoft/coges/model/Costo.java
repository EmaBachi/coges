package it.consoft.coges.model;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class Costo {
	
	private String codiceCosto;
	private String descrizione;
	private int tipologiaCosto;
	private boolean altroCosto;
	
	private double importoGenerale;
	private double importoRettificaGenerale;
	private double importoCoges;
	private double importoRettificaCoges;
	
	private double delta;

	public Costo(String codiceCosto, String descrizione, double importo, int tipologiaCosto){
		super();
		this.codiceCosto = codiceCosto;
		this.descrizione = descrizione;
		this.tipologiaCosto = tipologiaCosto;
		this.altroCosto = true;
		
		switch (tipologiaCosto){
		
		case 1:
			this.importoGenerale = importo;
			break;
		case 2:
			this.importoRettificaCoges = importo;
			break;
		case 3:
			this.importoCoges = importo;
			break;
		}
	}
	
	public Costo(String codiceCosto, String descrizione, double importoGenerale, double importoRettificaGenerale, double importoCoges, double importoRettifica){
		super();
		this.codiceCosto = codiceCosto;
		this.descrizione = descrizione;
		this.importoGenerale = importoGenerale;
		this.importoRettificaGenerale = importoRettificaGenerale;
		this.importoCoges = importoCoges;
		this.importoRettificaCoges = importoRettifica;
		this.delta = (this.importoGenerale + this.importoRettificaGenerale)- (this.importoCoges + this.importoRettificaCoges); 
	}
	
	public Costo(String codiceCosto, double importoRettificaGenerale){
		this.codiceCosto = codiceCosto;
		this.importoRettificaGenerale = importoRettificaGenerale;
	}
	
	public Costo(String codiceCosto){
		this.codiceCosto = codiceCosto;
	}
	

	public String getCodiceCosto() {
		return codiceCosto;
	}

	public void setCodiceCosto(String codiceCosto) {
		this.codiceCosto = codiceCosto;
	}

	public String getDescrizione() {
		return descrizione;
	}
	
	public double getImportoGenerale() {
		BigDecimal bd = new BigDecimal(Double.toString(this.importoGenerale));
	     bd = bd.setScale(3, BigDecimal.ROUND_HALF_UP);
	     return bd.doubleValue();
	}

	public void setImportoGenerale(double importoGenerale) {
		this.importoGenerale = this.importoGenerale + importoGenerale;
	}
	
	public double getImportoRettificaGenerale(){
		 BigDecimal bd = new BigDecimal(Double.toString(this.importoRettificaGenerale));
	     bd = bd.setScale(3, BigDecimal.ROUND_HALF_UP);
	     return bd.doubleValue();
	}
	
	public void setImportoRettificaGenerale(double importoRettificaGenerale){
		this.importoRettificaGenerale = this.importoRettificaGenerale + importoRettificaGenerale;
	}

	public double getImportoCoges() {
		BigDecimal bd = new BigDecimal(Double.toString(this.importoCoges));
	     bd = bd.setScale(3, BigDecimal.ROUND_HALF_UP);
	     return bd.doubleValue();
	}

	public void setImportoCoges(double importoCoges) {
		this.importoCoges = importoCoges + this.importoCoges;
	}

	public double getImportoRettificaCoges() {
		BigDecimal bd = new BigDecimal(Double.toString(this.importoRettificaCoges));
	     bd = bd.setScale(3, BigDecimal.ROUND_HALF_UP);
	     return bd.doubleValue();
	}

	public void setImportoRettificaCoges(double importoRettifica) {
		this.importoRettificaCoges = importoRettifica + this.importoRettificaCoges;
	}

	public int getTipologiaCosto(){
		return tipologiaCosto;
	}
	
	public void setTipologiaCosto(int tipologiaCosto){
		this.tipologiaCosto = tipologiaCosto;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public void setAltroCosto(boolean b){
		this.altroCosto = b;
	}
	
	public boolean getAltroCosto(){
		return this.altroCosto;
	}
	
	public void setDelta(){
		if(this.importoGenerale != 0 &&  (this.importoCoges != 0 || this.importoRettificaCoges != 0))
			this.delta = (this.importoGenerale+this.importoRettificaGenerale) - (this.importoCoges + this.importoRettificaCoges);
	}
	
	public double getDelta(){
		
		BigDecimal bd = new BigDecimal(Double.toString(this.delta));
	     bd = bd.setScale(3, BigDecimal.ROUND_HALF_UP);
	     return bd.doubleValue();
	}
	
	public void applicaRettifica(double importo, boolean con){
	
		if(con){
			this.importoRettificaGenerale = this.importoRettificaGenerale + importo;
		} else 
			this.importoCoges = this.importoCoges+ importo;
		this.setDelta();
	}
	
	public void pulisci(){
		
		this.importoGenerale = 0.0;
		this.importoCoges = 0.0;
		this.importoRettificaCoges = 0.0;
		this.importoRettificaGenerale = 0.0;
		this.setAltroCosto(true);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codiceCosto == null) ? 0 : codiceCosto.hashCode());
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
		Costo other = (Costo) obj;
		if (codiceCosto == null) {
			if (other.codiceCosto != null)
				return false;
		} else if (!codiceCosto.equals(other.codiceCosto))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.codiceCosto;
	}

	public String getImportoGeneralePerVisualizzazione() {
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.ITALY);
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

		symbols.setGroupingSeparator('.');
		formatter.setDecimalFormatSymbols(symbols);
		return formatter.format(this.importoGenerale);
	}
	
	public String getImportoRettificaGeneralePerVisualizzazione(){
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.ITALY);
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

		symbols.setGroupingSeparator('.');
		formatter.setDecimalFormatSymbols(symbols);
		return formatter.format(this.importoRettificaGenerale);
	}
	
	public String getImportoContabilitaCogesPerVisualizzazione(){
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.ITALY);
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

		symbols.setGroupingSeparator('.');
		formatter.setDecimalFormatSymbols(symbols);
		return formatter.format(this.importoCoges);
	}
	
	public String getImportoRettificaCogesPerVisualizzazione(){
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.ITALY);
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

		symbols.setGroupingSeparator('.');
		formatter.setDecimalFormatSymbols(symbols);
		return formatter.format(this.importoRettificaCoges);
	}
	
	public String getDeltaPerVisualizzazione(){
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.ITALY);
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

		symbols.setGroupingSeparator('.');
		formatter.setDecimalFormatSymbols(symbols);
		return formatter.format(this.delta);
	}

	
}
