package com.lgc.solutiontool.git.ui.javafx.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import com.lgc.solutiontool.git.services.ServiceProvider;
import com.lgc.solutiontool.git.services.StorageService;
import com.lgc.solutiontool.git.util.RequestType;
import com.lgc.solutiontool.git.util.URLManager;
import com.lgc.solutiontool.git.xml.Server;
import com.lgc.solutiontool.git.xml.Servers;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ServerInputWindowController {
	
	private final String WRONG_INPUT_MESSAGE = "Wrong input! Please, try again";
	private final String WRONG_SERVER_ADDRESS_MESSAGE = "Wrong URL! Please, try another one";
	private final String SERVER_ALREADY_EXIST_MESSAGE = "Such server already exist!";
	
	private StorageService storageService =
            (StorageService) ServiceProvider.getInstance().getService(StorageService.class.getName());
	
	@FXML
	private Label server;
	
	@FXML
	private TextField serverTextField;
	
	@FXML
	private Button okButton;
	
	@FXML
	private Label message;
	
	@FXML
	private ComboBox<String> api;
	
	public ServerInputWindowController() {
	}

	@FXML
	private void initialize() {
		message.setVisible(false);
		
		api.getItems().addAll("v3");
		api.setValue(api.getItems().get(0));
	}
	
	public void loadServerInputWindow(Parent root) throws IOException {
		Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.setTitle("Server selection");
		stage.showAndWait();
	}
	
	@FXML
	public void onOkButton() throws Exception {
		message.setVisible(false);
		
		if (isInputValid() && !isServerAlreadyExists() && isValidResponseCode()) {
			updateServersList();
			Stage stage = (Stage) okButton.getScene().getWindow();
			stage.close();
		}
	}
	
	private void updateServersList() {
		String inputServerName = URLManager.trimServerURL(serverTextField.getText());
		List<Server> servers = storageService.loadServers().getServers();
		int index = servers.size() - 1;
		if (inputServerName != null && !inputServerName.equals("")) {
			servers.add(index, new Server(inputServerName, api.getValue()));
		}
		storageService.updateServers(new Servers(servers));
	}
	
	private boolean isInputValid() {
		String url = serverTextField.getText();
		if (URLManager.isURLValid(url)) {
			return true;
		} else {
			message.setText(WRONG_INPUT_MESSAGE);
			message.setVisible(true);
			return false;
		}
	}
	
	private boolean isValidResponseCode() {
		String url = URLManager.trimServerURL(serverTextField.getText());
		int responseCode = getServerResponseCode(url);
		if (responseCode > 0 && responseCode != 503) {
			return true;
		} else {
			message.setText(WRONG_SERVER_ADDRESS_MESSAGE);
			message.setVisible(true);
			return false;
		}
	}
	
	private boolean isServerAlreadyExists() {
		String url = URLManager.trimServerURL(serverTextField.getText());
		List<Server> servers = storageService.loadServers().getServers();
		if (servers.contains(new Server(url, api.getValue()))) {
			message.setText(SERVER_ALREADY_EXIST_MESSAGE);
			message.setVisible(true);
			return true;
		} else {
			return false;
		}
	}
	
	private int getServerResponseCode(String url) {
		int responseCode = -1;
		try {
			//handshake
			URL obj = new URL(URLManager.completeServerURL(url));
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			con.setRequestMethod(RequestType.GET.toString());
			responseCode = con.getResponseCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(responseCode);
		return responseCode;
	}
	
	public void setAPIVersion() {
		//TODO: manage API version from ComboBox
	}
}
