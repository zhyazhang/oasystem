package com.aifurion.oasystem.dao.discuss;

import com.aifurion.oasystem.entity.discuss.VoteTitleUser;
import com.aifurion.oasystem.entity.discuss.VoteTitles;
import com.aifurion.oasystem.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/18 13:41
 */
public interface VoteTitlesUserDao extends JpaRepository<VoteTitleUser, Long> {

	//在用户投票的标题表中查找所有的同一标题的集合
	List<VoteTitleUser> findByVoteTitles(VoteTitles voteTitles);

	//在用户投票的标题表中查找所有的同一投票的集合
	List<VoteTitleUser> findByVoteId(Long voteId);

	VoteTitleUser findByVoteTitlesAndUser(VoteTitles voteTitles, User user);



}

