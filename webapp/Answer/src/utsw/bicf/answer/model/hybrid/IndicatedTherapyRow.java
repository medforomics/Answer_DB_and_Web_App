package utsw.bicf.answer.model.hybrid;

import java.util.ArrayList;
import java.util.List;

import utsw.bicf.answer.controller.serialization.FlagValue;
import utsw.bicf.answer.controller.serialization.VuetifyIcon;
import utsw.bicf.answer.model.extmapping.IndicatedTherapy;

public class IndicatedTherapyRow {

	String variant;
	String level;
	String indication;
	String tier;
	String type; //used to keep track of where the variant came from
	String oid; //used to keep counts of variants used
	boolean readonly;
	String drugs;
	boolean drugResistant;
	String biomarkers;
	boolean isAddendum;
	FlagValue iconFlags;
	
	public IndicatedTherapyRow() {
		super();
	}
	
	public IndicatedTherapyRow(IndicatedTherapy indicatedTherapy) {
		this.variant = indicatedTherapy.getVariant();
		this.level = indicatedTherapy.getLevel();
		this.indication = indicatedTherapy.getIndication();
		this.tier = indicatedTherapy.getTier();
		this.type = indicatedTherapy.getType();
		this.oid = indicatedTherapy.getOid();
		this.readonly = indicatedTherapy.isReadonly();
		this.drugs = indicatedTherapy.getDrugs();
		this.drugResistant = indicatedTherapy.isDrugResistant();
		this.biomarkers = indicatedTherapy.getBiomarkers();
		this.isAddendum = indicatedTherapy.isAddendum();
		
		if (this.isAddendum) {
			List<VuetifyIcon> icons = new ArrayList<VuetifyIcon>();
			icons.add(new VuetifyIcon("mdi-alert-decagram", "warning", "This is an addendum therapy"));
			iconFlags = new FlagValue(icons);
		}
	}
	
		public String getVariant() {
		return variant;
	}
	public void setVariant(String variant) {
		this.variant = variant;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getIndication() {
		return indication;
	}
	public void setIndication(String indication) {
		this.indication = indication;
	}

	public String getTier() {
		return tier;
	}

	public void setTier(String tier) {
		this.tier = tier;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public String getDrugs() {
		return drugs;
	}

	public void setDrugs(String drugs) {
		this.drugs = drugs;
	}

	public boolean isDrugResistant() {
		return drugResistant;
	}

	public void setDrugResistant(boolean drugResistant) {
		this.drugResistant = drugResistant;
	}

	public String getBiomarkers() {
		return biomarkers;
	}

	public void setBiomarkers(String biomarkers) {
		this.biomarkers = biomarkers;
	}

	public boolean isAddendum() {
		return isAddendum;
	}

	public void setAddendum(boolean isAddendum) {
		this.isAddendum = isAddendum;
	}

	public FlagValue getIconFlags() {
		return iconFlags;
	}

	public void setIconFlags(FlagValue iconFlags) {
		this.iconFlags = iconFlags;
	}
	
	
}
