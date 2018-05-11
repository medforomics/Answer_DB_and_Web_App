package utsw.bicf.answer.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.DataTableFilter;
import utsw.bicf.answer.controller.serialization.SearchItem;
import utsw.bicf.answer.controller.serialization.SearchItemString;
import utsw.bicf.answer.controller.serialization.vuetify.OpenCaseSummary;
import utsw.bicf.answer.controller.serialization.vuetify.VariantDetailsSummary;
import utsw.bicf.answer.controller.serialization.vuetify.VariantFilterItems;
import utsw.bicf.answer.controller.serialization.vuetify.VariantVcfAnnotationSummary;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.VCFAnnotation;
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.security.PermissionUtils;

@Controller
@RequestMapping("/")
public class OpenCaseController {
	
	static {
		PermissionUtils.permissionPerUrl.put("openCase", new PermissionUtils(true, false, false));
		PermissionUtils.permissionPerUrl.put("getCaseDetails", new PermissionUtils(true, false, false));
		PermissionUtils.permissionPerUrl.put("getVariantFilters", new PermissionUtils(true, false, false));
		PermissionUtils.permissionPerUrl.put("getVariantDetails", new PermissionUtils(true, false, false));
		PermissionUtils.permissionPerUrl.put("saveVariantSelection", new PermissionUtils(true, true, false));
		PermissionUtils.permissionPerUrl.put("commitAnnotations", new PermissionUtils(true, true, false));
	}

	@Autowired
	ServletContext servletContext;
	@Autowired
	ModelDAO modelDAO;

	@RequestMapping("/openCase/{caseId}")
	public String openCase(Model model, HttpSession session, @PathVariable String caseId) throws IOException {
		model.addAttribute("urlRedirect", "openCase/" + caseId);
		User user = (User) session.getAttribute("user");
		return ControllerUtil.initializeModel(model, servletContext, user);
	}
	
	
	
	@RequestMapping(value = "/getCaseDetails")
	@ResponseBody
	public String getCaseDetails(Model model, HttpSession session, @RequestParam String caseId,
			@RequestBody String filters)
			throws Exception {
		
		User user = (User) session.getAttribute("user"); //to verify that the user is assigned to the case
		//send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		OrderCase[] cases = utils.getActiveCases();
		OrderCase detailedCase = null;
		if (cases != null) {
			for (OrderCase c : cases) {
				if (c.getCaseId().equals(caseId)) {
					detailedCase = utils.getCaseDetails(caseId, filters);
					if (!detailedCase.getAssignedTo().contains(user.getUserId().toString())) {
						//user is not assigned to this case
						AjaxResponse response = new AjaxResponse();
						response.setIsAllowed(false);
						response.setSuccess(false);
						response.setMessage(user.getFullName() + " is not assigned to this case");
						return response.createObjectJSON();
					}
					break; //found that the case exists
				}
			}
		}
		OpenCaseSummary summary = new OpenCaseSummary(modelDAO, detailedCase, null, "chromPos", user);
		return summary.createVuetifyObjectJSON();
		
	}
	
