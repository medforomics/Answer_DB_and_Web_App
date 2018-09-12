package utsw.bicf.answer.model.extmapping;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.User;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Annotation {
	

	@JsonProperty("_id")
	MongoDBId mongoDBId;
	
	//Name of the organization this annotation is from (eg. UTSW)
	String origin;
	String text;
	String caseId;
	String geneId;
	String variantId;
	Integer userId;
	String createdDate;
	String modifiedDate;
	@JsonIgnore
	LocalDateTime createdLocalDateTime;
	@JsonIgnore
	LocalDateTime modifiedLocalDateTime;
	String createdSince;
	String modifiedSince;
	Boolean isVisible = true;
	Boolean markedForDeletion = false;
	List<String> pmids;
	Boolean isTumorSpecific;
	Boolean isCaseSpecific;
	Boolean isVariantSpecific;
	Boolean isGeneSpecific;
	Boolean isLeftSpecific;
	Boolean isRightSpecific;
	String category;
	String fullName;
	String classification;
	String tier;
	List<String> nctids;
	String type;
	List<String> cnvGenes;
	Boolean isSelected;
	String breadth;
	String leftGene;
	String rightGene;
	
	public Annotation() {
		
	}
	
	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}


	public Boolean getIsVisible() {
		return isVisible;
	}

	public Boolean getMarkedForDeletion() {
		return markedForDeletion;
	}

	public MongoDBId getMongoDBId() {
		return mongoDBId;
	}

	public void setMongoDBId(MongoDBId mongoDBId) {
		this.mongoDBId = mongoDBId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public void setIsVisible(Boolean isVisible) {
		this.isVisible = isVisible;
	}

	public void setMarkedForDeletion(Boolean markedForDeletion) {
		this.markedForDeletion = markedForDeletion;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getGeneId() {
		return geneId;
	}

	public void setGeneId(String geneId) {
		this.geneId = geneId;
	}

	public String getVariantId() {
		return variantId;
	}

	public void setVariantId(String variantId) {
		this.variantId = variantId;
	}

	public List<String> getPmids() {
		return pmids;
	}

	public void setPmids(List<String> pmids) {
		this.pmids = pmids;
	}

	public Boolean getIsTumorSpecific() {
		return isTumorSpecific;
	}

	public void setIsTumorSpecific(Boolean isTumorSpecific) {
		this.isTumorSpecific = isTumorSpecific;
	}

	public Boolean getIsCaseSpecific() {
		return isCaseSpecific;
	}

	public void setIsCaseSpecific(Boolean isCaseSpecific) {
		this.isCaseSpecific = isCaseSpecific;
	}

	public Boolean getIsVariantSpecific() {
		return isVariantSpecific;
	}

	public void setIsVariantSpecific(Boolean isVariantSpecific) {
		this.isVariantSpecific = isVariantSpecific;
	}

	public Boolean getIsGeneSpecific() {
		return isGeneSpecific;
	}

	public void setIsGeneSpecific(Boolean isGeneSpecific) {
		this.isGeneSpecific = isGeneSpecific;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public String getTier() {
		return tier;
	}

	public void setTier(String tier) {
		this.tier = tier;
	}

	public List<String> getNctids() {
		return nctids;
	}

	public void setNctids(List<String> nctids) {
		this.nctids = nctids;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getCnvGenes() {
		return cnvGenes;
	}

	public void setCnvGenes(List<String> cnvGenes) {
		this.cnvGenes = cnvGenes;
	}

	public LocalDateTime getCreatedLocalDateTime() {
		return createdLocalDateTime;
	}

	public void setCreatedLocalDateTime(LocalDateTime createdLocalDateTime) {
		this.createdLocalDateTime = createdLocalDateTime;
	}

	public LocalDateTime getModifiedLocalDateTime() {
		return modifiedLocalDateTime;
	}

	public void setModifiedLocalDateTime(LocalDateTime modifiedLocalDateTime) {
		this.modifiedLocalDateTime = modifiedLocalDateTime;
	}

	public String getCreatedSince() {
		return createdSince;
	}

	public void setCreatedSince(String createdSince) {
		this.createdSince = createdSince;
	}

	public String getModifiedSince() {
		return modifiedSince;
	}

	public void setModifiedSince(String modifiedSince) {
		this.modifiedSince = modifiedSince;
	}

	/**
	 * After the object has been retrieved, use this method to
	 * populate the LocalDateTime objects, user info, etc.
	 * @param a
	 * @param list 
	 * @param modelDAO
	 */
	public static void init(Annotation a, List<MongoDBId> selectedAnnotationIds, ModelDAO modelDAO) {
		User annotationUser = modelDAO.getUserByUserId(a.getUserId());
		if (annotationUser != null) {
			a.setFullName(annotationUser.getFullName());
		}
		OffsetDateTime createdUTCDatetime = OffsetDateTime.parse(a.getCreatedDate(), DateTimeFormatter.ISO_DATE_TIME);
//		LocalDateTime createdLocalDateTime = createdUTCDatetime.toLocalDateTime().atZone(ZoneId.systemDefault()).
		a.setCreatedLocalDateTime(createdUTCDatetime.toLocalDateTime());
		OffsetDateTime modifiedUTCDatetime = OffsetDateTime.parse(a.getModifiedDate(), DateTimeFormatter.ISO_DATE_TIME);
//		LocalDateTime modifiedLocalDateTime = modifiedUTCDatetime.toLocalDateTime().atZone(ZoneId.systemDefault()).toLocalDateTime();
		a.setModifiedLocalDateTime(modifiedUTCDatetime.toLocalDateTime());
		OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
		
		
		Duration createdSince = Duration.between(createdUTCDatetime, now);
		if (createdSince.toDays() >= 1) {
			a.setCreatedSince(createdSince.toDays() + " days ago");
		}
		else if (createdSince.toHours() >= 1) {
			a.setCreatedSince(createdSince.toHours() + " hours ago");
		}
		else if (createdSince.toMinutes() >= 1) {
			a.setCreatedSince(createdSince.toMinutes() + " minutes ago");
		}
		else if (createdSince.toMillis() >= 1000) {
			a.setCreatedSince(createdSince.toMillis() / 1000 + " seconds ago");
		}
		else {
			a.setCreatedSince("just now");
		}
		
		Duration modifiedSince = Duration.between(modifiedUTCDatetime, now);
		if (modifiedSince.toDays() >= 1) {
			a.setModifiedSince(modifiedSince.toDays() + " days ago");
		}
		else if (modifiedSince.toHours() >= 1) {
			a.setModifiedSince(modifiedSince.toHours() + " hours ago");
		}
		else if (modifiedSince.toMinutes() >= 1) {
			a.setModifiedSince(modifiedSince.toMinutes() + " minutes ago");
		}
		else if (modifiedSince.toMillis() >= 1000) {
			a.setModifiedSince(modifiedSince.toMillis() / 1000 + " seconds ago");
		}
		else {
			a.setModifiedSince("just now");
		}
		
		//the list of selected annotations is not specific to the annotation but to the case/variant
		if (selectedAnnotationIds != null) {
			a.isSelected = selectedAnnotationIds.contains(a.getMongoDBId());
		}
		else {
			a.isSelected = false;
		}
	}

	public Boolean getIsSelected() {
		return isSelected;
	}

	public void setIsSelected(Boolean isSelected) {
		this.isSelected = isSelected;
	}

	public String getBreadth() {
		return breadth;
	}

	public void setBreadth(String breadth) {
		this.breadth = breadth;
	}

	public Boolean getIsLeftSpecific() {
		return isLeftSpecific;
	}

	public void setIsLeftSpecific(Boolean isLeftSpecific) {
		this.isLeftSpecific = isLeftSpecific;
	}

	public Boolean getIsRightSpecific() {
		return isRightSpecific;
	}

	public void setIsRightSpecific(Boolean isRightSpecific) {
		this.isRightSpecific = isRightSpecific;
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




	
}
