package com.kokab.soda_service.web.controller;

import bottling.model.SodaDto;
import bottling.model.SodaPagedList;
import bottling.model.SodaStyleEnum;
import com.kokab.soda_service.service.SodaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/v1/")
@RestController
public class SodaController {

        private static final Integer DEFAULT_PAGE_NUMBER = 0;
        private static final Integer DEFAULT_PAGE_SIZE = 25;

        private final SodaService sodaService;

        @GetMapping(produces = { "application/json" }, path = "soda")
        public ResponseEntity<SodaPagedList> listSodas(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                       @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                       @RequestParam(value = "sodaName", required = false) String sodaName,
                                                       @RequestParam(value = "sodaStyle", required = false) SodaStyleEnum sodaStyle,
                                                       @RequestParam(value = "showInventoryOnHand", required = false) Boolean showInventoryOnHand){

            if (showInventoryOnHand == null) {
                showInventoryOnHand = false;
            }

            if (pageNumber == null || pageNumber < 0){
                pageNumber = DEFAULT_PAGE_NUMBER;
            }

            if (pageSize == null || pageSize < 1) {
                pageSize = DEFAULT_PAGE_SIZE;
            }

            SodaPagedList sodaList = sodaService.listSodas(sodaName, sodaStyle, PageRequest.of(pageNumber, pageSize), showInventoryOnHand);

            return new ResponseEntity<>(sodaList, HttpStatus.OK);
        }

        @GetMapping("soda/{sodaId}")
        public ResponseEntity<SodaDto> getSodaById(@PathVariable("sodaId") UUID sodaId,
                                                   @RequestParam(value = "showInventoryOnHand", required = false) Boolean showInventoryOnHand){
            if (showInventoryOnHand == null) {
                showInventoryOnHand = false;
            }

            return new ResponseEntity<>(sodaService.getById(sodaId, showInventoryOnHand), HttpStatus.OK);
        }

        @GetMapping("sodaUpc/{upc}")
        public ResponseEntity<SodaDto> getSodaByUpc(@PathVariable("upc") String upc){
            return new ResponseEntity<>(sodaService.getByUpc(upc), HttpStatus.OK);
        }

        @PostMapping(path = "soda")
        public ResponseEntity saveNewSoda(@RequestBody @Validated SodaDto sodaDto){
            return new ResponseEntity<>(sodaService.saveNewSoda(sodaDto), HttpStatus.CREATED);
        }

        @PutMapping("soda/{sodaId}")
        public ResponseEntity updateSodaById(@PathVariable("sodaId") UUID sodaId, @RequestBody @Validated SodaDto sodaDto){
            return new ResponseEntity<>(sodaService.updateSoda(sodaId, sodaDto), HttpStatus.NO_CONTENT);
        }
}