	@RequestMapping(value = "/getVariantFilters")
	@ResponseBody
	public String getVariantFilters(Model model, HttpSession session)
			throws Exception {
		List<DataTableFilter> filters = new ArrayList<DataTableFilter>();
		
		DataTableFilter chrFilter = new DataTableFilter("Chromosome", "chrom");
		chrFilter.setSelect(true);
		List<SearchItem> selectItems = new ArrayList<SearchItem>();
		for (int i = 1; i <= 23; i++) {
			selectItems.add(new SearchItemString("CHR" + i, "chr" + i));
		}
		selectItems.add(new SearchItemString("CHRX", "chrX"));
		selectItems.add(new SearchItemString("CHRY", "chrY"));
		filters.add(chrFilter);
		chrFilter.setSelectItems(selectItems);
		
		DataTableFilter geneFilter = new DataTableFilter("Gene Name", Variant.FIELD_GENE_NAME);
		geneFilter.setString(true);
		filters.add(geneFilter);
		
		DataTableFilter passQCFilter = new DataTableFilter("Pass QC", "Fail QC", Variant.FIELD_FILTERS);
		passQCFilter.setBoolean(true);
		filters.add(passQCFilter);
		
		DataTableFilter annotatedFilter = new DataTableFilter("Annotated", "Unknown", Variant.FIELD_ANNOTATIONS);
		annotatedFilter.setBoolean(true);
		filters.add(annotatedFilter);
		
		DataTableFilter tafFilter = new DataTableFilter("Tumor Alt %", Variant.FIELD_TUMOR_ALT_FREQUENCY);
		tafFilter.setNumber(true);
		filters.add(tafFilter);
		
//		DataTableFilter tumorDepthFilter = new DataTableFilter("Tumor Depth", "tumorAltDepth");
//		tumorDepthFilter.setNumber(true);
//		filters.add(tumorDepthFilter);
		
		DataTableFilter tumorTotalDepthFilter = new DataTableFilter("Tumor Total Depth", Variant.FIELD_TUMOR_TOTAL_DEPTH);
		tumorTotalDepthFilter.setNumber(true);
		filters.add(tumorTotalDepthFilter);
		
		DataTableFilter nafFilter = new DataTableFilter("Normal Alt %", Variant.FIELD_NORMAL_ALT_FREQUENCY);
		nafFilter.setNumber(true);
		filters.add(nafFilter);
		
//		DataTableFilter normalDepthFilter = new DataTableFilter("Normal Depth", "normalAltDepth");
//		normalDepthFilter.setNumber(true);
//		filters.add(normalDepthFilter);
		
		DataTableFilter normalTotalDepthFilter = new DataTableFilter("Normal Total Depth", Variant.FIELD_NORMAL_TOTAL_DEPTH);
		normalTotalDepthFilter.setNumber(true);
		filters.add(normalTotalDepthFilter);
		
		DataTableFilter rafFilter = new DataTableFilter("Rna Alt %", Variant.FIELD_RNA_ALT_FREQUENCY);
		rafFilter.setNumber(true);
		filters.add(rafFilter);
		
//		DataTableFilter rnaDepthFilter = new DataTableFilter("RNA Depth", "rnaAltDepth");
//		rnaDepthFilter.setNumber(true);
//		filters.add(rnaDepthFilter);
		
		DataTableFilter rnaTotalDepthFilter = new DataTableFilter("RNA Total Depth", Variant.FIELD_RNA_TOTAL_DEPTH);
		rnaTotalDepthFilter.setNumber(true);
		filters.add(rnaTotalDepthFilter);
		
		DataTableFilter effectFilter= new DataTableFilter("Effects", Variant.FIELD_EFFECTS);
		effectFilter.setCheckBox(true);
		filters.add(effectFilter);
		
		VariantFilterItems items = new VariantFilterItems();
		items.setFilters(filters);
		return  items.createVuetifyObjectJSON();
		
	}
	
	@RequestMapping(value = "/getVariantDetails")
	@ResponseBody
	public String getVariantDetails(Model model, HttpSession session, @RequestParam String variantId)
			throws Exception {
		
		//send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		Variant variantDetails = utils.getVariantDetails(variantId);
		VariantVcfAnnotationSummary summaryCanonical = null;
		VariantVcfAnnotationSummary summaryOthers = null;
		VariantDetailsSummary summary = null; 
		if (variantDetails != null) {
			List<VCFAnnotation> vcfAnnotations = variantDetails.getVcfAnnotations();
			if (!vcfAnnotations.isEmpty()) {
				List<VCFAnnotation> canonicalAnnotation = new ArrayList<VCFAnnotation>();
				canonicalAnnotation.add(vcfAnnotations.get(0));
				List<VCFAnnotation> otherAnnotations = new ArrayList<VCFAnnotation>();
				otherAnnotations.addAll(vcfAnnotations);
				otherAnnotations.remove(0);
				summaryCanonical = new VariantVcfAnnotationSummary(canonicalAnnotation, "proteinPosition");
				summaryOthers = new VariantVcfAnnotationSummary(otherAnnotations, "proteinPosition");
				summary = new VariantDetailsSummary(variantDetails, summaryCanonical, summaryOthers);
			}
			return summary.createVuetifyObjectJSON();
		}
		return null;
		
	}
	
	@RequestMapping(value = "/saveVariantSelection")
	@ResponseBody
	public String saveVariantSelection(Model model, HttpSession session,
			@RequestBody String selectedVariantIds, @RequestParam String caseId)
			throws Exception {
		RequestUtils utils = new RequestUtils(modelDAO);
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(true);
		utils.saveVariantSelection(response, caseId, selectedVariantIds);
		return response.createObjectJSON();
		
	}
	
	@RequestMapping(value = "/commitAnnotations")
	@ResponseBody
	public String commitAnnotations(Model model, HttpSession session,
			@RequestBody String annotations, @RequestParam String caseId)
			throws Exception {
		RequestUtils utils = new RequestUtils(modelDAO);
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(true);
		utils.commitAnnotation(response, caseId, annotations);
		return response.createObjectJSON();
		
	}
	
}
