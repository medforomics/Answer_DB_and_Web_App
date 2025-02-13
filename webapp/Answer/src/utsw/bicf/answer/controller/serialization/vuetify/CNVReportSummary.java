package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;

import utsw.bicf.answer.model.extmapping.CNVReport;

public class CNVReportSummary extends Summary<CNVReport> {
	
	public CNVReportSummary() {
		super();
	}
	
	public CNVReportSummary(List<CNVReport> cnvReport, String uniqueIdField) {
		super(cnvReport, uniqueIdField, null);
		boolean needsAddendum = false;
		for (CNVReport c : cnvReport) {
			if (c.isContainsAddendum()) {
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
//		Header chrom = new Header("CHR", "chrom");
//		chrom.setWidth("100px");
//		headers.add(chrom);
//		Header start = new Header("Start", "start");
//		start.setWidth("100px");
//		headers.add(start);
//		Header end = new Header("End", "end");
//		end.setWidth("100px");
//		headers.add(end);
		Header combined = new Header("Loci", "loci");
		combined.setWidth("200px");
		combined.setIsSafe(true);
		headers.add(combined);
		Header copyNumber = new Header(new String[] {"Copy", "Number"}, "copyNumber");
		copyNumber.setWidth("100px");
		copyNumber.setIsSafe(true);
		headers.add(copyNumber);
		Header gene = new Header("Genes", "genes");
		gene.setWidth("450px");
		gene.setAlign("left");
		gene.setCanHighlight(true);
		gene.setIsSafe(true);
		headers.add(gene);
		Header cytoband = new Header("Cytoband", "cytoband");
		cytoband.setWidth("100px");
		cytoband.setAlign("left");
		cytoband.setIsSafe(true);
//		cytoband.setCanHighlight(true);
		headers.add(cytoband);
		Header comment = new Header("Comment", "comment");
		comment.setAlign("left");
		comment.setIsSafe(false);
		headers.add(comment);
		
		
	}
	

}
