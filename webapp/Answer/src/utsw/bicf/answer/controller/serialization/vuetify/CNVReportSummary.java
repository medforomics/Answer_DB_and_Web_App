package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.model.extmapping.CNVReport;

public class CNVReportSummary extends Summary<CNVReport> {
	
	public CNVReportSummary() {
		super();
	}
	
	public CNVReportSummary(List<CNVReport> cnvReport, String uniqueIdField) {
		super(cnvReport, uniqueIdField);
	}

	@Override
	public void initializeHeaders() {
		Header chrom = new Header("CHR", "chrom");
		chrom.setWidth("100px");
		headers.add(chrom);
		Header start = new Header("Start", "start");
		start.setWidth("100px");
		headers.add(start);
		Header end = new Header("End", "end");
		end.setWidth("100px");
		headers.add(end);
		Header copyNumber = new Header(new String[] {"Copy", "Number"}, "copyNumber");
		copyNumber.setWidth("100px");
		headers.add(copyNumber);
		Header gene = new Header("Genes", "genes");
		gene.setWidth("450px");
		gene.setAlign("left");
		gene.setCanHighlight(true);
		headers.add(gene);
		Header comment = new Header("Comment", "comment");
		headers.add(comment);
		//keep in the same order
		headerOrder = headers.stream().map(aHeader -> aHeader.getValue()).collect(Collectors.toList());
		
		
	}
	

}
