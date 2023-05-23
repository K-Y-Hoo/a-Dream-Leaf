package com.DreamCoder.DreamLeaf.service;

import com.DreamCoder.DreamLeaf.dto.StoreDto;
import com.DreamCoder.DreamLeaf.repository.StoreRepository;
import com.DreamCoder.DreamLeaf.req.StoreReq;
import com.DreamCoder.DreamLeaf.req.UserCurReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreService {

    @Autowired
    private final StoreRepository storeRepository;



    public StoreDto save(StoreReq storeReq){
        return storeRepository.save(storeReq);
    }

    public Optional<StoreDto> findById(int storeId){
        return storeRepository.findById(storeId);
    }

    public List<StoreDto> findByKeyword(String keyword, UserCurReq userCurReq){
        return storeRepository.findByKeyword(keyword, userCurReq);
    }

    public List<StoreDto> findByCur(UserCurReq userCurReq){           //클라이언트에게 위치 정보를 받아서 거리 계산
        return storeRepository.findByCur(userCurReq);
    }



}
