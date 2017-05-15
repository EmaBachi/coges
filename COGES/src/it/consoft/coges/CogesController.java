package it.consoft.coges;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import it.consoft.coges.model.CategoriaDiCosto;
import it.consoft.coges.model.Costo;
import it.consoft.coges.model.Model;
import it.consoft.coges.model.RettificaGenerale;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CogesController {
	
	private Stage stage;
	
	private Model model;
	
	private RettificheController controllerRettifiche;
	private Stage dialogStageRettifiche;
	
	private StoricoRettificheController controllerStorico;
	private Stage dialogStageStorico;
	
	private ModificaAssociazioneController controllerModificaAssociazione;
	private Stage dialogStageModificaAssociazione;
	
	private Map<String, CategoriaDiCosto> mappa;
	
	private ObservableList<Costo> obsCostiCategoria;
	private ObservableList<Costo> obsCostiDettaglio;
	private ObservableList<CategoriaDiCosto> categorie;
	
	private CategoriaDiCosto categoriaFittiziaTotali;
	//modifica
	private CategoriaDiCosto categoriaFittiziaNoCategorie;
	
	private boolean primaVisualizzazione;
	
	public void setStage(Stage stage){
		this.stage = stage;
	}
	
	public void setModel(Model model){
    	this.model = model;
    }
	
	public void inizializzazione(){
		mappa = new TreeMap<String, CategoriaDiCosto>();
		
		obsCostiCategoria = FXCollections.observableArrayList();
		obsCostiDettaglio = FXCollections.observableArrayList();
		
		primaVisualizzazione = true;
		
		categoriaFittiziaTotali = new CategoriaDiCosto("TOTALI");
		categoriaFittiziaNoCategorie = new CategoriaDiCosto("TUTTI I CONTI");
		
		model.inizializzaStrutturaDati();
		
		bottoneRettifiche.setDisable(true);
		bottoneStoricoRettfiche.setDisable(true);
		bottoneImpostazioni.setDisable(true);
		
		comboCategoria.setVisible(false);
		
		labelSeleziona.setVisible(false);
		
		colonnaDescrizione.setVisible(false);
		
		bottoneEsportaInExcel.setVisible(false);
	       
	}
	
	 @FXML
	    private ResourceBundle resources;

	    @FXML
	    private URL location;
	    
	    @FXML
	    private TableView<Costo> tableTotali;

	    @FXML
	    private TableColumn<Costo, String> colonnaCodice;

	    @FXML
	    private TableColumn<Costo, String> colonnaDescrizione;

	    @FXML
	    private TableColumn<Costo, String> colonnaGenerale;
	    
	    @FXML
	    private TableColumn<Costo, String> colonnaRettificheGenerale;

	    @FXML
	    private TableColumn<Costo, String> colonnaCoges;

	    @FXML
	    private TableColumn<Costo, String> colonnaRettifiche;

	    @FXML
	    private TableColumn<Costo, String> colonnaDelta;

	    @FXML
	    private DatePicker calendarioDataInizio;

	    @FXML
	    private DatePicker calendarioDataFine;

	    @FXML
	    private Button bottoneControlla;
	    
	    @FXML
	    private Button bottoneRitornaAiTotali;
	    
	    @FXML
	    private Button bottoneRettifiche;
	    
	    @FXML
	    private Button bottoneStoricoRettfiche;
	    
	    @FXML
	    private Button bottoneImpostazioni;
	    
	    @FXML
	    private ComboBox<CategoriaDiCosto> comboCategoria;
	    
	    @FXML
	    private Label labelSeleziona;
	    
	    @FXML
	    private TextField testoCopia;
	    
	    @FXML
	    private ImageView bottoneEsportaInExcel;
	    
	    @FXML
	    private ComboBox<String> comboCompany;

	    @FXML
	    void doControlla(ActionEvent event) {
	    	this.caricamentoDati();
	    }
	    
	    public void caricamentoDati(){
	    	
	    	stage.getScene().setCursor(Cursor.WAIT);
	    	
	    	if(primaVisualizzazione == false)
	    		pulisci();
	    	
	    	try{
		    	LocalDate dataInizio = calendarioDataInizio.getValue();
		    	LocalDate dataFine = calendarioDataFine.getValue();
		    	String societa = comboCompany.getValue();
		    	
		    	if(dataInizio == null || dataFine == null || dataInizio.isAfter(dataFine)){
		    		
		    		Alert alert = new Alert(AlertType.ERROR);
		    		alert.setTitle("Errore");
		    		alert.setHeaderText("Errore Data");
		    		alert.setContentText("Inserisci la data e fai attenzione che la data di inizio controllo preceda la data di fine controllo");

		    		alert.showAndWait();
		    		
		    	} 
		    	else if(societa == null){
		    		
		    		Alert alert = new Alert(AlertType.ERROR);
		    		alert.setTitle("Errore");
		    		alert.setHeaderText("Società non selezionata");
		    		alert.setContentText("Per procedere selezionare una società");
		    		
		    		alert.showAndWait();
		    	}
		    	else {
		    		//metto la societa nel model
		    		model.setSocieta(societa);
		    		
		    		comboCategoria.setVisible(true);
			    	labelSeleziona.setVisible(true);
			    	bottoneEsportaInExcel.setVisible(true);
		    		bottoneRettifiche.setDisable(false);
			    	bottoneStoricoRettfiche.setDisable(false);
			    	bottoneImpostazioni.setDisable(false);
		    		//le prime quattro chiamate sono quelle standard
		    		//model.inizializzaStrutturaDati();//questa chiamata forse è meglio spostarla nel metodo inizializzazione
			    	model.caricaCostiContabilitaGenerale(dataInizio, dataFine);
					model.caricaCostiContabilitaCoges(dataInizio, dataFine);
					model.caricaRettificheCoges(dataInizio, dataFine);
					//la chiamata del metodo caricheRettificheContabilitaGenerale è necessario per tener conto delle rettifiche effettuate dall'utente.
					//il metodo viene chiamato qua quindi si capisce anche la funzionalità di refresh del bottone 'carica'
					model.caricaRettificheContabilitaGenerale();
			    	model.calcolaImporti();
			    	
			    	if(primaVisualizzazione == true){
			    		primaVisualizzazione = false;
			    		mappa = model.getMappa();
			    		this.visualizzaCategorie(mappa);
			    	}
			    	
			    	if(controllerStorico != null){
			    		controllerStorico.pulisciObsRettifiche();
			    		controllerStorico.setObsRettifiche(model.getRettificheContabilitaGenerale());
			    	}
			    	
			    	comboCategoria.getSelectionModel().selectFirst();
			    	this.visualizzaTotale(mappa);
			    	
		    	}
		    	
		    	stage.getScene().setCursor(Cursor.DEFAULT);
		    	
	    	} catch (Exception e){
	    	
	    		Alert alert = new Alert(AlertType.ERROR);
	    		alert.setTitle("Errore");
	    		alert.setHeaderText("Errore Data");
	    		alert.setContentText("Il formato della data è errato");

	    		alert.showAndWait();
	    		
	    		e.printStackTrace();
	    	
	    	}
	    }


		private void visualizzaTotale(Map<String, CategoriaDiCosto> mappa) {
		
			obsCostiCategoria.clear();
			
			mappa.remove(categoriaFittiziaTotali.getCategoria());
			mappa.remove(categoriaFittiziaNoCategorie.getCategoria());
			for(CategoriaDiCosto categoria : mappa.values()){
				Costo c = new Costo(categoria.getCategoria().substring(0, 1).toUpperCase() + categoria.getCategoria().substring(1), categoria.getCategoria(), categoria.getImportoContabilitaGenerale(), 
				categoria.getImportoRettificheGenerale(), categoria.getImportoContabilitaCoges(), categoria.getImportoRettifiche());
				obsCostiCategoria.add(c);
			}
			
			colonnaCodice.setText("Categoria");
			colonnaDescrizione.setVisible(false);
			tableTotali.setItems(obsCostiCategoria);
		}

		private void visualizzaCategorie(Map<String, CategoriaDiCosto> mappa) {
			mappa.put(categoriaFittiziaTotali.getCategoria(), categoriaFittiziaTotali);
			mappa.put(categoriaFittiziaNoCategorie.getCategoria(), categoriaFittiziaNoCategorie);
			categorie  = FXCollections.observableArrayList(mappa.values());
			comboCategoria.setItems(categorie);
		}

		
		private void visualizzaRisultato(CategoriaDiCosto categoria){
			
			obsCostiDettaglio = FXCollections.observableArrayList();
			
			for(Costo costo : model.getMappa().get(categoria.getCategoria()).getListaCosti()){
				obsCostiDettaglio.add(costo);
			}
			
			colonnaCodice.setText("Codice Conto");
			colonnaDescrizione.setVisible(true);
			tableTotali.setItems(obsCostiDettaglio);
		}
		
		private void visualizzaRisultatoElenco(){
			obsCostiDettaglio = FXCollections.observableArrayList();
			
			for(CategoriaDiCosto c : mappa.values()){
				for(Costo temp : c.getListaCosti()){
					obsCostiDettaglio.add(temp);
				}
			}
			
			colonnaCodice.setText("Codice Conto");
			colonnaDescrizione.setVisible(true);
			tableTotali.setItems(obsCostiDettaglio);
		}
		
		//metodo che mostra i totali non aggiornati
	    private void doRitornaAiTotali() {
	    	colonnaCodice.setText("Categoria");
			colonnaDescrizione.setVisible(false);
			tableTotali.setItems(obsCostiCategoria);
	    }

		//metodo che puliosce tutta la struttura dati
		private void pulisci(){
			for(CategoriaDiCosto c : mappa.values()){
				c.getMappa().clear();
				c.pulisciCategoria();
			}
			obsCostiCategoria.clear();
			obsCostiDettaglio.clear();
			model.pulisci();
		}
		
		
		//Questo metodo imposta il modo in cui la tabella deve presentare i dati.
		//la funzione setCellValueFactory non fa altro che indicare alla table column quale attributo mostrare della classe con cui è stata parametrizzata
		//la funzione setCellFactory si occupa invece dello stile della visualizzazione: ho deciso che dove la contabilità generale risultasse maggiore della contabilità coges,
		//il delta sia colorato di verde; in caso contrario il delta sarà colorato di rosso
		public void impostazionitabella() {
			
			colonnaCodice.setCellValueFactory(new Callback<CellDataFeatures<Costo, String>, ObservableValue<String>>() {
			     public ObservableValue<String> call(CellDataFeatures<Costo, String> p) {
			         return new ReadOnlyObjectWrapper<String>(p.getValue().getCodiceCosto());
			     }
			  });
			
			colonnaDescrizione.setCellValueFactory(new Callback<CellDataFeatures<Costo, String>, ObservableValue<String>>() {
			     public ObservableValue<String> call(CellDataFeatures<Costo, String> p) {
			         return new ReadOnlyObjectWrapper<String>(p.getValue().getDescrizione());
			     }
			  });
			
			colonnaGenerale.setCellValueFactory(new Callback<CellDataFeatures<Costo, String>, ObservableValue<String>>() {
			     public ObservableValue<String> call(CellDataFeatures<Costo, String> p) {
			         return new ReadOnlyObjectWrapper<String>(p.getValue().getImportoGeneralePerVisualizzazione());
			     }
			  });
			
			colonnaRettificheGenerale.setCellValueFactory(new Callback<CellDataFeatures<Costo, String>, ObservableValue<String>>() {
			     public ObservableValue<String> call(CellDataFeatures<Costo, String> p) {
			         return new ReadOnlyObjectWrapper<String>(p.getValue().getImportoRettificaGeneralePerVisualizzazione());
			     }
			  });
			
			colonnaCoges.setCellValueFactory(new Callback<CellDataFeatures<Costo, String>, ObservableValue<String>>() {
			     public ObservableValue<String> call(CellDataFeatures<Costo, String> p) {
			         return new ReadOnlyObjectWrapper<String>(p.getValue().getImportoContabilitaCogesPerVisualizzazione());
			     }
			  });
			
			colonnaRettifiche.setCellValueFactory(new Callback<CellDataFeatures<Costo, String>, ObservableValue<String>>() {
			     public ObservableValue<String> call(CellDataFeatures<Costo, String> p) {
			         return new ReadOnlyObjectWrapper<String>(p.getValue().getImportoRettificaCogesPerVisualizzazione());
			     }
			  });
			
			colonnaDelta.setCellValueFactory(new Callback<CellDataFeatures<Costo, String>, ObservableValue<String>>() {
			     public ObservableValue<String> call(CellDataFeatures<Costo, String> p) {
			         return new ReadOnlyObjectWrapper<String>(p.getValue().getDeltaPerVisualizzazione());
			     }
			  });
			
			
			colonnaDelta.setCellFactory(column -> {
			    return new TableCell<Costo, String>() {
			    	@Override
			        protected void updateItem(String importo, boolean empty) {
			            super.updateItem(importo, empty);

			            if (empty) {
			                setText(null);
			                setStyle("");
			            } else {
			                // Format date.
			                setText(""+importo);

			                // Style all dates in March with a different color.
			                if (importo.compareTo("0") > 0) {
			                    setTextFill(Color.GREEN);
			                    setStyle("");
			                } else if(importo.compareTo("0") < 0){
			                    setTextFill(Color.RED);
			                    setStyle("");
			                } else {
			                	setText("");
			                }
			            }
			        }
			    };
			});
		}
		
		//quando l'utente preme il pulsante rettifica invoco il metodo della classe main mostraDialogRettifiche, che ritorna un boolean.
		//nel caso in cui il boolean sia true incarico il model di effettuare alcune operazioni sui dati ricevuti in inout dall'utente.
		@FXML
		void doRettifche(ActionEvent event) {
	
			bottoneRettifiche.setDisable(true);
			
			controllerRettifiche.setOkClicked(false);
			controllerRettifiche.setCostiPerAutoCompletion(model.caricaContiPerDialogModifiche());
			boolean okClicked = this.mostraDialogRettifiche();
			 
			 if(okClicked){
				 
				 String numeroConto = controllerRettifiche.getNumeroConto().toUpperCase();
				 double importo = controllerRettifiche.getImporto();
				 String descrizione = controllerRettifiche.getDescrizione();
				 
				boolean b;
				try {
					//il controlle rnn fa altro che prendere i dati dal dialog delle rettifiche e chiama immediatamente il model. 
					//sarà il model ad occuparsi dell'inserimento delle rettifiche e dell'aggiornamento dei degli importi
					b = model.inserisciRettifica(numeroConto, descrizione, importo);
					
					if(b){
						
						this.caricamentoDati();
						
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Stato Rettfica");
						alert.setHeaderText("Rettifica avvenuta con successo");
						
						//la modifica è andata a buon fine quindi pulisci i campi del dialog delle rettifiche
				        controllerRettifiche.pulisciCampi();
				        
						alert.showAndWait();
						
					 } else {
						 Alert alert = new Alert(AlertType.INFORMATION);
						 alert.setTitle("Stato Rettifica");
						 alert.setHeaderText("Ops!! Si è verificato un errore");

						 alert.showAndWait();
					 }
						 
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			 
			 bottoneRettifiche.setDisable(false);
		}
		
		public void assegnaControllerRettifiche(){
			
			try{
				
				FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(getClass().getResource("rettifiche.fxml"));
		        AnchorPane page = (AnchorPane) loader.load();
		        
		        dialogStageRettifiche = new Stage();
		        dialogStageRettifiche.setTitle("Rettifica");
		        dialogStageRettifiche.initModality(Modality.WINDOW_MODAL);
		        Scene scene = new Scene(page);
		        dialogStageRettifiche.setScene(scene);
		        
		        controllerRettifiche = loader.getController();
		        controllerRettifiche.setDialogStage(dialogStageRettifiche);
		        
			} catch(IOException ioe){
				ioe.printStackTrace();
			}
		}
		
		private boolean mostraDialogRettifiche(){
			
			try{
				
		        dialogStageRettifiche.showAndWait();
		        
		        return controllerRettifiche.isOkClicked();
	        
			} catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
		
		/*metodo associato allpevento sul bottone rettifiche del controller coges.*/
		@FXML
		void doStoricoRettifiche (ActionEvent event){
			
			bottoneStoricoRettfiche.setDisable(true);
			
			controllerStorico.pulisciObsRettifiche();
			controllerStorico.setObsRettifiche(model.getRettificheContabilitaGenerale());
			
			this.mostraDialogStoricoRettifiche();
			
			bottoneStoricoRettfiche.setDisable(false);
			
		}
		
		//questo metodo assegna il file xml storicoRettifiche al controller controllerStorico
		public void assegnaControllerStoricoRettifiche(){
			
			try{
				
				FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(getClass().getResource("storicoRettifiche.fxml"));
		        AnchorPane page = (AnchorPane) loader.load();
		        
		        dialogStageStorico = new Stage();
		        dialogStageStorico.setTitle("Storico Rettifiche");
		        dialogStageStorico.initModality(Modality.WINDOW_MODAL);
		        Scene scene = new Scene(page);
		        dialogStageStorico.setScene(scene);
		        
		        controllerStorico = loader.getController();
		        controllerStorico.setDialogStage(dialogStageStorico);
		        controllerStorico.impostazioniTabella();
		        controllerStorico.setControllerCoges(this);
		        
			} catch(IOException ioe){
				ioe.printStackTrace();
			}
		}
		
		//questo metodo mostra il dialog, quindi siamo rimandati momentaneamente al cogesStrorico
		private void mostraDialogStoricoRettifiche(){	
			dialogStageStorico.show();
		}
		
		public boolean eliminaTutteRettifiche(boolean cancellaFile){
			return model.eliminaTutteRettifiche(cancellaFile);
		}
		
		public boolean eliminaSigoleRettifica(RettificaGenerale r){
			return model.eliminaSigolaRettifica(r);
		}
		
		public boolean modificaSingolaRettifica(RettificaGenerale r, double nuovoImporto){
			return model.modificaSingolaRettifica(r, nuovoImporto);
		}
		
		private void mostraDialogModifcaAssociazione(){
			
			dialogStageModificaAssociazione.showAndWait();
		}
		
		@FXML
		void doImpostazioni(ActionEvent event){
			
			bottoneImpostazioni.setDisable(true);
			
			controllerModificaAssociazione.setListaConti(model.caricaContiPerDialogModifiche());
			controllerModificaAssociazione.setListaCategorie(mappa.values());
			controllerModificaAssociazione.pulisci();
			this.mostraDialogModifcaAssociazione();
			
			this.pulisci();
			model.pulisciMappa();
			model.inizializzaStrutturaDati();
			this.caricamentoDati();
			
			bottoneImpostazioni.setDisable(false);
		}
		
		
		public void assegnaControllerModificaAssociazione(){
			try{
				
				FXMLLoader loader = new FXMLLoader();
		        loader.setLocation(getClass().getResource("modificaAssociazione.fxml"));
		        AnchorPane page = (AnchorPane) loader.load();
		        
		        dialogStageModificaAssociazione = new Stage();
		        dialogStageModificaAssociazione.setTitle("Impostazioni Categoria");
		        dialogStageModificaAssociazione.initModality(Modality.WINDOW_MODAL);
		        Scene scene = new Scene(page);
		        dialogStageModificaAssociazione.setScene(scene);
		        
		        controllerModificaAssociazione = loader.getController();
		        controllerModificaAssociazione.setDialogStage(dialogStageModificaAssociazione);
		        controllerModificaAssociazione.setControllerCoges(this);
		        controllerModificaAssociazione.impostazioniDialog();
		        
			} catch(IOException ioe){
				ioe.printStackTrace();
			}
		}
		
		public boolean modificaAssociazione(String numeroConto, int tipoConto, CategoriaDiCosto categoria){
			return model.modificaAssociazione(numeroConto, tipoConto, categoria);
		}
		
		@FXML
	    void doEsportaInExcel(MouseEvent event) {
			try {
	  
			        XSSFWorkbook workbook = new XSSFWorkbook();
			        XSSFSheet sheet = workbook.createSheet(comboCategoria.getValue().toString());
			        File tryFile = new File("D:/81_TiresiaApp"+comboCategoria.getValue().toString()+".xlsx");
			        File excelFile;
			        
			        if(tryFile.exists()) tryFile.delete();
			        
			        excelFile = new File("D:/81_TiresiaApp/"+comboCategoria.getValue().toString()+".xlsx");
			        excelFile.deleteOnExit();
			        FileOutputStream excelOutputStream = new FileOutputStream(excelFile);
			        
			        Row header = sheet.createRow(0);
			        
			        if(tableTotali.getColumns().size() == 7){
			        	Cell first = header.createCell(0);
			        	first.setCellValue("Codice Conto");
			        	Cell second = header.createCell(1);
			        	second.setCellValue("Descrizione");
			        	Cell third = header.createCell(2);
			        	third.setCellValue("Contabilità Generale");
			        	Cell fourth = header.createCell(3);
			        	fourth.setCellValue("Rettifiche Generale");
			        	Cell fifth = header.createCell(4);
			        	fifth.setCellValue("Contabilità Coges");
			        	Cell sixth = header.createCell(5);
			        	sixth.setCellValue("Rettifiche");
			        	Cell seventh = header.createCell(6);
			        	seventh.setCellValue("Delta");
			        }
			        
			        for (int row = 1; row < tableTotali.getItems().size()+1; row++) {
				    	
				    	Row rowExcel = sheet.createRow(row);
				    	
				        for (int column = 0; column < tableTotali.getColumns().size(); column++) {
				      
				        	Cell cellExcel = rowExcel.createCell(column);
				        	
				            Object cell = tableTotali.getColumns().get(column).getCellData(row-1);
				            
				            String str = (String)cell;
				            str = str.replaceAll("[.]", "");
				            str = str.replaceAll("[,]", ".");
				            try{
				            	double d = Double.parseDouble((String)str);
				            	cellExcel.setCellValue(d);
				            } catch(NumberFormatException nfe){
				            	cellExcel.setCellValue((String)str);
				            }
				        }
				    }
			        
			        workbook.write(excelOutputStream);
			        workbook.close();
			        Desktop dt = Desktop.getDesktop();
			        dt.open(excelFile);
			        
		        } catch(Exception e){
		        	e.printStackTrace();
		        	
		        	Alert alert = new Alert(AlertType.ERROR);
		    		alert.setTitle("Errore");
		    		alert.setHeaderText("Impossibile esportare in Excel");
		    		alert.setContentText("Assicurarsi che non esista già un file con lo stesso nome");
		    		
		    		alert.showAndWait();
		        }
		}
	
		@FXML
	    void initialize() {
	        assert calendarioDataInizio != null : "fx:id=\"calendarioDataInizio\" was not injected: check your FXML file 'coges.fxml'.";
	        assert calendarioDataFine != null : "fx:id=\"calendarioDataFine\" was not injected: check your FXML file 'coges.fxml'.";
	        assert bottoneControlla != null : "fx:id=\"bottoneControlla\" was not injected: check your FXML file 'coges.fxml'.";
	        assert tableTotali != null : "fx:id=\"tableTotali\" was not injected: check your FXML file 'coges.fxml'.";
	        assert colonnaCodice != null : "fx:id=\"colonnaCodice\" was not injected: check your FXML file 'coges.fxml'.";
	        assert colonnaDescrizione != null : "fx:id=\"colonnaDescrizione\" was not injected: check your FXML file 'coges.fxml'.";
	        assert colonnaGenerale != null : "fx:id=\"colonnaGenerale\" was not injected: check your FXML file 'coges.fxml'.";
	        assert colonnaRettificheGenerale != null : "fx:id=\"colonnaRettificheGenerale\" was not injected: check your FXML file 'coges.fxml'.";
	        assert colonnaCoges != null : "fx:id=\"colonnaCoges\" was not injected: check your FXML file 'cogesGiusto.fxml'.";
	        assert colonnaRettifiche != null : "fx:id=\"colonnaRettifiche\" was not injected: check your FXML file 'coges.fxml'.";
	        assert colonnaDelta != null : "fx:id=\"colonnaDelta\" was not injected: check your FXML file 'cogesGiusto.fxml'.";
	        assert bottoneRitornaAiTotali != null : "fx:id=\"bottonoRitonaAiTotali\" was not injected: check your FXML file 'coges.fxml'.";
	        assert bottoneRettifiche != null : "fx:id=\"bottoneRettifiche\" was not injected: check your FXML file 'coges.fxml'.";
	        assert comboCategoria != null : "fx:id=\"comboCategoria\" was not injected: check your FXML file 'coges.fxml'.";
	        assert testoCopia != null : "fx:id=\"testoCopia\" was not injected: check your FXML file 'coges.fxml'.";
	        assert bottoneEsportaInExcel != null : "fx:id=\"bottoneEsportaInExcel\" was not injected: check your FXML file 'coges.fxml'.";
	        assert bottoneImpostazioni != null : "fx:id=\"bottoneImpostazioni\" was not injected: check your FXML file 'coges.fxml'.";
	        
	        ObservableList<String> listaSocieta = FXCollections.observableArrayList();
	        listaSocieta.addAll("Consoft Spa", "Consoft Consulting", "CS INIT", "CARETEK");
	        comboCompany.setItems(listaSocieta);
	        
	        testoCopia.setVisible(false);
	        
	        comboCategoria.valueProperty().addListener(new ChangeListener<CategoriaDiCosto>() {
				@Override
				public void changed(ObservableValue<? extends CategoriaDiCosto> observable, CategoriaDiCosto oldValue,
						CategoriaDiCosto newValue) {
						if(newValue.equals(categoriaFittiziaTotali))
							doRitornaAiTotali();
						else if(newValue.equals(categoriaFittiziaNoCategorie))
							visualizzaRisultatoElenco();
						else
							visualizzaRisultato(newValue);
					
				}    
	        });
	        
	        tableTotali.getSelectionModel().setCellSelectionEnabled(true);
	        
	        tableTotali.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
	            if (newSelection != null) {
	                testoCopia.setText(newSelection.toString());
	                final Clipboard clipboard = Clipboard.getSystemClipboard();
	                final ClipboardContent content = new ClipboardContent();
	                content.putString(testoCopia.getText());
	                clipboard.setContent(content);
	            }
	        });
	    }
		
}
