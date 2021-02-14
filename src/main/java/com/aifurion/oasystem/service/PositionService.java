package com.aifurion.oasystem.service;

import com.aifurion.oasystem.entity.user.Position;

import java.util.List;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/1/23 9:35
 */
public interface PositionService {


    List<Position> findAll();


    Position findOne(Long id);

    Position save(Position position);

    void delete(Long id);




}
