package com.aifurion.oasystem.entity.process;


import com.aifurion.oasystem.entity.user.User;

import javax.persistence.*;
import java.util.Date;

/**
 * @author: zzy
 * @description: 离职表
 * @date: 2021/1/12 10:44
 * @Param: null
 */

@Entity
@Table(name="aoa_reviewed")
public class Reviewed {

	@Id
	@Column(name="reviewed_id")
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long reviewedId;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User userId;//审核人
	
	private String advice; //审核人意见
	
	private Long statusId;//审核人状态
	
	@Column(name="reviewed_time")
	private Date reviewedTime;//审核时间
	
	@ManyToOne
	@JoinColumn(name="pro_id")
	private ProcessList proId;
	
	@Column(name="del")
	private Boolean del=false;
	
	@Transient
	private String username;//传过来的审核人的名字
	

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Boolean getDel() {
		return del;
	}

	public void setDel(Boolean del) {
		this.del = del;
	}

	public ProcessList getProId() {
		return proId;
	}

	public void setProId(ProcessList proId) {
		this.proId = proId;
	}

	public Long getReviewedId() {
		return reviewedId;
	}

	public void setReviewedId(Long reviewedId) {
		this.reviewedId = reviewedId;
	}

	public User getUserId() {
		return userId;
	}

	public void setUserId(User userId) {
		this.userId = userId;
	}

	public String getAdvice() {
		return advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
	}

	public Long getStatusId() {
		return statusId;
	}

	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}

	public Date getReviewedTime() {
		return reviewedTime;
	}

	public void setReviewedTime(Date reviewedTime) {
		this.reviewedTime = reviewedTime;
	}

	@Override
	public String toString() {
		return "Reviewed [reviewedId=" + reviewedId + ", advice=" + advice + ", statusId=" + statusId
				+ ", reviewedTime=" + reviewedTime + ", del=" + del + ", username=" + username + "]";
	}

	
	
	
}
