package com.shopproject.service;

import com.shopproject.constant.ItemSellStatus;
import com.shopproject.dto.OrderDto;
import com.shopproject.entity.Item;
import com.shopproject.entity.Member;
import com.shopproject.entity.Order;
import com.shopproject.entity.OrderItem;
import com.shopproject.repository.ItemRepository;
import com.shopproject.repository.MemberRepository;
import com.shopproject.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class OrderServiceTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    MemberRepository memberRepository;

    public Item saveItem(){//테스트를 위해 주문할 상품과 회원 정보를 저장하는 메소드 생성
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(1000);
        item.setItemDetail("테스트 상품 상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        return itemRepository.save(item);
    }
    public Member saveMember(){
        Member member = new Member();
        member.setEmail("test@test.com");
        return memberRepository.save(member);

    }
    @Test
    @DisplayName("주문테스트")
    public void order(){
        Item item = saveItem();
        Member member = saveMember();

        OrderDto orderDto = new OrderDto();
        orderDto.setCount(10);//주문할 상품과 상품 수량을 orderDto 객체에 세팅
        orderDto.setItemId(item.getId());

        Long orderId = orderService.order(orderDto, member.getEmail());//주문 로직 호출 결과 생성된 주문 번호를 orderid 변수에 저장

        Order order = orderRepository.findById(orderId)//주문 번호를 이용하여 저장된 주문 정보를 조회합니다.
                .orElseThrow(EntityNotFoundException::new);

        List<OrderItem> orderItems = order.getOrderItems();

        int totalPrice = orderDto.getCount()*item.getPrice();//주문한 상품의 총 가격을 구합니다.

        assertEquals(totalPrice, order.getTotalPrice());//주문한 상품의 총 가격과 데이터 베이스에 저장된 상품의 가격을 비교
    }

}