package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;

import utsw.bicf.answer.model.hybrid.ClinicalSignificance;
import utsw.bicf.answer.model.hybrid.HeaderOrder;

public class ClinicalSignificanceSummary extends Summary<ClinicalSignificance> {

	public ClinicalSignificanceSummary() {
		super();
	}
	
	public ClinicalSignificanceSummary(List<ClinicalSignificance> clinicalSignificanceList, String uniqueIdField, List<HeaderOrder> headerOrders) {
		super(clinicalSignificanceList, uniqueIdField, headerOrders);
		boolean needsAddendum = false;
		for (ClinicalSignificance cs : clinicalSignificanceList) {
			if (cs.getAddendumAnnotation() != null) {
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
			
			Header annotation = new Header("Addendum", "addendumAnnotation");
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
		Header variant = new Header("Variant", "geneVariant");
		variant.setWidth("200px");
		variant.setAlign("left");
		variant.setIsSafe(true);
		headers.add(variant);
		Header category = new Header("Category", "category");
		category.setWidth("150px");
		category.setAlign("left");
		category.setIsSafe(true);
		headers.add(category);
		Header annotation = new Header("Annotation", "annotation");
		annotation.setAlign("left");
		annotation.setIsSafe(false);
		headers.add(annotation);

	}

}