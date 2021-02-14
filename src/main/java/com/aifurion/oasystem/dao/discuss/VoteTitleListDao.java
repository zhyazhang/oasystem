package com.aifurion.oasystem.dao.discuss;

import com.aifurion.oasystem.entity.discuss.VoteList;
import com.aifurion.oasystem.entity.discuss.VoteTitles;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/18 13:40
 */
public interface VoteTitleListDao extends JpaRepository<VoteTitles, Long> {

	//根据投票id来找所有投票标题的集合
	List<VoteTitles> findByVoteList(VoteList voteList);
}
