package com.ll.auth.domain.member.member.controller;

import com.ll.auth.domain.member.member.dto.MemberDto;
import com.ll.auth.domain.member.member.entity.Member;
import com.ll.auth.domain.member.member.service.MemberService;
import com.ll.auth.global.exceptions.ServiceException;
import com.ll.auth.global.rsData.RsData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class ApiV1MemberController {
    private final MemberService memberService;
    private final HttpServletRequest request;

    private Member checkAuthentication() {
        String credentials = request.getHeader("Authorization");
        String apiKey = credentials.substring("Bearer ".length());

        Optional<Member> opActor = memberService.findByApiKey(apiKey);

        if (opActor.isEmpty())
            throw new ServiceException("401-1", "비밀번호가 일치하지 않습니다.");

        return opActor.get();
    }

    record MemberJoinReqBody(
            @NotBlank
            @Length(min = 4)
            String username,
            @NotBlank
            @Length(min = 4)
            String password,
            @NotBlank
            @Length(min = 2)
            String nickname
    ) {
    }

    @PostMapping("/join")
    public RsData<MemberDto> join(
            @RequestBody @Valid MemberJoinReqBody reqBody
    ) {
        Member member = memberService.join(reqBody.username, reqBody.password, reqBody.nickname);

        return new RsData<>(
                "201-1",
                "%s님 환영합니다.".formatted(member.getNickname()),
                new MemberDto(member));
    }

    record MemberLoginReqBody(
            @NotBlank
            @Length(min = 4)
            String username,
            @NotBlank
            @Length(min = 4)
            String password
    ) {
    }

    record MemberLoginResBody(
            MemberDto item,
            String apiKey
    ) {
    }

    @PostMapping("/login")
    public RsData<MemberLoginResBody> login(
            @RequestBody @Valid MemberLoginReqBody reqBody
    ) {
        Member member = memberService.findByUsername(reqBody.username)
                .orElseThrow(() -> new ServiceException("401-1", "해당 회원은 존재하지 않습니다."));

        if (!member.getPassword().equals(reqBody.password)) {
            throw new ServiceException("401-2", "비밀번호가 일치하지 않습니다.");
        }

        String apiKey = member.getApiKey();

        return new RsData<>(
                "201-1",
                "%s님 환영합니다.".formatted(member.getNickname()),
                new MemberLoginResBody(
                        new MemberDto(member),
                        apiKey
                )
        );
    }

    @GetMapping("/me")
    public MemberDto me() {
        Member actor = checkAuthentication();

        return new MemberDto(actor);
    }
}