package utsw.bicf.answer.controller.serialization;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import utsw.bicf.answer.model.extmapping.CNV;
import utsw.bicf.answer.model.extmapping.Variant;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneVariantAndAnnotation {
	
	String geneVariant;
//	String annotation;
	String gene;
	String variant;
	String oid;
	boolean readonly;
	Map<String, String> annotationsByCategory;
	String position;
	String transcript;
	String taf;
	String tDepth;
	String copyNumber;
	String aberrationType;
	String type;
	String ref;
	String alt;
	String clinvarId;
	Map<String, String> annotationsAddendedByCategory = new HashMap<String, String>();
	boolean containsAddendum;
	
	public GeneVariantAndAnnotation() {
	}
	
	public GeneVariantAndAnnotation(Variant v, Map<String, String> annotationsByCategory) {
		this.geneVariant = v.getGeneName() + " " + v.getNotation();
		this.gene = v.getGeneName();
		this.variant = v.getNotation();
		this.oid = v.getMongoDBId().getOid();
		this.annotationsByCategory = annotationsByCategory;
		this.position = v.getChrom() + ":" + v.getPos();
		this.transcript = v.getVcfAnnotations().get(0).getFeatureId();
		v.getTumorAltFrequency(); //just to set the formatted TAF
		this.taf = v.getTumorAltFrequencyFormatted() + "%";
		this.tDepth = v.getTumorTotalDepth() + "";
		this.type = "snp";
		this.ref = v.getReference();
		this.alt = v.getAlt();
		if (v.getIds() != null) {
			for (String id : v.getIds()) {
				if (id != null && !id.equals("") && id.matches("[0-9]+")) {
					this.clinvarId = id;
				}
			}
		}
	}
	
	public GeneVariantAndAnnotation(CNV v, String genes, Map<String, String> annotationsByCategory) {
		this.geneVariant = genes;
		this.gene = genes;
		this.variant = genes;
		this.oid = v.getMongoDBId().getOid();
		this.annotationsByCategory = annotationsByCategory;
		this.position = v.getChrom() + ":" + v.getStart() + "-" + v.getEnd();
		this.copyNumber = v.getCopyNumber() + "";
		this.aberrationType = v.getAberrationType();
		this.type = "cnv";
	}

	public String getGeneVariant() {
		return geneVariant;
	}

	public void setGeneVariant(String geneVariant) {
		this.geneVariant = geneVariant;
	}

	public String getGene() {
		return gene;
	}

	public void setGene(String gene) {
		this.gene = gene;
	}

	public String getVariant() {
		return variant;
	}

	public void setVariant(String variant) {
		this.variant = variant;
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

	public Map<String, String> getAnnotationsByCategory() {
		return annotationsByCategory;
	}

	public void setAnnotationsByCategory(Map<String, String> annotationsByCategory) {
		this.annotationsByCategory = annotationsByCategory;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getTranscript() {
		return transcript;
	}

	public void setTranscript(String transcript) {
		this.transcript = transcript;
	}

	public String getTaf() {
		return taf;
	}

	public void setTaf(String taf) {
		this.taf = taf;
	}

	public String gettDepth() {
		return tDepth;
	}

	public void settDepth(String tDepth) {
		this.tDepth = tDepth;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public String getClinvarId() {
		return clinvarId;
	}

	public void setClinvarId(String clinvarId) {
		this.clinvarId = clinvarId;
	}

	public Map<String, String> getAnnotationsAddendedByCategory() {
		return annotationsAddendedByCategory;
	}

	public void setAnnotationsAddendedByCategory(Map<String, String> annotationsAddendedByCategory) {
		this.annotationsAddendedByCategory = annotationsAddendedByCategory;
	}

	public boolean isContainsAddendum() {
		return containsAddendum;
	}

	public void setContainsAddendum(boolean containsAddendum) {
		this.containsAddendum = containsAddendum;
	}

	


}
