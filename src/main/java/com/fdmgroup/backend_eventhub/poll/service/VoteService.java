package com.fdmgroup.backend_eventhub.poll.service;

import com.fdmgroup.backend_eventhub.authenticate.model.Account;
import com.fdmgroup.backend_eventhub.authenticate.repository.AccountRepository;
import com.fdmgroup.backend_eventhub.poll.model.Poll;
import com.fdmgroup.backend_eventhub.poll.model.PollOption;
import com.fdmgroup.backend_eventhub.poll.model.Vote;
import com.fdmgroup.backend_eventhub.poll.repository.IPollOptionRepository;
import com.fdmgroup.backend_eventhub.poll.repository.IPollRepository;
import com.fdmgroup.backend_eventhub.poll.repository.IVoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VoteService {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    IPollRepository pollRepository;
    @Autowired
    IPollOptionRepository pollOptionRepository;
    @Autowired
    IVoteRepository voteRepository;

    public Vote createVote(Long pollId, Long pollOptionId, String userDisplayName) {
        Optional<Poll> poll = pollRepository.findById(pollId);
        Optional<PollOption> pollOption = pollOptionRepository.findById(pollOptionId);

        // check if the poll, poll option and account is available
        if (poll.isEmpty()) {
            throw new RuntimeException("Poll not found");
        } if (pollOption.isEmpty()) {
            throw new RuntimeException("Poll Option not found");
        }

        //check if user has casted vote on this poll previously
        Optional<Vote> prevVote = voteRepository.findByPollAndUserDisplayName(poll.get(), userDisplayName);

        if (prevVote.isPresent()) {
            throw new RuntimeException("User already voted on this poll");
        }

        Vote vote = new Vote();
        vote.setPoll(poll.get());
        vote.setPollOption(pollOption.get());
        vote.setUserDisplayName(userDisplayName);

        return voteRepository.save(vote);
    }

    public Vote changeVote(long pollId, long newPollOptionId, String userDisplayName) {
        // find present vote of the user on the specific poll
        Optional<Poll> poll = pollRepository.findById(pollId);
        if (poll.isEmpty()) {
            throw new RuntimeException("Poll not found");
        }
        Optional<Vote> prevVote = voteRepository.findByPollAndUserDisplayName(poll.get(), userDisplayName);
        if (prevVote.isPresent()) {
            Vote newVote = prevVote.get();
            Optional<PollOption> pollOption = pollOptionRepository.findByIdAndPollId(newPollOptionId, pollId);
            if (pollOption.isPresent()) {
                // update new poll option if it exists
                newVote.setPollOption(pollOption.get());
                return voteRepository.save(newVote);
            } else {
                throw new RuntimeException("Poll Option does not exist in this poll");
            }

        } else {
            throw new RuntimeException("Vote not yet done by user in this poll");
        }
    }
}
