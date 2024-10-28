package com.fdmgroup.backend_eventhub.videostream.repository;

import com.fdmgroup.backend_eventhub.videostream.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IVideoRepository extends JpaRepository<Video, Long> {

}
