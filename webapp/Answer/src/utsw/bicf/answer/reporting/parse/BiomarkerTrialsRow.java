package utsw.bicf.answer.reporting.parse;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import utsw.bicf.answer.controller.serialization.FlagValue;
import utsw.bicf.answer.controller.serialization.VuetifyIcon;
import utsw.bicf.answer.model.extmapping.Trial;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BiomarkerTrialsRow {
	
	public static final String HEADER_SELECTED_BIOMARKER = "Selected Biomarker(s)*";
	public static final String HEADER_RELEVANT_BIOMARKER = "Relevant Biomarker(s)*";
	public static final String HEADER_DRUGS = "Drugs**";
	public static final String HEADER_TITLE = "Title";
	public static final String HEADER_NCTID = "NCTID";
	public static final String HEADER_MDACC_PROTOCOL_ID = "MDACC Protocol ID";
	public static final String HEADER_PHASE = "Phase";
	public static final String HEADER_PI = "PI";
	public static final String HEADER_DEPT = "Dept";
	public static final String HEADER_ADD_REQUIRED_BIOMARKERS = "Additional Required Biomarker(s)";
	
	String selectedBiomarker;
	String relevantBiomarker;
	String biomarker;
	String drugs;
	String title;
	String nctid;
	String mdaddProtocolId;
	String phase;
	String pi;
	String dept;
	String additionalRequiredBiomarkers;
	Boolean isSelected;
	boolean readonly;
	boolean isAddendum;
	FlagValue iconFlags;
	
	public BiomarkerTrialsRow() {
	}
	
	public BiomarkerTrialsRow(Trial trial) {
		this.biomarker = trial.getBiomarker();
		this.drugs = trial.getDrugs();
		this.title = trial.getTitle();
		this.nctid = trial.getNctId();
		this.phase = trial.getPhase();
		this.pi = trial.getContact();
		this.dept = trial.getLocation();
		
	}

	public String getSelectedBiomarker() {
		return selectedBiomarker;
	}
	public void setSelectedBiomarker(String selectedBiomarker) {
		this.selectedBiomarker = selectedBiomarker;
	}
	public String getRelevantBiomarker() {
		return relevantBiomarker;
	}
	public void setRelevantBiomarker(String relevantBiomarker) {
		this.relevantBiomarker = relevantBiomarker;
	}
	public String getDrugs() {
		return drugs;
	}
	public void setDrugs(String drugs) {
		this.drugs = drugs;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getNctid() {
		return nctid;
	}
	public void setNctid(String nctid) {
		this.nctid = nctid;
	}
	public String getMdaddProtocolId() {
		return mdaddProtocolId;
	}
	public void setMdaddProtocolId(String mdaddProtocolId) {
		this.mdaddProtocolId = mdaddProtocolId;
	}
	public String getPhase() {
		return phase;
	}
	public void setPhase(String phase) {
		this.phase = phase;
	}
	public String getPi() {
		return pi;
	}
	public void setPi(String pi) {
		this.pi = pi;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	
	public void prettyPrint() {
		String biomarker = selectedBiomarker != null ? selectedBiomarker : relevantBiomarker;
		System.out.println("biomarkers: " + biomarker + " NCTID: " + nctid + " PI: " + pi + " Drugs: " + drugs );
		
	}
	public String getAdditionalRequiredBiomarkers() {
		return additionalRequiredBiomarkers;
	}
	public void setAdditionalRequiredBiomarkers(String additionalRequiredBiomarkers) {
		this.additionalRequiredBiomarkers = additionalRequiredBiomarkers;
	}
	public String getBiomarker() {
		return biomarker;
	}
	public void setBiomarker(String biomarker) {
		this.biomarker = biomarker;
	}
	public Boolean getIsSelected() {
		return isSelected;
	}
	public void setIsSelected(Boolean isSelected) {
		this.isSelected = isSelected;
	}
	public boolean isReadonly() {
		return readonly;
	}
	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
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

	public void updateAsAddendum() {
		this.setAddendum(true);
		List<VuetifyIcon> icons = new ArrayList<VuetifyIcon>();
		icons.add(new VuetifyIcon("mdi-alert-decagram", "warning", "This is a trial addendum"));
		iconFlags = new FlagValue(icons);
	}
}
