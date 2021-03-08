package com.aifurion.oasystem.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author ：zzy
 * @description：TODO
 * @date ：2021/3/8 19:03
 */
public interface NotepaperService {


    void deleteNotepaper(Long id);

    String upload(MultipartFile file);




}
