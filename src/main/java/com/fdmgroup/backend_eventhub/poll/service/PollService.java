package com.fdmgroup.backend_eventhub.poll.service;

import com.fdmgroup.backend_eventhub.authenticate.model.Account;
import com.fdmgroup.backend_eventhub.authenticate.repository.AccountRepository;
import com.fdmgroup.backend_eventhub.poll.dto.PollOptionResponse;
import com.fdmgroup.backend_eventhub.poll.dto.EventPollResponse;
import com.fdmgroup.backend_eventhub.poll.model.Poll;
import com.fdmgroup.backend_eventhub.poll.model.PollOption;
import com.fdmgroup.backend_eventhub.poll.model.Vote;
import com.fdmgroup.backend_eventhub.eventsession.model.Event;
import com.fdmgroup.backend_eventhub.poll.repository.IPollOptionRepository;
import com.fdmgroup.backend_eventhub.poll.repository.IPollRepository;
import com.fdmgroup.backend_eventhub.poll.repository.IVoteRepository;
import com.fdmgroup.backend_eventhub.eventsession.repository.IEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PollService {
    @Autowired
    IEventRepository eventRepository;
    @Autowired
    IPollRepository pollRepository;
    @Autowired
    IPollOptionRepository pollOptionRepository;
    @Autowired
    IVoteRepository voteRepository;
    @Autowired
    AccountRepository accountRepository;

    public Poll createPoll(String code, String question) {
        Optional<Event> event = eventRepository.findByCode(code);

        if (event.isPresent()) {
            Poll poll = new Poll();

            poll.setQuestion(question);
            poll.setEvent(event.get());

            return pollRepository.save(poll);
        } else {
            throw new RuntimeException("Event not found");
        }
    }

    public PollOption createOption(Long pollId, String value, String description, String fileName) {
        Optional<Poll> poll = pollRepository.findById(pollId);

        if (poll.isPresent()) {
            PollOption option = new PollOption();
            option.setValue(value);
            option.setPoll(poll.get());
            // save description when not empty
            if (!description.isEmpty()) {
                option.setDescription(description);
            }
            PollOption newOption = pollOptionRepository.save(option);
            if (fileName == null) {
               return newOption;
            } else {
                // save image url in the db if there is any images
                String newImageUrl = newOption.getPoll().getId() + "-" + newOption.getId() + "-" + fileName;
                newOption.setImageUrl(newImageUrl);
                return pollOptionRepository.save(newOption);
            }

        } else {
            throw new RuntimeException("Poll not found");
        }
    }

    public List<PollOption> getPollOptionsByPoll(Long pollId) {
        return pollOptionRepository.findByPollId(pollId);
    }

    public EventPollResponse getEventPollResponse(String code, String userDisplayName) {
        EventPollResponse response = new EventPollResponse();
        Vote prevVote = new Vote();

        //query 1: return poll id of the watch party based on code?
        Optional<Poll> eventPoll = pollRepository.getPollIdByEventCode(code);
        if(eventPoll.isPresent()) {
            //check if user has casted vote on this poll previously
            Optional<Vote> prevVoteOptional = voteRepository.findByPollAndUserDisplayName(eventPoll.get(), userDisplayName);
            if (prevVoteOptional.isPresent()){
                prevVote = prevVoteOptional.get();
                response.setVoted(true);
            } else {
                prevVote = null;
            }
            response.setPollId(eventPoll.get().getId());
            response.setPollQuestion(eventPoll.get().getQuestion());

        } else {
            prevVote = null;
        }

        //query 2: return list of poll options based on poll id
        List<PollOption> pollOptions = pollOptionRepository.findByPollId(eventPoll.get().getId());
        if(pollOptions.size() >= 2) {
            List<PollOptionResponse> pollOptionResponses = new ArrayList<>();
            Vote finalPrevVote = prevVote;

            pollOptionResponses = pollOptions.stream().map(pollOption -> {
                        PollOptionResponse optionResponse = new PollOptionResponse();
                        optionResponse.setPollOptionId(pollOption.getId());
                        optionResponse.setValue(pollOption.getValue());
                        optionResponse.setDescription(pollOption.getDescription());
                        optionResponse.setImageUrl(pollOption.getImageUrl());
                        if(finalPrevVote != null && (finalPrevVote.getPollOption().getId() == pollOption.getId())) {
                            response.setSelectedPollOption(optionResponse);
                        }
                        return optionResponse;
                    }).collect(Collectors.toList());

            //query 3: return vote count for each poll option of the poll id
            List<Map<Long, Long>> voteCountList = voteRepository.getVoteCountByPollId(eventPoll.get().getId());
            for(int i=0; i<voteCountList.size(); i++){
                Map<Long,Long> voteCount = voteCountList.get(i);
                PollOptionResponse optionResponseWithVote = pollOptionResponses.get(i);
                optionResponseWithVote.setVoteCount(voteCount.get("vote_count"));
            }

            response.setPollOptionList(pollOptionResponses);
        } else {
            throw new RuntimeException("No poll option created for this poll");
        }

        return response;
    }

    public Poll updatePoll(Long accountID, Long pollId,  String question) {

        Optional<Account> account = accountRepository.findById(accountID);
        if (account.isPresent()) {
            Optional<Poll> poll = pollRepository.findById(pollId);
            if (poll.isPresent()) {
                Poll updatedPoll = poll.get();
                if (!question.equals(updatedPoll.getQuestion())) {
                    updatedPoll.setQuestion(question);
                    return pollRepository.save(updatedPoll);
                }
                return updatedPoll;
            } else {
                throw new RuntimeException("Poll not found");
            }

        } else {
            throw new RuntimeException("Account not found");
        }
    }

    public PollOption updateOption(Long accountID, Long pollOptionId, String value, String description, String fileName) {

        Optional<Account> account = accountRepository.findById(accountID);
        if (account.isPresent()) {
            Optional<PollOption> pollOption = pollOptionRepository.findById(pollOptionId);
            if (pollOption.isPresent()) {
                PollOption updatedPollOption = pollOption.get();
                boolean pollOptionChanged = false;
                if (!value.equals(updatedPollOption.getValue())) {
                    updatedPollOption.setValue(value);
                    pollOptionChanged = true;
                }

                if (description == null && updatedPollOption.getDescription() != null){
                    updatedPollOption.setDescription(null);
                    pollOptionChanged = true;
                } else if (description != null && !description.equals(updatedPollOption.getDescription())) {
                    updatedPollOption.setDescription(description);
                    pollOptionChanged = true;
                }

                if (fileName != null) {
                    // save image url in the db if there is any images
                    if (!fileName.equals(updatedPollOption.getImageUrl())) {
                        updatedPollOption.setImageUrl(fileName);
                        pollOptionChanged = true;
                    }
                } else {
                    // delete image url if the image is to be removed
                    if (updatedPollOption.getImageUrl() != null) {
                        updatedPollOption.setImageUrl(null);
                        pollOptionChanged = true;
                    }
                }
                return pollOptionChanged ? pollOptionRepository.save(updatedPollOption) : updatedPollOption;

            } else {
                throw new RuntimeException("Poll Option not found");
            }

        } else {
            throw new RuntimeException("Account not found");
        }
    }

    public void deletePollOption(Long pollOptionId) {
        pollOptionRepository.deleteById(pollOptionId);
    }
}
