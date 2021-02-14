package com.aifurion.oasystem.service.impl;

import com.aifurion.oasystem.dao.user.DeptDao;
import com.aifurion.oasystem.dao.user.PositionDao;
import com.aifurion.oasystem.entity.user.Position;
import com.aifurion.oasystem.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/23 9:36
 */

@Service
public class PositionServiceImpl implements PositionService {


    @Autowired
    private PositionDao positionDao;



    @Override
    public List<Position> findAll() {

        List<Position> positions = (List<Position>) positionDao.findAll();

        return positions;
    }

    @Override
    public Position findOne(Long id) {

        Position position = positionDao.findById(id).get();


        return position;
    }

    @Override
    public Position save(Position position) {


        Position save = positionDao.save(position);
        return save;
    }

    @Override
    public void delete(Long id) {

        positionDao.deleteById(id);

    }
}
