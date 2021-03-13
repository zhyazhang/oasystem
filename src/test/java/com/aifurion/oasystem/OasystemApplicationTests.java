package com.aifurion.oasystem;

import com.aifurion.oasystem.dao.address.AddressDao;
import com.aifurion.oasystem.dao.notic.NoticeDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
class OasystemApplicationTests {

    @Autowired
    private NoticeDao noticeDao;


    @Test
    void contextLoads() {

        List<Map<String, Object>> directors = noticeDao.findMyNotice(1L);

        //directors.forEach(System.out::println);

        for (Map<String, Object> director : directors) {

            System.out.println(director.values());
        }




    }

}
