package com.aifurion.oasystem.dao.discuss;

import com.aifurion.oasystem.entity.discuss.Comment;
import com.aifurion.oasystem.entity.discuss.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/18 13:28
 */
public interface CommentDao extends JpaRepository<Comment, Long> {
	//根据回复表来找有关的所有评论
	List<Comment> findByReply(Reply reply);

	@Query("from Comment t where t.reply.replyId in (?1)")
	List<Comment> findComments(Long[] taskids);

}
