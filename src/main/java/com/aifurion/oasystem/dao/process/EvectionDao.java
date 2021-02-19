package com.aifurion.oasystem.dao.process;

import com.aifurion.oasystem.entity.process.Evection;
import com.aifurion.oasystem.entity.process.ProcessList;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/19 10:29
 */
public interface EvectionDao extends PagingAndSortingRepository<Evection, Long> {

	Evection findByProId(ProcessList process);

}
