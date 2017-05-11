package it.consoft.coges.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import it.consoft.coges.db.CogesDAO;

public class Model {
	
	private CogesDAO dao;
	
	private Map<String, CategoriaDiCosto> mappaCategorie;
	
	private List<DescrizioneCosto> codiciContabilitaGenerale;
	private List<DescrizioneCosto> codiciContabilitaCoges;
	private List<DescrizioneCosto> codiciRettificheCoges;
	
	private List<RettificaGenerale> rettificheContabilitaGenerale;
	
	private final int tipoContabilitaGenerale = 1;
	private final int tipoRettificheCoges = 2;
	private final int tipoContabilitaCoges = 3;
	
	private final String percorsoFileTesto = "C:/Users/csto90/Desktop/tir/logFile.txt";
	
	private String societa;
	
	public Model(){
		this.mappaCategorie = new TreeMap<String, CategoriaDiCosto>();
		this.codiciContabilitaGenerale = new ArrayList<DescrizioneCosto>();
		this.codiciContabilitaCoges = new ArrayList<DescrizioneCosto>();
		this.codiciRettificheCoges = new ArrayList<DescrizioneCosto>();
		
		this.rettificheContabilitaGenerale = new ArrayList<RettificaGenerale>();
		
		this.dao = new CogesDAO();
		
		societa = "";
	}
	
	public void setSocieta(String societa){
		this.societa = societa;
	}

	/**
	 * metodo che viene invocato quando l'utente preme il pulsante controlla.
	 * il metodo va nel dao e legge una tabella usata per fare il mapping tra i costi di contabilita generale e costi di natura diversa.
	 * con il risultato del dao vengono popolate tre liste: quella della contabilita generale, quella della contabilita coges e quella delle rettifiche. le liste contengono i codici.
	 * a questo punto andiamo a creare le categorie (che sono dinamiche poichè lette dal databse) e viene popolata la mappa delle categorie.
	 * infine viene creata la categoria altri costi e viene inserita nella mappa. questa categoria ci sarà sempre quindi non abbiamo bisogno di leggerla dal database.
	 * Questo metodo viene chiamato una sola volta, all'inizio del programma.
	 */
	public void inizializzaStrutturaDati() {
		
		List<DescrizioneCosto> anagrafica = dao.caricaAnagraficaCosti();
		
		codiciContabilitaGenerale.clear();
		codiciContabilitaCoges.clear();
		codiciRettificheCoges.clear();
		
		for(DescrizioneCosto temp : anagrafica){
			
			switch (temp.getTipologiaCosto()){
				
			case 1:	
				codiciContabilitaGenerale.add(temp);
				break;
			
			case 2:
				codiciRettificheCoges.add(temp);
				break;
			
			case 3:
				codiciContabilitaCoges.add(temp);
				break;
			}
			
			if(mappaCategorie.get(temp.getDescrizione().toLowerCase()) == null){
				CategoriaDiCosto c = new CategoriaDiCosto(temp.getDescrizione().toLowerCase());
				mappaCategorie.put(c.getCategoria().toLowerCase(), c);
			}
		}
		
		CategoriaDiCosto altriCosti = new CategoriaDiCosto("altri costi");
		
		mappaCategorie.put(altriCosti.getCategoria(), altriCosti);
		
	}
	
	/*
	 * I metodi caricaCostiContabilitaGenerale, caricaCostiContabilitaCoges e caricaRettificheContabilitaCoges si interfacciano con il DAO e leggono i dati 
	 * tre tabelle differenti. una volta letti i dati invocano i metodi identificaCosti e assegnaAltriCosti. Il metodo caricaRettficiheCoges invoca anche altre due metodi,
	 * caricaRettificheCompensi e caricaRettificheIntercompany. Questa funzionalità in più di caricaRettificheCoges è dovuta al fatto che per le categorie intercompany e compensi
	 * bisogna fare delle query specifiche sul database
	 */
	public void caricaCostiContabilitaGenerale(LocalDate dataInizioLocal, LocalDate dataFineLocal){

		Timestamp dataInizio = this.convertitoreData(dataInizioLocal);
		Timestamp dataFine = this.convertitoreData(dataFineLocal);
		
		List<Costo> costi = dao.caricaCostiContabilitaGenerale(dataInizio, dataFine, societa);
		
		this.identificaCosti(costi, this.getCodiciContabilitaGenerale(), tipoContabilitaGenerale);
		this.assegnaAltriCosti(costi, tipoContabilitaGenerale);
	}
	
