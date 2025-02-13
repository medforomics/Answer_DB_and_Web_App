package utsw.bicf.answer.model.extmapping;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import utsw.bicf.answer.controller.serialization.FlagValue;
import utsw.bicf.answer.controller.serialization.VuetifyIcon;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CNVReport {
	
	Boolean isAllowed = true;
	
	@JsonProperty("_id")
	MongoDBId mongoDBId;
	String genes;
	String chrom;
	Integer start;
	Integer end;
	String startFormatted;
	String endFormatted;
	Integer copyNumber;
	String comment;
	String loci;
	String cytoband;
	boolean readonly;
	String aberrationType;
	String cytobandTruncated;
	String highestAnnotationTier;
	String breadth;
	boolean manualRow;
	boolean containsAddendum;
	String addedumComment;
	FlagValue iconFlags;
	
	public CNVReport() {
		
	}


	public CNVReport(String text, CNV c) {
		this.mongoDBId = c.mongoDBId;
		this.genes = c.getGenes().stream().sorted().collect(Collectors.joining(" "));
		this.chrom = c.chrom;
		this.start = c.start;
		this.end = c.end;
		this.startFormatted = c.startFormatted;
		this.endFormatted = c.endFormatted;
		this.copyNumber = c.copyNumber;
		this.comment = text;
		this.loci = this.chrom + ":" + this.start + "-" + this.end;
		this.cytoband = c.getCytoband();
		this.aberrationType = "";
		if ("gain".equals(c.getAberrationType())
				|| "amplification".equals(c.getAberrationType())) {
			aberrationType = "Gain ";
		}
		else if (c.getAberrationType() != null && c.getAberrationType().contains("loss")) {
			aberrationType = "Loss ";
		}
		else if (c.getAberrationType() != null && c.getAberrationType().equals("cnLOH")) {
			aberrationType = "cnLOH ";
		}
		this.cytobandTruncated = c.getCytoband().substring(0, 1);
	}


	public CNVReport(Annotation a, CNV c, String highestAnnotationTier, String breadth, String comment) {
		this.mongoDBId = c.mongoDBId;
		if (CNV.BREADTH_CHROM.equals(breadth)) {
			this.genes = c.getGenes().stream().sorted().collect(Collectors.joining(" "));
		}
		else if (CNV.BREADTH_FOCAL.equals(breadth)) {
			this.genes = a.getCnvGenes().stream().sorted().collect(Collectors.joining(" "));
		}
		this.chrom = c.chrom;
		this.start = c.start;
		this.end = c.end;
		this.startFormatted = c.startFormatted;
		this.endFormatted = c.endFormatted;
		this.copyNumber = c.copyNumber;
		this.loci = this.chrom + ":" + this.start + "-" + this.end;
		this.cytoband = c.getCytoband();
		this.highestAnnotationTier = highestAnnotationTier;
		this.breadth = breadth;
		this.aberrationType = c.getAberrationType();
		//if one p and one q, truncated should be empty
		boolean needTruncatedLetter = c.getCytoband() != null 
				&& !(StringUtils.countOccurrencesOf(c.getCytoband(), "p") == 1
				&& StringUtils.countOccurrencesOf(c.getCytoband(), "q") == 1);
		if (needTruncatedLetter) {
			this.cytobandTruncated = c.getCytoband().substring(0, 1);
		}
		else {
			this.cytobandTruncated = "";
		}
		this.comment = comment;
	}
	
	public void populateManualCNV() {
		boolean needTruncatedLetter = this.getCytoband() != null 
				&& !(StringUtils.countOccurrencesOf(this.getCytoband(), "p") == 1
				&& StringUtils.countOccurrencesOf(this.getCytoband(), "q") == 1);
		if (needTruncatedLetter) {
			this.cytobandTruncated = this.getCytoband().substring(0, 1);
		}
		else {
			this.cytobandTruncated = "";
		}
		if (this.copyNumber != null && this.copyNumber == 2) {
			this.aberrationType = "cnLOH";
		}
		else if (this.copyNumber != null && this.copyNumber > 2) {
			this.aberrationType = "gain";
		}
		else {
			this.aberrationType = "loss";
		}
	}
	
	public CNVReport(CNVReport incompleteCNV) {
	}


	public String getChrom() {
		return chrom;
	}


	public void setChrom(String chrom) {
		this.chrom = chrom;
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


	public String getGenes() {
		return genes;
	}


	public void setGenes(String genes) {
		this.genes = genes;
	}


	public Integer getStart() {
		return start;
	}


	public void setStart(Integer start) {
		this.start = start;
	}


	public Integer getEnd() {
		return end;
	}


	public void setEnd(Integer end) {
		this.end = end;
	}




	public Integer getCopyNumber() {
		return copyNumber;
	}


	public void setCopyNumber(Integer copyNumber) {
		this.copyNumber = copyNumber;
	}




	public String getStartFormatted() {
		if (startFormatted == null && start != null) {
			startFormatted = NumberFormat.getInstance().format(start);
		}
		return startFormatted;
	}


	public void setStartFormatted(String startFormatted) {
		this.startFormatted = startFormatted;
	}


	public String getEndFormatted() {
		if (endFormatted == null && end != null) {
			endFormatted = NumberFormat.getInstance().format(end);
		}
		return endFormatted;
	}


	public void setEndFormatted(String endFormatted) {
		this.endFormatted = endFormatted;
	}


	public String getComment() {
		return comment;
	}


	public void setComment(String comment) {
		this.comment = comment;
	}


	public String getLoci() {
		return loci;
	}


	public void setLoci(String loci) {
		this.loci = loci;
	}


	public String getCytoband() {
		return cytoband;
	}


	public void setCytoband(String cytoband) {
		this.cytoband = cytoband;
	}


	public boolean isReadonly() {
		return readonly;
	}


	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}


	public String getAberrationType() {
		return aberrationType;
	}


	public void setAberrationType(String aberrationType) {
		this.aberrationType = aberrationType;
	}


	public String getCytobandTruncated() {
		return cytobandTruncated;
	}


	public void setCytobandTruncated(String cytobandTruncated) {
		this.cytobandTruncated = cytobandTruncated;
	}


	public String getHighestAnnotationTier() {
		return highestAnnotationTier;
	}


	public void setHighestAnnotationTier(String highestAnnotationTier) {
		this.highestAnnotationTier = highestAnnotationTier;
	}


	public String getBreadth() {
		return breadth;
	}


	public void setBreadth(String breadth) {
		this.breadth = breadth;
	}


	public boolean isManualRow() {
		return manualRow;
	}


	public void setManualRow(boolean manualRow) {
		this.manualRow = manualRow;
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
		icons.add(new VuetifyIcon("mdi-alert-decagram", "warning", "This is an addendum cnv"));
		iconFlags = new FlagValue(icons);
	}






}
