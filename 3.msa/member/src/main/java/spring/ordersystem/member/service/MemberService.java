package spring.ordersystem.member.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.ordersystem.member.dto.MemberCreateDto;
import spring.ordersystem.member.dto.MemberResDto;
import spring.ordersystem.member.dto.MemberLoginReqDto;
import spring.ordersystem.member.entity.Member;
import spring.ordersystem.member.repository.MemberRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Long createMember(MemberCreateDto memberCreateDto){
        checkEmailExists(memberCreateDto.getEmail());
        String password = passwordEncoder.encode(memberCreateDto.getPassword());

        return memberRepository.save(memberCreateDto.toEntity(password)).getId();
    }

    public Member login(MemberLoginReqDto memberLoginReqDto) {
        Optional<Member> member = memberRepository.findByEmail(memberLoginReqDto.getEmail());

        boolean check = false;
        if(member.isPresent()){
            check = passwordEncoder.matches(memberLoginReqDto.getPassword(), member.get().getPassword());
        }
        if (!check) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        return member.get();
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 회원이 존재하지 않습니다."));
    }

    public MemberResDto getMyInfo(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 회원이 존재하지 않습니다."));
        return MemberResDto.fromEntity(member);
    }

    public void checkEmailExists(String email) {
        if (memberRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
    }

    public List<MemberResDto> getAllMembers() {
        return memberRepository.findAll().stream().map(MemberResDto::fromEntity).collect(Collectors.toList());
    }

    public void withdrawMember(String email){
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 회원이 존재하지 않습니다."));
        member.deleteMember("Y");
    }
}
