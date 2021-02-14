package com.aifurion.oasystem.dao.role;

import com.aifurion.oasystem.entity.role.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/19 9:42
 */
public interface RoleDao extends JpaRepository<Role, Long> {

	@Query("select ro from Role as ro where ro.roleName like %?1%")
    Page<Role> findbyrolename(String val, Pageable pa);

}
