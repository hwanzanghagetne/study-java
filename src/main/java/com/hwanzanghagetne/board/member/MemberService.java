package com.hwanzanghagetne.board.member;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    public Long signup(String loginId, String password, String name, String nickname, String email) {

        if (memberRepository.findByLoginId(loginId).isPresent()) {
            throw new IllegalStateException();
        }
        String encoded = passwordEncoder.encode(password);
        Member member = Member.builder()
                .email(email)
                .loginId(loginId)
                .name(name)
                .nickname(nickname)
                .password(encoded)
                .build();

        Member savedMember = memberRepository.save(member);
        return savedMember.getId();

    }
}
