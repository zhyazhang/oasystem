package com.aifurion.oasystem.dao.process;

import com.aifurion.oasystem.entity.process.EvectionMoney;
import com.aifurion.oasystem.entity.process.Traffic;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/19 10:22
 */
public interface TrafficDao  extends PagingAndSortingRepository<Traffic, Long> {

	List<Traffic> findByEvection(EvectionMoney money);
}

