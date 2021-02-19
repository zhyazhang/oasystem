package com.aifurion.oasystem.dao.process;

import com.aifurion.oasystem.entity.process.EvectionMoney;
import com.aifurion.oasystem.entity.process.Stay;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/19 10:23
 */
public interface StayDao extends PagingAndSortingRepository<Stay, Long> {

	List<Stay> findByEvemoney(EvectionMoney money);
}

