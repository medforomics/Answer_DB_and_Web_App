package utsw.bicf.answer.model.hybrid;

import java.util.ArrayList;
import java.util.List;

import utsw.bicf.answer.clarity.api.utils.TypeUtils;
import utsw.bicf.answer.controller.serialization.FlagValue;
import utsw.bicf.answer.controller.serialization.VuetifyIcon;

public class ClinicalSignificance {
	
	String geneVariant;
	String geneVariantAsKey;
	String category;
	String annotation;
	boolean readonly;
	
	//fields only used when adding a new row
	String position;
	String enst;
	String vaf;
	String depth;
	String copyNumber;
	String aberrationType;
	String csType;
	boolean additionalRow;
	String addendumAnnotation;
	
	FlagValue iconFlags;
	
	
	
	public ClinicalSignificance() {
	}
	public ClinicalSignificance(String geneVariant, String category, String annotation, boolean readonly, String addendumAnnotation) {
		super();
		this.geneVariant = geneVariant;
		this.geneVariantAsKey = geneVariant.replaceAll("\\.", "");
		this.category = category;
		this.annotation = annotation;
		this.readonly = readonly;
		this.addendumAnnotation = addendumAnnotation;
		if (TypeUtils.notNullNotEmpty(this.addendumAnnotation)) {
			List<VuetifyIcon> icons = new ArrayList<VuetifyIcon>();
			icons.add(new VuetifyIcon("mdi-alert-decagram", "warning", "This annotation contains an addendum"));
			iconFlags = new FlagValue(icons);
		}
	}
	public String getGeneVariant() {
		return geneVariant;
	}
	public void setGeneVariant(String geneVariant) {
		this.geneVariant = geneVariant;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getAnnotation() {
		return annotation;
	}
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}
	public boolean isReadonly() {
		return readonly;
	}
	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}
	public String getGeneVariantAsKey() {
		return geneVariantAsKey;
	}
	public void setGeneVariantAsKey(String geneVariantAsKey) {
		this.geneVariantAsKey = geneVariantAsKey;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getEnst() {
		return enst;
	}
	public void setEnst(String enst) {
		this.enst = enst;
	}
	public String getVaf() {
		return vaf;
	}
	public void setVaf(String vaf) {
		this.vaf = vaf;
	}
	public String getDepth() {
		return depth;
	}
	public void setDepth(String depth) {
		this.depth = depth;
	}
	public boolean isAdditionalRow() {
		return additionalRow;
	}
	public void setAdditionalRow(boolean additionalRow) {
		this.additionalRow = additionalRow;
	}
	public String getCopyNumber() {
		return copyNumber;
	}
	public void setCopyNumber(String copyNumber) {
		this.copyNumber = copyNumber;
	}
	public String getAberrationType() {
		return aberrationType;
	}
	public void setAberrationType(String aberrationType) {
		this.aberrationType = aberrationType;
	}
	public String getCsType() {
		return csType;
	}
	public void setCsType(String csType) {
		this.csType = csType;
	}
	public String getAddendumAnnotation() {
		return addendumAnnotation;
	}
	public void setAddendumAnnotation(String addendumAnnotation) {
		this.addendumAnnotation = addendumAnnotation;
	}
	public FlagValue getIconFlags() {
		return iconFlags;
	}
	public void setIconFlags(FlagValue iconFlags) {
		this.iconFlags = iconFlags;
	}

}
