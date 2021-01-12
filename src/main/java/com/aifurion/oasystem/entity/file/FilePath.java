package com.aifurion.oasystem.entity.file;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

/**
 * @author: zzy
 * @description: TODO
 * @date: 2021/1/12 10:40
 * @Param: null
 */




@Entity
@Table(name = "aoa_file_path")
public class FilePath {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "path_id")
	private Long id;	//路径id
	
	@Column(name = "parent_id")
	private Long parentId;
	
	@Column(name = "path_name")
	private String pathName;
	
	@Column(name = "path_istrash")
	private Long pathIstrash = 0L;
	
	@Column(name = "path_user_id")
	private Long pathUserId;
	
	@OneToMany(mappedBy = "fpath")
	@JsonIgnore
	private List<FileList> fileList;

	public FilePath() {

	}
	
	public FilePath(Long parentId, String pathName) {
		super();
		this.parentId = parentId;
		this.pathName = pathName;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}
	
	public Long getPathIstrash() {
		return pathIstrash;
	}

	public void setPathIstrash(Long pathIstrash) {
		this.pathIstrash = pathIstrash;
	}

	public Long getPathUserId() {
		return pathUserId;
	}

	public void setPathUserId(Long pathUserId) {
		this.pathUserId = pathUserId;
	}

	public List<FileList> getFileList() {
		return fileList;
	}

	public void setFileList(List<FileList> fileList) {
		this.fileList = fileList;
	}

	@Override
	public String toString() {
		return "FilePath [id=" + id + ", parentId=" + parentId + ", pathName=" + pathName + ", pathIstrash="
				+ pathIstrash + ", pathUserId=" + pathUserId + "]";
	}

}
