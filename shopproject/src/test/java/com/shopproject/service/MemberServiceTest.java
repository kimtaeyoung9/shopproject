package com.shopproject.service;

import com.shopproject.dto.MemberFormDto;
import com.shopproject.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional//테스트 클래스에 @Transactional 어노테이션을 선언할경우 테스트 실행 후 롤백 처리됩니다. 반복적인 테스트 가능
@TestPropertySource(locations = "classpath:application-test.properties")
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Member createMember(){//회원 정보를 입력한 Member 엔티티를 만드는 메소드를 작성합니다.
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("test@email.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("서울시 강서구 확고동");
        memberFormDto.setPassword("1234");
        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    @DisplayName("회권가입 테스트")
    public void saveMemberTest(){//Junit의 Assertions 클래스의 assertEquals 메소드를 이용하여 저장하려 했단 값과 실제 저장된 데이터를 비교합니다.
        //첫 번째 파라미터에는 기대값, 두번째 파라미터에는 실제로 저장된 값을 넣어줍니다.
        Member member = createMember();
        Member savedMember = memberService.saveMember(member);

        assertEquals(member.getEmail(), savedMember.getEmail());
        assertEquals(member.getName(), savedMember.getName());
        assertEquals(member.getAddress(),savedMember.getAddress());
        assertEquals(member.getPassword(), savedMember.getPassword());
        assertEquals(member.getRole(), savedMember.getRole());
    }
    @Test
    @DisplayName("중복 회원가입 테스트")
    public void saveDuplicateMemberTest(){
        Member member1 = createMember();
        Member member2 = createMember();
        memberService.saveMember(member1);
        //assertThrows 예외처리 상황 확인하기 위해서 사용
        Throwable e = assertThrows(IllegalStateException.class, () -> {
            memberService.saveMember(member2);});
        assertEquals("이미 가입된 회원입니다.", e.getMessage());
    }
}