package utsw.bicf.answer.reporting.finalreport;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageFitRectangleDestination;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.VerticalAlignment;
import be.quodlibet.boxable.image.Image;
import be.quodlibet.boxable.utils.PDStreamUtils;
import utsw.bicf.answer.controller.serialization.CellItem;
import utsw.bicf.answer.controller.serialization.GeneVariantAndAnnotation;
import utsw.bicf.answer.model.extmapping.CNVReport;
import utsw.bicf.answer.model.extmapping.IndicatedTherapy;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Report;
import utsw.bicf.answer.model.extmapping.TranslocationReport;
import utsw.bicf.answer.model.hybrid.PatientInfo;
import utsw.bicf.answer.reporting.parse.BiomarkerTrialsRow;
import utsw.bicf.answer.security.FileProperties;
import utsw.bicf.answer.security.OtherProperties;

public class FinalReportPDFTemplate {
	
	Report report;
	PDDocument mainDocument;
	float pageWidthMinusMargins;
	float pageHeight;
	float latestYPosition;
	FileProperties fileProps;
	OrderCase caseSummary;
	File tempFile;
	OtherProperties otherProps;
	List<Link> links = new ArrayList<Link>();
	
	public FinalReportPDFTemplate(Report report, FileProperties fileProps, OrderCase caseSummary, OtherProperties otherProps) {
		this.otherProps = otherProps;
		this.report = report;
		this.fileProps = fileProps;
		this.caseSummary = caseSummary;
		try {
			this.tempFile = new File(fileProps.getPdfFilesDir(), System.currentTimeMillis() + "_temp.pdf");
			if (tempFile.exists()) {
				tempFile.delete();
			}
			init();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	private void init() throws IOException, URISyntaxException {
		this.mainDocument = new PDDocument();
		PDPage page = new PDPage(PDRectangle.LETTER);
		mainDocument.addPage(page);
		this.pageWidthMinusMargins = page.getMediaBox().getWidth() - FinalReportTemplateConstants.MARGINLEFT
				- FinalReportTemplateConstants.MARGINRIGHT;
		this.pageHeight = page.getMediaBox().getHeight();
		
		this.addAddress();
		this.addNGSImageElement();
		this.addUTSWImageElement();
		this.addTitle();
		this.createPatientTable();
		this.addNotes();
		this.createNavigationTable();
		this.createIndicatedTherapiesTable();
		this.createClinicalTrialsTable();
		this.createClinicalSignificanceTables();
		this.createCNVTable();
		this.createTranslocationTable();
//		this.addInformationAboutTheTest();
		
		this.addFooters();
		this.addLinks();
	}
	
	private void addAddress() throws IOException {
		FinalReportTemplateConstants.MAIN_FONT_TYPE = PDType0Font.load(mainDocument,
				fileProps.getPdfFontFile());
		FinalReportTemplateConstants.MAIN_FONT_TYPE_BOLD = PDType0Font.load(mainDocument,
				fileProps.getPdfFontFile());
		PDPage firstPage = mainDocument.getPage(0);
		float yPos = pageHeight - FinalReportTemplateConstants.LOGO_MARGIN_TOP;

		float tableWidth = pageWidthMinusMargins / 3;

		BaseTable table = new BaseTable(yPos, yPos, 0, tableWidth, FinalReportTemplateConstants.MARGINLEFT,
				mainDocument, firstPage, false, true);
		for (String line : FinalReportTemplateConstants.ADDRESS) {
			Cell<PDPage> cell = table.createRow(FinalReportTemplateConstants.ADDRESS_FONT_SIZE).createCell(100, line);
			cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
			cell.setFontSize(FinalReportTemplateConstants.ADDRESS_FONT_SIZE);
			cell.setBottomPadding(0);
			cell.setTopPadding(0);
			cell.setLeftPadding(0);
		}
		latestYPosition = table.draw();

	}

	private void addNGSImageElement() throws IOException {
		PDPage firstPage = mainDocument.getPage(0);
		PDPageContentStream contentStream = new PDPageContentStream(mainDocument, firstPage,
				PDPageContentStream.AppendMode.APPEND, true);
		float yPos = pageHeight - FinalReportTemplateConstants.LOGO_MARGIN_TOP;
		File ngsLogoFile = new File(fileProps.getPdfLogoDir(), FinalReportTemplateConstants.NGS_LOGO_PATH);
		Image ngsImage = new Image(ImageIO.read(ngsLogoFile));
		ngsImage = ngsImage.scaleByWidth(118);
		ngsImage.draw(mainDocument, contentStream, 247, yPos);
		contentStream.close();
	}

	private void addUTSWImageElement() throws IOException {
		PDPage firstPage = mainDocument.getPage(0);
		PDPageContentStream contentStream = new PDPageContentStream(mainDocument, firstPage,
				PDPageContentStream.AppendMode.APPEND, true);
		float yPos = pageHeight - FinalReportTemplateConstants.LOGO_MARGIN_TOP;
		Image ngsImage = new Image(ImageIO.read(new File(fileProps.getPdfLogoDir(), FinalReportTemplateConstants.UTSW_LOGO_PATH)));
		ngsImage = ngsImage.scaleByWidth(182);
		ngsImage.draw(mainDocument, contentStream, 400, yPos);
		contentStream.close();
	}

	private void addTitle() throws IOException {
		latestYPosition -= FinalReportTemplateConstants.PARAGRAPH_PADDING_BOTTOM;
		PDPage firstPage = mainDocument.getPage(0);
		PDPageContentStream contentStream = new PDPageContentStream(mainDocument, firstPage,
				PDPageContentStream.AppendMode.APPEND, true);
		PDStreamUtils.write(contentStream, FinalReportTemplateConstants.TITLE, FinalReportTemplateConstants.MAIN_FONT_TYPE, 16,
				FinalReportTemplateConstants.MARGINLEFT, latestYPosition, Color.BLACK);
		latestYPosition = latestYPosition - 30; // position of the patient table title
		contentStream.close();
	}
	
	private void addNotes() throws IOException {
		// Title
		float tableWidth = pageWidthMinusMargins;
		PDPage currentPage = this.mainDocument.getPage(0);
		BaseTable table = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, FinalReportTemplateConstants.MARGINBOTTOM,
				tableWidth, FinalReportTemplateConstants.MARGINLEFT, mainDocument, currentPage, false, true);
		Cell<PDPage> cell = table.createRow(FinalReportTemplateConstants.DEFAULT_TEXT_FONT_SIZE).createCell(100, "Case Summary");
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setFontSize(FinalReportTemplateConstants.TITLE_TEXT_FONT_SIZE);

		// Content
		Row<PDPage> row = table.createRow(12);
		cell = row.createCell(100, report.getSummary());
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setFontSize(FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE);
		cell.setTextColor(Color.BLACK);
		cell.setBottomPadding(10);
		latestYPosition = table.draw();
	}
	
	private void createPatientTable() throws IOException {
		PatientInfo patientDetails = report.getPatientInfo();
		
		PDPage firstPage = mainDocument.getPage(0);
		
		PDPageContentStream contentStream = new PDPageContentStream(mainDocument, firstPage,
				PDPageContentStream.AppendMode.APPEND, true);
		PDStreamUtils.write(contentStream, FinalReportTemplateConstants.PATIENT_DETAILS_TITLE, FinalReportTemplateConstants.MAIN_FONT_TYPE,
				FinalReportTemplateConstants.TITLE_TEXT_FONT_SIZE,
				FinalReportTemplateConstants.MARGINLEFT + 5, latestYPosition, Color.BLACK);
		latestYPosition = latestYPosition - 20; // position of the patient table
		contentStream.close();
		
		float tableWidth = pageWidthMinusMargins / 3;

		boolean cellBorder = false;
		float defaultFont = FinalReportTemplateConstants.SMALLEST_TEXT_FONT_SIZE;
		
		BaseTable leftTable = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT, mainDocument, firstPage, cellBorder, true);
		List<CellItem> leftTableItems = patientDetails.getPatientTables().get(0).getItems();
		for (CellItem item : leftTableItems) {
			this.createRow(leftTable, item.getLabel(), item.getValue(), defaultFont);
		}
		leftTable.draw();
		float maxTableHeight = leftTable.getHeaderAndDataHeight();

		BaseTable middleTable = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT + leftTable.getWidth(), mainDocument, firstPage, cellBorder,
				true);
		List<CellItem> middleTableItems = patientDetails.getPatientTables().get(1).getItems();
		for (CellItem item : middleTableItems) {
			this.createRow(middleTable, item.getLabel(), item.getValue(), defaultFont);
		}
		middleTable.draw();
		
