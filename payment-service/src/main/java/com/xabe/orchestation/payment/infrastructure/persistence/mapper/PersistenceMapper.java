package com.xabe.orchestation.payment.infrastructure.persistence.mapper;

import com.xabe.orchestation.payment.domain.entity.Payment;
import com.xabe.orchestation.payment.infrastructure.persistence.dto.PaymentDTO;
import java.util.List;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR, collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, componentModel = "cdi")
public interface PersistenceMapper {

  Payment toEntity(PaymentDTO paymentDTO);

  List<Payment> toEntities(List<PaymentDTO> paymentDTOS);

  PaymentDTO toDTO(Payment payment);
}
