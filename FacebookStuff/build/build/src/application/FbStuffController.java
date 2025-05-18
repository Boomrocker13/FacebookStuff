package application;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

public class FbStuffController {
	
	private List<File> files = new ArrayList<File>();
	
	private List<File> filestmp = new ArrayList<File>();
	
	HashMap<String, Integer> names;
	
	@FXML
	TextArea text;
	
	@FXML
	Button count;
	
	@FXML
	Button AddDay;
	
	@FXML
	Button OpenOld;
	
	@FXML
	Button OpenNew;
	
	@FXML
	Button deleter;
	
	@FXML
	private void initialize() {
		deleter.setVisible(false);
		deleter.setDisable(true);
		count.setDisable(true);
		count.setVisible(false);
		AddDay.setDisable(true);
		AddDay.setVisible(false);
		OpenNew.setVisible(false);
		OpenNew.setDisable(true);
		OpenOld.setVisible(false);
		OpenOld.setDisable(true);
	}
	
	@FXML
	private void Open(ActionEvent event) throws IOException, ParseException {
		List<File> files = new ArrayList<File>();
		FileChooser fc = new FileChooser();
		fc.setInitialDirectory(new File("D:\\facebook-alexatha13\\messages\\inbox"));
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Htm(l) files", "*.html","*.htm"));
		files = fc.showOpenMultipleDialog(null);
		if (files==null)
			return;
		else {
			deleter.setVisible(true);
			deleter.setDisable(false);
			OpenNew.setDisable(false);
			OpenNew.setVisible(true);
			for(File x:files) {
				if(!(new File(x.getCanonicalPath().replace(".html", "new.html")).exists())) {
					deleter.setVisible(false);
					deleter.setDisable(true);
					OpenNew.setDisable(true);
					OpenNew.setVisible(false);
				}
			}
			OpenOld.setVisible(true);
			OpenOld.setDisable(false);
			count.setVisible(true);
			count.setDisable(false);
			AddDay.setVisible(true);
			AddDay.setDisable(false);
			this.files=files;
		}
	}
	
	@FXML
	private void OpenOld(ActionEvent event) throws IOException {
		for(File x:files)
			Desktop.getDesktop().open(x);
	}
	
	@FXML
	private void OpenNew(ActionEvent event) throws IOException {
		for(File x:files) {
			try {
				x = new File(x.getCanonicalPath().replace(".html", "new.html"));
				Desktop.getDesktop().open(x);
			}catch(IllegalArgumentException e) {
				text.appendText("File not created...\n");
				return;
			}
			
		}
	}
	
	@FXML
	private void DeleteNew(ActionEvent event) throws IOException {
		deleter.setVisible(false);
		deleter.setDisable(true);
		OpenNew.setVisible(false);
		OpenNew.setDisable(true);
		for(File x:files) {
			x = new File(x.getCanonicalPath().replace(".html", "new.html"));
			x.delete();
		}
	}
	
	@FXML
	private void AddDay(ActionEvent event) throws IOException, ParseException {
		deleter.setVisible(true);
		deleter.setDisable(false);
		OpenNew.setVisible(true);
		OpenNew.setDisable(false);
		for(File x: files) {
			FileInputStream fis = new FileInputStream(x);
		    InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
		    BufferedReader reader = new BufferedReader(isr);
			String line = reader.readLine();
			String[] splitter = line.split("<div class=\"_3-94 _2lem\">");
			String path = x.getCanonicalPath();
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path.replace(".html", "new.html")),StandardCharsets.UTF_8);
			for(int i=0;i<splitter.length;i++) {
				if(i==0) {
					writer.write(splitter[i]);
					continue;
				}
				//_3-94 _2lem
				String[] splitted = splitter[i].split("</div>");
				splitted = splitter[i].split("</div>")[0].split(",");
				String currentDate = splitted[0].split(" ")[1]+"/"+splitted[0].split(" ")[0]+"/"+splitted[1].trim();
				SimpleDateFormat simpleDateformat = new SimpleDateFormat("E"); // the day of the week abbreviated
				Date sdf = new SimpleDateFormat("d/MMM/yyyy").parse(currentDate);
				currentDate = splitted[0]+", "+splitted[1].trim()+",";
				String RetValue = "<div class=\"_3-94 _2lem\">"+simpleDateformat.format(sdf) +" "+currentDate;
				splitted = splitter[i].split(",");
				for(int j=2;j<splitted.length;j++) {
					RetValue += splitted[j];
				}
				writer.write(RetValue);
			}
			reader.close();
			writer.close();
			text.appendText("Done\n");
		}
	}
	
	@FXML
	private void Count(ActionEvent event) throws IOException {
		names = new HashMap<String, Integer>();
		if(filestmp == files) {
			text.appendText("You already did that...Change the file\n");
			return;
		}
		
		else if(files==null) {
			text.appendText("Select a file...\n");
			return;
		}
		
		else
			filestmp = files;
		for(File x: files) {
			FileInputStream fis = new FileInputStream(x);
		    InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
		    BufferedReader reader = new BufferedReader(isr);
			String line = reader.readLine();
			String[] splitter = line.split("<div class=\"_3-96 _2pio _2lek _2lel\">");
			for(int i=1;i<splitter.length;i++) {
				String[] current = splitter[i].split("</div>");
				if(!names.containsKey(current[0].trim()))
					names.put(current[0].trim(), 1);
				else
					names.put(current[0].trim(), names.get(current[0].trim())+1);
				/*for(int j=0;j<current.length;j++)
					System.out.println("current["+j+"] "+current[j]);*/
			
			}
			
			reader.close();
		}
		
		text.appendText("=========Conversation Start=========\n");
		
		for(String x: names.keySet()) {
			text.appendText(x+" \t"+":\t"+names.get(x)+"\n");
		}
		
		if(names.size()==2) {
			int diff = 0;
			
			for(String x: names.keySet()) {
				diff = Math.abs(names.get(x)-diff);
			}
			if(diff!=0) {
				text.appendText("Difference is "+diff+" message");
				if(diff!=1)
					text.appendText("s");
				text.appendText(".\n");
			}
			else
				text.appendText("There is no difference in messages.\n");
		}
	}
}
