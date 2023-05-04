package com.shopproject.config;

import com.shopproject.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity//1
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    //WebSecurityConfigurerAdapter deprecated로 인한 SecurityConfig 파일 수정
    @Autowired
    MemberService memberService;
    @Override
    protected void configure(HttpSecurity http) throws Exception{//3
        //3
        //http 요청에 대한 보안 설정을 합니다. 페이지 권한 설정,로그인 페이지 설정, 로그아웃 메소드 등에 대한 설정을 작성합니다.
   http.formLogin()
           .loginPage("/members/login")//로그인 페이지 url설정
           .defaultSuccessUrl("/")//로그인 성공시 이동할 url설정
           .usernameParameter("email")//로그인 시 사용할 파라미터 이름으로 email을 지정
           .failureUrl("/members/login/error")//로그인 실패시 이동할 url설정
           .and()
           .logout()
           .logoutRequestMatcher(new AntPathRequestMatcher
                   ("/members/logout"))//로그아웃 url설정
           .logoutSuccessUrl("/")//로그아웃 성공시 이동할 url설정
           ;

   http.authorizeRequests()//시큐리티 처리에 HttpServletRequest를 이용한다는 것을 의미
           .mvcMatchers("/","/members/**","/item/**","/images/**").permitAll()//premitAll을 통해 모든 사용자가 로그인 없이 해당 경로를 접근할수 있도록 설정
           //메인페이지,회원 관련 URL 상품상세 페이지 ,상품 이미지를 불러오는 경로가 이에 해당합니다.
           .mvcMatchers("/admin/**").hasRole("ADMIN")
           //.admin으로 시작하는 경로는 해당 계정이 ADMIN Role일 경우에만 접근가능
           .anyRequest().authenticated()// 위에 설정해준 경로를 제외한 나머지 경로들은 모드 인증을 요구하도록 설정합니다.
           ;

   http.exceptionHandling()
           .authenticationEntryPoint
                   (new CustomAuthenticationEntryPoint())//인증되지 않은 사용자가 리소스에 접근하였을 떄 수행되는 핸들러를 등록합니다.
           ;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){//4
        //4
        //비밀번호를 데이터 베이스에 그대로 저장했을 경우, 데이터베이스가 해킹당하면 고객의 회원 정보가 그대로 노출됩니다.
        //이것을 해결하기 위해 BCryptPasswordEncoder의 함수를 이용하여 비밀번호를 암호화해 저장합니다.
        return new BCryptPasswordEncoder();
    }
    @Override
    public void configure(WebSecurity web)throws Exception{
        web.ignoring().antMatchers("/css/**", "/js/**","/img/**");
    }
}
