package com.aifurion.oasystem.entity.task;

import javax.persistence.*;
import java.util.Date;


/**
 * @author: zzy
 * @description: 任务日志表
 * @date: 2021/1/12 10:47
 * @Param: null
 */

@Entity
@Table(name="aoa_task_logger")

public class Tasklogger {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	@Column(name="logger_id")
	private Long loggerId;//任务日志id主键
	
	@Column(name="create_time")
	private Date createTime;//任务日志创建时间
	
	@Column(name="logger_ticking")
	private String loggerTicking;//任务日志反馈内容
	
	
	@ManyToOne
	@JoinColumn(name="task_id")
	private Tasklist taskId;//任务id外键
	
	@Column(name="username")
	private String username;//任务日志生成人
	
	@Column(name="logger_statusid")
	private Integer loggerStatusid; //状态id
	
	
	
	public Tasklogger(){}

	public Long getLoggerId() {
		return loggerId;
	}

	public void setLoggerId(Long loggerId) {
		this.loggerId = loggerId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getLoggerTicking() {
		return loggerTicking;
	}

	public void setLoggerTicking(String loggerTicking) {
		this.loggerTicking = loggerTicking;
	}

	

	public Tasklist getTaskId() {
		return taskId;
	}

	public void setTaskId(Tasklist taskId) {
		this.taskId = taskId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	
	public Integer getLoggerStatusid() {
		return loggerStatusid;
	}

	public void setLoggerStatusid(Integer loggerStatusid) {
		this.loggerStatusid = loggerStatusid;
	}

	@Override
	public String toString() {
		return "Tasklogger [loggerId=" + loggerId + ", createTime=" + createTime + ", loggerTicking=" + loggerTicking
				+ ", username=" + username + ", loggerStatusid=" + loggerStatusid + "]";
	}

	

	
	
	
}
