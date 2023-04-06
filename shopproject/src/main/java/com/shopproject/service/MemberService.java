package com.shopproject.service;

import com.shopproject.entity.Member;
import com.shopproject.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional//비즈니스 로직을 담당하는 서비스 계층 클래스에 @Transactional 어노테이션을 선언합니다.
//로직을 처리하다 에러가 발생하면 변경된 데이터를 로직을 수행하기 전 상태로 콜백 시켜줍니다.
@RequiredArgsConstructor//2
public class MemberService implements UserDetailsService { //MemberService가 UserDetailService를 구현합니다.

    private final MemberRepository memberRepository;//3
//2,3
    //빈을 주입하는 방법으로는 @Autowired 어노테이션을 사용하거나,필드 주입 생성자 주입을 이용하는 방법이 있습니다.
    //@RequiredArgsConstructor 어노테이션은 final이나 @NonNull을 붙은 필드에 생성자를 생성해줍니다.
    //빈이 생성자가 1개이고 생성자의 파라미터 타입이 빈으로 등로기 가능하다면 @Autowired 어노테이션 없이 의존성 주입이 가능합니다.
    public Member saveMember(Member member){
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }
    private void validateDuplicateMember(Member member){
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if (findMember != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws
            UsernameNotFoundException{//UserDetailService인터페이스의 loadUserByUsername() 메소드를 오버라이딩 합니다.
        //로그인 할 유저의 email을 파라미터로 전달 받습니다.
        Member member = memberRepository.findByEmail(email);

        if (member == null){
            throw new UsernameNotFoundException(email);
        }
        return User.builder()//UserDetail을 구현하고 있는 User 객체를 반환, User객체를 생성하기 위해 생성자로 회원의 이메일
                //비밀번호를 role파라미터로 넘겨 줍니다.
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }
}
