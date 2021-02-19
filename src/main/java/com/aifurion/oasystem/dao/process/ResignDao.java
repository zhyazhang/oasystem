package com.aifurion.oasystem.dao.process;

import com.aifurion.oasystem.entity.process.ProcessList;
import com.aifurion.oasystem.entity.process.Resign;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/19 10:34
 */
public interface ResignDao extends PagingAndSortingRepository<Resign, Long> {

	Resign findByProId(ProcessList process);

}
