package com.bnifp.mufis.postservice.dto.input;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostLikeInput {
    private Long postId;
}
