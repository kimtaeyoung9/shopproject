package com.shopproject.controller;

import com.shopproject.dto.CartDetailDto;
import com.shopproject.dto.CartItemDto;
import com.shopproject.dto.CartOrderDto;
import com.shopproject.service.CartService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping(value = "/cart")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto,
                                              BindingResult bindingResult, Principal principal){

        if (bindingResult.hasErrors()){//장바구니에 담을 상품 정보를 받는 cartItemDto 객체에 데이터 바인딩 시 에러가있는지 검사
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors){
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }
        String email = principal.getName();//현재 로그인한 회원의 이메일 정보를 변수에 저장합니다.
        Long cartItemId;

        try {
            cartItemId = cartService.addCart(cartItemDto, email);//화면으로부터 넘어온 장바구니에 담을 상품 정보와 현재 로그인한 회원의 이메일 정보를 이용하여
            //상품을 장바구니에 담는 로직을 호출합니다.
        } catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);//결과값으로 생성된 장바구니 상품 아이디와 요청이 성공하였다는 HTTP 응답 상태 코드 반환.
    }
    @GetMapping(value = "/cart")
    public String orderHist(Principal principal, Model model){
        List<CartDetailDto> cartDetailDtoList =
                cartService.getCartList(principal.getName());//현재 로그인한 사용자의 이메일 정보를 이용해 장바구니에 담겨있는 상품정보를 조회합니다.
        model.addAttribute("cartItems", cartDetailDtoList);//조회한 장바구니 상품 정보를 뷰로 전환합니다.
        return "cart/cartList";
    }
    @PatchMapping(value = "/cartItem/{cartItemId}")//HTTP 메소드에서 PATCH는 요청된 자원의 일부를 업데이트할 때 PATCH를 사용합니다.
    //장바구니 상품의 수량만 업데이트하기 때문에 @PatchMapping을 사용하겠습니다.
    public @ResponseBody ResponseEntity updateCartItem
            (@PathVariable("cartItemId") Long cartItemId, int count, Principal principal){

        if (count <= 0){//장바구니에 담겨있는 사움의 개수를 0개 이하로업데이트 요청을 할 때 에러 메시지를 담아 반환
            return new ResponseEntity<String>
                    ("최소 1개 이상 담아주세요.",HttpStatus.BAD_REQUEST);
        }else if (!cartService.validateCartItem(cartItemId, principal.getName())){//수정권한 체크
            return new ResponseEntity<String>
                    ("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.updateCartItemCount(cartItemId, count);//장바구니 상품의 개수를 업데이트.
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }
    @DeleteMapping(value = "/cartItem/{cartItemId}")//@DeleteMapping 장바구니 상품 삭제
    public @ResponseBody ResponseEntity deleteCartItem
            (@PathVariable("cartItemId") Long cartItemId, Principal principal){

        if (!cartService.validateCartItem(cartItemId, principal.getName())){//수정권한 체크
            return new ResponseEntity<String>("수정 권한이 없습니다.",HttpStatus.FORBIDDEN);
        }

        cartService.deleteCartItem(cartItemId);//해당 장바구니 상품 삭제
        return new ResponseEntity<Long>(cartItemId,HttpStatus.OK);
    }
    @PostMapping(value = "/cart/orders")
    public @ResponseBody ResponseEntity orderCartItem(@RequestBody CartOrderDto cartOrderDto,
                                                      Principal principal){
        System.out.println(cartOrderDto.getCartItemId());
        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();

        if(cartOrderDtoList == null || cartOrderDtoList.size() == 0){
            return new ResponseEntity<String>("주문할 상품을 선택해주세요.",HttpStatus.FORBIDDEN);
        }
        for(CartOrderDto cartOrder : cartOrderDtoList){
            if(!cartService.validateCartItem(cartOrder.getCartItemId(), principal.getName())){
                return new ResponseEntity<String>("주문 권한이 없습니다.",HttpStatus.FORBIDDEN);
            }
        }
        Long orderId = cartService.orderCartItem(cartOrderDtoList, principal.getName());

        return new ResponseEntity<Long>(orderId,HttpStatus.OK);
    }
}
