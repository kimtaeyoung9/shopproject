package com.shopproject.controller;

import com.shopproject.dto.ItemDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/thymeleaf")
public class ThymeleafExController {
    @GetMapping(value = "/ex01") // Get 방식 중에 /thymeleaf/ex01 이경로이면 실행 아래 함수
    public String thymeleafExample01(Model model) { // Model View에 사용할 모델
        //모델에 속성추가 합니다. 속성이름 : data 값 : 타임리프 예제 입니다
        model.addAttribute("data", "타임리프 예제 입니다.");
        return "thymeleafEx/thymeleafEx01";
        // 문자열을 리턴하면 그 경로에 맞는 HTML이 호출 됩니다.
    }
    @GetMapping(value = "/ex2")
    public String thymeleafExample02(Model model){
        ItemDto itemDto = new ItemDto();
        itemDto.setItemDetail("상품 상세 설명");
        itemDto.setItemNm("테스트 상품1");
        itemDto.setPrice(10000);
        itemDto.setRegTime(LocalDateTime.now());

        model.addAttribute("itemDto",itemDto);
        return "tymeleafEx/thymeleafEx02";
    }
    @GetMapping(value = "/ex03")
    public String thymeleafExaple03(Model model){

        List<ItemDto> itemDtoList = new ArrayList<>();

        for (int i=1; i<=10; i++){ //반복문을 통해 화면에 출력할 10개의 itemDto 객체를 만들어서 itemDtoList에 넣어줍니다.

            ItemDto itemDto = new ItemDto();
            itemDto.setItemDetail("상품 상세 설명"+i);
            itemDto.setItemNm("테스트 상품"+i);
            itemDto.setPrice(1000*i);
            itemDto.setRegTime(LocalDateTime.now());

            itemDtoList.add(itemDto);
        }
        model.addAttribute("itemDtoList", itemDtoList);//화면에 출력할 itemDtoList를 model에 담에서 View에 전달합니다.
        return "thymeleafEx/thymeleafEx03";
    }
    @GetMapping(value = "/ex04")
    public String thymeleafExample04(Model model){

        List<ItemDto> itemDtoList = new ArrayList<>();

        for (int i=1; i<=10; i++){

            ItemDto itemDto = new ItemDto();
            itemDto.setItemDetail("상품 상세 설명"+i);
            itemDto.setItemNm("테스트 상품"+i);
            itemDto.setPrice(1000*i);
            itemDto.setRegTime(LocalDateTime.now());

            itemDtoList.add(itemDto);
        }

        model.addAttribute("itemDtoList",itemDtoList);
        return "thymeleafEx/thymeleafEx04";
    }
    @GetMapping(value = "/ex05")
    public String thymeleafExample05(){
        return "thymeleafEx/thymeleafEx05";
    }
    @GetMapping(value = "/ex06")
    public String thymeleafExample06(String param1, String param2, Model model){
        model.addAttribute("param1",param1);
        model.addAttribute("param2",param2);
        return "thymeleafEx/thymeleafEx06";
    }
    @GetMapping(value = "/ex07")
    public String thymeleafExample07(){
        return "thymeleafEx/thymeleafEx07";
    }
}
