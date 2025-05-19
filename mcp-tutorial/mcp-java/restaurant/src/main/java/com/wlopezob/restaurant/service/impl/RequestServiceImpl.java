package com.wlopezob.restaurant.service.impl;

import com.wlopezob.restaurant.dto.DishDTO;
import com.wlopezob.restaurant.dto.MenuDTO;
import com.wlopezob.restaurant.dto.RequestDTO;
import com.wlopezob.restaurant.mapper.DishMapper;
import com.wlopezob.restaurant.mapper.RequestMapper;
import com.wlopezob.restaurant.model.Request;
import com.wlopezob.restaurant.repository.DishRepository;
import com.wlopezob.restaurant.repository.RequestRepository;
import com.wlopezob.restaurant.service.MenuService;
import com.wlopezob.restaurant.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final DishRepository dishRepository;
    private final RequestMapper requestMapper;
    private final MenuService menuService;
    private final DishMapper dishMapper;

    @Override
    public Flux<RequestDTO> getAllRequests() {
        return requestRepository.findAll()
                .flatMap(this::enrichRequestWithMenusAndDishes);
    }

    @Override
    public Mono<RequestDTO> getRequestById(String id) {
        return requestRepository.findById(id)
                .flatMap(this::enrichRequestWithMenusAndDishes);
    }

    @Override
    public Mono<RequestDTO> createRequest(RequestDTO requestDTO) {
        Request request = requestMapper.toEntity(requestDTO);
        return requestRepository.save(request)
                .flatMap(this::enrichRequestWithMenusAndDishes);
    }

    @Override
    public Mono<RequestDTO> updateRequest(String id, RequestDTO requestDTO) {
        return requestRepository.findById(id)
                .flatMap(existingRequest -> {
                    Request updatedRequest = requestMapper.toEntity(requestDTO);
                    updatedRequest = new Request(
                            existingRequest.id(),
                            updatedRequest.menusId(),
                            updatedRequest.extraDishesId(),
                            updatedRequest.totalPrice(),
                            updatedRequest.enabled()
                    );
                    return requestRepository.save(updatedRequest);
                })
                .flatMap(this::enrichRequestWithMenusAndDishes);
    }

    @Override
    public Mono<Void> deleteRequest(String id) {
        return requestRepository.deleteById(id);
    }

    private Mono<RequestDTO> enrichRequestWithMenusAndDishes(Request request) {
        RequestDTO requestDTO = requestMapper.toDto(request);
        
        // Enriquecer con los menús
        Flux<MenuDTO> menusMono = Flux.fromIterable(request.menusId())
                .flatMap(menuId -> menuService.getMenuById(menuId))
                .collectList()
                .flatMapMany(Flux::fromIterable);
        
        // Enriquecer con los platos extra
        Flux<DishDTO> extraDishesMono = Flux.fromIterable(request.extraDishesId())
                .flatMap(dishId -> dishRepository.findById(dishId)
                        .map(dishMapper::toDto))
                .collectList()
                .flatMapMany(Flux::fromIterable);
        
        return Mono.zip(menusMono.collectList(), extraDishesMono.collectList(), (menus, extraDishes) -> {
            requestDTO.setMenus(menus);
            requestDTO.setExtraDishes(extraDishes);
            return requestDTO;
        });
    }
} 