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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Documento;
import model.exceptions.ValidationException;
import model.services.DocumentoService;

public class DocumentoFormController implements Initializable{

    @FXML
    private TextField txtTipoDocumento;

    @FXML
    private Label labelDescricao;

    @FXML
    private Button btCancel;

    @FXML
    private TextField txtDescricao;

    @FXML
    private Button btSave;

    @FXML
    private Label labelTipoDocumento;

    @FXML
    private Label labelErrorName;
    
    private Documento entity;
	
	private DocumentoService service;
	
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
    public void setDocumento(Documento entity) {
		this.entity = entity;
	}
	
	public void setDocumentoService(DocumentoService service) {
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

	private Documento getFormData() {
		Documento obj = new Documento();
		
		ValidationException exception = new ValidationException("Erro de Validação");
		
		obj.setTipoDoc(Utils.tryParseToInt(txtTipoDocumento.getText()));
		
		if (txtDescricao.getText() == null || txtDescricao.getText().trim().equals("")) {
			exception.addError("Descricao", "Campo não pode estar vazio");
		}
		obj.setDescricao(txtDescricao.getText());
		
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
		Constraints.setTextFieldInteger(txtTipoDocumento);
		Constraints.setTextFieldMaxLength(txtDescricao, 30);
	}
	
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException ("Entidade está vazia");
		}
		
		txtTipoDocumento.setText(String.valueOf(entity.getTipoDoc()));
		txtDescricao.setText(entity.getDescricao());
	}
	
	private void setErrorMessages(Map<String, String> errors ) {
		Set<String> fields = errors.keySet();
		
		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}

}
