package com.aifurion.oasystem.dao.address;

import com.aifurion.oasystem.entity.note.Director;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/17 12:37
 */
public interface AddressDao extends JpaRepository<Director, Long> {
}
