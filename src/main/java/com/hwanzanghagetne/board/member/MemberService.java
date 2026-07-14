package com.hwanzanghagetne.board.member;


import com.hwanzanghagetne.board.exception.BusinessException;
import com.hwanzanghagetne.board.exception.ErrorCode;
import com.hwanzanghagetne.board.member.dto.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    public Long signup(String loginId, String password, String name, String nickname, String email) {

        if (memberRepository.findByLoginId(loginId).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_LOGIN_ID);
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

    public MemberResponse getProfile(String loginId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return new MemberResponse(member.getLoginId(), member.getName(), member.getNickname(), member.getEmail());
    }

    @Transactional
    public void updateProfile(String loginId, String nickname, String email) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        member.updateProfile(nickname, email);
    }

    @Transactional
    public void changePassword(String loginId, String currentPassword, String newPassword) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        String encoded = passwordEncoder.encode(newPassword);
        member.changePassword(encoded);
    }

    @Transactional
    public void withdraw(String loginId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        member.withdraw();
    }
}
