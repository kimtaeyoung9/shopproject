package com.shopproject.service;

import com.shopproject.dto.CartDetailDto;
import com.shopproject.dto.CartItemDto;
import com.shopproject.dto.CartOrderDto;
import com.shopproject.dto.OrderDto;
import com.shopproject.entity.Cart;
import com.shopproject.entity.CartItem;
import com.shopproject.entity.Item;
import com.shopproject.entity.Member;
import com.shopproject.repository.CartItemRepository;
import com.shopproject.repository.CartRepository;
import com.shopproject.repository.ItemRepository;
import com.shopproject.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    private final OrderService orderService;
    public Long addCart(CartItemDto cartItemDto, String email){
        Item item = itemRepository.findById(cartItemDto.getItemId())//장바구니에 담을 상품 엔티티를 조회
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email);//현재 로그인한 회원 엔티티를 조회합니다.

        Cart cart = cartRepository.findByMemberId(member.getId());//현재 로그인한 회원의 장바구니 엔티티를 조회
        if (cart == null){//상품을 처음으로 장바구니에 담을 경우 해당 회원의 장바구니 엔티티를 설정
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        CartItem savedCartItem =
                cartItemRepository.findByCartIdAndItemId(cart.getId(),item.getId());//현재 상품이 장바구니에 있는지 조회
        if (savedCartItem != null){
            savedCartItem.addCount(cartItemDto.getCount());//장바구니에 이미 있던 상품일 경우 기존 수량에 현재 장바구니에 담을 수량만큼 더해줌
            return savedCartItem.getId();
        }else {
            CartItem cartItem =
                    CartItem.createCartItem(cart, item ,cartItemDto.getCount());//장바구니 엔티티, 상품 엔티티, 장바구니에 담을 수량을 이용하여 CartItem엔티티 설정
            cartItemRepository.save(cartItem);//장바구니에 들어갈 상품을 저장
            return cartItem.getId();
        }
    }
    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email){
        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMemberId(member.getId());//로그인한 회원의 장바구니 엔티티 조회
        if (cart == null){//장바구니에 상품ㅇ르 한번도 안 담았을 경우 장바구니 엔티티가 없으므로 빈 리스트를 반환합니다.
            return cartDetailDtoList;
        }

        cartDetailDtoList=
                cartItemRepository.findCartDetailDtoList(cart.getId());//장바구니에 담겨있는 상품 정볼르 조회합니다.

        return cartDetailDtoList;
    }
    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email){
        Member curMember = memberRepository.findByEmail(email);//로그인한 회원 조회
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = cartItem.getCart().getMember();//장바구니 상품을 저장한 회원 조회

        if (!StringUtils.equals(curMember.getEmail(),
                savedMember.getEmail())){//로그인한 회원과 장바구니 상품을 저장한 회원이 다를경우 false,같으면 true 를 반환
            return false;
        }
        return true;
    }
    public void updateCartItemCount(Long cartItemId, int count){//장바구니 상품의 수량을 업데이트 하는 메소드
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        cartItem.updateCount(count);
    }
    public void deleteCartItem(Long cartItemId){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
    }
    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email){
        List<OrderDto> orderDtoList = new ArrayList<>();
        for(CartOrderDto cartOrderDto : cartOrderDtoList){
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityExistsException::new);
            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }
        Long orderId = orderService.orders(orderDtoList, email);

        for(CartOrderDto cartOrderDto : cartOrderDtoList){
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityExistsException::new);
            cartItemRepository.delete(cartItem);
        }
        return orderId;
    }
}
