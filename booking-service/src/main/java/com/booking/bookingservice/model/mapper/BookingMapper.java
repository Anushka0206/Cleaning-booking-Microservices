package com.booking.bookingservice.model.mapper;

import com.booking.bookingservice.model.dto.response.BookingResponse;
import com.booking.bookingservice.model.entity.BookingEntity;
import com.booking.common.model.mapper.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper extends BaseMapper<BookingEntity, BookingResponse> {

  @Override
  @Mapping(target = "vehicleName", ignore = true)
  @Mapping(target = "assignedCleaners", ignore = true)
  BookingResponse map(BookingEntity source);
}
