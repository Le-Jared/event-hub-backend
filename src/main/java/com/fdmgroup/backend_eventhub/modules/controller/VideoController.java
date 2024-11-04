package com.fdmgroup.backend_eventhub.modules.controller;

import com.fdmgroup.backend_eventhub.modules.model.VideoModule;
import com.fdmgroup.backend_eventhub.modules.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/videos")
public class VideoController {
    //TODO: add methods to return a list of videos, as well as return the necessary
    // information for a single video when a request is received from the frontend

    private final String VIDEO_BASE_URL = "http://localhost:8080/encoded/";
    private final String THUMBNAIL_BASE_URL = "http://localhost:8080/thumbnails/";

    @Autowired
    VideoService videoService;

    @GetMapping
    private ResponseEntity<List<VideoModule>> getVideos() {
        // TODO: Implement pagination of videos instead of returning all videos
        // TODO: Decide which properties of the video need to be sent over to the frontend
        return ResponseEntity.ok(videoService.findAllVideos());
    }

    @GetMapping("/{id}")
    private ResponseEntity<VideoModule> getVideoById(@PathVariable("id") long videoId) {
        Optional<VideoModule> videoOptional = videoService.findVideoById(videoId);
        if ( videoOptional.isEmpty() ) {
            return ResponseEntity.notFound().build();
        } else {
            VideoModule videoModule = videoOptional.get();
            videoModule.setVideoURL(VIDEO_BASE_URL + videoModule.getVideoURL());
            videoModule.setThumbnailURL(THUMBNAIL_BASE_URL + videoModule.getThumbnailURL());
            return  ResponseEntity.ok(videoModule);
        }
    }
}
