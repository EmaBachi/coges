package it.consoft.coges;

import java.net.URL;
import java.util.Collection;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.AutoCompletionBinding.AutoCompletionEvent;
import org.controlsfx.control.textfield.TextFields;

import it.consoft.coges.model.Costo;


//Rettifiche controller è la classe 'controller' del file rettifiche.fxml
public class RettificheController implements Initializable{
	
	private Stage dialogStage;
	
	ObservableList<Costo> costi = FXCollections.observableArrayList();
	
	private String numeroConto;
	private double importo;
	private String descrizione;
	
	private boolean okClicked = false; //il boolean okClicked indica quando l'utente preme il pulsante 'ok' del dialog di conferma dopo aver premuto il pulsante
									// effettua rettifiche del form delle rettifiche.
	
	private boolean primoLancio = true;

	@FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField textNumeroConto;
    
    @FXML
    private TextField textImporto;
    
    @FXML
    private TextArea textDescrizione;

    @FXML
    private Button bottoneEffettuaRettifica;

    @FXML
    private Button bottoneCancella;
    
    //il metodo doCancella viene invocato quando l'utente preme il pulsante 'cancella' e ha il compito di pulire tutti i campi del form.
    @FXML
    void doCancella(ActionEvent event) {
    	textNumeroConto.clear();
    	textImporto.clear();
    	textDescrizione.clear();
    }
    
    //il metodo doEffettua rettifiche viene invocato quando l'utente preme il pulsante effettua rettifiche. Il suo compito principale è quello di aggiornare il valore del
    //boolean okClicked in base alle azioni dell'utente
    @FXML
    void doEffettuaRettifica(ActionEvent event) {
    	
    	boolean inputValido = true;
    	//okClicked = false;
    	
    	//un po' di controlli sull'input dell'utente
    	numeroConto = textNumeroConto.getText().trim();
    	if(numeroConto.equals("")){
    		Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Numero Conto Errato");
			alert.setContentText("Popolare il campo relativo al numero di conto");
			alert.showAndWait();
			inputValido = false;
    	}
    	
    	try{
    		importo = Double.parseDouble(textImporto.getText());
    	}
    	catch(Exception e){
    		Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Importo errato");
			alert.setContentText("L'importo deve essere un numero");
			alert.showAndWait();
			inputValido = false;
    	}
        
    	
    	descrizione = textDescrizione.getText();
    	
    	//Se l'input è valido entro qua dentro e mostro un dialog che permette all'utente di confermare la rettifica
    	if(inputValido == true){
	    		
	        Alert alert = new Alert(AlertType.CONFIRMATION);
	        alert.setTitle("Confirmation Dialog");
	        alert.setHeaderText("Le rettifiche effettuate saranno inserite sul database.");
	        alert.setContentText("Vuoi continuare?");
	        
	        //se la rettifica è confermata nel dialog alòlora okClicked diventa true. Altrimenti rimane false
	        Optional<ButtonType> result = alert.showAndWait();
	        if (result.get() == ButtonType.OK){
	        	okClicked = true;
	        	dialogStage.close();
	           
	        } else {
	        	okClicked = false;
	            this.pulisciCampi();
	        }
    	}

    }
    
    //un metodo per pulire i campi quando l'utente ha finito di apportare le rettifiche desiderate
    public void pulisciCampi(){
    	this.textNumeroConto.clear();
    	this.textImporto.clear();
    	this.textDescrizione.clear();
    }
    
    //un po' di getter e setter
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    public String getNumeroConto(){
    	return this.numeroConto;
    }
    
    public double getImporto(){
    	return this.importo;
    }
    
    public boolean isOkClicked() {
        return okClicked;
    }
    
    public String getDescrizione(){
    	return this.descrizione;
    }
    
    public void setDescrizione(String descrizione){
    	this.descrizione = descrizione;
    }
    
    public void setNumeroConto(String numeroConto){
    	this.numeroConto = numeroConto;
    }
    
    public void setImporto(double importo){
    	this.importo = importo;
    }
    
    public void setOkClicked(boolean b){
    	this.okClicked = b;
    }
    
    public void setCostiPerAutoCompletion(Collection<Costo> costi){
    	if(primoLancio){
    		this.costi.addAll(costi);
        	this.iniziallizaAutoCompletion();
        	primoLancio = false;
    	}
     }
    
    @FXML
    void initialize() {
        assert textNumeroConto != null : "fx:id=\"textNumeroConto\" was not injected: check your FXML file 'rettifiche.fxml'.";
        assert textImporto != null : "fx:id=\"textImporto\" was not injected: check your FXML file 'rettifiche.fxml'.";
        assert bottoneEffettuaRettifica != null : "fx:id=\"bottoneEffettuaRettifica\" was not injected: check your FXML file 'rettifiche.fxml'.";
        assert bottoneCancella != null : "fx:id=\"bottoneCancella\" was not injected: check your FXML file 'rettifiche.fxml'.";
        assert textDescrizione != null : "fx:id=\"textDescrizione\" was not injected: check your FXML file 'rettifiche.fxml'.";
        
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
	
	private void iniziallizaAutoCompletion(){
		AutoCompletionBinding<Costo> acb;
		acb = TextFields.bindAutoCompletion(textNumeroConto, costi);
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
