package utsw.bicf.answer.model.hybrid;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.controller.serialization.Button;
import utsw.bicf.answer.controller.serialization.FlagValue;
import utsw.bicf.answer.controller.serialization.VuetifyIcon;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.CaseHistory;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Report;
import utsw.bicf.answer.security.MongoProperties;

public class OrderCaseArchived {
	
	String epicOrderNumber;
	String epicOrderDate;
	String dateReceived;
	String icd10;
	String caseId;
	String assignedTo;
	List<String> assignedToIds;
	String patientName;
	FlagValue typeFlags;
	String caseType;
	String reportId;
	String oncotreeDiagnosis;
	
	List<Button> buttons = new ArrayList<Button>();
//	FlagValue progressFlags;
	
	public OrderCaseArchived(ModelDAO modelDAO, OrderCase orderCase, List<User> users, User currentUser, MongoProperties mongoProps) {
		this.epicOrderNumber = orderCase.getEpicOrderNumber();
		this.epicOrderDate =orderCase.getEpicOrderDate();
		this.oncotreeDiagnosis = orderCase.getOncotreeDiagnosis();
		this.icd10 = orderCase.getIcd10();
		this.caseId = orderCase.getCaseId();
		this.dateReceived = orderCase.getReceivedDate();
		this.assignedToIds = orderCase.getAssignedTo();
		this.patientName = orderCase.getPatientName();
		List<String> userNames = new ArrayList<String>();
		for (String userId : orderCase.getAssignedTo()) {
			for (User user : users) {
				if (userId.equals(user.getUserId().toString())) {
					userNames.add(user.getFullName());
				}
			}
		}
		this.assignedTo = userNames.stream().collect(Collectors.joining("<br/>"));
//		if (currentUser.getIndividualPermission().getCanAssign()) {
//			buttons.add(new Button("assignment_ind", "assignToUser", "Assign To", "info"));
//		}
		if (currentUser.getIndividualPermission().getCanView()) {
			buttons.add(new Button("visibility", "open-read-only", "Open in View Only Mode", "info"));
		}
		if (currentUser.getIndividualPermission().getCanView() 
				&& CaseHistory.lastStepMatches(orderCase, CaseHistory.STEP_FINALIZED)) {
			RequestUtils utils = new RequestUtils(modelDAO, mongoProps);
			List<Report> allReports;
			try {
				allReports = utils.getExistingReports(caseId);
				Report lastFinalized = null;
				if (allReports != null) {
					for (Report r : allReports) {
						if (r.getFinalized() != null) {
							if (lastFinalized != null) { //compare dates
								OffsetDateTime lastFinalizedUTCDatetime = OffsetDateTime.parse(lastFinalized.getDateFinalized(), DateTimeFormatter.ISO_DATE_TIME);
								OffsetDateTime rFinalizedUTCDatetime = OffsetDateTime.parse(r.getDateFinalized(), DateTimeFormatter.ISO_DATE_TIME);
								if (rFinalizedUTCDatetime.isAfter(lastFinalizedUTCDatetime)) {
									lastFinalized = r; //current report is older than lastFinalized
								}
							}
							else { //first finalized report, lastFinalized is still null
								lastFinalized = r;
							}
						}
					}
				}
				if (lastFinalized != null) {
					this.reportId = lastFinalized.getMongoDBId().getOid();
					buttons.add(new Button("mdi-pdf-box", "downloadPDFReport", "Download Finalized Report", "info"));
				}
			} catch (UnsupportedOperationException | IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		}
		
		
		List<VuetifyIcon> typeIcons = new ArrayList<VuetifyIcon>();
		String iconName = null;
		String tooltip = null;
		if (OrderCase.TYPE_CLINICAL.equals(orderCase.getType())) {
			iconName = "fa-user-md";
			tooltip = OrderCase.TYPE_CLINICAL + " case";
		}
		else if (OrderCase.TYPE_RESEARCH.equals(orderCase.getType())) {
			iconName = "fa-flask";
			tooltip = OrderCase.TYPE_RESEARCH + " case";
		}
		else if (OrderCase.TYPE_CLINICAL_RESEARCH.equals(orderCase.getType())) {
			iconName = "fa-flask";
			tooltip = OrderCase.TYPE_CLINICAL_RESEARCH + " case";
		}
		typeIcons.add(new VuetifyIcon(iconName, "grey", tooltip, 20));
		typeFlags = new FlagValue(typeIcons);
		
		this.caseType = orderCase.getType();
		
//		List<VuetifyIcon> icons = new ArrayList<VuetifyIcon>();
//		int step = 0;
//		int totalSteps = OrderCase.getTotalSteps();
//		if (orderCase.getCaseHistory() != null && !orderCase.getCaseHistory().isEmpty()) {
//			step = orderCase.getCaseHistory().get(orderCase.getCaseHistory().size() - 1).getStep(); //get the last step
//		}
//		
//		for (int i = 0; i < totalSteps; i++) {
//			String color = i <= step ? "info" : "grey";
//			icons.add(new VuetifyIcon("mdi-numeric-" + i + "-box", color, OrderCase.getStepTooltip(i)));
//		}
		
//		progressFlags = new FlagValue(icons);
	}

	public String getEpicOrderNumber() {
		return epicOrderNumber;
	}

	public String getEpicOrderDate() {
		return epicOrderDate;
	}

	public String getDateReceived() {
		return dateReceived;
	}

	public String getIcd10() {
		return icd10;
	}

	public List<Button> getButtons() {
		return buttons;
	}

	public String getCaseId() {
		return caseId;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public List<String> getAssignedToIds() {
		return assignedToIds;
	}

	public String getPatientName() {
		return patientName;
	}


	public FlagValue getTypeFlags() {
		return typeFlags;
	}

//	public FlagValue getProgressFlags() {
//		return progressFlags;
//	}

	public String getCaseType() {
		return caseType;
	}

	public String getReportId() {
		return reportId;
	}

	public String getOncotreeDiagnosis() {
		return oncotreeDiagnosis;
	}



}
