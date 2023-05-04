package com.shopproject.controller;

import com.shopproject.dto.OrderDto;
import com.shopproject.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping(value = "/order")
    public @ResponseBody ResponseEntity order (@RequestBody @Valid OrderDto orderDto,
                                               BindingResult bindingResult, Principal principal){
        //스프링에서 비동기 처리를 할 때 @ResponseBody 와 @RequestBody 어노테이션을 사용합니다.
        //@ResponseBody: HTTP 요청의 본문 body에 담긴 내용울 자바 객체로 전달
        //@RequestBody: 자바 객체를 HTTP 요청으로 body로 전달
        if (bindingResult.hasErrors()){//주문 정볼르 받는 orderDto 객체에 데이터 바인딩시 에러가 있는지 검사
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors){
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(),
                    HttpStatus.BAD_REQUEST);//에러 정보를 ResponseEntitiy 객체에 담아서 반환
        }

        String email = principal.getName();
        //현재 로그인 유저의 정보를 얻기 위해서 @Controller 어노테이션이 선언된 클래스에서 베소드 인자로 principal 객체를 넘겨 줄 경우
        //해당 객체에 직접 접근할수있습니다. principal 객체에서 현재 로그인한 회원의 이메일 정보를 조회.
        Long orderId;

        try {
            orderId = orderService.order(orderDto, email);//화면으로부터 넘어오는 주문 정보와 회원의 이메일 정보를 이요하여 주문 로직을 호출
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(),
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);//결과값으로 생성된 주문 번호와 요청이 성공했다는 HTTP 응답 상태 코드를 반환
    }
}
