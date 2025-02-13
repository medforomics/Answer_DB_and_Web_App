package utsw.bicf.answer.model.extmapping;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import utsw.bicf.answer.controller.serialization.FlagValue;
import utsw.bicf.answer.controller.serialization.VuetifyIcon;
import utsw.bicf.answer.reporting.finalreport.FTLReportWithHighestTier;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TranslocationReport {
	
	Boolean isAllowed = true;
	
	@JsonProperty("_id")
	MongoDBId mongoDBId;
	String leftGene;
	String rightGene;
	String fusionName;
	String comment;
	String firstExon;
	String lastExon;
	boolean readonly;
	String highestAnnotationTier;
	boolean containsAddendum;
	String addedumComment;
	FlagValue iconFlags;
	
	public TranslocationReport() {
		
	}

	public TranslocationReport(String text, FTLReportWithHighestTier ftlWithTier) {
		Translocation ftl = ftlWithTier.getFtl();
		mongoDBId = ftl.getMongoDBId();
		this.leftGene = ftl.getLeftGene();
		this.rightGene = ftl.getRightGene();
		this.fusionName = ftl.getFusionName();
//		this.firstExon = t.getFirstExon();
//		this.lastExon = t.getLastExon();
		//reverse the 2 exons for the report
		this.firstExon = ftl.getLastExon();
		this.lastExon = ftl.getFirstExon();
		this.comment = text;
		this.highestAnnotationTier = ftlWithTier.getHighestAnnotationTier();
		
	}

	public Boolean getIsAllowed() {
		return isAllowed;
	}

	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}

	public MongoDBId getMongoDBId() {
		return mongoDBId;
	}

	public void setMongoDBId(MongoDBId mongoDBId) {
		this.mongoDBId = mongoDBId;
	}

	public String getLeftGene() {
		return leftGene;
	}

	public void setLeftGene(String leftGene) {
		this.leftGene = leftGene;
	}

	public String getRightGene() {
		return rightGene;
	}

	public void setRightGene(String rightGene) {
		this.rightGene = rightGene;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getFusionName() {
		return fusionName;
	}

	public void setFusionName(String fusionName) {
		this.fusionName = fusionName;
	}

	public String getFirstExon() {
		return firstExon;
	}

	public void setFirstExon(String firstExon) {
		this.firstExon = firstExon;
	}

	public String getLastExon() {
		return lastExon;
	}

	public void setLastExon(String lastExon) {
		this.lastExon = lastExon;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public String getHighestAnnotationTier() {
		return highestAnnotationTier;
	}

	public void setHighestAnnotationTier(String highestAnnotationTier) {
		this.highestAnnotationTier = highestAnnotationTier;
	}

	public boolean isContainsAddendum() {
		return containsAddendum;
	}

	public void setContainsAddendum(boolean containsAddendum) {
		this.containsAddendum = containsAddendum;
	}

	public String getAddedumComment() {
		return addedumComment;
	}

	public void setAddedumComment(String addedumComment) {
		this.addedumComment = addedumComment;
	}

	public FlagValue getIconFlags() {
		return iconFlags;
	}

	public void setIconFlags(FlagValue iconFlags) {
		this.iconFlags = iconFlags;
	}

	public void updateAsAddendum(String diff) {
		this.setAddedumComment(diff);
		this.setContainsAddendum(true);
		List<VuetifyIcon> icons = new ArrayList<VuetifyIcon>();
		icons.add(new VuetifyIcon("mdi-alert-decagram", "warning", "This is an addendum translocation"));
		iconFlags = new FlagValue(icons);
	}


}