	public void caricaCostiContabilitaCoges(LocalDate dataInizioLocal, LocalDate dataFineLocal){
		
		Timestamp dataInizio = this.convertitoreData(dataInizioLocal);
		Timestamp dataFine = this.convertitoreData(dataFineLocal);
		
		List<Costo> costi = dao.caricaCostiContabilitaCoges(dataInizio, dataFine, societa);
		
		this.identificaCosti(costi, this.getCodiciContabilitaCoges(), tipoContabilitaCoges);
		this.assegnaAltriCosti(costi, tipoContabilitaCoges);
	}
	
	public void caricaRettificheCoges(LocalDate dataInizioLocal, LocalDate dataFineLocal){
		
		Timestamp dataInizio = this.convertitoreData(dataInizioLocal);
		Timestamp dataFine = this.convertitoreData(dataFineLocal);
		
		List<Costo> rettifiche = dao.caricaRettificheCoges(dataInizio, dataFine, societa);
		
		this.identificaCosti(rettifiche, this.getCodiciRettificheCoges(), tipoRettificheCoges);
		
		if(this.getMappa().get("intercompany") != null)
			this.caricaRettificheIntercompany(dataInizio, dataFine);
		
		if(this.getMappa().get("compensi") != null)
			this.caricaRettificheCompensi(dataInizio, dataFine);
	}
	
	private Timestamp convertitoreData(LocalDate data){
		Timestamp ritorno = Timestamp.valueOf(data.toString()+ " 00:00:00");
		return ritorno;
	}
	
	/**
	 * Anche questo metodo viene invocato quando l'utente preme il pulsante 'carica'. 
	 * Per prima cosa invoca il dao che va a leggere da una tabella di appoggio tutte le rettifiche effettuate dall'utente nel tempo.
	 * Successivamente ciclo su tutte le rettifiche (che mi arrivano sotto forma di costo) e per ogni rettifica ciclo sulle categorie.
	 * quando nella mappa di una categoria trovo una corrispondenza con la rettifica considerata (stesso codice di costo) allora aggiorno l'importo delle rettfiche geenrali.
	 * Successivamente imposto il delta.
	 * Con questa funzione da praticone riesco a gestire anche l'inseriemnto da parte dell'utente di un costo che non è previsto dal piano dei conti: infatti in questo caso
	 * la condizione di if non verrà mai rispettata
	 */
	public void caricaRettificheContabilitaGenerale(){
		
		rettificheContabilitaGenerale = dao.caricaRettificheContabilitaGenerale(societa);
		
		for(RettificaGenerale r : rettificheContabilitaGenerale){
			for(CategoriaDiCosto c : mappaCategorie.values()){
				if(c.getMappa().get(r.getCodiceCosto()) != null){
					c.getMappa().get(r.getCodiceCosto()).setImportoRettificaGenerale(r.getImporto());
					c.getMappa().get(r.getCodiceCosto()).setDelta();
				}
			}
		}
	}
	
	/**
	 * Il metodo serve per assegnare i costi alle varie categorie di costo. In realtà a questo livello si riconosce solamente l'appartenenza di un costo ad una determinata categoria.
	 * il metodo delegato all'assegnazione dei vari costi alla rispettiva categoria è il raggruppaCosti
	 */
	private void identificaCosti(List<Costo> costi, List<DescrizioneCosto> listaDiMapping, int tipoCosto) {
		for(Costo costo : costi){
			for(DescrizioneCosto d : listaDiMapping){
				if(costo.getCodiceCosto().equals(d.getCodiceCosto())){
					costo.setAltroCosto(false);
					this.raggruppaCosti(costo, d.getDescrizione());
				}
			}
		}
	}
	
