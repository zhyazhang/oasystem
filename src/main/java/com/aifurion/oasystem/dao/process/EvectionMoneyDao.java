package com.aifurion.oasystem.dao.process;

import com.aifurion.oasystem.entity.process.EvectionMoney;
import com.aifurion.oasystem.entity.process.ProcessList;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/19 10:18
 */
public interface EvectionMoneyDao extends PagingAndSortingRepository<EvectionMoney, Long> {

	EvectionMoney findByProId(ProcessList pro);
}