package com.aifurion.oasystem.dao.discuss;

import com.aifurion.oasystem.entity.discuss.Discuss;
import com.aifurion.oasystem.entity.discuss.Reply;
import com.aifurion.oasystem.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/18 13:28
 */
public interface ReplyDao extends JpaRepository<Reply, Long> {
	//根据讨论表的id来找所有的回复表,分页处理
	Page<Reply> findByDiscuss(Discuss discuss, Pageable pa);

	//根据讨论表和用户来查找，并分页处理
	Page<Reply> findByDiscussAndUser(Discuss discuss, User user, Pageable pa);

	List<Reply> findByDiscuss(Discuss discuss);
}
