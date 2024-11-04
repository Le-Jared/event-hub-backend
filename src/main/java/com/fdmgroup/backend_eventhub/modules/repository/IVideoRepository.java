package com.fdmgroup.backend_eventhub.modules.repository;

import com.fdmgroup.backend_eventhub.modules.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IVideoRepository extends JpaRepository<Video, Long> {

}
