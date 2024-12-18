package com.mgmresorts.loyalty.service;


import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.loyalty.dto.services.IssuePromoEventTicketRequest;
import com.mgmresorts.loyalty.dto.services.IssuePromoEventTicketResponse;

public interface IPromoEventTicketIssuanceService {

    public IssuePromoEventTicketResponse issueEventPromo(IssuePromoEventTicketRequest promoEvent) throws AppException;
}
