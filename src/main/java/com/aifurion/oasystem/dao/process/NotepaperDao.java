package com.aifurion.oasystem.dao.process;

import com.aifurion.oasystem.entity.process.Notepaper;
import com.aifurion.oasystem.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/13 15:57
 */

@Repository
public interface NotepaperDao extends JpaRepository<Notepaper, Long> {

    //查找
	@Query(nativeQuery=true,value="SELECT * from aoa_notepaper n where n.notepaper_user_id=?1 ORDER BY n.create_time DESC LIMIT 0,5")
    List<Notepaper> findByUserIdOrderByCreateTimeDesc(long userid);

	// 根据用户找便签
	Page<Notepaper> findByUserIdOrderByCreateTimeDesc(User user, Pageable pa);

	// 根据用户找便签
	Page<Notepaper> findByUserIdOrderByCreateTimeDesc(long userid,Pageable page);

	/**
	 * 模糊查询
	 *
	 * @param baseKey
	 * @param page
	 * @return
	 */
	Page<Notepaper> findByTitleLikeOrderByCreateTimeDesc(String baseKey, Pageable page);

}
