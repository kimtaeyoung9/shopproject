package com.shopproject.entity;

import com.shopproject.constant.Role;
import com.shopproject.dto.MemberFormDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Entity
@Table(name = "member")
@Getter
@Setter
@ToString
public class Member extends BaseEntity {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(unique = true)//회원은 이메일ㅇ르 통해 유일하게 구분해야 하기 때문에, 동일한 값이 데이터베이스에 들어올 수 없도록 uniqie속성을 저장
    private String email;

    private String password;

    private String address;

    @Enumerated(EnumType.STRING)//자바의 enum 타입을 엔티티 속성으로 지정할수 있습니다. enum을 사용할 떄 기본적으로 순서가 저장되는데
    //enum의 순서가 바뀔 경우 문제가 발생할수 있으니 EnumType.STRING 옵션을 사용해 String 으로 저장합니다.

    private Role role;

    public static Member createMember(MemberFormDto memberFormDto,
                                      PasswordEncoder passwordEncoder){//<-
        //member 엔티티를 생성하는 메소드입니다. member 엔티티에 회원을 생성하는 메소드를 만들어서 관리를 한다면 코드가 변경되더라도 한군데만 수정하면되는 이점이 있습니다.
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        String password = passwordEncoder.encode(memberFormDto.getPassword());//<-
        //스프링 시큐리티 설정 클래스에 등록한BCryptPasswordEncoder Bean을 파라미터로 넘겨서 비밀번호 암호화를 합니다.
        member.setPassword(password);
        member.setRole(Role.ADMIN);
        return  member;
    }
}
