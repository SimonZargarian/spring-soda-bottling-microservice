package com.kokab.soda_service.service;

import bottling.model.SodaDto;
import bottling.model.SodaPagedList;
import bottling.model.SodaStyleEnum;
import com.kokab.soda_service.web.controller.NotFoundException;
import com.kokab.soda_service.domain.Soda;
import com.kokab.soda_service.repository.SodaRepository;
import com.kokab.soda_service.web.mapper.SodaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@Service
public class SodaServiceImpl implements SodaService {
    private final SodaRepository sodaRepository;
    private final SodaMapper sodaMapper;

    @Cacheable(cacheNames = "sodaListCache", condition = "#showInventoryOnHand == false ")
    @Override
    public SodaPagedList listSodas(String sodaName, SodaStyleEnum sodaStyle, PageRequest pageRequest, Boolean showInventoryOnHand) {

        SodaPagedList sodaPagedList;
        Page<Soda> sodaPage;

        if (!StringUtils.isEmpty(sodaName) && !StringUtils.isEmpty(sodaStyle)) {
            //search both
            sodaPage = sodaRepository.findAllBySodaNameAndSodaStyle(sodaName, sodaStyle, pageRequest);
        } else if (!StringUtils.isEmpty(sodaName) && StringUtils.isEmpty(sodaStyle)) {
            //search soda_service name
            sodaPage = sodaRepository.findAllBySodaName(sodaName, pageRequest);
        } else if (StringUtils.isEmpty(sodaName) && !StringUtils.isEmpty(sodaStyle)) {
            //search soda_service style
            sodaPage = sodaRepository.findAllBySodaStyle(sodaStyle, pageRequest);
        } else {
            sodaPage = sodaRepository.findAll(pageRequest);
        }

        if (showInventoryOnHand){
            sodaPagedList = new SodaPagedList(sodaPage
                    .getContent()
                    .stream()
                    .map(sodaMapper::sodaToSodaDtoWithInventory)
                    .collect(Collectors.toList()),
                    PageRequest
                            .of(sodaPage.getPageable().getPageNumber(),
                                    sodaPage.getPageable().getPageSize()),
                    sodaPage.getTotalElements());
        } else {
            sodaPagedList = new SodaPagedList(sodaPage
                    .getContent()
                    .stream()
                    .map(sodaMapper::sodaToSodaDto)
                    .collect(Collectors.toList()),
                    PageRequest
                            .of(sodaPage.getPageable().getPageNumber(),
                                    sodaPage.getPageable().getPageSize()),
                    sodaPage.getTotalElements());
        }

        return sodaPagedList;
    }

    @Cacheable(cacheNames = "sodaCache", key = "#sodaId", condition = "#showInventoryOnHand == false ")
    @Override
    public SodaDto getById(UUID sodaId, Boolean showInventoryOnHand) {
        if (showInventoryOnHand) {
            return sodaMapper.sodaToSodaDtoWithInventory(
                    sodaRepository.findById(sodaId).orElseThrow(NotFoundException::new)
            );
        } else {
            return sodaMapper.sodaToSodaDto(
                    sodaRepository.findById(sodaId).orElseThrow(NotFoundException::new)
            );
        }
    }

    @Override
    public SodaDto saveNewSoda(SodaDto sodaDto) {
        return sodaMapper.sodaToSodaDto(sodaRepository.save(sodaMapper.sodaDtoToSoda(sodaDto)));
    }

    @Override
    public SodaDto updateSoda(UUID sodaId, SodaDto sodaDto) {
        Soda soda = sodaRepository.findById(sodaId).orElseThrow(NotFoundException::new);

        soda.setSodaName(sodaDto.getSodaName());
        soda.setSodaStyle(sodaDto.getSodaStyle().name());
        soda.setPrice(sodaDto.getPrice());
        soda.setUpc(sodaDto.getUpc());

        return sodaMapper.sodaToSodaDto(sodaRepository.save(soda));
    }

    @Cacheable(cacheNames = "sodaUpcCache")
    @Override
    public SodaDto getByUpc(String upc) {
        return sodaMapper.sodaToSodaDto(sodaRepository.findByUpc(upc));
    }
}
