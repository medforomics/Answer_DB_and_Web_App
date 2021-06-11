package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.model.extmapping.IndicatedTherapy;
import utsw.bicf.answer.model.hybrid.IndicatedTherapyRow;

public class IndicatedTherapySummary extends Summary<IndicatedTherapyRow> {

	public IndicatedTherapySummary() {
		super();
	}
	
	public IndicatedTherapySummary(List<IndicatedTherapy> indicatedTherapies, String uniqueIdField) {
		super(indicatedTherapies.stream().map(it -> new IndicatedTherapyRow(it)).collect(Collectors.toList()), uniqueIdField, null);
		boolean needsAddendum = false;
		for (IndicatedTherapy cs : indicatedTherapies) {
			if (cs.isAddendum()) {
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
			this.updateHeaderOrder();
			this.setHiddenStatus();
		}
	}

	@Override
	public void initializeHeaders() {
		Header drugs = new Header("Drugs", "drugs");
		drugs.setWidth("200px");
		drugs.setIsSafe(true);
		headers.add(drugs);
		Header variant = new Header("Variant", "variant");
		variant.setWidth("150px");
		variant.setIsSafe(true);
		headers.add(variant);
		Header level = new Header("Level", "level");
		level.setWidth("150px");
		level.setIsSafe(true);
		headers.add(level);
		Header indication = new Header("Indication", "indication");
		indication.setAlign("left");
		indication.setWidth("400px");
		indication.setIsSafe(false);
		headers.add(indication);
	}
	
	public List<IndicatedTherapy> getIndicatedTherapies() {
		return this.items.stream().map(it -> new IndicatedTherapy(it)).collect(Collectors.toList());
	}

}