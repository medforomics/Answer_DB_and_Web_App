package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.ArrayList;
import java.util.List;

import utsw.bicf.answer.model.ClinicalTest;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.hybrid.ClinicalTestTableRow;
import utsw.bicf.answer.model.hybrid.HeaderOrder;

public class ClinicalTestTableSummary extends Summary<ClinicalTestTableRow>{

	public ClinicalTestTableSummary(List<ClinicalTest> tests, List<HeaderOrder> headerOrders, User user) {
		super(createGroupTableRows(tests, user), "name", headerOrders);
	}
	
	
	private static List<ClinicalTestTableRow> createGroupTableRows(List<ClinicalTest> tests, User user) {
		List<ClinicalTestTableRow> rows = new ArrayList<ClinicalTestTableRow>();
		for (ClinicalTest t : tests) {
			rows.add(new ClinicalTestTableRow(t, user));
		}
		return rows;
	}


	@Override
	public void initializeHeaders() {
		headers.add(new Header("Test Name", "name"));
		Header url = new Header("URL", "url");
		headers.add(url);
		Header text = new Header("Text", "lines");
		text.setIsList(true);
		text.setAlign("left");
		headers.add(text);
		Header actions = new Header(new String[] {"Edit", "Clinical Test"}, "actions");
		actions.setButtons(true);
		actions.setAlign("left");
		actions.setIsSafe(true);
		headers.add(actions);
		
	}
}
