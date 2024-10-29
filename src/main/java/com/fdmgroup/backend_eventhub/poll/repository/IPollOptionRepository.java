package com.fdmgroup.backend_eventhub.poll.repository;

import com.fdmgroup.backend_eventhub.poll.model.PollOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IPollOptionRepository extends JpaRepository<PollOption, Long> {
    List<PollOption> findByPollId(long pollId);
    Optional<PollOption> findByIdAndPollId(long id, long pollId);
}
