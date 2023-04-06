package com.shopproject.entity;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "cart")
@Getter
@Setter
@ToString

public class Cart extends BaseEntity{
    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) //OneToOne 어노테이션을 이용해 회원 엔티티와 일대일 매핑을 합니다.
    @JoinColumn(name = "member_id")// @JoinColumn 어노테이션을 이용해 매핑할 외래키를 지정합니다.
    //name 속성에는 매핑할 외래키의 이름을 성정합니다. @JoinColumn의 name을 명시하지 않으면 JPA가 알아서 id를찾지만 컬럼명이 원하는데로 생성되지 않을 수 있어 직접설정합니다.
    private Member member;

}