	/**
	 * Questo metodo ha lo scopo di raggruppare i costi, in questo senso: quando arriva un costo con la sua categoria, controlla se nella amppa dei csti di quella
	 * categoria è gia presente il costo. Se il costo non è ancora presente lo aggiunge (il valore dell'importo sarà già diversificato poichè prendo quello assegnato dal costruttore),
	 * se invece il il costo è già presente, prendo il costo dalla mappa e ci vado a sommare l'importo in base alla tipologia di costo.
	 */
	private void raggruppaCosti(Costo costo, String d){
		
		if(mappaCategorie.get(d).getMappa().get(costo.getCodiceCosto()) == null){
			mappaCategorie.get(d).aggiungiCosto(costo);
	     } else {
			switch (costo.getTipologiaCosto()){
			case 1:
				mappaCategorie.get(d).getMappa().get(costo.getCodiceCosto()).setImportoGenerale(costo.getImportoGenerale());
				mappaCategorie.get(d).getMappa().get(costo.getCodiceCosto()).setDelta();
				break;
			case 2:
				mappaCategorie.get(d).getMappa().get(costo.getCodiceCosto()).setImportoRettificaCoges(costo.getImportoRettificaCoges());
				mappaCategorie.get(d).getMappa().get(costo.getCodiceCosto()).setDelta();
				break;
			case 3:
				mappaCategorie.get(d).getMappa().get(costo.getCodiceCosto()).setImportoCoges(costo.getImportoCoges());
				mappaCategorie.get(d).getMappa().get(costo.getCodiceCosto()).setDelta();
				break;
			}
		}
	}
	
	/**
	 * Per assegnare i costi alla tipologia altri costi si procede per differenza: i costi il cui flag altriCosti  è true, vengono asseganti alla categoria,
	 * gli altri costi, quelli con il flag false, non vengono considerati poichè sono già stati assegnati ad altre categorie.
	 */
	private void assegnaAltriCosti(List<Costo> costi, int tipologiaCosto){
		for(Costo costo : costi){
			if(costo.getAltroCosto())
				this.raggruppaCosti(costo, "altri costi");
		}
			
	}

	/**
	 * i metodi caticaRettificheIntercompany e caricaRettificheCompensi vengono invocati nel motodo caricaRettificheCoges. il caricamento delle rettifiche per queste
	 * categorie avviene in modo diverso rispetto alle altre categorie: per intercompany e compensi bisgna fare delle query specifiche sul database
	 */
	private void caricaRettificheIntercompany(Timestamp dataInizio, Timestamp dataFine){
		for(Costo costo : dao.caricaRettificheIntercompany(dataInizio, dataFine, societa))
			this.raggruppaCosti(costo, "intercompany");
	}
	
	private void caricaRettificheCompensi(Timestamp dataInizio, Timestamp dataFine){
		for(Costo costo : dao.caricaRettificheCompensi(dataInizio, dataFine, societa))
			this.raggruppaCosti(costo, "compensi");
	}
	
	
	public void calcolaImporti(){
		for(CategoriaDiCosto categoria : this.getMappa().values())
			categoria.calcolaImportiContabilita();
	}
	
	/**
	 * il metodo inserisciRettifica si occupa sia di invocare il dao per l'inserimento della rettifica nel database, sia della scrittura del file di log.
	 * l'eccezione è scatenata dalla funzione sleep. in questo momento nn la sto gestendo perchè non vedo in quali circostanze della vita dell'applicazione lo sleep
	 * possa causare dei problemi
	 */
	public boolean inserisciRettifica(String numeroConto, String descrizione, double importo){
		
		String tipoRettifica = "INSERIMENTO";
		
		Timestamp dataInserimento = new Timestamp(System.currentTimeMillis());
		
		if(dao.InserisciRettifica(dataInserimento, numeroConto, importo, societa)){
			//delego un metod apposta per la scrittura del file
			scriviNelFile(numeroConto, descrizione, tipoRettifica, importo, societa);
			return true;
		} else
			return false;
	}
	
