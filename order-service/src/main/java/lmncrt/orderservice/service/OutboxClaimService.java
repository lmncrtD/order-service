package lmncrt.orderservice.service;

import lmncrt.orderservice.entity.OutboxEntity;

import java.util.List;

public interface OutboxClaimService {

    List<OutboxEntity> claimBatch();

}
