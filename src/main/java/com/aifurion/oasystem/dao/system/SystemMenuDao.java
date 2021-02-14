package com.aifurion.oasystem.dao.system;

import com.aifurion.oasystem.entity.system.SystemMenu;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/12 12:21
 */

@Repository
public interface SystemMenuDao extends PagingAndSortingRepository<SystemMenu,Long> {
}
