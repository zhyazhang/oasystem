package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.dao.discuss.DiscussDao;
import com.aifurion.oasystem.dao.discuss.VoteListDao;
import com.aifurion.oasystem.dao.discuss.VoteTitleListDao;
import com.aifurion.oasystem.dao.discuss.VoteTitlesUserDao;
import com.aifurion.oasystem.entity.discuss.Discuss;
import com.aifurion.oasystem.entity.discuss.VoteList;
import com.aifurion.oasystem.entity.discuss.VoteTitleUser;
import com.aifurion.oasystem.entity.discuss.VoteTitles;
import com.aifurion.oasystem.entity.user.User;
import com.aifurion.oasystem.service.DiscussService;
import com.aifurion.oasystem.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.*;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/18 13:39
 */


@Service
public class VoteServiceImpl implements VoteService {


    @Autowired
    DiscussDao discussDao;
	@Autowired
    DiscussService disService;
	@Autowired
    VoteListDao voteListDao;
	@Autowired
    VoteTitleListDao voteTitleDao;
	@Autowired
    VoteTitlesUserDao voteUserDao;
	@Autowired
	VoteTitleListDao voteTitlesDao;





    @Override
    public VoteList savaVoteList(VoteList voteList) {
       	return voteListDao.save(voteList);
    }

    @Override
    public VoteTitleUser savaVoteTitleUser(VoteTitleUser voteTitleUser) {
       return voteUserDao.save(voteTitleUser);
    }

    @Override
    public void voteServiceHandle(Model model, User user, Discuss discuss) {

        if(!Objects.isNull(discuss.getVoteList())){
			List<VoteTitles> voteTitles=voteTitlesDao.findByVoteList(discuss.getVoteList());
			List<Map<String, Object>> voteTitlesList=new ArrayList<>();
            for (VoteTitles voteTitle : voteTitles) {
                Map<String, Object> result = new HashMap<>();
                result.put("titleId", voteTitle.getTitleId());
                result.put("title", voteTitle.getTitle());
                result.put("users", voteUserDao.findByVoteTitles(voteTitle));
                result.put("color", voteTitle.getColor());
                result.put("count", voteUserDao.findByVoteTitles(voteTitle).size());
                result.put("countNum", voteUserDao.findByVoteId(voteTitle.getVoteList().getVoteId()).size());
                result.put("contain", !Objects.isNull(voteUserDao.findByVoteTitlesAndUser(voteTitle, user)));
                voteTitlesList.add(result);
            }
			VoteList vote=discuss.getVoteList();
			Date date=new Date();
			if(date.getTime()<vote.getStartTime().getTime()){
				model.addAttribute("dateType", 1);
			}else if(date.getTime()>vote.getEndTime().getTime()){
				model.addAttribute("dateType", 2);
			}else{
				model.addAttribute("dateType", 3);
			}
			model.addAttribute("voteTitles", voteTitlesList);
			model.addAttribute("voteList", discuss.getVoteList());
		}

    }
}
