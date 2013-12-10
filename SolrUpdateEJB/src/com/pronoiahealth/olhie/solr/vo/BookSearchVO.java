package com.pronoiahealth.olhie.solr.vo;

import java.io.Serializable;

public class BookSearchVO implements Serializable{

	private static final long serialVersionUID = -8739271565402230102L;

	public final static String BLANK = " ";
	
	private String bookId 				= null;
	
	private String bookTitle			= null;
	private String introduction			= null;
	private String keywords				= null;
	private String category				= null;
	private String coverName			= null;
	
	private String author 				= null;
	
	private String bookAssetDecription 	= null;
	
	private String bookAsset 			= null;
	
	private StringBuffer search 		= null;
	private boolean isData 				= false;
	
	public String getBookSearchString() {
		
		search = new StringBuffer();
		isData = false;
		
		addData(bookTitle);
		addData(introduction);
		addData(keywords);
		addData(category);
		addData(coverName);
		addData(author);
		addData(bookAssetDecription);
		addData(bookAsset);
		
		return search.toString();
		
	}
	
	private void addData(String s){
		if(s != null && s.length() > 0){
			
			if(isData == false){
				search.append(s);
			}
			else{
				search.append(BLANK + s);
			}
			
			isData = true;
		}
	}

	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public String getBookTitle() {
		return bookTitle;
	}

	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCoverName() {
		return coverName;
	}

	public void setCoverName(String coverName) {
		this.coverName = coverName;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getBookAssetDecription() {
		return bookAssetDecription;
	}

	public void setBookAssetDecription(String bookAssetDecription) {
		this.bookAssetDecription = bookAssetDecription;
	}

	public String getBookAsset() {
		return bookAsset;
	}

	public void setBookAsset(String bookAsset) {
		this.bookAsset = bookAsset;
	}
}