		maxTableHeight = Math.max(maxTableHeight, middleTable.getHeaderAndDataHeight());

		BaseTable rightTable = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT + leftTable.getWidth() + middleTable.getWidth(),
				mainDocument, firstPage, cellBorder, true);
		List<CellItem> rightTableItems = patientDetails.getPatientTables().get(2).getItems();
		for (CellItem item : rightTableItems) {
			this.createRow(rightTable, item.getLabel(), item.getValue(), defaultFont);
		}
		rightTable.draw();
		maxTableHeight = Math.max(maxTableHeight, rightTable.getHeaderAndDataHeight());

		// draw borders
		BaseTable leftTableEmpty = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT, mainDocument, firstPage, true, true);
		Row<PDPage> row = leftTableEmpty.createRow(maxTableHeight);
		this.applyPatientRecordTableBorderFormatting(row.createCell(100, ""));
		leftTableEmpty.draw();
		BaseTable middleTableEmpty = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT + leftTable.getWidth(), mainDocument, firstPage, true,
				true);
		row = middleTableEmpty.createRow(maxTableHeight);
		this.applyPatientRecordTableBorderFormatting(row.createCell(100, ""));
		middleTableEmpty.draw();
		BaseTable rightTableEmpty = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, 0, tableWidth,
				FinalReportTemplateConstants.MARGINLEFT + leftTable.getWidth() + middleTable.getWidth(),
				mainDocument, firstPage, true, true);
		row = rightTableEmpty.createRow(maxTableHeight);
		this.applyPatientRecordTableBorderFormatting(row.createCell(100, ""));
		rightTableEmpty.draw();

		latestYPosition -= maxTableHeight + 10;
	}
	
	private void applyPatientRecordTableBorderFormatting(Cell<PDPage> cell) {
		cell.setTopBorderStyle(FinalReportTemplateConstants.THINLINE_OUTTER_ANSWER_GREEN);
		cell.setBottomBorderStyle(FinalReportTemplateConstants.THINLINE_OUTTER_ANSWER_GREEN);
		cell.setLeftBorderStyle(FinalReportTemplateConstants.NO_BORDER);
		cell.setRightBorderStyle(FinalReportTemplateConstants.NO_BORDER);
	}
	
	private void updatePotentialNewPagePosition() {
		if (latestYPosition <= FinalReportTemplateConstants.MARGINBOTTOM * 2) { //start on a new page if to low on the page
			mainDocument.addPage(new PDPage(PDRectangle.LETTER));
			latestYPosition = pageHeight - FinalReportTemplateConstants.MARGINTOP;
		}
		else {
			latestYPosition -= 20;
		}
	}
	
	private void createNavigationTable() throws IOException {
		this.updatePotentialNewPagePosition();
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		BaseTable table = createNewTable(currentPage);
//		//Title
//		Row<PDPage> row = table.createRow(12); 
//		table.addHeaderRow(row);
//		Cell<PDPage> cellHeader = row.createCell(100, FinalReportTemplateConstants.INDICATED_THERAPIES_TITLE);
//		this.applyTitleHeaderFormatting(cellHeader);
		
		//Headers
//		row = table.createRow(12); 
//		table.addHeaderRow(row);
//		cellHeader = row.createCell(25, "VARIANT");
//		this.applyHeaderFormatting(cellHeader, defaultFont);
//		cellHeader = row.createCell(20, "LEVEL");
//		this.applyHeaderFormatting(cellHeader, defaultFont);
//		cellHeader = row.createCell(55, "INDICATION");
//		this.applyHeaderFormatting(cellHeader, defaultFont);
		
		
		Row<PDPage> row = table.createRow(12);
		Cell<PDPage> cell = row.createCell(25, FinalReportTemplateConstants.INDICATED_THERAPIES_TITLE_NAV);
		this.applyNavigationCellFormatting(cell, FinalReportTemplateConstants.THERAPY_COLOR);
		cell = row.createCell(25, FinalReportTemplateConstants.CLINICAL_TRIALS_TITLE_NAV);
		this.applyNavigationCellFormatting(cell, FinalReportTemplateConstants.TRIAL_COLOR);
		cell = row.createCell(25, FinalReportTemplateConstants.CLINICAL_SIGNIFICANCE_NAV);
		this.applyNavigationCellFormatting(cell, FinalReportTemplateConstants.CLIN_SIGNIFICANCE_COLOR);
		cell = row.createCell(10, FinalReportTemplateConstants.CNV_TITLE_SHORT);
		this.applyNavigationCellFormatting(cell, FinalReportTemplateConstants.CNV_COLOR);
		cell = row.createCell(15, FinalReportTemplateConstants.TRANSLOCATION_TITLE_SHORT);
		this.applyNavigationCellFormatting(cell, FinalReportTemplateConstants.FTL_COLOR);

		latestYPosition = table.draw() - 20;
	}
	
	private BaseTable createNewTable(PDPage currentPage) throws IOException {
		return new BaseTable(latestYPosition, pageHeight - FinalReportTemplateConstants.MARGINTOP, FinalReportTemplateConstants.MARGINBOTTOM, pageWidthMinusMargins,
				FinalReportTemplateConstants.MARGINLEFT, mainDocument, currentPage, true, true);
	}
	
	private void createIndicatedTherapiesTable() throws IOException {
		this.updatePotentialNewPagePosition();
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		float defaultFont = FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE;
		
		BaseTable table = createNewTable(currentPage);
		List<IndicatedTherapy> items = report.getIndicatedTherapies();
		
		//Title
		Row<PDPage> row = table.createRow(12); 
		table.addHeaderRow(row);
		Cell<PDPage> cellHeader = row.createCell(100, FinalReportTemplateConstants.INDICATED_THERAPIES_TITLE);
		this.applyTitleHeaderFormatting(cellHeader);
		cellHeader.setFillColor(FinalReportTemplateConstants.THERAPY_COLOR);
		links.add(new Link(FinalReportTemplateConstants.INDICATED_THERAPIES_TITLE_NAV, this.mainDocument.getNumberOfPages() - 1));
		
		//Headers
		row = table.createRow(12); 
		table.addHeaderRow(row);
		cellHeader = row.createCell(25, "VARIANT");
		this.applyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(20, "LEVEL");
		this.applyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(55, "INDICATION");
		this.applyHeaderFormatting(cellHeader, defaultFont);
		
		boolean greyBackground = false;
		for (IndicatedTherapy item : items) {
			Color color = Color.WHITE;
			if (greyBackground) {
				color = FinalReportTemplateConstants.BACKGROUND_LIGHT_GRAY;
			}
			row = table.createRow(12);
			Cell<PDPage> cell = row.createCell(25, item.getVariant());
			this.applyCellFormatting(cell, defaultFont, color);
			cell = row.createCell(20, item.getLevel());
			this.applyCellFormatting(cell, defaultFont, color);
			cell = row.createCell(55, item.getIndication());
			this.applyCellFormatting(cell, defaultFont, color);
			greyBackground = !greyBackground;
		}
		latestYPosition = table.draw() - 20;
	}
	
	private void createClinicalTrialsTable() throws IOException, URISyntaxException {
		if (report.getClinicalTrials() == null) {
			return;
		}
		this.updatePotentialNewPagePosition();
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		float defaultFont = FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE;
		
		BaseTable table = createNewTable(currentPage);
		
		//Title
		Row<PDPage> row = table.createRow(12); 
		table.addHeaderRow(row);
		Cell<PDPage> cellHeader = row.createCell(100, FinalReportTemplateConstants.CLINICAL_TRIALS_TITLE);
		this.applyTitleHeaderFormatting(cellHeader);
		cellHeader.setFillColor(FinalReportTemplateConstants.TRIAL_COLOR);
		links.add(new Link(FinalReportTemplateConstants.CLINICAL_TRIALS_TITLE_NAV, this.mainDocument.getNumberOfPages() - 1));
		
		
		//Headers
		row = table.createRow(12); 
		table.addHeaderRow(row);
		cellHeader = row.createCell(25, "TITLE");
		this.applyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(10, "PHASE");
		this.applyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(20, "TARGETS");
		this.applyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(25, "LOCATIONS");
		this.applyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(20, "NCT ID");
		
		this.applyHeaderFormatting(cellHeader, defaultFont);
		
		boolean greyBackground = false;
		for (BiomarkerTrialsRow item : report.getClinicalTrials()) {
			Color color = Color.WHITE;
			if (greyBackground) {
				color = FinalReportTemplateConstants.BACKGROUND_LIGHT_GRAY;
			}
			row = table.createRow(12);
			Cell<PDPage> cell = row.createCell(25, item.getTitle());
			this.applyCellFormatting(cell, defaultFont, color);
			cell = row.createCell(10, item.getPhase());
			this.applyCellFormatting(cell, defaultFont, color);
			cell = row.createCell(20, item.getBiomarker());
			this.applyCellFormatting(cell, defaultFont, color);
			cell = row.createCell(25, item.getPi() + "<br/>" + item.getDept());
			this.applyCellFormatting(cell, defaultFont, color);
			cell = row.createCell(25, item.getNctid());
			links.add(new Link(item.getNctid(), "https://clinicaltrials.gov/ct2/show/" + item.getNctid()));
			this.applyCellFormatting(cell, defaultFont, color);
			this.applyLinkCellFormatting(cell);
			greyBackground = !greyBackground;
		}
		latestYPosition = table.draw() - 20;
	}
	
	private void applyHeaderFormatting(Cell<PDPage> cell, float defaultFont) {
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE_BOLD);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setValign(VerticalAlignment.MIDDLE);
		cell.setFontSize(defaultFont);
		cell.setTextColor(Color.BLACK);
		cell.setFillColor(FinalReportTemplateConstants.BACKGROUND_GRAY);
		cell.setTopBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
		cell.setBottomBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
		cell.setLeftBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
		cell.setRightBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
	}
	
	private void applyTitleHeaderFormatting(Cell<PDPage> cell) {
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setFontBold(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setValign(VerticalAlignment.MIDDLE);
		cell.setFontSize(FinalReportTemplateConstants.TITLE_TEXT_FONT_SIZE);
		cell.setTextColor(Color.BLACK);
		cell.setTopBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
		cell.setBottomBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
		cell.setLeftBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
		cell.setRightBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
	}
	
	private void applyCellFormatting(Cell<PDPage> cell, float defaultFont, Color color) {
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setValign(VerticalAlignment.TOP);
		cell.setFontSize(defaultFont);
		cell.setTextColor(Color.BLACK);
		cell.setBottomPadding(10);
		if (color.equals(Color.WHITE)) {
			cell.setTopBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
			cell.setBottomBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
			cell.setLeftBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
			cell.setRightBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
		}
		else {
			cell.setTopBorderStyle(FinalReportTemplateConstants.LIGHT_GRAY_BORDER_THIN);
			cell.setBottomBorderStyle(FinalReportTemplateConstants.LIGHT_GRAY_BORDER_THIN);
			cell.setLeftBorderStyle(FinalReportTemplateConstants.LIGHT_GRAY_BORDER_THIN);
			cell.setRightBorderStyle(FinalReportTemplateConstants.LIGHT_GRAY_BORDER_THIN);
		}
		cell.setFillColor(color);
	}
	
	private void applyNavigationCellFormatting(Cell<PDPage> cell, Color fillColor) {
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setAlign(HorizontalAlignment.CENTER);
		cell.setValign(VerticalAlignment.MIDDLE);
		cell.setFontSize(FinalReportTemplateConstants.DEFAULT_TEXT_FONT_SIZE);
		cell.setTextColor(Color.BLACK);
		cell.setTopBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
		cell.setBottomBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
		cell.setLeftBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
		cell.setRightBorderStyle(FinalReportTemplateConstants.NO_BORDER_THIN);
		cell.setFillColor(fillColor);
	}
	
	private void applyLinkCellFormatting(Cell<PDPage> cell) {
		cell.setTextColor(FinalReportTemplateConstants.LINK_ANSWER_GREEN);
	}
	
	private void createClinicalSignificanceTables() throws IOException {
		List<Map<String, GeneVariantAndAnnotation>> clinicalSignifanceTables = new ArrayList<Map<String, GeneVariantAndAnnotation>>();
		clinicalSignifanceTables.add(report.getSnpVariantsStrongClinicalSignificance());
		clinicalSignifanceTables.add(report.getSnpVariantsPossibleClinicalSignificance());
		clinicalSignifanceTables.add(report.getSnpVariantsUnknownClinicalSignificance());
		
		String[] tableTitles = new String[] {"VARIANTS OF STRONG CLINICAL SIGNIFICANCE",
		                                  "VARIANTS OF POSSIBLE CLINICAL SIGNIFICANCE",
		                                  "VARIANTS OF UNKNOWN CLINICAL SIGNIFICANCE"};
		int counter = 0;
		for (Map<String, GeneVariantAndAnnotation> table : clinicalSignifanceTables) {
			
//			PDPageContentStream contentStream = new PDPageContentStream(mainDocument, currentPage,
//					PDPageContentStream.AppendMode.APPEND, true);
//			PDStreamUtils.write(contentStream, tableTitles[counter], FinalReportTemplateConstants.MAIN_FONT_TYPE,
//					FinalReportTemplateConstants.TITLE_TEXT_FONT_SIZE,
//					FinalReportTemplateConstants.MARGINLEFT + 5, latestYPosition, Color.BLACK);
//			contentStream.close();
//			latestYPosition -= 20;
			boolean addLink = counter == 0;
			this.createAClinicalSignificanceTable(table, tableTitles[counter], addLink);
			counter++;
			
			
		}
	}
	
	
	private void createAClinicalSignificanceTable(Map<String, GeneVariantAndAnnotation> tableItems, String tableTitle, boolean addLink) throws IOException{
		this.updatePotentialNewPagePosition();
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		if (addLink) {
			links.add(new Link(FinalReportTemplateConstants.CLINICAL_SIGNIFICANCE_NAV, this.mainDocument.getNumberOfPages() - 1));
		}
		
		float defaultFont = FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE;
		
		BaseTable table = createNewTable(currentPage);
		List<CNVReport> items = report.getCnvs();
		
		//Title
		Row<PDPage> row = table.createRow(12); 
		table.addHeaderRow(row);
		Cell<PDPage> cellHeader = row.createCell(100, tableTitle);
		this.applyTitleHeaderFormatting(cellHeader);
		cellHeader.setFillColor(FinalReportTemplateConstants.CLIN_SIGNIFICANCE_COLOR);
		
		if (items == null) {
			row = table.createRow(12);
			Cell<PDPage> cell = row.createCell(100, "No data for this level");
			this.applyCellFormatting(cell, defaultFont, Color.WHITE);
		}
		else {
			//Headers
			row = table.createRow(12); 
			table.addHeaderRow(row);
			cellHeader = row.createCell(30, "VARIANT");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			cellHeader = row.createCell(70, "COMMENT");
			this.applyHeaderFormatting(cellHeader, defaultFont);
			
			boolean greyBackground = false;
			Color color = Color.WHITE;
			if (greyBackground) {
				color = FinalReportTemplateConstants.BACKGROUND_LIGHT_GRAY;
			}
			for (String variant : tableItems.keySet()) {
				GeneVariantAndAnnotation item = tableItems.get(variant);
				row = table.createRow(12);
				Cell<PDPage> cell = row.createCell(30, item.getGeneVariant());
				this.applyCellFormatting(cell, defaultFont, color);
				cell = row.createCell(70, item.getAnnotation());
				this.applyCellFormatting(cell, defaultFont, color);
				greyBackground = !greyBackground;
			}
		}

		latestYPosition = table.draw() - 20;
		
	}
	
	private void createCNVTable() throws IOException {
		this.updatePotentialNewPagePosition();
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		float defaultFont = FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE;
		
		BaseTable table = createNewTable(currentPage);
		List<CNVReport> items = report.getCnvs();
		
		//Title
		Row<PDPage> row = table.createRow(12); 
		table.addHeaderRow(row);
		Cell<PDPage> cellHeader = row.createCell(100, FinalReportTemplateConstants.CNV_TITLE);
		this.applyTitleHeaderFormatting(cellHeader);
		cellHeader.setFillColor(FinalReportTemplateConstants.CNV_COLOR);
		links.add(new Link(FinalReportTemplateConstants.CNV_TITLE_SHORT, this.mainDocument.getNumberOfPages() - 1));
		
		//Headers
		row = table.createRow(12); 
		table.addHeaderRow(row);
		cellHeader = row.createCell(25, "CHR:START-END");
		this.applyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(10, "COPY NB.");
		this.applyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(30, "GENES");
		this.applyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(35, "COMMENT");
		this.applyHeaderFormatting(cellHeader, defaultFont);
		
		if (items == null) {
			row = table.createRow(12);
			Cell<PDPage> cell = row.createCell(100, "No additional CNV");
			this.applyCellFormatting(cell, defaultFont, Color.WHITE);
		}
		else {
			boolean greyBackground = false;
			Color color = Color.WHITE;
			if (greyBackground) {
				color = FinalReportTemplateConstants.BACKGROUND_LIGHT_GRAY;
			}
			for (CNVReport item : items) {
				row = table.createRow(12);
				Cell<PDPage> cell = row.createCell(25, item.getChrom() + ":" +item.getStartFormatted() + "-" + item.getEndFormatted());
				this.applyCellFormatting(cell, defaultFont, color);
				cell = row.createCell(10, item.getCopyNumber() + "");
				this.applyCellFormatting(cell, defaultFont, color);
				cell.setAlign(HorizontalAlignment.RIGHT); //align numbers to the right
				cell = row.createCell(30, item.getGenes());
				this.applyCellFormatting(cell, defaultFont, color);
				cell = row.createCell(35, item.getComment());
				this.applyCellFormatting(cell, defaultFont, color);
				greyBackground = !greyBackground;
			}
		}

		latestYPosition = table.draw() - 20;
	}
	
	private void createTranslocationTable() throws IOException {
		this.updatePotentialNewPagePosition();
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		float defaultFont = FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE;
		
		BaseTable table = createNewTable(currentPage);
		List<TranslocationReport> items = report.getTranslocations();
		
		//Title
		Row<PDPage> row = table.createRow(12); 
		table.addHeaderRow(row);
		Cell<PDPage> cellHeader = row.createCell(100, FinalReportTemplateConstants.TRANSLOCATION_TITLE);
		this.applyTitleHeaderFormatting(cellHeader);
		cellHeader.setFillColor(FinalReportTemplateConstants.FTL_COLOR);
		links.add(new Link(FinalReportTemplateConstants.TRANSLOCATION_TITLE_SHORT, this.mainDocument.getNumberOfPages() - 1));
		
		
		//Headers
		row = table.createRow(12); 
		table.addHeaderRow(row);
		cellHeader = row.createCell(20, "FUSION NAME");
		this.applyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(10, "GENE1");
		this.applyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(10, "LAST EXON");
		this.applyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(10, "GENE2");
		this.applyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(10, "FIRST EXON");
		this.applyHeaderFormatting(cellHeader, defaultFont);
		cellHeader = row.createCell(40, "COMMENT");
		this.applyHeaderFormatting(cellHeader, defaultFont);
		
		boolean greyBackground = false;
		Color color = Color.WHITE;
		if (greyBackground) {
			color = FinalReportTemplateConstants.BACKGROUND_LIGHT_GRAY;
		}
		for (TranslocationReport item : items) {
			row = table.createRow(12);
			Cell<PDPage> cell = row.createCell(20, item.getFusionName());
			this.applyCellFormatting(cell, defaultFont, color);
			cell = row.createCell(10, item.getLeftGene());
			this.applyCellFormatting(cell, defaultFont, color);
			cell = row.createCell(10, "0");
			this.applyCellFormatting(cell, defaultFont, color);
			cell.setAlign(HorizontalAlignment.RIGHT); //align numbers to the right
			cell = row.createCell(10, item.getRightGene());
			this.applyCellFormatting(cell, defaultFont, color);
			cell = row.createCell(10, "0");
			this.applyCellFormatting(cell, defaultFont, color);
			cell.setAlign(HorizontalAlignment.RIGHT); //align numbers to the right
			cell = row.createCell(40, item.getComment());
			this.applyCellFormatting(cell, defaultFont, color);
			greyBackground = !greyBackground;
		}

		latestYPosition = table.draw() - 20;
	}
	
	private Row<PDPage> createRow(BaseTable table, String title, String value, float fontSize) throws IOException {
		return createRow(table, title, value, fontSize, FinalReportTemplateConstants.MAIN_FONT_TYPE);
	}

	private Row<PDPage> createRow(BaseTable table, String title, String value, float fontSize, PDFont font) throws IOException {
		if (title == null) {
			title = "";
		}
		if (value == null) {
			value = "";
		}
		float titleWidth = font.getStringWidth(title);
		float valueWidth = font.getStringWidth(value);
		float titleRatio = (float) titleWidth / (float) (valueWidth + titleWidth);
		if (value.split("-").length == 3 && titleRatio > 0.6) {
			titleRatio = 60; //leave 40% of space for dates to avoid line returns with long titles
		}
		else {
			titleRatio = Math.min(80, Math.max(20, 100 * titleRatio)); //avoid too small or too large
		}
		float valueRatio = 100 - titleRatio;
		Row<PDPage> row = table.createRow(12);
		Cell<PDPage> cell = row.createCell(titleRatio, title, HorizontalAlignment.LEFT, VerticalAlignment.TOP);
		cell.setFont(font);
		cell.setTextColor(Color.GRAY);
		cell.setFontSize(fontSize);
		cell = row.createCell(valueRatio, value, HorizontalAlignment.RIGHT, VerticalAlignment.TOP);
		cell.setFont(font);
		cell.setTextColor(Color.BLACK);
		cell.setFontSize(fontSize);
		return row;
	}
	
	private Cell<PDPage> createFooterCell(Row<PDPage> row, String text, HorizontalAlignment align, float widthPct) {
		Cell<PDPage> cell = row.createCell(widthPct, text);
		cell.setBorderStyle(null);
		cell.setAlign(align);
		cell.setFontSize(FinalReportTemplateConstants.ADDRESS_FONT_SIZE);
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setTextColor(FinalReportTemplateConstants.LIGHT_GRAY);
		return cell;
	}
	
	private void addFooters() throws IOException {
		int pageTotal = this.mainDocument.getNumberOfPages();
		float tableYPos = FinalReportTemplateConstants.MARGINBOTTOM;
		float tableWidth = pageWidthMinusMargins;
		for (int i = 0; i < pageTotal; i++) {
			PDPage currentPage = this.mainDocument.getPage(i);
			BaseTable table = new BaseTable(tableYPos, tableYPos, 0, tableWidth,
					FinalReportTemplateConstants.MARGINLEFT, mainDocument, currentPage, false, true);
			Row<PDPage> row = table.createRow(12);
			this.createFooterCell(row, "BICF Custom", HorizontalAlignment.LEFT, 20);
			this.createFooterCell(row, "MRN " + caseSummary.getMedicalRecordNumber() + " " + caseSummary.getPatientName(), HorizontalAlignment.CENTER, 60);
			this.createFooterCell(row, "page " + (i + 1) + "/" + pageTotal, HorizontalAlignment.RIGHT, 20);
			table.draw();
		}
	}
	
	private void addInformationAboutTheTest() throws IOException {
		// Title
		this.updatePotentialNewPagePosition();
		float tableWidth = pageWidthMinusMargins;
		PDPage currentPage = this.mainDocument.getPage(this.mainDocument.getNumberOfPages() - 1);
		BaseTable table = new BaseTable(latestYPosition, FinalReportTemplateConstants.MARGINTOP, FinalReportTemplateConstants.MARGINBOTTOM,
				tableWidth, FinalReportTemplateConstants.MARGINLEFT, mainDocument, currentPage, false, true);
		Cell<PDPage> cell = table.createRow(12).createCell(100, FinalReportTemplateConstants.DISCLAMER_TITLE);
		cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
		cell.setAlign(HorizontalAlignment.LEFT);
		cell.setFontSize(FinalReportTemplateConstants.DEFAULT_TEXT_FONT_SIZE);

		// Content
		for (String info : FinalReportTemplateConstants.ABOUT_THE_TEST) {
			Row<PDPage> row = table.createRow(12);
			cell = row.createCell(100, info);
			cell.setFont(FinalReportTemplateConstants.MAIN_FONT_TYPE);
			cell.setAlign(HorizontalAlignment.LEFT);
			cell.setFontSize(FinalReportTemplateConstants.SMALLER_TEXT_FONT_SIZE);
			cell.setTextColor(Color.BLACK);
			cell.setBottomPadding(10);
		}
		latestYPosition = table.draw();
	}
	
	public void saveTemp() throws IOException {
		mainDocument.save(tempFile);
		mainDocument.close();
	}
	
	public String createPDFLink(FileProperties fileProps) throws IOException {
		File target = new File(fileProps.getPdfFilesDir(), tempFile.getName());
		if (!target.exists()) {
			return null;
		}
		String random = RandomStringUtils.random(25, true, true);
		String linkName = random + ".pdf";
		File link = new File(fileProps.getPdfLinksDir(), linkName);
		Files.createSymbolicLink(link.toPath(), target.toPath());

		return linkName;
	}
	
	public void addLinks() throws IOException {
		this.saveTemp(); // save and close to a temp file
		mainDocument = PDDocument.load(tempFile);
		for (Link link : links) {
			this.addLink(link);
		}
	}

	/**
	 * Create a link as an annotation after the document is done being written. Each
	 * link object is added to links and then all links are created at the end
	 * 
	 * FIXME there could be an issue with finding the same string multiple times.
	 * All instances would have a link.
	 * 
	 * @param link
	 * @throws IOException
	 */
	public void addLink(Link link) throws IOException {

		PDBorderStyleDictionary borderULine = new PDBorderStyleDictionary();
		borderULine.setStyle(PDBorderStyleDictionary.STYLE_UNDERLINE);
		borderULine.setWidth(1);

		HyperLinkReplacer linker = new HyperLinkReplacer(link, link.getUrlLabel());
		// for now, scan all pages
		linker.setStartPage(0);
		linker.setEndPage(mainDocument.getNumberOfPages());

		Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
		linker.writeText(mainDocument, dummy);

		//now we should have a list of coordinates for each link
		for (LinkCoordinates coords : linker.getLinkCoords()) {
			PDAnnotationLink annotation = new PDAnnotationLink();
			annotation.setBorderStyle(borderULine);
			
			List<PDAnnotation> annotations = mainDocument.getPage(coords.getCurrentPageNb()).getAnnotations();
			annotations.add(annotation);
			
			PDRectangle position = new PDRectangle();
			position.setLowerLeftX(coords.getLowerLeftX());
			position.setLowerLeftY(this.pageHeight - coords.getLowerLeftY());
			position.setUpperRightX(coords.getUpperRightX());
			position.setUpperRightY(this.pageHeight - coords.getUpperRightY());
			annotation.setRectangle(position);
			
			if (link.getUrl() != null) {
				PDActionURI action = new PDActionURI();
				action.setURI(link.getUrl());
				annotation.setAction(action);
			}
			else if (link.getDestinationPageNb() != null) {
				PDActionGoTo gotoAction = new PDActionGoTo();
				PDPageFitRectangleDestination dest = new PDPageFitRectangleDestination();
				dest.setPage(mainDocument.getPage(link.getDestinationPageNb()));
				gotoAction.setDestination(dest);
				annotation.setAction(gotoAction);
			}
			
			
		}
		

	}
}
