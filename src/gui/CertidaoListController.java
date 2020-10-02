package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
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
import model.entities.Certidao;
import model.services.CertidaoService;
import model.services.ColaboradorService;
import model.services.DocumentoService;
import model.services.ZonaService;


public class CertidaoListController implements Initializable, DataChangeListener {

    @FXML
    private TableColumn<Certidao, String> tableColumnColaborador;

    @FXML
    private TableColumn<Certidao, String> tableColumnZona;

    @FXML
    private Button btNew;

    @FXML
    private TableColumn<Certidao, String> tableColumnTipoDoc;

    @FXML
    private TableColumn<Certidao, Date> tableColumnDtEmissao;

    @FXML
    private TableColumn<Certidao, String> tableColumnRequerente;

    @FXML
    private TableColumn<Certidao, Certidao> tableColumnEDIT;

    @FXML
    private TableColumn<Certidao, Certidao> tableColumnREMOVE;

    @FXML
    private TableView<Certidao> tableViewCertidao;

    @FXML
    private TableColumn<Certidao, String> tableColumnCertidao;
    
    
    private CertidaoService service;
    
    private ObservableList<Certidao>obsList;
    
    @FXML
    void onBtNewAction(ActionEvent event) {
    	Stage parentStage = Utils.currentStage(event);
    	Certidao obj = new Certidao();
		createDialogForm(obj, "/gui/CertidaoForm.fxml", parentStage);
    }
    
    public void setCertidaoService(CertidaoService service) {
  		this.service = service;
  	}

  	@Override
  	public void initialize(URL url, ResourceBundle rb) {
  		initializeNodes();
  	}

  	private void initializeNodes() {

  		tableColumnCertidao.setCellValueFactory(new PropertyValueFactory<>("certidao"));
  		tableColumnColaborador.setCellValueFactory(new PropertyValueFactory<>("colaborador"));
  		tableColumnTipoDoc.setCellValueFactory(new PropertyValueFactory<>("tipoDoc"));
  		tableColumnZona.setCellValueFactory(new PropertyValueFactory<>("zona"));
  		tableColumnRequerente.setCellValueFactory(new PropertyValueFactory<>("requerente"));
  		tableColumnDtEmissao.setCellValueFactory(new PropertyValueFactory<>("dataEmissao"));
  		Utils.formatTableColumnDate(tableColumnDtEmissao,"dd/MM/yyyy");
  		
  		Stage stage = (Stage) Main.getMainScene().getWindow();
  		tableViewCertidao.prefHeightProperty().bind(stage.heightProperty());

  	}

  	public void updateTableView() {
  		if (service == null) {
  			throw new IllegalStateException("Serviço nulo");
  		}
  		List<Certidao> list = service.findAll();
  		obsList = FXCollections.observableArrayList(list);
  		tableViewCertidao.setItems(obsList);
  		initEditButtons();
  		initRemoveButtons();
  	}

  	private void createDialogForm(Certidao obj, String absoluteName, Stage parentStage) {
  		try {
  			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
  			Pane pane = loader.load();

  			CertidaoFormController controller = loader.getController();
  			
  			controller.setCertidao(obj);

  			controller.setCertidaoService(new CertidaoService(), new ZonaService(), new ColaboradorService(), new DocumentoService());
  			
  			controller.loadAssociatedObjects();
  
  			controller.subscribeDataChangeListener(this);

  			controller.updateFormData();

  			Stage dialogStage = new Stage();
  			dialogStage.setTitle("Entre com os dados da Certidão:");
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
  		tableColumnEDIT.setCellFactory(param -> new TableCell<Certidao, Certidao>() {
  			private final Button button = new Button("Editar");

  			@Override
  			protected void updateItem(Certidao obj, boolean empty) {
  				super.updateItem(obj, empty);
  				if (obj == null) {
  					setGraphic(null);
  					return;
  				}
  				setGraphic(button);
  				button.setOnAction(
  						event -> createDialogForm(obj, "/gui/CertidaoForm.fxml", Utils.currentStage(event)));
  			}
  		});
  	}

  	private void initRemoveButtons() {
  		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
  		tableColumnREMOVE.setCellFactory(param -> new TableCell<Certidao, Certidao>() {
  			private final Button button = new Button("Remover");

  			@Override
  			protected void updateItem(Certidao obj, boolean empty) {
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

  	private void removeEntity(Certidao obj) {
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
