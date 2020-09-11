package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Callback;
import model.entities.Certidao;
import model.entities.Colaborador;
import model.entities.Documento;
import model.entities.Zona;
import model.exceptions.ValidationException;
import model.services.CertidaoService;
import model.services.ColaboradorService;
import model.services.DocumentoService;
import model.services.ZonaService;

public class CertidaoFormController implements Initializable {

    @FXML
    private Label labelZonaEleitoral;

    @FXML
    private Label labelDtEmissao;

    @FXML
    private Button btCancel;

    @FXML
    private Button btSave;

    @FXML
    private TextField txtCertidao;

    @FXML
    private Label labelColaborador;

    @FXML
    private Label labelRequerente;

    @FXML
    private TextField txtRequerente;

    @FXML
    private ComboBox<Colaborador> comboBoxColaborador;

    @FXML
    private ComboBox<Documento> comboBoxTipDoc;

    @FXML
    private Label labelCertidao;

    @FXML
    private DatePicker datePickerDtEmissao;

    @FXML
    private Label labelTipDoc;

    @FXML
    private Label labelErrorName;

    @FXML
    private ComboBox<Zona> comboBoxZonaEleitoral;
    
    private Certidao entity;
	
  	private CertidaoService service;
  	
  	private ObservableList<Zona> obsListZona;
  	
  	private ObservableList<Colaborador> obsListColaborador;
  	
  	private ObservableList<Documento> obsListDocumento;
  	
  	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
  	
  	private ZonaService zonaService;
  	 
  	private DocumentoService docService;
  	
  	private ColaboradorService colabService;

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
    
    public void setCertidao(Certidao entity) {
		this.entity = entity;
	}
	
	public void setCertidaoService(CertidaoService service, ZonaService zonaService, ColaboradorService colabService, DocumentoService docService) {
		this.service = service;
		this.zonaService = zonaService;
		this.colabService = colabService;
		this.docService = docService;
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Certidao getFormData() {
		Certidao obj = new Certidao();
		
		ValidationException exception = new ValidationException("Erro de Validação");
		
		obj.setCertidao(Utils.tryParseToInt(txtCertidao.getText()));	
		
		if (txtRequerente.getText() == null || txtRequerente.getText().trim().equals("")) {
			exception.addError("Nome", "Campo não pode estar vazio");
		}
		obj.setRequerente(txtRequerente.getText());		
		
		if (exception.getErrors().size()>0) {
			throw exception;
		}
		
		obj.setZona(comboBoxZonaEleitoral.getValue());
		obj.setColaborador(comboBoxColaborador.getValue());
		obj.setTipoDoc(comboBoxTipDoc.getValue());
		
		if (datePickerDtEmissao.getValue() == null) {
			exception.addError("DtEmissao", "Data da Emissão não pode ser nula");
		}
		else {
			Instant instant = Instant.from(datePickerDtEmissao.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setDataEmissao(Date.from(instant));
		}
		
		return obj;
		
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
		initializeComboBoxZona();
		initializeComboBoxColaborador();
		initializeComboBoxDocumento();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtCertidao);
		Utils.formatDatePicker(datePickerDtEmissao, "dd/MM/yyyy");
		
	}
	
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException ("Entidade está vazia");
		}
		
		txtCertidao.setText(String.valueOf(entity.getCertidao()));
		txtRequerente.setText(entity.getRequerente());
		
		if (entity.getZona() == null) {
			comboBoxZonaEleitoral.getSelectionModel().selectFirst();
		}
		else {
			comboBoxZonaEleitoral.setValue(entity.getZona());
		}
		
		if (entity.getTipoDoc() == null) {
			comboBoxTipDoc.getSelectionModel().selectFirst();
		}
		else {
			comboBoxTipDoc.setValue(entity.getTipoDoc());
		}
		
		if (entity.getColaborador() == null) {
			comboBoxColaborador.getSelectionModel().selectFirst();
		}
		else {
			comboBoxColaborador.setValue(entity.getColaborador());
		}
		
		if(entity.getDataEmissao() != null) {
		datePickerDtEmissao.setValue(LocalDate.of(entity.getDataEmissao().getYear(), entity.getDataEmissao().getMonth(), entity.getDataEmissao().getDay()));
		}
	
	}
	
	public void loadAssociatedObjects() {
		if (zonaService == null) {
			throw new IllegalStateException("Lista de Zonas está vazia");
		}
		List<Zona> list = zonaService.findAll();
		obsListZona = FXCollections.observableArrayList(list);
		comboBoxZonaEleitoral.setItems(obsListZona);
	
		if (docService == null ) {
			throw new IllegalStateException("Lista de Documentos está vazia");
		}
		List<Documento> listDoc = docService .findAll();
		obsListDocumento = FXCollections.observableArrayList(listDoc);
		comboBoxTipDoc.setItems(obsListDocumento);
		
		if (colabService == null ) {
			throw new IllegalStateException("Lista de Colaboradores está vazia");
		}
		List<Colaborador> listColab = colabService.findAll();
		obsListColaborador = FXCollections.observableArrayList(listColab);
		comboBoxColaborador.setItems(obsListColaborador);
	
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
		comboBoxZonaEleitoral.setCellFactory(factory);
		comboBoxZonaEleitoral.setButtonCell(factory.call(null));
	}
	
	private void initializeComboBoxDocumento() {
		Callback<ListView<Documento>, ListCell<Documento>> factory = lv -> new ListCell<Documento>() {
			@Override
			protected void updateItem(Documento item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getDescricao());
			}
		};
		comboBoxTipDoc.setCellFactory(factory);
		comboBoxTipDoc.setButtonCell(factory.call(null));
	}
	
	private void initializeComboBoxColaborador() {
		Callback<ListView<Colaborador>, ListCell<Colaborador>> factory = lv -> new ListCell<Colaborador>() {
			@Override
			protected void updateItem(Colaborador item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		comboBoxColaborador.setCellFactory(factory);
		comboBoxColaborador.setButtonCell(factory.call(null));
	}

}
