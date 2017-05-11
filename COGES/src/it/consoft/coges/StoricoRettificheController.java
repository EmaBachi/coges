package it.consoft.coges;

import java.net.URL;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import it.consoft.coges.model.RettificaGenerale;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.stage.Stage;
import javafx.util.Callback;

public class StoricoRettificheController {
	
	private Stage dialogStage;
	
	private CogesController controllerCoges;
	
	private ObservableList<RettificaGenerale> obsRettifiche = FXCollections.observableArrayList();
	
	private boolean rimozioneFile;
	
	public void setDialogStage(Stage dialogStage){
		this.dialogStage = dialogStage;
	}
	
	public void setControllerCoges(CogesController controllerCoges){
		this.controllerCoges = controllerCoges;
	}
	
	public void setObsRettifiche(List<RettificaGenerale> lista){
		obsRettifiche.addAll(lista);
		tabellaStorico.setItems(obsRettifiche);
	}
	
	public void pulisciObsRettifiche(){
		obsRettifiche.clear();
	}
	
	@FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button bottoneCancellaStorico;

    @FXML
    private TableView<RettificaGenerale> tabellaStorico;

    @FXML
    private TableColumn<RettificaGenerale, Timestamp> colonnaDataRettifica;

    @FXML
    private TableColumn<RettificaGenerale, String> colonnaCodiceConto;

    @FXML
    private TableColumn<RettificaGenerale, Double> colonnaImporto;

    @FXML
    private Button bottoneModificaRettifica;

    @FXML
    private Button bottoneEliminaRettifica;

    @FXML
    void doCancellaStorico(ActionEvent event) {
    	
    	Alert alertConfirm = new Alert(AlertType.CONFIRMATION);
        alertConfirm.setTitle("Scegli una opzione");
        alertConfirm.setHeaderText("Lo storico delle rettifiche verrà cancellato.");
        alertConfirm.setContentText("Vuoi cancellare le rettifiche della società selezionata o tutte le rettifiche?");
        
        ButtonType bottoneCancellaRettifiche = new ButtonType("Solo rettifiche della societa");
        ButtonType bottoneCancellaRettificheConFile = new ButtonType("Tutte le rettifiche e il file");
        ButtonType buttonTypeCancel = new ButtonType("Cancella", ButtonData.CANCEL_CLOSE);
        
        alertConfirm.getButtonTypes().setAll(bottoneCancellaRettifiche, bottoneCancellaRettificheConFile, buttonTypeCancel);

        //se la rettifica è confermata nel dialog allora okClicked diventa true. Altrimenti rimane false
        Optional<ButtonType> result = alertConfirm.showAndWait();
        
        if (result.get() == bottoneCancellaRettifiche){
        	if(controllerCoges.eliminaTutteRettifiche(false)){
        		Alert alertInform = new Alert(AlertType.INFORMATION);
				alertInform.setTitle("Stato Rettifiche");
				alertInform.setHeaderText("Rettifiche eliminate con successo");
				
				controllerCoges.caricamentoDati();
				alertInform.showAndWait();
        	} else {
        		Alert alertError = new Alert(AlertType.INFORMATION);
				alertError.setTitle("Stato Rettifiche");
				alertError.setHeaderText("Ops!! Si è verificato un errore");
				
				alertError.showAndWait();
        	}
        } 
        if(result.get() == bottoneCancellaRettificheConFile){
        	if(controllerCoges.eliminaTutteRettifiche(true)){
        		Alert alertInform = new Alert(AlertType.INFORMATION);
				alertInform.setTitle("Stato Rettifiche");
				alertInform.setHeaderText("Rettifiche e file rettifiche eliminati con successo");
				
				controllerCoges.caricamentoDati();
				alertInform.showAndWait();
        	} else {
        		Alert alertError = new Alert(AlertType.INFORMATION);
				alertError.setTitle("Stato Rettifiche");
				alertError.setHeaderText("Ops!! Si è verificato un errore");
				
				alertError.showAndWait();
        	}
        }
    }

    @FXML
    void doEliminaSingolaRettifica(ActionEvent event) {
    	
    	Alert alertConfirm = new Alert(AlertType.CONFIRMATION);
        alertConfirm.setTitle("Scegli una opzione");
        alertConfirm.setHeaderText("La rettifica verrà cancellata definitivamente.");
        alertConfirm.setContentText("Vuoi continuare?");
        
        Optional<ButtonType> result = alertConfirm.showAndWait();
        if (result.get() == ButtonType.OK){
        	if(controllerCoges.eliminaSigoleRettifica(tabellaStorico.getSelectionModel().getSelectedItem())){
        		Alert alertInform = new Alert(AlertType.INFORMATION);
				alertInform.setTitle("Stato Rettifica");
				alertInform.setHeaderText("Rettifica eliminata con successo");
				
				controllerCoges.caricamentoDati();

				alertInform.showAndWait();
        	} else {
        		Alert alertError = new Alert(AlertType.INFORMATION);
				alertError.setTitle("Stato Rettifica");
				alertError.setHeaderText("Ops!! Si è verificato un errore");
				
				alertError.showAndWait();
        	}
        }
    }

