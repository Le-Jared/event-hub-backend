package com.fdmgroup.backend_eventhub.modules.service;

import com.fdmgroup.backend_eventhub.modules.model.VideoModule;
import com.fdmgroup.backend_eventhub.modules.repository.IVideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VideoService {

    @Autowired
    IVideoRepository videoRepository;

    public List<VideoModule> findAllVideos() {
        return videoRepository.findAll();
    }

    public Optional<VideoModule> findVideoById(long id) {
        return videoRepository.findById(id);
    }
}
