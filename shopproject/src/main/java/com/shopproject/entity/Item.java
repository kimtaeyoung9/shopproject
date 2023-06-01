package com.shopproject.entity;

import com.shopproject.constant.ItemSellStatus;
import com.shopproject.dto.ItemFormDto;
import com.shopproject.exception.OutOfStockException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity{
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; //상품코드

    @Column(nullable = false, length = 50)
    private String itemNm; //상품명

    @Column(name = "price", nullable = false)
    private int price; //상품 가격

    @Column(nullable = false)
    private int stockNumber; //재고수량

    @Lob
    @Column(nullable = false)
    private String itemDetail; //상품 상세 설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; //상품 판매 상태

    //private LocalDateTime regTime; //등록시간

    //private LocalDateTime updateTime; //수정시간

    @ManyToMany
    @JoinTable(
            name = "member_item",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Member> member;
    public void updateItem(ItemFormDto itemFormDto){
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }
    public void removeStock(int stockNumber){
        int restStock = this.stockNumber - stockNumber;//상품의 재고 수량에서 주문 후 남은 재고수량을 구합니다.
        if (restStock<0){
            throw new OutOfStockException("상품 재고가 부족합니다. (현재 재고수량: "+this.stockNumber+")");//상품의 재고가 주문 수량보다 적을 경우 재고부족 예외처리
        }
        this.stockNumber = restStock;//주문 후 남은 재고 수량을 상품의 현재 재고 값으로 할당.
    }
    public void addStock(int stockNumber){
        this.stockNumber += stockNumber; //상품의 재고를 증가시키는 메소드
    }
}
