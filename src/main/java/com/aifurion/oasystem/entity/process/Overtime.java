package com.aifurion.oasystem.entity.process;

import javax.persistence.*;


/**
 * @author: zzy
 * @description: 加班表
 * @date: 2021/1/12 10:43
 * @Param: null
 */


@Table
@Entity(name="aoa_overtime")
public class Overtime {

	@Id
	@Column(name="overtime_id")
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long overtimeId;
	
	private Long typeId; //加班类型
	
	@OneToOne(cascade= CascadeType.ALL)
	@JoinColumn(name="pro_id")
	private ProcessList proId;
	
	@Column(name="personnel_advice")
	private String personnelAdvice;//人事部意见及说明
	
	@Column(name="manager_advice")
	private String managerAdvice;//经理意见及说明
	
	@Transient
	private String nameuser;
	
	

	public String getNameuser() {
		return nameuser;
	}

	public void setNameuser(String nameuser) {
		this.nameuser = nameuser;
	}

	public Long getOvertimeId() {
		return overtimeId;
	}

	public void setOvertimeId(Long overtimeId) {
		this.overtimeId = overtimeId;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public ProcessList getProId() {
		return proId;
	}

	public void setProId(ProcessList proId) {
		this.proId = proId;
	}
	
	

	public String getPersonnelAdvice() {
		return personnelAdvice;
	}

	public void setPersonnelAdvice(String personnelAdvice) {
		this.personnelAdvice = personnelAdvice;
	}

	public String getManagerAdvice() {
		return managerAdvice;
	}

	public void setManagerAdvice(String managerAdvice) {
		this.managerAdvice = managerAdvice;
	}

	@Override
	public String toString() {
		return "Overtime [overtimeId=" + overtimeId + ", typeId=" + typeId + ", personnelAdvice=" + personnelAdvice
				+ ", managerAdvice=" + managerAdvice + ", nameuser=" + nameuser + "]";
	}

	

	
	
	
}
