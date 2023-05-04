package com.shopproject.service;

import com.shopproject.dto.ItemFormDto;
import com.shopproject.dto.ItemImgDto;
import com.shopproject.dto.ItemSearchDto;
import com.shopproject.dto.MainItemDto;
import com.shopproject.entity.Item;
import com.shopproject.entity.ItemImg;
import com.shopproject.repository.ItemImgRepository;
import com.shopproject.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;

    public Long saveItem(ItemFormDto itemFormDto,
                         List<MultipartFile> itemImgFileList) throws Exception{

        //상품 등록
        Item item = itemFormDto.createItem();//상품 등록 폼으로부터 입력받은 데이털르 이용하여 item 객체를 생성
        itemRepository.save(item);//상품 데이터 저장

        //이미지 등록
        for (int i=0;i<itemImgFileList.size(); i++){
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);
            if (i ==0)//첫 번째 이미지일 경우 대표 상품 이미지 여부 값을 "Y"로 세팅. 나머지는 "N"으로 설정
                itemImg.setRepImgYn("Y");
            else
                itemImg.setRepImgYn("N");
            itemImgService.saveItemImg(itemImg,itemImgFileList.get(i));//상품 이미지 정보 저장.
        }
        return item.getId();
    }
    @Transactional(readOnly = true)//상품 데이터를 읽어오는 트랜잭션을 읽기 전용을 설정 합니다.
    //이럴 경우 JPA가 더티체킹(변경감지)을 수행하지 않아서 성능 향상 가능합니다.
    public ItemFormDto getItemDtl(Long itemId){

        List<ItemImg> itemImgList =
                itemImgRepository.findByItemIdOrderByIdAsc(itemId);//해당 상품의 이미지를 조회합니다. 등록순으로 가지고 오기 위해서
        //상품 이미지 아이디 오름차순으로 가지고 오겠습니다.
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for (ItemImg itemImg : itemImgList){//조회한 ItemImg 엔티티를 ItemImgDto 객체로 만들어서 리스트에 추가합니다.
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

        Item item = itemRepository.findById(itemId)//상품의 아이디를 통해 상품 엔티티를 조회합니다. 존재하지 않을 때는 EntityNotFoundException을 발생시킵니다.
                .orElseThrow(EntityNotFoundException::new);
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;
    }
    public Long updateItem(ItemFormDto itemFormDto,
                           List<MultipartFile> itemImgFileList) throws Exception{
        //상품 수정
        Item item = itemRepository.findById(itemFormDto.getId())//상품 등록 화면으로부터 전달 받은 상품 아이디를 이용해 상품 엔티티를 조회합니다.
                .orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);//상품 등록 화면으로 전달 받은 ItemFormDto를 통해 상품 엔티티를 업데이트합니다.

        List<Long> itemImgIds = itemFormDto.getItemImgIds();//상품 이미지 아이디 리스트를 조회합니다.
        //이미지 등록
        for (int i=0;i<itemImgFileList.size();i++){
            itemImgService.updateItemImg(itemImgIds.get(i),
                    itemImgFileList.get(i));//상품 이미지 업데이트를 위해 updateItemImg()메소드에 상품 이미지 아이디와, 상품 이미지 파일정보를
            //파라미터로 전달합니다.
        }
        return item.getId();
    }
    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto,
                                       Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }
    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }
}
