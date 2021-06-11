package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;

import utsw.bicf.answer.model.extmapping.TranslocationReport;

public class TranslocationReportSummary extends Summary<TranslocationReport> {
	
	public TranslocationReportSummary() {
		super();
	}
	
	public TranslocationReportSummary(List<TranslocationReport> translocationReports, String uniqueIdField) {
		super(translocationReports, uniqueIdField, null);
		boolean needsAddendum = false;
		for (TranslocationReport t : translocationReports) {
			if (t.isContainsAddendum()) {
				needsAddendum = true;
				break;
			}
		}
		if (needsAddendum) {
			Header iconFlags = new Header("New", "iconFlags");
			iconFlags.setIsFlag(true);
			iconFlags.setSortable(false);
			iconFlags.setAlign("center");
			iconFlags.setIsSafe(true);
			iconFlags.setWidthValue(15);
			iconFlags.setWidth("15px");
			headers.add(0, iconFlags);
			
			Header annotation = new Header("Addendum", "addedumComment");
			annotation.setAlign("left");
			annotation.setWidth("200px");
			annotation.setIsSafe(false);
			headers.add(annotation);
			this.updateHeaderOrder();
			this.setHiddenStatus();
		}
	}

	@Override
	public void initializeHeaders() {
		Header fusionName = new Header(new String[] {"Fusion", "Name"}, "fusionName");
		fusionName.setWidth("150px");
		fusionName.setIsSafe(false);
		headers.add(fusionName);
		
		Header leftGene = new Header("Gene1", "leftGene");
		leftGene.setWidth("100px");
		leftGene.setIsSafe(false);
		headers.add(leftGene);
		
		Header lastExon = new Header(new String[] {"Last", "Exon"}, "lastExon");
		lastExon.setWidth("75px");
		lastExon.setIsSafe(false);
		headers.add(lastExon);
		
		Header rightGene = new Header("Gene2", "rightGene");
		rightGene.setWidth("100px");
		rightGene.setIsSafe(false);
		headers.add(rightGene);
		
		Header firstExon = new Header(new String[] {"First", "Exon"}, "firstExon");
		firstExon.setWidth("75px");
		firstExon.setIsSafe(false);
		headers.add(firstExon);
		
		Header comment = new Header("Comment", "comment");
		comment.setAlign("left");
		comment.setIsSafe(false);
		headers.add(comment);		
		
	}
	
}
