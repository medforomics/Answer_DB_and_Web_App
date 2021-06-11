package utsw.bicf.answer.model.hybrid;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import utsw.bicf.answer.controller.ControllerUtil;
import utsw.bicf.answer.controller.serialization.Button;
import utsw.bicf.answer.model.ClinicalTest;
import utsw.bicf.answer.model.User;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClinicalTestTableRow {
	
	Integer testId;
	String name;
	String url;
	List<String> lines;
	String textConcat;
	
	List<Button> buttons = new ArrayList<Button>();
	
	
	
	public ClinicalTestTableRow() {
	}

	public ClinicalTestTableRow(ClinicalTest test, User currentUser) {
		this.testId = test.getClinicalTestId();
		this.name = test.getTestName();
		this.lines = test.getDisclaimerTexts() != null ? test.getDisclaimerTexts().stream().map(u -> u.getText()).collect(Collectors.toList()) : new ArrayList<String>();
		this.url = test.getTestLink();
		
		if (ControllerUtil.isOwnerOrAdmin(currentUser,currentUser)) {
			buttons.add(new Button("create", "editClinicalTest", "Edit Clinical Test", "info"));
		}
	}

	public Integer getTestId() {
		return testId;
	}

	public void setTestId(Integer testId) {
		this.testId = testId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<String> getLines() {
		return lines;
	}

	public void setLines(List<String> lines) {
		this.lines = lines;
	}

	public List<Button> getButtons() {
		return buttons;
	}

	public void setButtons(List<Button> buttons) {
		this.buttons = buttons;
	}

	public String getTextConcat() {
		return textConcat;
	}

	public void setTextConcat(String textConcat) {
		this.textConcat = textConcat;
	}


}
