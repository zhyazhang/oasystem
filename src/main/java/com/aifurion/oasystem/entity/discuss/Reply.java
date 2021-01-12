package com.aifurion.oasystem.entity.discuss;


import com.aifurion.oasystem.entity.user.User;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * @author: zzy
 * @description: 回复表
 * @date: 2021/1/12 10:36
 * @Param: null
 */

@Entity
@Table(name="aoa_reply_list")
public class Reply {

	@Id
	@Column(name="reply_id")
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long replyId;      
	
	@Column(name="replay_time")
	private Date replayTime;     //回复时间
	
	private String content;     //内容
	
	@ManyToOne
	@JoinColumn(name = "reply_user_id")
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "discuss_id")
	private Discuss discuss;
	
	@ManyToMany
	@JoinTable(
		name = "aoa_love_user",
		joinColumns = {
				@JoinColumn(name = "reply_id")
		},
		inverseJoinColumns = {
				@JoinColumn(name = "user_id")
		}
			)
	private Set<User> users;
	
	@OneToMany(mappedBy="reply",fetch= FetchType.LAZY,cascade= CascadeType.REMOVE)
	private Set<Comment> comments;
	
	
	
	public Set<Comment> getComments() {
		return comments;
	}

	public void setComments(Set<Comment> comments) {
		this.comments = comments;
	}

	public Long getReplyId() {
		return replyId;
	}

	public void setReplyId(Long replyId) {
		this.replyId = replyId;
	}

	public Date getReplayTime() {
		return replayTime;
	}

	public void setReplayTime(Date replayTime) {
		this.replayTime = replayTime;
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Discuss getDiscuss() {
		return discuss;
	}

	public void setDiscuss(Discuss discuss) {
		this.discuss = discuss;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	@Override
	public String toString() {
		return "Reply [replyId=" + replyId + ", replayTime=" + replayTime + ", content=" + content + "]";
	}

	public Reply(Date replayTime, String content, User user, Discuss discuss) {
		super();
		this.replayTime = replayTime;
		this.content = content;
		this.user = user;
		this.discuss = discuss;
	}

	public Reply() {
		
	}

}
