package com.aifurion.oasystem.dao.address;

import com.aifurion.oasystem.entity.note.Director;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/17 12:37
 */

@Repository
public interface AddressDao extends JpaRepository<Director, Long> {




    @Query(value = "select u.director_users_id,d.director_id,d.user_id,u.catelog_name,d.companyname,d.user_name,d.sex,d.phone_number," +
            "d.email,d.image_path from  aoa_director d LEFT join aoa_director_users u on u.director_u_id = d.director_id where u.user_u_id=?1 " +
            "and u.director_u_id !='' and u.is_handle=1 and if(?2 !='ALL',d.pinyin like ?2,1=1) and if(?3 is not null and ?3 !='',u.catelog_name=?3,1=1) " +
            "and if(?4 !='',d.user_name like ?4 or d.phone_number like ?4 or d.companyname like ?4 or d.pinyin like ?4 or u.catelog_name like ?4,1=1) order by u.catelog_name" ,nativeQuery = true)
    List<Map<String, Object>> getOutDirector(Long userId, String pinyin, String outtype, String baseKey);
}
