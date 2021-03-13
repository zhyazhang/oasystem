package com.aifurion.oasystem.dao.notic;

import com.aifurion.oasystem.entity.note.Note;
import com.aifurion.oasystem.entity.notice.NoticesList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/3/13 16:14
 */

@Repository
public interface NoticeDao extends JpaRepository<NoticesList, Long> {

     //默认根据置顶、修改时间排序


    @Query(value = "SELECT n.*,u.* FROM aoa_notice_list AS n LEFT JOIN aoa_notice_user_relation AS u " +
            "ON n.notice_id=u.relatin_notice_id WHERE u.relatin_user_id=?1 ORDER BY n.is_top DESC,u.is_read ASC ,n.modify_time DESC",nativeQuery = true)
	List<Map<String, Object>> findMyNotice(Long userId);

	//与上面一直，限制条数为5条



    @Query(value = "SELECT n.*,u.* FROM aoa_notice_list AS n LEFT JOIN aoa_notice_user_relation AS u ON n.notice_id=u.relatin_notice_id WHERE " +
            "u.relatin_user_id=?1 ORDER BY n.is_top DESC,u.is_read ASC ,n.modify_time DESC LIMIT 5",nativeQuery = true)
	List<Map<String, Object>> findMyNoticeLimit(Long userId);

	//进行逻辑判断，来根据那个排序，类型、状态、修改时间



}
