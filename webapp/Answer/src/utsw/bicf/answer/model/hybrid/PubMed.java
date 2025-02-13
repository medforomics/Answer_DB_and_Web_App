package utsw.bicf.answer.model.hybrid;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import utsw.bicf.answer.model.extmapping.pubmed.Author;
import utsw.bicf.answer.model.extmapping.pubmed.PubmedArticle;

public class PubMed {
	
	String title;
	String authors;
	String description;
	String pmid;
	String date;
	boolean addendum;
	
	
	public PubMed()  {
	}


	public PubMed(PubmedArticle article, String pmId) throws ParseException {
		List<String> authors = new ArrayList<String>();
		for (Author author : article.getMedlineCitation().getArticle().getAuthorList()) {
			authors.add(author.getPrettyPrint());
		}
		this.authors = authors.stream().collect(Collectors.joining(",")); //authors
		this.title = article.getMedlineCitation().getArticle().getArticleTitle();
		StringBuilder sb = new StringBuilder();
		this.description = sb.append(article.getMedlineCitation().getArticle().getJournal().getJournalIssue().getVolume()) //journal info
				.append("(")
				.append(article.getMedlineCitation().getArticle().getJournal().getJournalIssue().getIssue())
				.append(")").append(":")
			.append(article.getMedlineCitation().getArticle().getPagination().getMedlinePgn()).append(".") //pagination
			.toString();
		this.pmid = pmId;
		this.date = article.getMedlineCitation().getDateCompleted().getPrettyPrint();
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getAuthors() {
		return authors;
	}


	public void setAuthors(String authors) {
		this.authors = authors;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getPmid() {
		return pmid;
	}


	public void setPmid(String pmid) {
		this.pmid = pmid;
	}


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}


	public boolean isAddendum() {
		return addendum;
	}


	public void setAddendum(boolean addendum) {
		this.addendum = addendum;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		   if (obj == this) { return true; }
		   if (obj.getClass() != getClass()) {
		     return false;
		   }
		   PubMed rhs = (PubMed) obj;
		   return new EqualsBuilder()
		                 .append(pmid, rhs.pmid)
		                 .isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(pmid).toHashCode();
	}


}
