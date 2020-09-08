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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Zona;
import model.services.ZonaService;

public class ZonaListController implements Initializable, DataChangeListener {

    @FXML
    private TableView<Zona> tableViewZona;

    @FXML
    private TableColumn<Zona, Integer> tableColumnZona;

    @FXML
    private Button btNew;

    @FXML
    private TableColumn<Zona, Integer> tableColumnChaveZona;

    @FXML
    private TableColumn<Zona, String> tableColumnSede;

    @FXML
    private TableColumn<Zona, Zona> tableColumnEDIT;

    @FXML
    private TableColumn<Zona, Zona> tableColumnREMOVE;

    @FXML
    private TableColumn<Zona, String> tableColumnUF;
    
    private ZonaService service;
    
    private ObservableList<Zona>obsList;

    @FXML
    void onBtNewAction(ActionEvent event) {
    	Stage parentStage = Utils.currentStage(event);
    	Zona obj = new Zona();
		createDialogForm(obj, "/gui/ZonaForm.fxml", parentStage);
    }
    
    public void setZonaService(ZonaService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {

		tableColumnChaveZona.setCellValueFactory(new PropertyValueFactory<>("zona"));
		tableColumnZona.setCellValueFactory(new PropertyValueFactory<>("zonaEleitoral"));
		tableColumnUF.setCellValueFactory(new PropertyValueFactory<>("uf"));
		tableColumnSede.setCellValueFactory(new PropertyValueFactory<>("sede"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewZona.prefHeightProperty().bind(stage.heightProperty());

	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Serviço nulo");
		}
		List<Zona> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewZona.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	private void createDialogForm(Zona obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			ZonaFormController controller = loader.getController();
			
			controller.setZona(obj);

			controller.setZonaService(new ZonaService());

			controller.subscribeDataChangeListener(this);

			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Entre com os dados da Zona:");
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
		tableColumnEDIT.setCellFactory(param -> new TableCell<Zona, Zona>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Zona obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/ZonaForm.fxml", Utils.currentStage(event)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Zona, Zona>() {
			private final Button button = new Button("Remover");

			@Override
			protected void updateItem(Zona obj, boolean empty) {
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

	private void removeEntity(Zona obj) {
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
