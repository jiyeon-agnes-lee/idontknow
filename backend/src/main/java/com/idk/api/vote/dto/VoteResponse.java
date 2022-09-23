package com.idk.api.vote.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.idk.api.common.category.Category;
import com.idk.api.common.category.SubCategory;
import com.idk.api.vote.domain.entity.Ballot;
import com.idk.api.vote.domain.entity.Vote;
import lombok.*;

import java.time.LocalDateTime;

public class VoteResponse {
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class OnlyId {
        private Long id;

        public static VoteResponse.OnlyId build(Vote vote) {
            return VoteResponse.OnlyId.builder()
                    .id(vote.getId())
                    .build();
        }

    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Info {
        private Long voteId;
        private String category;
        private String subCategory;
        private String title;
        private Long userId;
        private String name;
        private int hitCount;
        private int commentCount;
        private String optionA;
        private int aCount;
        private String optionB;
        private int bCount;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd hh:mm", timezone = "Asia/Seoul")
        private LocalDateTime createdAt;
        private boolean status;
        private String result;
        private Long ballotId;
        private String voted;

        public static VoteResponse.Info build(Vote vote, Ballot ballot) {
            InfoBuilder builder = Info.builder()
                                            .voteId(vote.getId())
                                            .category(vote.getCategory().getName())
                                            .subCategory(vote.getSubCategory().getName())
                                            .title(vote.getTitle())
                                            .userId(vote.getUser().getId())
                                            .name(vote.getUser().getName())
                                            .hitCount(vote.getHitCount())
                                            .commentCount(vote.getCommentCount())
                                            .optionA(vote.getOptionA())
                                            .aCount(vote.getACount())
                                            .optionB(vote.getOptionB())
                                            .bCount(vote.getBCount())
                                            .createdAt(vote.getCreatedAt())
                                            .status(vote.isStatus())
                                            .result(vote.getResult());
            if (ballot != null) {
                builder.ballotId(ballot.getId())
                        .voted(ballot.getChoice());
            }
            return builder.build();
        }

    }
}