package com.hwanzanghagetne.board.member;

import com.hwanzanghagetne.board.member.dto.ChangePasswordRequest;
import com.hwanzanghagetne.board.member.dto.LoginRequest;
import com.hwanzanghagetne.board.member.dto.MemberResponse;
import com.hwanzanghagetne.board.member.dto.SignupRequest;
import com.hwanzanghagetne.board.member.dto.UpdateProfileRequest;
import com.hwanzanghagetne.board.post.PostService;
import com.hwanzanghagetne.board.post.dto.PostResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final PostService postService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<Long> signup(@RequestBody SignupRequest request) {
        Long memberId = memberService.signup(
                request.loginId(),
                request.password(),
                request.name(),
                request.nickname(),
                request.email()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(memberId);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid LoginRequest request, HttpServletRequest httpRequest) {

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(request.loginId(), request.password());

        Authentication authentication = authenticationManager.authenticate(authToken);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<String> me(Authentication authentication) {
        return ResponseEntity.ok(authentication.getName());
    }

    @GetMapping("/me/profile")
    public MemberResponse getProfile(Authentication authentication) {
        return memberService.getProfile(authentication.getName());
    }

    @GetMapping("/me/posts")
    public Page<PostResponse> getMyPage(Authentication authentication, Pageable pageable) {
        return postService.getMyPosts(authentication.getName(), pageable);
    }

    @PutMapping("/me")
    public ResponseEntity<Void> updateProfile(Authentication authentication, @RequestBody @Valid UpdateProfileRequest request) {
        memberService.updateProfile(authentication.getName(), request.nickname(), request.email());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(Authentication authentication, @RequestBody @Valid ChangePasswordRequest request) {
        memberService.changePassword(authentication.getName(), request.currentPassword(), request.newPassword());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> withdraw(Authentication authentication, HttpServletRequest request) {
        memberService.withdraw(authentication.getName());

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }
}
