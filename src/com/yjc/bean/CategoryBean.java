package com.yjc.bean;

import java.io.Serializable;
/**
 * ¿‡–Õbean
 * @author yjc jeff.yao
 *
 */
public class CategoryBean implements Serializable{
	private int _id;
	private int category_id;
	private int order;
	private String text;
	private int isadd;
	public int getCategory_id() {
		return category_id;
	}
	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getIsadd() {
		return isadd;
	}
	public void setIsadd(int isadd) {
		this.isadd = isadd;
	}
	@Override
	public String toString() {
		return "CategoryBean [_id=" + _id + ", category_id=" + category_id
				+ ", order=" + order + ", text=" + text + ", isadd=" + isadd
				+ "]";
	}
	public CategoryBean(int category_id, int order, String text, int isadd) {
		super();
		this.category_id = category_id;
		this.order = order;
		this.text = text;
		this.isadd = isadd;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + _id;
		result = prime * result + category_id;
		result = prime * result + isadd;
		result = prime * result + order;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		CategoryBean other = (CategoryBean) obj;
		if (_id != other._id)
			return false;
		if (category_id != other.category_id)
			return false;
		if (isadd != other.isadd)
			return false;
		if (order != other.order)
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}
	
}
