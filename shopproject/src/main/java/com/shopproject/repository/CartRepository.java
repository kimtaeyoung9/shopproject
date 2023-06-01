package com.shopproject.repository;

import com.shopproject.entity.Cart;
import com.shopproject.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findByMemberId(Long memberId);
}
