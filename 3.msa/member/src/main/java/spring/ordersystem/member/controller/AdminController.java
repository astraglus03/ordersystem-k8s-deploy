package spring.ordersystem.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.ordersystem.common.dto.CommonDto;
import spring.ordersystem.member.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final MemberService memberService;

    @GetMapping("/member/list")
    public ResponseEntity<?> getMemberList() {
        return new ResponseEntity<>(CommonDto.builder()
                .data(memberService.getAllMembers())
                .status_code(HttpStatus.OK.value())
                .status_message("회원 목록 조회 성공")
                .build(), HttpStatus.OK);
    }
}