    @FXML
    void doModificaSingolaRettifica(ActionEvent event) {
    
    	TextInputDialog dialog = new TextInputDialog();
    	dialog.setTitle("Modifica Rettifica");
    	dialog.setHeaderText("Modifica Rettifica");
    	dialog.setContentText("Inserire il nuovo importo:");

    	// Traditional way to get the response value.
    	Optional<String> result = dialog.showAndWait();
    	if (result.isPresent()){
    		try{
    			double nuovoImporto = Double.parseDouble(result.get().trim());
    			
    			if(controllerCoges.modificaSingolaRettifica(tabellaStorico.getSelectionModel().getSelectedItem(), nuovoImporto)){
    				Alert alertInform = new Alert(AlertType.INFORMATION);
					alertInform.setTitle("Stato Rettifica");
					alertInform.setHeaderText("Rettifica modificata con successo");
					
					controllerCoges.caricamentoDati();

					alertInform.showAndWait();
    			} else {
    				Alert alertError = new Alert(AlertType.INFORMATION);
					alertError.setTitle("Stato Rettifica");
					alertError.setHeaderText("Ops!! Si è verificato un errore");
					
					alertError.showAndWait();
    			}
    		} catch (Exception e){
    			Alert alert = new Alert(AlertType.ERROR);
	    		alert.setTitle("Errore");
	    		alert.setHeaderText("Errore Input");
	    		alert.setContentText("Inserire un importo valido");

	    		alert.showAndWait();
    		}
    	}
    }
    
    public boolean isRimozioneFile(){
    	return this.rimozioneFile;
    }
    
    public void setRimozioneFile(boolean rimozioneFile){
    	this.rimozioneFile = rimozioneFile;
    }
    
    public void impostazioniTabella(){
    	
    	colonnaDataRettifica.setCellValueFactory(new Callback<CellDataFeatures<RettificaGenerale, Timestamp>, ObservableValue<Timestamp>>() {
		     public ObservableValue<Timestamp> call(CellDataFeatures<RettificaGenerale, Timestamp> p) {
		         return new ReadOnlyObjectWrapper<Timestamp>(p.getValue().getData());
		     }
		  });
    	
    	colonnaCodiceConto.setCellValueFactory(new Callback<CellDataFeatures<RettificaGenerale, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<RettificaGenerale, String> p) {
		         return new ReadOnlyObjectWrapper<String>(p.getValue().getCodiceCosto());
		     }
		  });
    	
    	colonnaImporto.setCellValueFactory(new Callback<CellDataFeatures<RettificaGenerale, Double>, ObservableValue<Double>>() {
		     public ObservableValue<Double> call(CellDataFeatures<RettificaGenerale, Double> p) {
		         return new ReadOnlyObjectWrapper<Double>(p.getValue().getImporto());
		     }
		  });
    }
    
    @FXML
    void initialize() {
        assert bottoneCancellaStorico != null : "fx:id=\"bottoneCancellaStorico\" was not injected: check your FXML file 'storicoRettifiche.fxml'.";
        assert tabellaStorico != null : "fx:id=\"tabellaStorico\" was not injected: check your FXML file 'storicoRettifiche.fxml'.";
        assert colonnaDataRettifica != null : "fx:id=\"colonnaDataRettifica\" was not injected: check your FXML file 'storicoRettifiche.fxml'.";
        assert colonnaCodiceConto != null : "fx:id=\"colonnaCodiceConto\" was not injected: check your FXML file 'storicoRettifiche.fxml'.";
        assert colonnaImporto != null : "fx:id=\"colonnaImporto\" was not injected: check your FXML file 'storicoRettifiche.fxml'.";
        assert bottoneModificaRettifica != null : "fx:id=\"bottoneModificaRettifica\" was not injected: check your FXML file 'storicoRettifiche.fxml'.";
        assert bottoneEliminaRettifica != null : "fx:id=\"bottoneEliminaRettifica\" was not injected: check your FXML file 'storicoRettifiche.fxml'.";
        
        bottoneModificaRettifica.setDisable(true);
		bottoneEliminaRettifica.setDisable(true);

		tabellaStorico.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
		    if (newSelection != null) {
		       bottoneModificaRettifica.setDisable(false);
		       bottoneEliminaRettifica.setDisable(false);
		    } else {
		    	bottoneModificaRettifica.setDisable(true);
				bottoneEliminaRettifica.setDisable(true);
		    }
		});

    }
}

