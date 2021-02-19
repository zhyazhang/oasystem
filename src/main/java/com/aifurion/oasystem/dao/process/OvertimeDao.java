package com.aifurion.oasystem.dao.process;

import com.aifurion.oasystem.entity.process.Overtime;
import com.aifurion.oasystem.entity.process.ProcessList;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/19 10:32
 */
public interface OvertimeDao extends PagingAndSortingRepository<Overtime, Long> {

	Overtime findByProId(ProcessList pro);

}
