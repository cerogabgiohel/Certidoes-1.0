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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.entities.Zona;
import model.exceptions.ValidationException;
import model.services.ZonaService;

public class ZonaFormController implements Initializable {

    @FXML
    private Label labelSede;

    @FXML
    private TextField txtSede;

    @FXML
    private Label labelZonaEleitoral;

    @FXML
    private TextField txtZonaEleitoral;

    @FXML
    private Button btCancel;

    @FXML
    private TextField txtUF;

    @FXML
    private Button btSave;

    @FXML
    private Label labelChaveZona;

    @FXML
    private TextField txtChaveZona;

    @FXML
    private Label labelUF;

    @FXML
    private Label labelErrorName;
    
    private Zona entity;
	
  	private ZonaService service;
  	
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
    
    public void setZona(Zona entity) {
		this.entity = entity;
	}
	
	public void setZonaService(ZonaService service) {
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

	private Zona getFormData() {
		Zona obj = new Zona();
		
		ValidationException exception = new ValidationException("Erro de Validação");
		
		obj.setZona(Utils.tryParseToInt(txtChaveZona.getText()));
		
		obj.setZonaEleitoral(Utils.tryParseToInt(txtZonaEleitoral.getText()));
		
		if (txtUF.getText() == null || txtUF.getText().trim().equals("")) {
			exception.addError("UF", "Campo não pode estar vazio");
		}
		obj.setUf(txtUF.getText());
		
		if (txtSede.getText() == null || txtSede.getText().trim().equals("")) {
			exception.addError("Sede", "Campo não pode estar vazio");
		}
		obj.setSede(txtSede.getText());
		
		if (exception.getErrors().size()>0) {
			throw exception;
		}
		
		return obj;
		
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtChaveZona);
		Constraints.setTextFieldInteger(txtZonaEleitoral);
		Constraints.setTextFieldMaxLength(txtUF, 2);
		
	}
	
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException ("Entidade está vazia");
		}
		
		txtChaveZona.setText(String.valueOf(entity.getZona()));
		txtZonaEleitoral.setText(String.valueOf(entity.getZonaEleitoral()));
		txtUF.setText(entity.getUf());
		txtSede.setText(entity.getSede());
	}
	
	private void setErrorMessages(Map<String, String> errors ) {
		Set<String> fields = errors.keySet();
		
		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}

}
