package spring.ordersystem.common.common;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import spring.ordersystem.member.entity.Member;
import spring.ordersystem.member.entity.Role;
import spring.ordersystem.member.repository.MemberRepository;

// CommandLineRunner 인터페이스를 구현함으로써 해당 컴포넌트가 스프링빈으로 등록되는 시점에 run에서도 자동실행.

@Component
@RequiredArgsConstructor
public class InitialDataLoader implements CommandLineRunner {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if(memberRepository.findByEmail("admin@naver.com").isPresent()){
            return;
        }
        Member member = Member.builder()
                .email("admin@naver.com")
                .role(Role.ADMIN)
                .name("관리자")
                .password(passwordEncoder.encode("admin12341234"))
                .build();

        memberRepository.save(member);
    }

}
