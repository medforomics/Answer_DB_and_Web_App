package utsw.bicf.answer.security;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileProperties {

	String mdaFilesPath;
	File mdaFilesDir;
	String excelFilesPath;
	File excelFilesDir;
	String bamLinksPath;
	File bamLinksDir;
	String bamFilesPath;
	File bamFilesDir;
	String vcfLinksPath;
	File vcfLinksDir;
	String vcfFilesPath;
	File vcfFilesDir;
	String igvLinksPath;
	File igvLinksDir;
	String igvTemplateFilesPath;
	File igvTemplateFilesDir;
	String genePanelSearchUrl;
	String pdfFontPath;
	File pdfFontFile;
	String pdfFontBoldPath;
	File pdfFontBoldFile;
	String pdfFilesPath;
	File pdfFilesDir;
	String pdfLogoPath;
	File pdfLogoDir;
	String pdfLinksPath;
	File pdfLinksDir;
	String pdfNGSLogoName;
	String pdfUTSWLogoName;
	String pdfDraftWatermarkName;
	String pdfFinalizedFilesPath;
	File pdfFinalizedFilesDir;
	String pdfFinalizedFilesBackupPath;
	File pdfFinalizedFilesBackupDir;
	String imageLinksPath;
	File imageLinksDir;
	File imageFilesDir;
	String imageFilesPath;
	String pdfAddressPath;
	List<String> pdfAddressLines;
	
	public String getMdaFilesPath() {
		return mdaFilesPath;
	}
	public void setMdaFilesPath(String mdaFilesPath) {
		this.mdaFilesPath = mdaFilesPath;
	}
	public File getMdaFilesDir() {
		if (mdaFilesPath == null) {
			return null;
		}
		if (mdaFilesDir == null) {
			mdaFilesDir = new File(mdaFilesPath);
		}
		return mdaFilesDir;
	}
	public void setMdaFilesDir(File mdaFilesDir) {
		this.mdaFilesDir = mdaFilesDir;
	}
	public String getExcelFilesPath() {
		return excelFilesPath;
	}
	public void setExcelFilesPath(String excelFilesPath) {
		this.excelFilesPath = excelFilesPath;
	}
	public File getExcelFilesDir() {
		if (excelFilesPath == null) {
			return null;
		}
		if (excelFilesDir == null) {
			excelFilesDir = new File(excelFilesPath);
		}
		return excelFilesDir;
	}
	public void setExcelFilesDir(File excelFilesDir) {
		this.excelFilesDir = excelFilesDir;
	}
	public String getBamLinksPath() {
		return bamLinksPath;
	}
	public void setBamLinksPath(String bamLinksPath) {
		this.bamLinksPath = bamLinksPath;
	}
	public File getBamLinksDir() {
		if (bamLinksPath == null) {
			return null;
		}
		if (bamLinksDir == null) {
			bamLinksDir = new File(bamLinksPath);
		}
		return bamLinksDir;
	}
	public void setBamLinksDir(File bamLinksDir) {
		this.bamLinksDir = bamLinksDir;
	}
	public String getBamFilesPath() {
		return bamFilesPath;
	}
	public void setBamFilesPath(String bamFilesPath) {
		this.bamFilesPath = bamFilesPath;
	}
	public File getBamFilesDir() {
		if (bamFilesPath == null) {
			return null;
		}
		if (bamFilesDir == null) {
			bamFilesDir = new File(bamFilesPath);
		}
		return bamFilesDir;
	}
	public void setBamFilesDir(File bamFilesDir) {
		this.bamFilesDir = bamFilesDir;
	}
	public String getGenePanelSearchUrl() {
		return genePanelSearchUrl;
	}
	public void setGenePanelSearchUrl(String genePanelSearchUrl) {
		this.genePanelSearchUrl = genePanelSearchUrl;
	}
	public String getPdfFontPath() {
		return pdfFontPath;
	}
	public void setPdfFontPath(String pdfFontPath) {
		this.pdfFontPath = pdfFontPath;
	}
	public File getPdfFontFile() {
		if (pdfFontPath == null) {
			return null;
		}
		if (pdfFontFile == null) {
			pdfFontFile = new File(pdfFontPath);
		}
		return pdfFontFile;
	}
	public void setPdfFontFile(File pdfFontFile) {
		this.pdfFontFile = pdfFontFile;
	}
	public String getPdfFilesPath() {
		return pdfFilesPath;
	}
	public void setPdfFilesPath(String pdfFilesPath) {
		this.pdfFilesPath = pdfFilesPath;
	}
	public File getPdfFilesDir() {
		if (pdfFilesPath == null) {
			return null;
		}
		if (pdfFilesDir == null) {
			pdfFilesDir = new File(pdfFilesPath);
		}
		return pdfFilesDir;
	}
	public void setPdfFilesDir(File pdfFilesDir) {
		this.pdfFilesDir = pdfFilesDir;
	}
	public String getPdfLogoPath() {
		return pdfLogoPath;
	}
	public void setPdfLogoPath(String pdfLogoPath) {
		this.pdfLogoPath = pdfLogoPath;
	}
	public File getPdfLogoDir() {
		if (pdfLogoPath == null) {
			return null;
		}
		if (pdfLogoDir == null) {
			pdfLogoDir = new File(pdfLogoPath);
		}
		return pdfLogoDir;
	}
	public void setPdfLogoDir(File pdfLogoDir) {
		this.pdfLogoDir = pdfLogoDir;
	}
	public String getPdfLinksPath() {
		return pdfLinksPath;
	}
	public void setPdfLinksPath(String pdfLinksPath) {
		this.pdfLinksPath = pdfLinksPath;
	}
	public File getPdfLinksDir() {
		if (pdfLinksPath == null) {
			return null;
		}
		if (pdfLinksDir == null) {
			pdfLinksDir = new File(pdfLinksPath);
		}
		return pdfLinksDir;
	}
	public void setPdfLinksDir(File pdfLinksDir) {
		this.pdfLinksDir = pdfLinksDir;
	}
	public String getPdfNGSLogoName() {
		return pdfNGSLogoName;
	}
	public void setPdfNGSLogoName(String pdfNGSLogoName) {
		this.pdfNGSLogoName = pdfNGSLogoName;
	}
	public String getPdfUTSWLogoName() {
		return pdfUTSWLogoName;
	}
	public void setPdfUTSWLogoName(String pdfUTSWLogoName) {
		this.pdfUTSWLogoName = pdfUTSWLogoName;
	}
	public String getPdfDraftWatermarkName() {
		return pdfDraftWatermarkName;
	}
	public void setPdfDraftWatermarkName(String pdfDraftWatermarkName) {
		this.pdfDraftWatermarkName = pdfDraftWatermarkName;
	}
	public String getPdfFontBoldPath() {
		return pdfFontBoldPath;
	}
	public void setPdfFontBoldPath(String pdfFontBoldPath) {
		this.pdfFontBoldPath = pdfFontBoldPath;
	}
	public File getPdfFontBoldFile() {
		if (pdfFontBoldPath == null) {
			return null;
		}
		if (pdfFontBoldFile == null) {
			pdfFontBoldFile = new File(pdfFontBoldPath);
		}
		return pdfFontBoldFile;
	}
	public void setPdfFontBoldFile(File pdfFontBoldFile) {
		this.pdfFontBoldFile = pdfFontBoldFile;
	}
	public String getPdfFinalizedFilesPath() {
		return pdfFinalizedFilesPath;
	}
	public void setPdfFinalizedFilesPath(String pdfFinalizedFilesPath) {
		this.pdfFinalizedFilesPath = pdfFinalizedFilesPath;
	}
	public File getPdfFinalizedFilesDir() {
		if (pdfFinalizedFilesPath == null) {
			return null;
		}
		if (pdfFinalizedFilesDir == null) {
			pdfFinalizedFilesDir = new File(pdfFinalizedFilesPath);
		}
		return pdfFinalizedFilesDir;
	}
	public void setPdfFinalizedFilesDir(File pdfFinalizedFilesDir) {
		this.pdfFinalizedFilesDir = pdfFinalizedFilesDir;
	}
	public String getPdfFinalizedFilesBackupPath() {
		return pdfFinalizedFilesBackupPath;
	}
	public void setPdfFinalizedFilesBackupPath(String pdfFinalizedFilesBackupPath) {
		this.pdfFinalizedFilesBackupPath = pdfFinalizedFilesBackupPath;
	}
	public File getPdfFinalizedFilesBackupDir() {
		if (pdfFinalizedFilesBackupPath == null) {
			return null;
		}
		if (pdfFinalizedFilesBackupDir == null) {
			pdfFinalizedFilesBackupDir = new File(pdfFinalizedFilesBackupPath);
		}
		return pdfFinalizedFilesBackupDir;
	}
	public void setPdfFinalizedFilesBackupDir(File pdfFinalizedFilesBackupDir) {
		this.pdfFinalizedFilesBackupDir = pdfFinalizedFilesBackupDir;
	}
	public String getVcfLinksPath() {
		return vcfLinksPath;
	}
	public void setVcfLinksPath(String vcfLinksPath) {
		this.vcfLinksPath = vcfLinksPath;
	}
	public File getVcfLinksDir() {
		if (vcfLinksPath == null) {
			return null;
		}
		if (vcfLinksDir == null) {
			vcfLinksDir = new File(vcfLinksPath);
		}
		return vcfLinksDir;
	}
	public void setVcfLinksDir(File vcfLinksDir) {
		this.vcfLinksDir = vcfLinksDir;
	}
	public String getVcfFilesPath() {
		return vcfFilesPath;
	}
	public void setVcfFilesPath(String vcfFilesPath) {
		this.vcfFilesPath = vcfFilesPath;
	}
	public File getVcfFilesDir() {
		if (vcfFilesPath == null) {
			return null;
		}
		if (vcfFilesDir == null) {
			vcfFilesDir = new File(vcfFilesPath);
		}
		return vcfFilesDir;
	}
	public void setVcfFilesDir(File vcfFilesDir) {
		this.vcfFilesDir = vcfFilesDir;
	}
	public String getIgvLinksPath() {
		return igvLinksPath;
	}
	public void setIgvLinksPath(String igvLinksPath) {
		this.igvLinksPath = igvLinksPath;
	}
	public File getIgvLinksDir() {
		if (igvLinksPath == null) {
			return null;
		}
		if (igvLinksDir == null) {
			igvLinksDir = new File(igvLinksPath);
		}
		return igvLinksDir;
	}
	public void setIgvLinksDir(File igvLinksDir) {
		this.igvLinksDir = igvLinksDir;
	}
	public String getIgvTemplateFilesPath() {
		return igvTemplateFilesPath;
	}
	public void setIgvTemplateFilesPath(String igvTemplateFilesPath) {
		this.igvTemplateFilesPath = igvTemplateFilesPath;
	}
	public File getIgvTemplateFilesDir() {
		if (igvTemplateFilesPath == null) {
			return null;
		}
		if (igvTemplateFilesDir == null) {
			igvTemplateFilesDir = new File(igvTemplateFilesPath);
		}
		return igvTemplateFilesDir;
	}
	public void setIgvTemplateFilesDir(File igvTemplateFilesDir) {
		this.igvTemplateFilesDir = igvTemplateFilesDir;
	}
	
	public File getImageLinksDir() {
		if (imageLinksPath == null) {
			return null;
		}
		if (imageLinksDir == null) {
			imageLinksDir = new File(imageLinksPath);
		}
		return imageLinksDir;
	}
	public void setImageLinksDir(File imageLinksDir) {
		this.imageLinksDir = imageLinksDir;
	}
	public String getImageFilesPath() {
		return imageFilesPath;
	}
	public void setImageFilesPath(String imageFilesPath) {
		this.imageFilesPath = imageFilesPath;
	}
	public String getImageLinksPath() {
		return imageLinksPath;
	}
	public void setImageLinksPath(String imageLinksPath) {
		this.imageLinksPath = imageLinksPath;
	}
	public File getImageFilesDir() {
		if (imageFilesPath == null) {
			return null;
		}
		if (imageFilesDir == null) {
			imageFilesDir = new File(imageFilesPath);
		}
		return imageFilesDir;
	}
	public void setImageFilesDir(File imageFilesDir) {
		this.imageFilesDir = imageFilesDir;
	}
	public String getPdfAddressPath() {
		return pdfAddressPath;
	}
	public void setPdfAddressPath(String pdfAddressPath) {
		this.pdfAddressPath = pdfAddressPath;
	}
	public List<String> getPdfAddressLines() throws IOException {
		if (pdfAddressLines == null || pdfAddressLines.isEmpty()) {
			pdfAddressLines = Files.readAllLines(Paths.get(this.pdfAddressPath));
		}
		return pdfAddressLines;
	}
	public void setPdfAddressLines(List<String> pdfAddressLines) {
		this.pdfAddressLines = pdfAddressLines;
	}
	
}
