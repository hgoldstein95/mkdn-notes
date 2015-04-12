package app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import util.Parser;
import util.Sorter;

public class App extends Application {

	public File currentFile = null;
	public ViewState state = ViewState.MAIN;
	public int pageScroll;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		
		Parser p = new Parser();
		Sorter s = new Sorter();
		
		BorderPane main = new BorderPane();
		
		MenuBar menu = new MenuBar();
		Menu fileMenu = new Menu("File");
		MenuItem newItem = new MenuItem("New");
		MenuItem saveItem = new MenuItem("Save");
		MenuItem saveAsItem = new MenuItem("Save As");
		MenuItem openItem = new MenuItem("Open");
		MenuItem exportHTMLItem = new MenuItem("Export HTML");
		fileMenu.getItems().addAll(newItem, saveItem, saveAsItem, openItem, exportHTMLItem);
		Menu editMenu = new Menu("Edit");
		Menu aboutMenu = new Menu("About");
		menu.getMenus().addAll(fileMenu, editMenu, aboutMenu);
		
		HBox center = new HBox();
		VBox inputBox = new VBox();
		TextArea input = new TextArea();
		// input.setParagraphGraphicFactory(LineNumberFactory.get(input));
		input.setMinSize(700, 700);
		input.setWrapText(true);
		input.setStyle("-fx-font-size: 12pt;"
					+ "-fx-font-family: 'Lucida Console';");
		HBox controlBox = new HBox();
		Button eqnButton = new ControlButton("$");
		controlBox.getChildren().addAll(eqnButton);
		inputBox.getChildren().addAll(controlBox, input);
		VBox browserBox = new VBox();
		WebView browser = new WebView();
		browser.setMinHeight(700);
		WebEngine webEngine = browser.getEngine();
		webEngine.javaScriptEnabledProperty().set(true);
		HBox buttonBox = new HBox();
		Button mainButton = new ViewButton("Main");
		Button outlineButton = new ViewButton("Outline");
		Button codeButton = new ViewButton("Code");
		Button mathButton = new ViewButton("Equations");
		Button definitionButton = new ViewButton("Definitons");
		Button quoteButton = new ViewButton("Quotes");
		Button thmButton = new ViewButton("Theorems");
		buttonBox.getChildren().addAll(
				mainButton, 
				outlineButton, 
				codeButton,
				mathButton, 
				definitionButton, 
				quoteButton,
				thmButton
				);
		buttonBox.setSpacing(5);
		
		browserBox.getChildren().addAll(buttonBox, browser);
		center.getChildren().addAll(inputBox, browserBox);
		
		Label saveLabel = new Label();
		
		main.setCenter(center);
		main.setTop(menu);
		main.setBottom(saveLabel);
		
