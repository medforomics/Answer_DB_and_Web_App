package utsw.bicf.answer.db.api.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.Utils;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.AnswerDBCredentials;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.VariantFilterList;
import utsw.bicf.answer.model.extmapping.Annotation;
import utsw.bicf.answer.model.extmapping.AnnotationSearchResult;
import utsw.bicf.answer.model.extmapping.CNRData;
import utsw.bicf.answer.model.extmapping.CNSData;
import utsw.bicf.answer.model.extmapping.CNV;
import utsw.bicf.answer.model.extmapping.CNVPlotData;
import utsw.bicf.answer.model.extmapping.CNVPlotDataRaw;
import utsw.bicf.answer.model.extmapping.CaseAnnotation;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.SearchSNPAnnotation;
import utsw.bicf.answer.model.extmapping.SelectedVariantIds;
import utsw.bicf.answer.model.extmapping.Translocation;
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.security.QcAPIAuthentication;

/**
 * All API requests to the annotation DB should be here.
 * 
 * @author Guillaume
 *
 */
public class RequestUtils {

	ModelDAO modelDAO;
	AnswerDBCredentials dbProps;
	QcAPIAuthentication qcAPI;

	public RequestUtils(ModelDAO modelDAO) {
		super();
		this.modelDAO = modelDAO;
		this.dbProps = modelDAO.getAnswerDBCredentials();
	}
	
	public RequestUtils(QcAPIAuthentication qcAPI) {
		this.qcAPI = qcAPI;
	}

	public final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private HttpGet requestGet = null;
	private HttpPost requestPost = null;
	private HttpPut requestPut = null;
	private HttpClient client = HttpClientBuilder.create().build();
	private ObjectMapper mapper = new ObjectMapper();

	private void addAuthenticationHeader(HttpGet requestMethod) {
		requestMethod.setHeader(HttpHeaders.AUTHORIZATION, createAuthHeader());
	}

	private void addAuthenticationHeader(HttpPost requestMethod) {
		requestMethod.setHeader(HttpHeaders.AUTHORIZATION, createAuthHeader());
	}

	private void addAuthenticationHeader(HttpPut requestMethod) {
		requestMethod.setHeader(HttpHeaders.AUTHORIZATION, createAuthHeader());
	}

