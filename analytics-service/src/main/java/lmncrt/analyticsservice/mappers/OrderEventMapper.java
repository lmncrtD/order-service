package lmncrt.analyticsservice.mappers;

import lmncrt.analyticsservice.dto.OrderEventDto;
import lmncrt.analyticsservice.entity.OrderEventEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderEventMapper {

    OrderEventDto toDto(OrderEventEntity entity);

    OrderEventEntity toEntity(OrderEventDto dto);

}
