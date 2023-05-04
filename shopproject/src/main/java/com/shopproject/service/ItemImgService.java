package com.shopproject.service;

import com.shopproject.dto.ItemFormDto;
import com.shopproject.entity.Item;
import com.shopproject.entity.ItemImg;
import com.shopproject.repository.ItemImgRepository;
import com.shopproject.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {

    @Value("${itemImgLocation}")
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;

    private final FileService fileService;


    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile)
        throws Exception{
        String  oriImgName = itemImgFile.getOriginalFilename();
        String imgName ="";
        String imgUrl="";

        //파일 업로드
        if (!StringUtils.isEmpty(oriImgName)){
            imgName = fileService.uploadFile(itemImgLocation, oriImgName,
                    itemImgFile.getBytes());
            imgUrl = "/images/item/"+imgName;
        }

        //상품 이미지 정보 저장
        itemImg.updateItemImg(oriImgName, imgName,imgUrl);
        itemImgRepository.save(itemImg);
    }
    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile)
        throws Exception{
        if (!itemImgFile.isEmpty()){//상품 이미지를 수정한 경우 상품 이미지를 업데이트 합니다.
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId)//상품 이미지 아이디를 이용하여 기존에 저장했던 상품 이미지 엔티티를 조회
                    .orElseThrow(EntityNotFoundException::new);
            //기존 이미지 파일 삭제
            if (!StringUtils.isEmpty(savedItemImg.getImgName())){//기존에 등록된 상품 이미지 파일이 있을 경우 해당 파일을 삭제.
                fileService.deleteFile(itemImgLocation+"/"+
                        savedItemImg.getImgName());
            }

            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation,
                    oriImgName,itemImgFile.getBytes());//업데이트한 상품 이미지 파일을 업로드
            String imgUrl = "images/item"+ imgName;
            savedItemImg.updateItemImg(oriImgName, imgName,imgUrl);
            //변경된 상품 이미지 정보를 세팅합니다. 상픔 등록때처럼 itemImgRepository.save()로직 호출하지 않습니다. savedItemImg 엔티티는 현재
            //영속상태이므로 데이터를 변경하는 것만으로 변경감지 기능이 동작하여 트랜잭션이 끝날때 update 쿼리가 실행됩니다.
            //증요한 점은 엔티티가 영속 상태여야 합니다.
        }
    }

}
