package spring.ordersystem.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.ordersystem.common.auth.JwtTokenProvider;
import spring.ordersystem.common.dto.CommonDto;
import spring.ordersystem.member.dto.*;
import spring.ordersystem.member.entity.Member;
import spring.ordersystem.member.service.MemberService;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/create")
    public ResponseEntity<?> createMember(@Valid @RequestBody MemberCreateDto memberCreateDto) {
        Long member = memberService.createMember(memberCreateDto);
        return new ResponseEntity<>(CommonDto.builder()
                .data(member)
                .status_code(HttpStatus.CREATED.value())
                .status_message("회원가입 성공")
                .build(), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginMember(@RequestBody MemberLoginReqDto memberLoginReqDto) {
        Member member = memberService.login(memberLoginReqDto);
        String accessToken = jwtTokenProvider.createAtToken(member);
//        rt토큰 생성
        String refreshToken = jwtTokenProvider.createRtToken(member);

        MemberLoginResDto resDto = MemberLoginResDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return new ResponseEntity<>(CommonDto.builder()
                .data(resDto)
                .status_code(HttpStatus.OK.value())
                .status_message("로그인 성공")
                .build(),
                HttpStatus.OK
        );
    }

//    rt룰 통한 at 갱신 요청
    @PostMapping("/refresh-at")
    public ResponseEntity<?> generateNewAt(@RequestBody RefreshTokenDto refreshTokenDto) {
//        rt 검증 로직
        Member member = jwtTokenProvider.validateRt(refreshTokenDto.getRefreshToken());
//        at 신규 생성
        String accessToken = jwtTokenProvider.createAtToken(member);

        MemberLoginResDto resDto = MemberLoginResDto.builder()
                .accessToken(accessToken)
                .build();

        return new ResponseEntity<>(CommonDto.builder()
                .data(resDto)
                .status_code(HttpStatus.OK.value())
                .status_message("로그인 성공")
                .build(),
                HttpStatus.OK
        );
    }

    @GetMapping("/myInfo")
    public ResponseEntity<?> getMyInfo(@RequestHeader("X-User-Email")String email) {
        MemberResDto myInfo = memberService.getMyInfo(email);
        return new ResponseEntity<>(CommonDto.builder()
                .data(myInfo)
                .status_code(HttpStatus.OK.value())
                .status_message("회원 정보 조회 성공")
                .build(), HttpStatus.OK);
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<?> withdrawMember(@RequestHeader("X-User-Email")String email) {
        memberService.withdrawMember(email);

        return new ResponseEntity<>(CommonDto.builder()
                .data("OK")
                .status_code(HttpStatus.OK.value())
                .status_message("회원 탈퇴 성공")
                .build(), HttpStatus.OK);
    }
}
