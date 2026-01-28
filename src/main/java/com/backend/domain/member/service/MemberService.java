package com.backend.domain.member.service;

import com.backend.domain.member.entity.Member;
import com.backend.domain.member.join.MemberJoin;
import com.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member getMemberByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public Member join(MemberJoin memberJoin) {
        Member member = Member.builder()
                .username(memberJoin.getUsername())
                .password(memberJoin.getPassword())
                .email(memberJoin.getEmail())
                .build();
        memberRepository.save(member);

        return member;
        }


}
