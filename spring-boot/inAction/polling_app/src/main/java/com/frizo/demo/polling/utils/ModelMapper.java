package com.frizo.demo.polling.utils;

import com.frizo.demo.polling.entity.Poll;
import com.frizo.demo.polling.entity.User;
import com.frizo.demo.polling.payload.ChoiceResponse;
import com.frizo.demo.polling.payload.PollResponse;
import com.frizo.demo.polling.payload.UserSummary;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModelMapper {

    /**
     * @param poll 投票項目
     * @param choiceVotesMap 選項的得票數 <choice_id, count>
     * @param creator 投票項目的建立者
     * @param userVote 訪問者的投的選項號碼
     * @return 回傳一個給 user 的投票資訊統計回應物件
     */
    public static PollResponse mapPollToPollResponse(Poll poll, Map<Long, Long> choiceVotesMap, User creator, Long userVote) {
        PollResponse pollResponse = new PollResponse();
        pollResponse.setId(poll.getId());
        pollResponse.setQuestion(poll.getQuestion());
        pollResponse.setCreationDateTime(poll.getCreatedAt());
        pollResponse.setExpirationDateTime(poll.getExpirationDateTime());
        Instant now = Instant.now();
        pollResponse.setExpired(poll.getExpirationDateTime().isBefore(now));
        List<ChoiceResponse> choiceResponses = poll.getChoices().stream()
                .map(choice -> {
                    ChoiceResponse choiceResp = new ChoiceResponse();
                    choiceResp.setId(choice.getId());
                    choiceResp.setText(choice.getText());
                    if(choiceVotesMap.containsKey(choice.getId())) {
                        choiceResp.setVoteCount(choiceVotesMap.get(choice.getId()));
                    }
                    return choiceResp;
                }).collect(Collectors.toList());
        pollResponse.setChoices(choiceResponses);

        UserSummary creatorSummary = new UserSummary(creator.getId(), creator.getUsername(), creator.getName());
        pollResponse.setCreatedBy(creatorSummary);

        if(userVote != null) {
            pollResponse.setSelectedChoice(userVote);
        }

        long totalVotes = pollResponse.getChoices().stream().mapToLong(ChoiceResponse::getVoteCount).sum();
        pollResponse.setTotalVotes(totalVotes);
        return pollResponse;
    }

}
