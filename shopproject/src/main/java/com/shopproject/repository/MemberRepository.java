package com.shopproject.repository;

import com.shopproject.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.Email;

public interface MemberRepository extends JpaRepository<Member, Long> {
    
    Member findByEmail(String email); //회원가입시 중복회원 검사 메소드


}
