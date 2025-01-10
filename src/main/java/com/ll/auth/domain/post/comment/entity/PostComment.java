package com.ll.auth.domain.post.comment.entity;

import com.ll.auth.domain.member.member.entity.Member;
import com.ll.auth.domain.post.post.entity.Post;
import com.ll.auth.global.exceptions.ServiceException;
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

    public void modify(String content) {
        this.content = content;
    }

    public void checkActorCanModify(Member actor) {
        if (actor == null) throw new ServiceException("401-1", "로그인 후 이용해주세요.");

        if (actor.equals(author)) return;

        throw new ServiceException("403-2", "작성자만 댓글을 수정할 수 있습니다.");
    }

    public void checkActorCanDelete(Member actor) {
        if (actor == null) throw new ServiceException("401-1", "로그인 후 이용해주세요.");

        if (actor.isAdmin()) return;

        if (actor.equals(author)) return;

        throw new ServiceException("403-2", "작성자만 댓글을 삭제할 수 있습니다.");
    }
}
