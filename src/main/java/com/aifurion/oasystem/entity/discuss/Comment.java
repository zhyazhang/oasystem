package com.aifurion.oasystem.entity.discuss;


import com.aifurion.oasystem.entity.user.User;

import javax.persistence.*;
import java.util.Date;



/**
 * @author: zzy
 * @description: 评论表
 * @date: 2021/1/12 10:34
 * @Param: null
 */


@Entity
@Table(name="aoa_comment_list")
public class Comment {
	@Id
	@Column(name="comment_id")
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long commentId; 
	
	private Date time; //发布时间
	
	private String comment;//发布内容
	
	@ManyToOne
	@JoinColumn(name = "comment_user_id")
	private User user;
	
	
	@ManyToOne
	@JoinColumn(name = "reply_id")
	private Reply reply;
	
	
	
	public Reply getReply() {
		return reply;
	}

	public void setReply(Reply reply) {
		this.reply = reply;
	}

	public Long getCommentId() {
		return commentId;
	}

	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	

	@Override
	public String toString() {
		return "Comment [commentId=" + commentId + ", time=" + time + ", comment=" + comment + "]";
	}

	

	public Comment(Date time, String comment, User user, Reply reply) {
		super();
		this.time = time;
		this.comment = comment;
		this.user = user;
		this.reply = reply;
	}

	public Comment() {
		super();
		// TODO Auto-generated constructor stub
	} 
	
	
}
