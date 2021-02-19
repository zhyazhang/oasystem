package com.aifurion.oasystem.dao.process;

import com.aifurion.oasystem.entity.process.Bursement;
import com.aifurion.oasystem.entity.process.DetailsBurse;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/2/19 10:19
 */
public interface DetailsBurseDao  extends PagingAndSortingRepository<DetailsBurse, Long> {

	List<DetailsBurse> findByBurs(Bursement bu);
}

