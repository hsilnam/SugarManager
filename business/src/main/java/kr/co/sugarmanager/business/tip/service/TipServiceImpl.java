package kr.co.sugarmanager.business.tip.service;

import kr.co.sugarmanager.business.tip.dto.TipDTO;
import kr.co.sugarmanager.business.tip.entity.TipEntity;
import kr.co.sugarmanager.business.tip.repository.TipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TipServiceImpl implements TipService{

    private final TipRepository tipRepository;
    public TipDTO.Response tipOfTheDay(){
        TipEntity tip = tipRepository.findTipOfTheDay();
        return TipDTO.Response.builder()
                .title(tip.getTitle())
                .content(tip.getContent())
                .build();
    }
}
