package it.consoft.coges;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

import it.consoft.coges.model.CategoriaDiCosto;
import it.consoft.coges.model.Costo;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.AutoCompletionBinding.AutoCompletionEvent;
import org.controlsfx.control.textfield.TextFields;

public class ModificaAssociazioneController implements Initializable{
	
	private Stage dialogStage;
	private CogesController controllerCoges;
	
	private ObservableList<Costo> conti = FXCollections.observableArrayList();
	private ObservableList<CategoriaDiCosto> categorie = FXCollections.observableArrayList();
	
	private boolean primoLancio = true;
	
	public void setDialogStage(Stage dialogStageModificaAssociazione){
		this.dialogStage = dialogStageModificaAssociazione;
	}
	
	public void setControllerCoges(CogesController controllerCoges){
		this.controllerCoges = controllerCoges;
	}

	public void setListaCategorie(Collection<CategoriaDiCosto> catgeorie){
		this.categorie.clear();
		this.categorie.addAll(catgeorie);
		riempiCategorie();
	}
	
	private void riempiCategorie(){
		comboCategoria.setItems(categorie);
	}
	
	public void setListaConti(Collection<Costo> conti){
		if(primoLancio){
			this.conti.addAll(conti);
			this.iniziallizaAutoCompletion();
			primoLancio = false;
		}
	}
	
	@FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField testoConto;

    @FXML
    private ComboBox<String> comboTipoConto;

    @FXML
    private ComboBox<CategoriaDiCosto> comboCategoria;

    @FXML
    private CheckBox checkNuovaCategoria;

    @FXML
    private Button bottoneApplca;

    @FXML
    private TextField testoNuovaCategoria;

    @FXML
    void doApplica(ActionEvent event) {
    	
    	boolean primaValidazione = true;
    	
    	String numeroConto = testoConto.getText().trim();
    	CategoriaDiCosto categoria = null;
    	
    	//controllo errori sulla textField
    	if(numeroConto.equals("")){
    		Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Errore");
    		alert.setHeaderText("Errore Numero Conto");
    		alert.setContentText("Inserire un numero conto valido.");
    		primaValidazione = false;

    		alert.showAndWait();
    	}
    	
    	int tipoContoCodificato = 0;
    	
    	String tipoConto = comboTipoConto.getValue();
    	//controllo errori sulla combobox tipoConto
    	if(tipoConto == null){
    		Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Errore");
    		alert.setHeaderText("Errore Tipo Conto");
    		alert.setContentText("Selezionare un tipo di conto.");
    		primaValidazione = false;

    		alert.showAndWait();
    	}
    	
    	if(primaValidazione){
	    	switch (tipoConto){
	    	case "Contabilità generale":
	    		tipoContoCodificato = 1;
	    		break;
	    	case "Contabilità coges":
	    		tipoContoCodificato = 3;
	    		break;
	    	case "Rettifica coges":
	    		tipoContoCodificato = 2;
	    		break;
	    	}
	    	
	    	if(!checkNuovaCategoria.isSelected()){
	    		categoria = comboCategoria.getValue();
	    		
	    	} else {
	    		categoria = new CategoriaDiCosto(testoNuovaCategoria.getText().trim());
	    	}
	    	
	    	if(categoria != null)
	    		if(controllerCoges.modificaAssociazione(numeroConto, tipoContoCodificato, categoria)){
	    			Alert alert = new Alert(AlertType.CONFIRMATION);
		    		alert.setTitle("Conferma modifica");
		    		alert.setHeaderText("Modifica apportata con successo");

		    		alert.showAndWait();
		    		dialogStage.close();
	    		}
	    	else{
	    		Alert alert = new Alert(AlertType.ERROR);
	    		alert.setTitle("Errore");
	    		alert.setHeaderText("Errore categoria");
	    		alert.setContentText("Selezionare una categoria");

	    		alert.showAndWait();
	    	}
    	}
    }
    
    public void impostazioniDialog(){
    	ObservableList<String> tipiConto = FXCollections.observableArrayList();
        tipiConto.add("Contabilità");
        tipiConto.add("Rettifica coges");
        
        comboTipoConto.setItems(tipiConto);

        testoNuovaCategoria.setVisible(false);
        
        checkNuovaCategoria.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if(newValue == true)
					testoNuovaCategoria.setVisible(true);
				else
					testoNuovaCategoria.setVisible(false);
				
			}
        });
    }
    
    public void pulisci(){
    	this.testoConto.clear();
    	this.testoNuovaCategoria.clear();
    	this.checkNuovaCategoria.setSelected(false);
    	this.comboCategoria.setValue(null);
    	this.comboTipoConto.setValue(null);
    }

    @FXML
    void initialize() {
        assert testoConto != null : "fx:id=\"testoConto\" was not injected: check your FXML file 'modificaAssociazione.fxml'.";
        assert comboTipoConto != null : "fx:id=\"comboTipoCOnto\" was not injected: check your FXML file 'modificaAssociazione.fxml'.";
        assert comboCategoria != null : "fx:id=\"comboCategoria\" was not injected: check your FXML file 'modificaAssociazione.fxml'.";
        assert checkNuovaCategoria != null : "fx:id=\"checkNuovaCategoria\" was not injected: check your FXML file 'modificaAssociazione.fxml'.";
        assert bottoneApplca != null : "fx:id=\"bottoneApplca\" was not injected: check your FXML file 'modificaAssociazione.fxml'.";
        assert testoNuovaCategoria != null : "fx:id=\"testoNuovaCatgeoria\" was not injected: check your FXML file 'modificaAssociazione.fxml'.";
        
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
	private void iniziallizaAutoCompletion(){
		AutoCompletionBinding<Costo> acb;
		acb = TextFields.bindAutoCompletion(testoConto, conti);
		acb.setOnAutoCompleted(new EventHandler<AutoCompletionBinding.AutoCompletionEvent<Costo>>()
		{

		  @Override
		  public void handle(AutoCompletionEvent<Costo> event)
		  {
		   	Costo valoreAutoCompletamento = event.getCompletion();
		  }
		});
	}

}
