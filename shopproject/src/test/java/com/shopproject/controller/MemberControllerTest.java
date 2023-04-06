package com.shopproject.controller;

import com.shopproject.dto.MemberFormDto;
import com.shopproject.entity.Member;
import com.shopproject.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;

@SpringBootTest
@AutoConfigureMockMvc//MockMvc 테스트를 위해 @AutoConfigureMockMvc 어노테이션을 선언합니다.
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class MemberControllerTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MockMvc mockMvc;//MockMvc 클래스를 이용해 실제 객체와 비슷하지만 테스트에 필요한 기능만 가지는 가짜 객체입니다.
    //MockMvc객체를 이용하면 웹 브라우저에서 요청하는 것처럼 테스트 할수있습니다.

    @Autowired
    PasswordEncoder passwordEncoder;

    public Member createMember(String email, String password) {//로그인 예제 진행을 위해서 로그인 전 회원 등록 메소드를 만듭니다.
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail(email);
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("서울시 강서구 화곡동");
        memberFormDto.setPassword(password);
        Member member = Member.createMember(memberFormDto, passwordEncoder);
        return memberService.saveMember(member);

    }

    @Test
    @DisplayName("로그인 성공 테스트")
    public void loginSuccessTest() throws Exception {
        String email = "test@email.com";
        String password = "test1234!";
        this.createMember(email, password);
        mockMvc.perform(formLogin().userParameter("email")
                        .loginProcessingUrl("/members/login")//회원가입 메소드를 실행 후 가입된 회원 정보로 로그인이 되는지를
                        //테스트를 진행합니다. userParameter()를 이용하여 이메일을 아이디로 세팅하고 로그인 URL에 요청합니다.
                        .user(email).password("est1234!"))
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated());//로그인이 성공하여 인증되었다면 통화합니다.
    }

    @Test
    @DisplayName("로그인 실패 테스트")
    public void loginFailTest() throws Exception {
        String email = "test@email.com";
        String password = "test1234!";
        this.createMember(email, password);
        mockMvc.perform(formLogin().userParameter("email")
                        .loginProcessingUrl("/members/login")
                        .user(email).password("12345"))
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
    }
}