package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Translocation;
import utsw.bicf.answer.model.hybrid.HeaderOrder;
import utsw.bicf.answer.model.hybrid.TranslocationRow;

public class TranslocationSummary extends Summary<TranslocationRow> {
	
	public TranslocationSummary(ModelDAO modelDAO, OrderCase aCase, String uniqueIdField, List<HeaderOrder> ftlOrders) {
		super(createRows(modelDAO, aCase), uniqueIdField, ftlOrders);
	}

	private static List<TranslocationRow> createRows(ModelDAO modelDAO, OrderCase aCase) {
		List<TranslocationRow> rows = new ArrayList<TranslocationRow>();
		for (Translocation translocation : aCase.getTranslocations()) {
			rows.add(new TranslocationRow(translocation));
			
		}
		return rows;
	}

	@Override
	public void initializeHeaders() {
		Header fusionName = new Header(new String[] {"Fusion", "Name"}, "fusionName");
		fusionName.setWidth("150px");
		fusionName.setIsSafe(true);
		headers.add(fusionName);
		
		Header iconFlags = new Header("Flags", "iconFlags");
		iconFlags.setWidth("100px");
		iconFlags.setIsFlag(true);
		iconFlags.setSortable(false);
		iconFlags.setIsSafe(true);
		headers.add(iconFlags);
		
		Header leftGene = new Header(new String[] {"Left", "Gene"}, "leftGene");
		leftGene.setWidth("100px");
		leftGene.setIsSafe(true);
		headers.add(leftGene);
		
		Header rightGene = new Header(new String[] {"Right", "Gene"}, "rightGene");
		rightGene.setWidth("100px");
		rightGene.setIsSafe(true);
		headers.add(rightGene);
		
		Header leftExon = new Header(new String[] {"Left", "Exons"}, "leftExons");
		leftExon.setWidth("100px");
		leftExon.setIsSafe(true);
		headers.add(leftExon);
		
		Header rightExon = new Header(new String[] {"Right", "Exons"}, "rightExons");
		rightExon.setWidth("100px");
		rightExon.setIsSafe(true);
		headers.add(rightExon);
		
		Header leftBreakpoint = new Header(new String[] {"Left", "Breakpoint"}, "leftBreakpoint");
		leftBreakpoint.setWidth("100px");
		leftBreakpoint.setIsSafe(true);
		headers.add(leftBreakpoint);
		
		Header rightBreakpoint = new Header(new String[] {"Right", "Breakpoint"}, "rightBreakpoint");
		rightBreakpoint.setWidth("100px");
		rightBreakpoint.setIsSafe(true);
		headers.add(rightBreakpoint);
		
		Header leftStrand = new Header(new String[] {"Left", "Strand"}, "leftStrand");
		leftStrand.setWidth("100px");
		leftStrand.setIsSafe(true);
		headers.add(leftStrand);
		
		Header rightStrand = new Header(new String[] {"Right", "Strand"}, "rightStrand");
		rightStrand.setWidth("100px");
		rightStrand.setIsSafe(true);
		headers.add(rightStrand);
		
		Header rnaReads = new Header(new String[] {"RNA", "Reads"}, "rnaReads");
		rnaReads.setWidth("100px");
		rnaReads.setIsSafe(true);
		headers.add(rnaReads);
		
		Header dnaReads = new Header(new String[] {"DNA", "Reads"}, "dnaReads");
		dnaReads.setWidth("100px");
		dnaReads.setIsSafe(true);
		headers.add(dnaReads);
		
	}
	
}
