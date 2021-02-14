package com.aifurion.oasystem.dao.file;

import com.aifurion.oasystem.entity.file.FilePath;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/16 17:16
 */
@Repository
public interface FilePathdao extends PagingAndSortingRepository<FilePath, Long> {

	List<FilePath> findByParentId(Long parentId);

	List<FilePath> findByParentIdAndPathIstrash(Long parentId,Long istrash);

	FilePath findByPathNameAndParentId(String pathname,Long parentId);

	FilePath findByPathName(String pathname);

	List<FilePath> findByPathUserIdAndPathIstrash(Long userid,Long istrash);

	FilePath findByParentIdAndPathUserId(Long parentId,Long userid);

	List<FilePath> findByPathUserIdAndPathIstrashAndPathNameLikeAndParentIdNot(Long userid,Long istrash,String likefilename,Long userrootpathid);

}
