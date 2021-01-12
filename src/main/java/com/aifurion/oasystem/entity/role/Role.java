package com.aifurion.oasystem.entity.role;

import javax.persistence.*;



/**
 * @author: zzy
 * @description: 角色表
 * @date: 2021/1/12 10:45
 * @Param: null
 */
@Entity
@Table(name="aoa_role_")

public class Role {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name="role_id")
	private Long roleId;//角色id
	
	@Column(name="role_name")
	private String roleName;//角色名
	
	@Column(name="role_value")
	private Integer  roleValue;//角色权限值
	
	public Role(){}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public Integer getRoleValue() {
		return roleValue;
	}

	public void setRoleValue(Integer roleValue) {
		this.roleValue = roleValue;
	}

	@Override
	public String toString() {
		return "Role [roleId=" + roleId + ", roleName=" + roleName + ", roleValue=" + roleValue + "]";
	}

	public Role( String roleName, Integer roleValue) {
		super();
		
		this.roleName = roleName;
		this.roleValue = roleValue;
	}
	
	
}
