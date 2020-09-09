package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.CertidaoService;
import model.services.ColaboradorService;
import model.services.DocumentoService;
import model.services.ZonaService;


public class MainViewController implements Initializable {

	 @FXML
	    private MenuItem menuItemColaborador;

	    @FXML
	    private MenuItem menuItemZonasEleitorais;

	    @FXML
	    private MenuItem menuItemTipoDocumento;

	    @FXML
	    private MenuItem menuItemCertidao;

	    @FXML
	    void onMenuItemCertidaoAction() {
	    	loadView("/gui/CertidaoList.fxml", (CertidaoListController controller) -> {
				controller.setCertidaoService(new CertidaoService());
				controller.updateTableView();
	    		});
	    }

	    @FXML
	    void onMenuItemColaboradorAction() {
	    	
	    	loadView("/gui/ColaboradorList.fxml", (ColaboradorListController controller) -> {
				controller.setColaboradorService(new ColaboradorService());
				controller.updateTableView();
	    		});


	    }

	    @FXML
	    void onMenuItemTipoDocumentoAction() {
	    	
	    	loadView("/gui/DocumentoList.fxml", (DocumentoListController controller) -> {
				controller.setDocumentoService(new DocumentoService());
				controller.updateTableView();
			});

	    }

	    @FXML
	    void onMenuItemZonasEleitoraisAction() {
	    	
	    	loadView("/gui/ZonaList.fxml", (ZonaListController controller) -> {
				controller.setZonaService(new ZonaService());
				controller.updateTableView();
			});

	    }
	
    
    @Override
	public void initialize(URL uri, ResourceBundle rb) {
		
	}
	
	private synchronized <T> void loadView (String absoluteName, Consumer<T> initializingAction ) {
		try {
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox = loader.load();
			
			Scene mainScene = Main.getMainScene();
			
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
		 
			Node mainMenu = mainVBox.getChildren().get(0);
		
			mainVBox.getChildren().clear();
			
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			T controller = loader.getController();
			initializingAction.accept(controller);
						
		}
		catch (IOException e) {
			Alerts.showAlert("Exceção de E/S", "Erro ao carregar tela", e.getMessage(), AlertType.ERROR);
		}
		
	}

}
