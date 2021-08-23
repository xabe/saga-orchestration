package com.xabe.orchestation.shipping.infrastructure.persistence;

import com.xabe.orchestation.shipping.infrastructure.persistence.dto.ShippingDTO;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ShippingRepositoryPanache implements PanacheRepositoryBase<ShippingDTO, Long> {

}
