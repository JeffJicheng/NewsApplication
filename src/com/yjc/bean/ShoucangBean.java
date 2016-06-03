package com.yjc.bean;

import java.io.Serializable;
/**
 * 收藏bean
 * @author yjc Jeff.Yao
 *
 */
public class ShoucangBean implements Serializable{
	private int _id;
	private int news_id;//新闻的id
	private String title;//新闻的标题
	private String author;//新闻的来源
	private String time;//新闻的时间
	public int getNews_id() {
		return news_id;
	}
	public void setNews_id(int news_id) {
		this.news_id = news_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public ShoucangBean(int news_id, String title, String author, String time) {
		super();
		this.news_id = news_id;
		this.title = title;
		this.author = author;
		this.time = time;
	}
	@Override
	public String toString() {
		return "ShoucangBean [news_id=" + news_id + ", title=" + title
				+ ", author=" + author + ", time=" + time + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + _id;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + news_id;
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShoucangBean other = (ShoucangBean) obj;
		if (_id != other._id)
			return false;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (news_id != other.news_id)
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
	
}