		// Action Handlers and Event Listeners
		input.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0,
					String arg1, String arg2) {
				if(state == ViewState.MAIN) {
					webEngine.loadContent(p.parse(arg2));
				}
			}
		});
		
		input.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {
				if(arg2.booleanValue()) {
					state = ViewState.MAIN;
					webEngine.loadContent(p.parse(input.getText()));
					saveLabel.setText("Unsaved");
				}
			}
		});
		
		webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() { 
			
			public void changed(ObservableValue<? extends State> o, State old, final State state) { 
				if(state == State.SUCCEEDED) { 
					webEngine.executeScript("window.scrollTo(0, " + pageScroll + ");");
				} 
				if(state == State.SCHEDULED) {
					pageScroll = Integer.parseInt(webEngine.executeScript("window.pageYOffset").toString());
				}
			} 
		});
		
		newItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if(currentFile != null) {
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Save Changes?");
					alert.setHeaderText(null);
					alert.setContentText("Create a new document and save changes?");
					
					ButtonType buttonTypeYes = new ButtonType("Yes", ButtonData.CANCEL_CLOSE);
					ButtonType buttonTypeNo = new ButtonType("No, Don't Save", ButtonData.CANCEL_CLOSE);
					ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
	
					alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);
	
					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == buttonTypeYes){
					    Platform.runLater(new Runnable() {
	
							@Override
							public void run() {
								saveItem.fire();
								input.clear();
								currentFile = null;
							}
					    });
					}
					else if(result.get() == buttonTypeNo) {
						Platform.runLater(new Runnable() {
	
							@Override
							public void run() {
								input.clear();
								currentFile = null;
							}
					    });
					}
				}
				else {
					saveAsItem.fire();
				}
			}
		});
		newItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
		
		openItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				
				if(currentFile != null) {
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Save Changes?");
					alert.setHeaderText(null);
					alert.setContentText("Open a new document and save changes?");
					
					ButtonType buttonTypeYes = new ButtonType("Yes", ButtonData.CANCEL_CLOSE);
					ButtonType buttonTypeNo = new ButtonType("No, Don't Save", ButtonData.CANCEL_CLOSE);
					ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
	
					alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);
	
					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == buttonTypeYes){
					    Platform.runLater(new Runnable() {
	
							@Override
							public void run() {
								saveItem.fire();
							}
					    });
					}
					else if(result.get() == buttonTypeCancel) {
						return;
					}
				}
				
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Save As");
				fileChooser.setInitialDirectory(new File("C:\\Users\\Harry\\Google Drive\\Interesting Projects\\JavaProjects\\Annote"));
				File file = fileChooser.showOpenDialog(stage);
				
				if(file == null) return;
				
				currentFile = file;
				
				state = ViewState.MAIN;
				
				try {
					BufferedReader br = new BufferedReader(new FileReader(currentFile));
					StringBuilder sb = new StringBuilder();
					while(br.ready()) {
						sb.append(br.readLine() + "\n");
					}
					br.close();
					input.clear();
					input.appendText(sb.toString());
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		openItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
		
		saveAsItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Save As");
				File file = fileChooser.showSaveDialog(stage);
				
				if(file == null) return;
				
				currentFile = file;
				
				saveItem.fire();
			}
		});
		
		saveItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if (currentFile == null) {
					saveAsItem.fire();
					arg0.consume();
					return;
				}
				try {
					BufferedWriter bw = new BufferedWriter(new FileWriter(currentFile));
					bw.write(input.getText());
					bw.close();
					
					saveLabel.setText("Saved");
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
		
		exportHTMLItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				try {
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("Save HTML");
					File file = fileChooser.showSaveDialog(stage);
					
					if(file == null) return;
					
					BufferedWriter bw = new BufferedWriter(new FileWriter(file));
					bw.write(p.parse(input.getText()));
					bw.close();
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		mainButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				state = ViewState.MAIN;
				webEngine.loadContent(p.parse(input.getText()));
			}
		});
		
		outlineButton.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0) {
				state = ViewState.OUTLINE;
				String html = p.parse(input.getText());
				webEngine.loadContent(p.parse(s.makeOutline(html)));
			}
		});
		
		codeButton.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0) {
				state = ViewState.CODE;
				String html = p.parse(input.getText());
				webEngine.loadContent(p.parse(s.sortCode(html)));
			}
		});
		
		mathButton.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0) {
				state = ViewState.MATH;
				String html = p.parse(input.getText());
				webEngine.loadContent(p.parse(s.sortMath(html)));
			}
		});
		
		definitionButton.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0) {
				state = ViewState.DEFINITION;
				String html = p.parse(input.getText());
				webEngine.loadContent(p.parse(s.sortDefinition(html)));
			}
		});
		
		quoteButton.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0) {
				state = ViewState.QUOTE;
				String html = p.parse(input.getText());
				webEngine.loadContent(p.parse(s.sortQuote(html)));
			}
		});
		
		thmButton.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0) {
				state = ViewState.THEOREM;
				String html = p.parse(input.getText());
				webEngine.loadContent(p.parse(s.sortTheorem(html)));
			}
		});
		
		eqnButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				if(input.getSelection().getLength() == 0) {
					input.insertText(input.getCaretPosition(), "$  $");
					input.positionCaret(input.getCaretPosition() - 2);
				}
				else {
					IndexRange ir = input.getSelection();
					input.insertText(ir.getEnd(), " $");
					input.insertText(ir.getStart(), "$ ");
				}
				input.requestFocus();
			}
		});
		
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent arg0) {
				if(currentFile != null && !saveLabel.getText().equals("Saved")) {
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("Save and Quit?");
					alert.setHeaderText(null);
					alert.setContentText("Save before quitting?");
					
					ButtonType buttonTypeYes = new ButtonType("Yes", ButtonData.CANCEL_CLOSE);
					ButtonType buttonTypeNo = new ButtonType("No", ButtonData.CANCEL_CLOSE);

					alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == buttonTypeYes){
					    Platform.runLater(new Runnable() {

							@Override
							public void run() {
								saveItem.fire();
							}
					    });
					}
				}
			}
		});
		
		Scene scene = new Scene(main);
		stage.setScene(scene);
		stage.show();
		
		input.requestFocus();
	}
}
