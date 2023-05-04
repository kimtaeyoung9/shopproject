package com.shopproject.service;

import com.shopproject.dto.OrderDto;
import com.shopproject.entity.Item;
import com.shopproject.entity.Member;
import com.shopproject.entity.Order;
import com.shopproject.entity.OrderItem;
import com.shopproject.repository.ItemRepository;
import com.shopproject.repository.MemberRepository;
import com.shopproject.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;

    public Long order(OrderDto orderDto, String email){
        Item item = itemRepository.findById(orderDto.getItemId())//주문할 상품을 조회합니다.
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email);//현재 로그인한 회원의 이메일 정보를 이용해 회원정보를 조회

        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem =
                OrderItem.createOrderItem(item, orderDto.getCount());//주문할 상품 엔티티와 주문 수량을 이용하여 주문 상품 엔티티를 생성
        orderItemList.add(orderItem);

        Order order = Order.createOrder(member, orderItemList);//회원정보와 주문할 상품리스트 정보를 이용해 주문 엔티티를 생성
        orderRepository.save(order);//생성한 주문 엔티티 저장

        return order.getId();
    }
}
