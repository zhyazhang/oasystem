package com.aifurion.oasystem.dao.note;

import com.aifurion.oasystem.entity.note.Director;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/13 15:13
 */

@Repository
public interface DirectorDao extends PagingAndSortingRepository<Director, Long> {

}
