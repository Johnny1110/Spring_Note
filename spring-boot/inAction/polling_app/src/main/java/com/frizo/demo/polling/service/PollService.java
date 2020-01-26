package com.frizo.demo.polling.service;

import com.frizo.demo.polling.entity.Poll;
import com.frizo.demo.polling.payload.PagedResponse;
import com.frizo.demo.polling.payload.PollRequest;
import com.frizo.demo.polling.payload.PollResponse;
import com.frizo.demo.polling.payload.VoteRequest;
import com.frizo.demo.polling.security.UserPrincipal;

public interface PollService {
    PagedResponse<PollResponse> getAllPolls(UserPrincipal currentUser, int from, int to);

    PagedResponse<PollResponse> getPollsCreatedBy(String username, UserPrincipal currentUser, int from, int to);

    PagedResponse<PollResponse> getPollsVotedBy(String username, UserPrincipal currentUser, int page, int size);

    Poll createPoll(PollRequest pollRequest);

    PollResponse getPollById(Long pollId, UserPrincipal currentUser);

    PollResponse castVoteAndGetUpdatedPoll(Long pollId, VoteRequest voteRequest, UserPrincipal currentUser);

    Long countByCreatedBy(Long id);
}
