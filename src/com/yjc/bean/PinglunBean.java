package com.yjc.bean;

import java.io.Serializable;
/**
 * ÆÀÂÛbean
 * @author yjc Jeff.Yao
 *
 */
public class PinglunBean implements Serializable{
	private int pinglun_id;
	private String text;
	private String fromname;
	private String fromaddr;
	private int new_id;
	private String fromimg;
	public int getPinglun_id() {
		return pinglun_id;
	}
	public void setPinglun_id(int pinglun_id) {
		this.pinglun_id = pinglun_id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getFromname() {
		return fromname;
	}
	public void setFromname(String fromname) {
		this.fromname = fromname;
	}
	public String getFromaddr() {
		return fromaddr;
	}
	public void setFromaddr(String fromaddr) {
		this.fromaddr = fromaddr;
	}
	public int getNew_id() {
		return new_id;
	}
	public void setNew_id(int new_id) {
		this.new_id = new_id;
	}
	public String getFromimg() {
		return fromimg;
	}
	public void setFromimg(String fromimg) {
		this.fromimg = fromimg;
	}
	public PinglunBean(int pinglun_id, String text, String fromname,
			String fromaddr, int new_id, String fromimg) {
		super();
		this.pinglun_id = pinglun_id;
		this.text = text;
		this.fromname = fromname;
		this.fromaddr = fromaddr;
		this.new_id = new_id;
		this.fromimg = fromimg;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fromaddr == null) ? 0 : fromaddr.hashCode());
		result = prime * result + ((fromimg == null) ? 0 : fromimg.hashCode());
		result = prime * result
				+ ((fromname == null) ? 0 : fromname.hashCode());
		result = prime * result + new_id;
		result = prime * result + pinglun_id;
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
		PinglunBean other = (PinglunBean) obj;
		if (fromaddr == null) {
			if (other.fromaddr != null)
				return false;
		} else if (!fromaddr.equals(other.fromaddr))
			return false;
		if (fromimg == null) {
			if (other.fromimg != null)
				return false;
		} else if (!fromimg.equals(other.fromimg))
			return false;
		if (fromname == null) {
			if (other.fromname != null)
				return false;
		} else if (!fromname.equals(other.fromname))
			return false;
		if (new_id != other.new_id)
			return false;
		if (pinglun_id != other.pinglun_id)
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}
	
}
