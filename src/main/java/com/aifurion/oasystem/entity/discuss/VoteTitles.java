package com.aifurion.oasystem.entity.discuss;

import javax.persistence.*;
import java.util.Set;



/**
 * @author: zzy
 * @description: TODO
 * @date: 2021/1/12 10:36
 * @Param: null
 */


@Entity
@Table(name="aoa_voteTitles")
public class VoteTitles{
		
	@Id
	@Column(name="title_id")
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private  Long titleId;

	private String  title;				//投票标题
	
	private String color;				//进度条颜色
	
	@OneToMany(mappedBy="voteTitles",fetch= FetchType.LAZY,cascade= CascadeType.ALL)
	private Set<VoteTitleUser> voteTitleUsers;
	
	@ManyToOne
	@JoinColumn(name = "vote_id")
	private VoteList voteList;			//关联投标表      投票id

	
	public Long getTitleId() {
		return titleId;
	}

	public void setTitleId(Long titleId) {
		this.titleId = titleId;
	}


	public String getTitle() {
		return title;
	}

	public VoteList getVoteList() {
		return voteList;
	}

	public void setVoteList(VoteList voteList) {
		this.voteList = voteList;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public VoteTitles() {
		super();
	}

	@Override
	public String toString() {
		return "VoteTitles [titleId=" + titleId + ", title=" + title + ", color=" + color + "]";
	}
	
	
	
	
}
