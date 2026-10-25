package lmncrt.orderservice.mapper;


import lmncrt.orderservice.dto.OrdersDto;
import lmncrt.orderservice.entity.OrdersEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrdersMapper {

    @Mapping(source = "createdAt", target = "date")
    OrdersDto toDto(OrdersEntity entity);

    List<OrdersDto> toDtoList(List<OrdersEntity> entity);

    OrdersEntity toEntity(OrdersDto dto);

}
