package com.wlopezob.crud.mapper;

import com.wlopezob.crud.dto.TransactionRequest;
import com.wlopezob.crud.dto.TransactionResponse;
import com.wlopezob.crud.entity.Transaction;
import com.wlopezob.crud.util.ConversionUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    imports = {ConversionUtil.class}
)
public interface TransactionMapper {
    
    @Mapping(target = "fecha", expression = "java(ConversionUtil.localDateTimeToString(transaction.getFecha()))")
    @Mapping(target = "senderNombre", ignore = true)
    @Mapping(target = "receiverNombre", ignore = true)
    TransactionResponse toResponse(Transaction transaction);
    
    List<TransactionResponse> toResponseList(List<Transaction> transactions);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fecha", ignore = true)
    Transaction toEntity(TransactionRequest request);
} 