	public boolean eliminaTutteRettifiche(boolean cancellaFile){
		boolean b = false;
		
		if(cancellaFile){
			pulisciFile();
			b = dao.eliminaTutteRettifiche();
			if(b) scriviNelFile("", "ELIMINATE TUTTE LE RETTIFICHE DAL DB", "RIMOZIONE", 0.0, "");
		}
		else{
			b = dao.eliminaRettifiche(societa);
			if(b) scriviNelFile("", "ELIMINATE TUTTE LE RETTIFICHE DAL DB", "RIMOZIONE", 0.0, societa);
		}
		
		return b;
	}
	
	public boolean eliminaSigolaRettifica(RettificaGenerale r){
		boolean b = dao.eliminaSingolaRettifica(r.getData(), r.getCodiceCosto(), societa);
		
		if(b){
			scriviNelFile(r.getCodiceCosto(), "rimozione singola rettifica", "RIMOZIONE", 0.0, societa);
		}
		
		return b;
	}
	
	public boolean modificaSingolaRettifica(RettificaGenerale r, double nuovoImporto){
		boolean b = dao.modificaRettifica(r.getData(), r.getCodiceCosto(), nuovoImporto, societa);
		
		if(b){
			scriviNelFile(r.getCodiceCosto(), "", "MODIFICA", nuovoImporto, societa);
		}
		
		return b;
	}