	private String createAuthHeader() {
		String auth = dbProps.getUsername() + ":" + dbProps.getPassword();
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("UTF-8")));
		String authHeader = "Basic " + new String(encodedAuth);
		return authHeader;
	}

	public OrderCase[] getActiveCases() throws URISyntaxException, ClientProtocolException, IOException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("cases");
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);

		addAuthenticationHeader(requestGet);

		HttpResponse response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			OrderCase[] cases = mapper.readValue(response.getEntity().getContent(), OrderCase[].class);
			return cases;
		}
		return null;
	}

	public AjaxResponse assignCaseToUser(List<User> users, String caseId)
			throws ClientProtocolException, IOException, URISyntaxException {
		String userIds = users.stream().map(user -> user.getUserId().toString()).collect(Collectors.joining(","));
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(caseId).append("/assignusers").append("?userIds=").append(userIds);
		URI uri = new URI(sbUrl.toString());

		requestPut = new HttpPut(uri);

		addAuthenticationHeader(requestPut);

		HttpResponse response = client.execute(requestPut);

		AjaxResponse ajaxResponse = new AjaxResponse();
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ajaxResponse.setSuccess(true);
		} else {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Something went wrong");
		}
		return ajaxResponse;
	}

	public OrderCase getCaseDetails(String caseId, String data)
			throws ClientProtocolException, IOException, URISyntaxException {
		if (data == null) {
			data = "{\"filters\": []}";
		}
		VariantFilterList filterList = Utils.parseFilters(data, false);
		String filterParam = filterList.createJSON();
		System.out.println(filterParam);

		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(caseId).append("/filter");
		URI uri = new URI(sbUrl.toString());

		// requestGet = new HttpGet(uri);
		// addAuthenticationHeader(requestGet);

		// TODO Ben needs to build the API for filtering
		requestPost = new HttpPost(uri);
		addAuthenticationHeader(requestPost);
		requestPost.setEntity(new StringEntity(filterParam, ContentType.APPLICATION_JSON));

		HttpResponse response = client.execute(requestPost);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			OrderCase orderCase = mapper.readValue(response.getEntity().getContent(), OrderCase.class);
			return orderCase;
		}
		return null;
	}

	/**
	 * Get all information about a variant, including annotations
	 * @param variantType 
	 * 
	 * @param caseId
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public Variant getVariantDetails(String variantId) throws ClientProtocolException, IOException, URISyntaxException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("variant/"); 
		sbUrl.append(variantId);
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);

		addAuthenticationHeader(requestGet);

		HttpResponse response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			
			Variant variant = mapper.readValue(response.getEntity().getContent(), Variant.class);
			return variant;
		}
		return null;
	}
	
	/**
	 * Get all information about a variant, including annotations
	 * @param variantType 
	 * 
	 * @param caseId
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public CNV getCNVDetails(String variantId) throws ClientProtocolException, IOException, URISyntaxException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("cnv/"); 
		sbUrl.append(variantId);
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);

		addAuthenticationHeader(requestGet);

		HttpResponse response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			CNV cnv = mapper.readValue(response.getEntity().getContent(), CNV.class);
			return cnv;
		}
		return null;
	}
	
	public Translocation getTranslocationDetails(String variantId) throws URISyntaxException, ClientProtocolException, IOException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("translocation/"); 
		sbUrl.append(variantId);
		URI uri = new URI(sbUrl.toString());
		requestGet = new HttpGet(uri);

		addAuthenticationHeader(requestGet);

		HttpResponse response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			Translocation translocation = mapper.readValue(response.getEntity().getContent(), Translocation.class);
			return translocation;
		}
		return null;
	}

	public void saveVariantSelection(AjaxResponse ajaxResponse, String caseId, List<String> selectedSNPVariantIds, 
			List<String> selectedCNVIds, List<String> selectedTranslocationIds)
			throws ClientProtocolException, IOException, URISyntaxException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(caseId).append("/selectvariants");
		URI uri = new URI(sbUrl.toString());

		requestPost = new HttpPost(uri);
		addAuthenticationHeader(requestPost);
		SelectedVariantIds variantIds = new SelectedVariantIds();
		variantIds.setSelectedSNPVariantIds(selectedSNPVariantIds);
		variantIds.setSelectedCNVIds(selectedCNVIds);
		variantIds.setSelectedTranslocationIds(selectedTranslocationIds);
		requestPost.setEntity(new StringEntity(variantIds.createObjectJSON(), ContentType.APPLICATION_JSON));

		HttpResponse response = client.execute(requestPost);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ajaxResponse.setSuccess(true);
		} else {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Something went wrong");
		}
	}

	public boolean commitAnnotation(AjaxResponse ajaxResponse, String caseId, String variantId,
			List<Annotation> annotations) throws URISyntaxException, ClientProtocolException, IOException {
		boolean didChange = false;
		ObjectMapper mapper = new ObjectMapper();
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("annotations/");
		URI uri = new URI(sbUrl.toString());
		requestPost = new HttpPost(uri);
		addAuthenticationHeader(requestPost);
		System.out.println(mapper.writeValueAsString(annotations));
		requestPost.setEntity(new StringEntity(mapper.writeValueAsString(annotations), ContentType.APPLICATION_JSON));
		HttpResponse response = client.execute(requestPost);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Something went wrong");
		}
		else {
			String r = IOUtils.toString(response.getEntity().getContent(), Charset.defaultCharset());
			didChange = r.contains("true");
			ajaxResponse.setSuccess(true);
		}
		return didChange;
	}

	/**
	 * Get a summary of the case with basic information
	 * and no variant info
	 * @param caseId
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public OrderCase getCaseSummary(String caseId) throws ClientProtocolException, IOException, URISyntaxException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(caseId).append("/summary");
		URI uri = new URI(sbUrl.toString());

		requestGet = new HttpGet(uri);
		addAuthenticationHeader(requestGet);

		HttpResponse response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			OrderCase orderCase = mapper.readValue(response.getEntity().getContent(), OrderCase.class);
			return orderCase;
		}
		return null;
	}
	
	/**
	 * Get a summary of the case with basic information
	 * and no variant info
	 * @param caseId
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public OrderCase saveCaseSummary(String caseId, OrderCase caseSummary) throws ClientProtocolException, IOException, URISyntaxException {

		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(caseId).append("/summary");
		URI uri = new URI(sbUrl.toString());

		requestPost = new HttpPost(uri);
		addAuthenticationHeader(requestPost);
		requestPost.setEntity(new StringEntity(mapper.writeValueAsString(caseSummary), ContentType.APPLICATION_JSON));
		HttpResponse response = client.execute(requestPost);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			OrderCase orderCase = mapper.readValue(response.getEntity().getContent(), OrderCase.class);
			return orderCase;
		}
		return null;
	}

	public CaseAnnotation getCaseAnnotation(String caseId) throws URISyntaxException, JsonParseException, JsonMappingException, UnsupportedOperationException, IOException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(caseId).append("/annotation");
		URI uri = new URI(sbUrl.toString());

		requestGet = new HttpGet(uri);
		addAuthenticationHeader(requestGet);

		HttpResponse response = client.execute(requestGet);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			CaseAnnotation caseAnnotation = mapper.readValue(response.getEntity().getContent(), CaseAnnotation.class);
			return caseAnnotation;
		}
		return null;
	}

	public void saveCaseAnnotation(AjaxResponse ajaxResponse, CaseAnnotation annotationToSave) throws URISyntaxException, UnsupportedCharsetException, ClientProtocolException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(annotationToSave.getCaseId()).append("/annotation");
		URI uri = new URI(sbUrl.toString());
		requestPost = new HttpPost(uri);
		addAuthenticationHeader(requestPost);
		System.out.println(mapper.writeValueAsString(annotationToSave));
		requestPost.setEntity(new StringEntity(mapper.writeValueAsString(annotationToSave), ContentType.APPLICATION_JSON));
		HttpResponse response = client.execute(requestPost);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Something went wrong");
		}
		else {
			ajaxResponse.setSuccess(true);
			ajaxResponse.setIsAllowed(true);
		}

		
	}

	public void saveVariant(AjaxResponse ajaxResponse, Object variant, String variantType) throws URISyntaxException, ClientProtocolException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		String oid = null;
		if (variantType.equals("snp")) {
			oid = ((Variant) variant).getMongoDBId().getOid();
			sbUrl.append("variant/");
		}
		if (variantType.equals("cnv")) {
			oid = ((CNV) variant).getMongoDBId().getOid();
			sbUrl.append("cnv/");
		}
		else {
			ajaxResponse.setSuccess(false);
		}
		sbUrl.append(oid);
		URI uri = new URI(sbUrl.toString());
		requestPost = new HttpPost(uri);
		addAuthenticationHeader(requestPost);
		
		requestPost.setEntity(new StringEntity(mapper.writeValueAsString(variant), ContentType.APPLICATION_JSON));
		HttpResponse response = client.execute(requestPost);
//		System.out.println(mapper.writeValueAsString(variant));

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Something went wrong");
		}
		else {
			ajaxResponse.setSuccess(true);
			ajaxResponse.setIsAllowed(true);
		}

	}
	
	public void saveSelectedAnnotations(AjaxResponse ajaxResponse, Object variant, String variantType, String oid) throws URISyntaxException, ClientProtocolException, IOException {
			ObjectMapper mapper = new ObjectMapper();
			StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
			if (variantType.equals("snp")) {
				sbUrl.append("variant/");
			}
			else if (variantType.equals("cnv")) {
				sbUrl.append("cnv/"); //TODO we might use the same api "variant"
			}
			else if (variantType.equals("translocation")) {
				sbUrl.append("translocation/");  //TODO we might use the same api "variant"
			}
			else {
				ajaxResponse.setSuccess(false);
			}
			sbUrl.append(oid).append("/selectannotations");
			URI uri = new URI(sbUrl.toString());
			requestPost = new HttpPost(uri);
			addAuthenticationHeader(requestPost);
			
			requestPost.setEntity(new StringEntity(mapper.writeValueAsString(variant), ContentType.APPLICATION_JSON));
			HttpResponse response = client.execute(requestPost);
//			System.out.println(mapper.writeValueAsString(variant));

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				ajaxResponse.setSuccess(false);
				ajaxResponse.setMessage("Something went wrong");
			}
			else {
				ajaxResponse.setSuccess(true);
				ajaxResponse.setIsAllowed(true);
			}
		
	}

	public void sendVariantSelectionToMDA(AjaxResponse ajaxResponse, String caseId, List<String> selectedSNPVariantIds,
			List<String> selectedCNVIds, List<String> selectedTranslocationIds) throws URISyntaxException, UnsupportedCharsetException, ClientProtocolException, IOException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(caseId).append("/sendToMDA");
		URI uri = new URI(sbUrl.toString());

		requestPost = new HttpPost(uri);
		addAuthenticationHeader(requestPost);
		SelectedVariantIds variantIds = new SelectedVariantIds();
		variantIds.setSelectedSNPVariantIds(selectedSNPVariantIds);
		variantIds.setSelectedCNVIds(selectedCNVIds);
		variantIds.setSelectedTranslocationIds(selectedTranslocationIds);
		requestPost.setEntity(new StringEntity(variantIds.createObjectJSON(), ContentType.APPLICATION_JSON));

		HttpResponse response = client.execute(requestPost);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			ajaxResponse.setSuccess(true);
		} else {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Something went wrong");
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getMocliaContent(AjaxResponse ajaxResponse, String caseId, List<String> selectedSNPVariantIds,
			List<String> selectedCNVIds, List<String> selectedTranslocationIds) throws URISyntaxException, UnsupportedCharsetException, ClientProtocolException, IOException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(caseId).append("/moclia");
		URI uri = new URI(sbUrl.toString());

		requestGet = new HttpGet(uri);
		addAuthenticationHeader(requestGet);
//		SelectedVariantIds variantIds = new SelectedVariantIds();
//		variantIds.setSelectedSNPVariantIds(selectedSNPVariantIds);
//		variantIds.setSelectedCNVIds(selectedCNVIds);
//		variantIds.setSelectedTranslocationIds(selectedTranslocationIds);
//		requestPost.setEntity(new StringEntity(variantIds.createObjectJSON(), ContentType.APPLICATION_JSON));

		HttpResponse response = client.execute(requestGet);
		List<String> result = null;
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			result = mapper.readValue(response.getEntity().getContent(), List.class);
			if (result != null && !result.isEmpty()) {
				ajaxResponse.setSuccess(true);
			}
			else {
				ajaxResponse.setSuccess(false);
				ajaxResponse.setMessage("Nothing to do. No variant matching MDA requirements were selected.");
			}
		} else {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Something went wrong");
		}
		return result;
	}

	public AnnotationSearchResult getGetAnnotationsByGeneAndVariant(String gene, String variant) throws URISyntaxException, JsonParseException, JsonMappingException, UnsupportedOperationException, IOException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("searchannotations/");
		URI uri = new URI(sbUrl.toString());

		requestPost = new HttpPost(uri);
		addAuthenticationHeader(requestPost);
		SearchSNPAnnotation search = new SearchSNPAnnotation();
		search.setGeneSymbolOrSynonym(gene);
		search.setVariant(variant);
		
		requestPost.setEntity(new StringEntity(search.createObjectJSON(), ContentType.APPLICATION_JSON));

		HttpResponse response = client.execute(requestPost);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			AnnotationSearchResult result = mapper.readValue(response.getEntity().getContent(), AnnotationSearchResult.class);
			return result;
		}
		return null;
	}

	public void caseReadyForReview(AjaxResponse ajaxResponse, String caseId) throws URISyntaxException, ClientProtocolException, IOException {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(caseId).append("/review");
		URI uri = new URI(sbUrl.toString());
		requestPost = new HttpPost(uri);
		addAuthenticationHeader(requestPost);
		
		HttpResponse response = client.execute(requestPost);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Something went wrong");
		}
		else {
			ajaxResponse.setSuccess(true);
			ajaxResponse.setIsAllowed(true);
		}
		
	}
	
	public void caseReadyForReport(AjaxResponse ajaxResponse, String caseId) throws URISyntaxException, ClientProtocolException, IOException  {
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(caseId).append("/report");
		URI uri = new URI(sbUrl.toString());
		requestPost = new HttpPost(uri);
		addAuthenticationHeader(requestPost);
		
		HttpResponse response = client.execute(requestPost);

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			ajaxResponse.setSuccess(false);
			ajaxResponse.setMessage("Something went wrong");
		}
		else {
			ajaxResponse.setSuccess(true);
			ajaxResponse.setIsAllowed(true);
		}
		
	}

	public CNVPlotData getCnvPlotData(String caseId, String chrom) throws URISyntaxException, ClientProtocolException, IOException {
		List<String> toSkip = new ArrayList<String>();
		toSkip.add("chrX");
		toSkip.add("chrY");
		toSkip.add("chr22_KI270879v1_alt");
		
		StringBuilder sbUrl = new StringBuilder(dbProps.getUrl());
		sbUrl.append("case/").append(caseId).append("/cnvplot");
		URI uri = new URI(sbUrl.toString());

		requestGet = new HttpGet(uri);
		addAuthenticationHeader(requestGet);

		HttpResponse response = client.execute(requestGet);

//		return test(chrom);
		
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			CNVPlotDataRaw plotDataRaw = mapper.readValue(response.getEntity().getContent(), CNVPlotDataRaw.class);
			CNVPlotData plotData = new CNVPlotData();
			List<CNRData> cnrDataList = new ArrayList<CNRData>();
			List<CNSData> cnsDataList = new ArrayList<CNSData>();
			
			for (List<String> items : plotDataRaw.getCnr()) {
				if (items.get(0).equals("Gene")) {
					continue; //skip the 1st row
				}
				String cnrChrom = items.get(1);
				if (chrom == null || cnrChrom.equals(chrom)) {
					String gene = items.get(0);
					Long start = Long.parseLong(items.get(2));
					Long end = Long.parseLong(items.get(3));
					Double depth = Double.parseDouble(items.get(5));
					Double log2 = Double.parseDouble(items.get(4));
					Double weight = Double.parseDouble(items.get(6));
					if (!toSkip.contains(cnrChrom)) { //skip chromosomes in the toSkip list
						cnrDataList.add(new CNRData(cnrChrom, start, end, gene, log2, weight));
					}
				}
				
			}
			
			for (List<String> items : plotDataRaw.getCns()) {
				if (items.get(0).equals("Chromosome")) {
					continue; //skip the 1st row
				}
				String cnrChrom = items.get(0);
				if (chrom == null || cnrChrom.equals(chrom)) {
					Long start = Long.parseLong(items.get(1));
					Long end = Long.parseLong(items.get(2));
					Double log2 = Double.parseDouble(items.get(3));
					Integer cn = Integer.parseInt(items.get(4));
					if (!toSkip.contains(cnrChrom)) { //skip chromosomes in the toSkip list
						cnsDataList.add(new CNSData(cnrChrom, start, end, log2, cn));
					}
				}
				
			}
			
			
			plotData.setCaseId(plotDataRaw.getCaseId());
			plotData.setCnrData(cnrDataList);
			plotData.setCnsData(cnsDataList);
			return plotData;
		}
		return null;
	}

	private CNVPlotData test(String chromFilter) throws IOException {
		List<String> cnrRows = FileUtils.readLines(new File("/opt/answer/files/cnr2.csv"));
		List<String> cnsRows = FileUtils.readLines(new File("/opt/answer/files/cns2.csv"));
		
		List<CNRData> cnrDataList = new ArrayList<CNRData>();
		List<CNSData> cnsDataList = new ArrayList<CNSData>();
		for (String row : cnrRows) {
			if (row.startsWith("chromosome")) {
				continue;
			}
			String[] items = row.split(",");
			String cnrChrom = items[0];
			if (chromFilter == null || cnrChrom.equals(chromFilter)) {
				cnrDataList.add(new CNRData(cnrChrom, Long.parseLong(items[1]), Long.parseLong(items[2]), items[3], Double.parseDouble(items[4]), Double.parseDouble(items[4])));
			}
		}
		for (String row : cnsRows) {
			if (row.startsWith("chromosome")) {
				continue;
			}
			String[] items = row.split(",");
			if (items.length >= 5) {
				String cnsChrom = items[0];
				if (chromFilter == null || cnsChrom.equals(chromFilter)) {
					cnsDataList.add(new CNSData(cnsChrom, Long.parseLong(items[1]), Long.parseLong(items[2]), Double.parseDouble(items[3]), Integer.parseInt(items[4])));
				}
			}
		}
		CNVPlotData data = new CNVPlotData();
		data.setCnrData(cnrDataList);
		data.setCnsData(cnsDataList);
		return data;
	}







}
