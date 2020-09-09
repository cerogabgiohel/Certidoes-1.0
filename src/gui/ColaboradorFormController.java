package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.scene.control.Alert.AlertType;
import model.entities.Colaborador;
import model.entities.Zona;
import model.exceptions.ValidationException;
import model.services.ColaboradorService;
import model.services.ZonaService;

public class ColaboradorFormController implements Initializable {

    @FXML
    private Label labelZona;

    @FXML
    private TextField txtColaborador;

    @FXML
    private Button btCancel;

    @FXML
    private Label labelNome;

    @FXML
    private TextField txtNome;

    @FXML
    private Button btSave;

    @FXML
    private Label labelColaborador;

    @FXML
    private ComboBox<Zona> comboBoxZona;
    
    private ZonaService zonaService;

    @FXML
    private Label labelErrorName;
    
    private Colaborador entity;
	
  	private ColaboradorService service;
  	
  	private ObservableList<Zona> obsListZona;
  	
  	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    @FXML
    void onBtSaveAction(ActionEvent event) {
       	if (entity == null) {
    			throw new IllegalStateException("Entidade nula");
    		}
    		if (service == null) {
    			throw new IllegalStateException("Serviço nulo");
    		}
    		try {
    			entity = getFormData();
    			service.saveOrUpdate(entity);
    			notifyDataChangeListeners();
    			Utils.currentStage(event).close();
    		}
    		catch (ValidationException e) {
    			setErrorMessages(e.getErrors());
    		}
    		catch (DbException e) {
    			Alerts.showAlert("Erro ao salvar objeto", null, e.getMessage(), AlertType.ERROR);
    		}
        	

    }

    @FXML
    void onBtCancelAction(ActionEvent event) {
    	Utils.currentStage(event).close();
    }
    
    public void setColaborador(Colaborador entity) {
		this.entity = entity;
	}
	
	public void setColaboradorService(ColaboradorService service) {
		this.service = service;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Colaborador getFormData() {
		Colaborador obj = new Colaborador();
		
		ValidationException exception = new ValidationException("Erro de Validação");
		
		obj.setColaborador(Utils.tryParseToInt(txtColaborador.getText()));	
		
		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addError("Nome", "Campo não pode estar vazio");
		}
		obj.setNome(txtNome.getText());		
		
		if (exception.getErrors().size()>0) {
			throw exception;
		}
		
		obj.setZona(comboBoxZona.getValue());
		
		return obj;
		
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		initializeComboBoxZona();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtColaborador);
		
		
	}
	
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException ("Entidade está vazia");
		}
		
		txtColaborador.setText(String.valueOf(entity.getColaborador()));
		txtNome.setText(entity.getNome());
		
		if (entity.getZona() == null) {
			comboBoxZona.getSelectionModel().selectFirst();
		}
		else {
			comboBoxZona.setValue(entity.getZona());
		}
	
	}
	
	public void loadAssociatedObjects() {
		if (zonaService == null) {
			throw new IllegalStateException("Lista de Zonas está vazia");
		}
		List<Zona> list = zonaService.findAll();
		obsListZona = FXCollections.observableArrayList(list);
		comboBoxZona.setItems(obsListZona);
	
	}
	
	private void setErrorMessages(Map<String, String> errors ) {
		Set<String> fields = errors.keySet();
		
		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}
	
	private void initializeComboBoxZona() {
		Callback<ListView<Zona>, ListCell<Zona>> factory = lv -> new ListCell<Zona>() {
			@Override
			protected void updateItem(Zona item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.toString());
			}
		};
		comboBoxZona.setCellFactory(factory);
		comboBoxZona.setButtonCell(factory.call(null));
	}

}
