package com.ll.auth.domain.post.comment.entity;

import com.ll.auth.domain.member.member.entity.Member;
import com.ll.auth.domain.post.post.entity.Post;
import com.ll.auth.global.jpa.entity.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostComment extends BaseTime {
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member author;

    @Column(columnDefinition = "TEXT")
    private String content;
}
