package com.fdmgroup.backend_eventhub.modules.repository;

import com.fdmgroup.backend_eventhub.modules.model.VideoModule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IVideoRepository extends JpaRepository<VideoModule, Long> {

}
