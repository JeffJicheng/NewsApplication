package com.yjc.bean;

import java.io.Serializable;
/**
 * 新闻bean
 * @author yjc Jeff.Yao
 *
 */
public class NewsBean implements Serializable{
	private int _id;
	private int category_id;
	private int news_id;//新闻
	private String leixing;
	private String text;//html代码
	private String keyword;
	public int getCategory_id() {
		return category_id;
	}
	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}
	public int getNews_id() {
		return news_id;
	}
	public void setNews_id(int news_id) {
		this.news_id = news_id;
	}
	public String getLeixing() {
		return leixing;
	}
	public void setLeixing(String leixing) {
		this.leixing = leixing;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	@Override
	public String toString() {
		return "NFPLvBean [_id=" + _id + ", category_id=" + category_id
				+ ", news_id=" + news_id + ", leixing=" + leixing + ", text="
				+ text + ", keyword=" + keyword + "]";
	}
	public NewsBean(int category_id, int news_id, String leixing, String text,
			String keyword) {
		super();
		this.category_id = category_id;
		this.news_id = news_id;
		this.leixing = leixing;
		this.text = text;
		this.keyword = keyword;
	}
	
	
}
