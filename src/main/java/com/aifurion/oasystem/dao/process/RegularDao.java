package com.aifurion.oasystem.dao.process;

import com.aifurion.oasystem.entity.process.ProcessList;
import com.aifurion.oasystem.entity.process.Regular;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/19 10:33
 */
public interface RegularDao extends PagingAndSortingRepository<Regular, Long> {

	Regular findByProId(ProcessList pro);

}

