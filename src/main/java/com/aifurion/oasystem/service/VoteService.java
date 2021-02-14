package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.discuss.Discuss;
import com.aifurion.oasystem.entity.discuss.VoteList;
import com.aifurion.oasystem.entity.discuss.VoteTitleUser;
import com.aifurion.oasystem.entity.user.User;
import org.springframework.ui.Model;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/18 13:38
 */
public interface VoteService {


    VoteList savaVoteList(VoteList voteList);

    VoteTitleUser savaVoteTitleUser(VoteTitleUser voteTitleUser);

    void voteServiceHandle(Model model, User user, Discuss discuss);
}