	//metodo incaricato di scrivere nel file di testo.
	//Nel file di testo vengono inseriti i dettagli della modifica, inclusa la data in cui la modifica è stata effettuata
	private void scriviNelFile(String numeroConto, String descrizione, String tipoRettifica, double importo, String soc) {
		
		try {
			//dichiarazione variabili di file output
			FileWriter writer;
			BufferedWriter bufferedWriter;
			
			//inizializzazione
			writer = new FileWriter(percorsoFileTesto, true);
			bufferedWriter = new BufferedWriter(writer);
			
			//scrittura nel file
			String timeLog = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss").format(Calendar.getInstance().getTime());
			bufferedWriter.write(timeLog);
			bufferedWriter.newLine();
			if(numeroConto.compareTo("") == 0 && importo == 0.0 && soc.compareTo("") == 0){
				bufferedWriter.write("Tipo rettifica: "+tipoRettifica+". Descrizione: "+descrizione+".");
				bufferedWriter.newLine();
				bufferedWriter.newLine();
			}
			else if(numeroConto.compareTo("") == 0 && importo == 0.0){
				bufferedWriter.write("Società: "+soc.toUpperCase()+". Tipo rettifica: "+tipoRettifica+". Descrizione: "+descrizione+".");
				bufferedWriter.newLine();
				bufferedWriter.newLine();
			}
			else if(importo == 0.0){
				bufferedWriter.write("Societa: "+soc.toUpperCase()+". Numero conto: "+numeroConto+". Tipo rettifica: "+tipoRettifica+". Descrizione: "+descrizione+".");
				bufferedWriter.newLine();
				bufferedWriter.newLine();
			} else if(descrizione.compareTo("") == 0){
				bufferedWriter.write("Societa: "+soc.toUpperCase()+". Numero conto: "+numeroConto+". Tipo rettifica: "+tipoRettifica+". Importo: "+importo+".");
				bufferedWriter.newLine();
				bufferedWriter.newLine();
			} else {
				bufferedWriter.write("Societa: "+soc.toUpperCase()+"Numero conto: "+numeroConto+". Tipo rettifica: "+tipoRettifica+". Descrizione: "+descrizione+". Importo: "+importo+".");
				bufferedWriter.newLine();
				bufferedWriter.newLine();
			}
			
			//chiusura
			bufferedWriter.close();
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	//il mtodo pulisciFile elimina il contenuto del file di testo. è invocato dal controller qaundo l'utente decide di eliminare lo storico delle rettifiche
	private boolean pulisciFile(){
		try {
			
			FileWriter writer;
			BufferedWriter bufferedWriter;
				
			writer = new FileWriter(percorsoFileTesto, false);
			bufferedWriter = new BufferedWriter(writer);
				
			bufferedWriter.write("");
				
			bufferedWriter.close();
			writer.close();
			return true;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	/*
	 * Questo metodo viene invocato quando l'utente preme il tasto Storico Rettifiche. non fa altro che leggere il file e restituirlo in una stringa leggibile
	 * QUESTO METODO MOLTO PROBABILMENTE CON LE NUOVE SPECIFICHE NON SARà PIù NECESSARIO
	 */
	public String leggiFile(){
		try {

			FileReader reader = new FileReader(percorsoFileTesto);
			BufferedReader br = new BufferedReader(reader);
			
			String file = "";

			String rigaCorrente;

			while ((rigaCorrente = br.readLine()) != null) {
				file += rigaCorrente+"\n";
			}
			
			br.close();
			reader.close();
			
			return file;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * Ogni volta che si fa il refresh andiamo a ricaricarci tuto da capo. Nel futuro si potrebbe eventualmente definire una strategia migliore per il refresh,
	 * guardando magari anche se le date selezionate dall'utente variano: se le date non variano, non vale la pensa andarsi a ricaricare l'anagrafica dei conti che per un dato 
	 * periodo sicuramente non varia. per quanto riguarda gli importi, temo che questi debbano essere ricalcolati ogni volta
	 */
	public void pulisci(){
		
		
		//mappaCategorie.get(d).getMappa().get(costo.getCodiceCosto()
		
		for(CategoriaDiCosto categoria : this.mappaCategorie.values()){
			for(Costo costo : mappaCategorie.get(categoria.getCategoria()).getMappa().values())//categoria.getMappa().values())
				costo.pulisci();
			mappaCategorie.get(categoria.getCategoria()).getMappa().clear();
			categoria.getMappa().clear();
		}
		
		rettificheContabilitaGenerale.clear();
	}
	
	public void pulisciMappa(){
		this.mappaCategorie.clear();
	}
	
	//un po' di getter e setter
	public Collection<Costo> getListCostodaCategoria(String categoria){
		return this.getMappa().get(categoria).getListaCosti();
	}
	
	
	public Map<String, CategoriaDiCosto> getMappa(){
		return this.mappaCategorie;
	}
	
	private List<DescrizioneCosto> getCodiciContabilitaGenerale(){
		return this.codiciContabilitaGenerale;
	}
	
	private List<DescrizioneCosto> getCodiciContabilitaCoges(){
		return this.codiciContabilitaCoges;
	}
	
	private List<DescrizioneCosto> getCodiciRettificheCoges(){
		return this.codiciRettificheCoges;
	}
	
	public List<RettificaGenerale> getRettificheContabilitaGenerale(){
		return this.rettificheContabilitaGenerale;
	}
	
	public Collection<Costo> caricaContiPerDialogModifiche(){
		return dao.caricaContiPerDialogModifiche();
	}
	
	public boolean modificaAssociazione(String numeroConto, int tipoConto, CategoriaDiCosto categoria){
		int selezione = dao.controllaRimuoviAssociazione(numeroConto);
		if(selezione == 0){
			
			return (dao.inserisciAssociazione(numeroConto, tipoContabilitaGenerale, categoria.getCategoria().substring(0, 1).toUpperCase() + categoria.getCategoria().substring(1))
					&&	dao.inserisciAssociazione(numeroConto, tipoContabilitaCoges, categoria.getCategoria().substring(0, 1).toUpperCase() + categoria.getCategoria().substring(1)));
		}
		else if(selezione == 1){
			return dao.inserisciAssociazione(numeroConto, tipoConto, categoria.getCategoria().substring(0, 1).toUpperCase() + categoria.getCategoria().substring(1));
		}
		else if(selezione == 2){
			return (dao.inserisciAssociazione(numeroConto, tipoContabilitaGenerale, categoria.getCategoria().substring(0, 1).toUpperCase() + categoria.getCategoria().substring(1))
					&& dao.inserisciAssociazione(numeroConto, tipoContabilitaCoges, categoria.getCategoria().substring(0, 1).toUpperCase() + categoria.getCategoria().substring(1)));
		}
		
		return false;
	}
	
}
