package utsw.bicf.answer.model.extmapping;

import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import utsw.bicf.answer.model.hybrid.IndicatedTherapyRow;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IndicatedTherapy {

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
	
	public IndicatedTherapy() {
	}
	
	public IndicatedTherapy(Annotation a, Variant v) {
		this.variant = a.getGeneId() + " " + v.getNotation();
		if (a.getTier() != null) {
			switch(a.getTier()) {
			case "1A": this.level = "FDA-Approved"; break;
			case "1B": this.level = "Strong Evidence"; break;
			case "2C": this.level = "Weak Evidence"; break;
			}
			this.tier = a.getTier();
		}
		this.indication = a.getText();
		this.type = v.getType();
		this.oid = v.getMongoDBId().getOid();
		this.drugs = a.getDrugs();
		this.drugResistant = a.isDrugResistant();
		if (this.drugResistant) {
			this.level = "Resistant";
		}
		this.biomarkers = v.getGeneName();
	}
	public IndicatedTherapy(Annotation a, CNV v) {
		this.variant = a.getCnvGenes().stream().collect(Collectors.joining(" "));
		if (v.getAberrationType().equals("ITD")) {
			this.variant += "-ITD";
		}
		this.biomarkers = this.variant;
		if (a.getTier() != null) {
			switch(a.getTier()) {
			case "1A": this.level = "FDA-Approved"; break;
			case "1B": this.level = "Strong Evidence"; break;
			case "2C": this.level = "Weak Evidence"; break;
			}
			this.tier = a.getTier();
		}
		this.indication = a.getText();
		this.type = v.getType();
		this.oid = v.getMongoDBId().getOid();
		this.drugs = a.getDrugs();
		this.drugResistant = a.isDrugResistant();
		if (this.drugResistant) {
			this.level = "Resistant";
		}
	}
	public IndicatedTherapy(Annotation a, Translocation v) {
		this.variant = a.getLeftGene() + "::" + a.getRightGene();
		this.biomarkers = this.variant;
		if (a.getTier() != null) {
			switch(a.getTier()) {
			case "1A": this.level = "FDA-Approved"; break;
			case "1B": this.level = "Strong Evidence"; break;
			case "2C": this.level = "Weak Evidence"; break;
			}
			this.tier = a.getTier();
		}
		this.indication = a.getText();
		this.type = v.getType();
		this.oid = v.getMongoDBId().getOid();
		this.drugs = a.getDrugs();
		this.drugResistant = a.isDrugResistant();
		if (this.drugResistant) {
			this.level = "Resistant";
		}
	}
//	public IndicatedTherapy(AnnotationCategory cat, Variant v) {
//		this.variant = v.getGeneName() + " " + v.getNotation();
//		this.indication = cat.getText();
//	}
//	public IndicatedTherapy(AnnotationCategory cat, Translocation v) {
//		this.variant = v.getLeftGene() + "-" + v.getRightGene();
//		this.indication = cat.getText();
//	}


	public IndicatedTherapy(IndicatedTherapyRow indicatedTherapy) {
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
	
	
	
}
