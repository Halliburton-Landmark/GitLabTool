package com.lgc.solutiontool.git.ui.javafx.controllers;

import java.util.List;

import com.lgc.solutiontool.git.services.ServiceProvider;
import com.lgc.solutiontool.git.services.StorageService;
import com.lgc.solutiontool.git.xml.Server;
import com.lgc.solutiontool.git.xml.Servers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ServerSelectionWindowController {
	
	private StorageService storageService =
            (StorageService) ServiceProvider.getInstance().getService(StorageService.class.getName());
	
	@FXML
	private Label server;
	
	@FXML
	private TextField serverTextField;
	
	@FXML
	private Button okButton;
	
	public ServerSelectionWindowController() {
	}

	@FXML
	private void initialize() {
	}
	
	@FXML
	public void onOkButton() throws Exception {
		updateServers();
		Stage stage = (Stage) okButton.getScene().getWindow();
		stage.close();
	}
	
	private void updateServers() {
		String inputServerName = serverTextField.getText();
		List<Server> servers = storageService.loadServers().getServers();
		int index = servers.size() - 1;
		if (inputServerName != null && !inputServerName.equals("") && !servers.contains(inputServerName)) {
			servers.add(index, new Server(inputServerName));
		}
		storageService.updateServers(new Servers(servers));
	}
}
