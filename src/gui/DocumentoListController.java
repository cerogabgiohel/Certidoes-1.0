package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Documento;
import model.services.DocumentoService;

public class DocumentoListController implements Initializable, DataChangeListener {

    @FXML
    private Button btNew;

    @FXML
    private TableColumn<Documento, Integer> tableColumnTipoDocumento;

    @FXML
    private TableView<Documento> tableViewDocumento;

    @FXML
    private TableColumn<Documento, Documento> tableColumnEDIT;

    @FXML
    private TableColumn<Documento, Documento> tableColumnREMOVE;

    @FXML
    private TableColumn<Documento, String> tableColumnDescricao;
    
    private DocumentoService service;
    
    private ObservableList<Documento>obsList;

    @FXML
    void onBtNewAction(ActionEvent event) {
    	Stage parentStage = Utils.currentStage(event);
    	Documento obj = new Documento();
		createDialogForm(obj, "/gui/DocumentoForm.fxml", parentStage);
    }
    
    public void setDocumentoService(DocumentoService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {

		tableColumnTipoDocumento.setCellValueFactory(new PropertyValueFactory<>("tipoDoc"));
		tableColumnDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDocumento.prefHeightProperty().bind(stage.heightProperty());

	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Serviço nulo");
		}
		List<Documento> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewDocumento.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(Documento obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			DocumentoFormController controller = loader.getController();
			
			controller.setDocumento(obj);

			controller.setDocumentoService(new DocumentoService());

			controller.subscribeDataChangeListener(this);

			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Entre com os dados do Tipo de Documento:");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("Exceção de E/S", "Erro de carregamento de Tela", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChanged() {
		updateTableView();
	}

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Documento, Documento>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Documento obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/DocumentoForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Documento, Documento>() {
			private final Button button = new Button("Remover");

			@Override
			protected void updateItem(Documento obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Documento obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Tem certeza que deseja excluir?");
		
		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Serviço Nulo");
			}
			try {
				service.remove(obj);
				updateTableView();
			}
			catch (DbIntegrityException e) {
				Alerts.showAlert("Erro ao remover o registro", null, e.getMessage(), AlertType.ERROR);
			}
			
		}
	}

}
