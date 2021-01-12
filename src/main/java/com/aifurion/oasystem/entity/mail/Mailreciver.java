package com.aifurion.oasystem.entity.mail;


import com.aifurion.oasystem.entity.user.User;

import javax.persistence.*;




/**
 * @author: zzy
 * @description: 内部邮件收件人中间表
 * @date: 2021/1/12 10:39
 * @Param: null
 */
@Entity
@Table(name="aoa_mail_reciver")

public  class Mailreciver {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name="pk_id")
	private Long pkId;
	
	
	@ManyToOne
	@JoinColumn(name="mail_id")
	private Inmaillist mailId;//内部邮件id
	
	
	@ManyToOne
	@JoinColumn(name="mail_reciver_id")
	private User reciverId;//内部用户id
	
	@Column(name="is_read")
	private Boolean read=false;//是否已读
	
	@Column(name="is_star")
	private Boolean star=false;//是否星标
	
	@Column(name="is_del")
	private Boolean del=false;//是否真正删除
	
	public Boolean getDel() {
		return del;
	}

	public void setDel(Boolean del) {
		this.del = del;
	}

	public Mailreciver(){}
	
	public Long getPkId() {
		return pkId;
	}
	
	public void setPkId(Long pkId) {
		this.pkId = pkId;
	}

	
	

	public Boolean getStar() {
		return star;
	}

	public void setStar(Boolean star) {
		this.star = star;
	}

	public Inmaillist getMailId() {
		return mailId;
	}

	public void setMailId(Inmaillist mailId) {
		this.mailId = mailId;
	}

	public User getReciverId() {
		return reciverId;
	}

	public void setReciverId(User reciverId) {
		this.reciverId = reciverId;
	}

	public Boolean getRead() {
		return read;
	}

	public void setRead(Boolean read) {
		this.read = read;
	}

	@Override
	public String toString() {
		return "Mailreciver [pkId=" + pkId + ", mailId=" + mailId + ", read=" + read + ", star=" + star + ", del=" + del
				+ "]";
	}

	

	

	

	
	
	
}
