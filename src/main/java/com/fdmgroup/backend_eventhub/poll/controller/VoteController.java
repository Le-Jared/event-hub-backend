package com.fdmgroup.backend_eventhub.poll.controller;

import com.fdmgroup.backend_eventhub.poll.model.Vote;
import com.fdmgroup.backend_eventhub.poll.service.PollService;
import com.fdmgroup.backend_eventhub.poll.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vote")
public class VoteController {
    @Autowired
    PollService pollService;
    @Autowired
    VoteService voteService;

    @PostMapping(path="/create")
    public ResponseEntity<Vote> createVote(
            @RequestParam("pollId") long pollId,
            @RequestParam("pollOptionId") long pollOptionId,
            @RequestParam("userDisplayName") String userDisplayName) {

        // create vote
        Vote vote = voteService.createVote(pollId, pollOptionId, userDisplayName);
        return ResponseEntity.status(HttpStatus.CREATED).body(vote);

    }

    @PostMapping(path="/change")
    public ResponseEntity<Vote> changeVote(
            @RequestParam("pollId") long pollId,
            @RequestParam("newPollOptionId") long newPollOptionId,
            @RequestParam("userDisplayName") String userDisplayName) {
        // change vote
        Vote vote = voteService.changeVote(pollId, newPollOptionId, userDisplayName);
        return ResponseEntity.status(HttpStatus.CREATED).body(vote);
    }
}
