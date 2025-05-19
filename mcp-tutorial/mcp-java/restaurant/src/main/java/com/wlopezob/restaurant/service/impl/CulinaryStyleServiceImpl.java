package com.wlopezob.restaurant.service.impl;

import com.wlopezob.restaurant.dto.CulinaryStyleDTO;
import com.wlopezob.restaurant.mapper.CulinaryStyleMapper;
import com.wlopezob.restaurant.model.CulinaryStyle;
import com.wlopezob.restaurant.repository.CulinaryStyleRepository;
import com.wlopezob.restaurant.service.CulinaryStyleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CulinaryStyleServiceImpl implements CulinaryStyleService {

    private final CulinaryStyleRepository culinaryStyleRepository;
    private final CulinaryStyleMapper culinaryStyleMapper;

    @Override
    public Flux<CulinaryStyleDTO> getAllCulinaryStyles() {
        return culinaryStyleRepository.findAll()
                .map(culinaryStyleMapper::toDto);
    }

    @Override
    public Mono<CulinaryStyleDTO> getCulinaryStyleById(String id) {
        return culinaryStyleRepository.findById(id)
                .map(culinaryStyleMapper::toDto);
    }

    @Override
    public Mono<CulinaryStyleDTO> createCulinaryStyle(CulinaryStyleDTO culinaryStyleDTO) {
        CulinaryStyle culinaryStyle = culinaryStyleMapper.toEntity(culinaryStyleDTO);
        return culinaryStyleRepository.save(culinaryStyle)
                .map(culinaryStyleMapper::toDto);
    }

    @Override
    public Mono<CulinaryStyleDTO> updateCulinaryStyle(String id, CulinaryStyleDTO culinaryStyleDTO) {
        return culinaryStyleRepository.findById(id)
                .flatMap(existingCulinaryStyle -> {
                    CulinaryStyle updatedCulinaryStyle = culinaryStyleMapper.toEntity(culinaryStyleDTO);
                    updatedCulinaryStyle = new CulinaryStyle(
                            existingCulinaryStyle.id(),
                            updatedCulinaryStyle.name(),
                            updatedCulinaryStyle.description()
                    );
                    return culinaryStyleRepository.save(updatedCulinaryStyle);
                })
                .map(culinaryStyleMapper::toDto);
    }

    @Override
    public Mono<Void> deleteCulinaryStyle(String id) {
        return culinaryStyleRepository.deleteById(id);
    }
} 