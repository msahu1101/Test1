package com.mgmresorts.loyalty.transformer;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.Unmarshaller;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.exception.AppRuntimeException;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.common.utils.Dates;
import com.mgmresorts.common.utils.Utils;
import com.mgmresorts.loyalty.common.JaxbContext;
import com.mgmresorts.loyalty.data.entity.PlayerComment;
import com.mgmresorts.loyalty.dto.customer.CustomerComment;
import com.mgmresorts.loyalty.dto.patron.comments.CRMAcresMessage;
import com.mgmresorts.loyalty.errors.Errors;

public class CommentTransformer implements ITransformer<List<PlayerComment>, List<CustomerComment>> {
    private final Logger logger = Logger.get(CommentTransformer.class);

    @Override
    public List<PlayerComment> toLeft(List<CustomerComment> customerComments) throws AppException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<CustomerComment> toRight(List<PlayerComment> playerCommentList) throws AppException {
        List<CustomerComment> customerComments = new ArrayList<CustomerComment>();
        if (!Utils.isEmpty(playerCommentList)) {
            playerCommentList.stream().forEach(playerComment -> {
                if (playerComment != null) {
                    String xml = playerComment.getResult();
                    if (!Utils.isEmpty(xml)) {
                        try {
                            final Unmarshaller unmarshallRequest = JaxbContext.getInstance().getUnmarshaller();
                            CRMAcresMessage crmAcresMessage = (CRMAcresMessage) unmarshallRequest.unmarshal(new StringReader(xml));
                            if (crmAcresMessage != null && crmAcresMessage.getBody() != null && crmAcresMessage.getBody().getComments() != null
                                    && !Utils.isEmpty(crmAcresMessage.getBody().getComments().getComment())) {
                                crmAcresMessage.getBody().getComments().getComment().stream().forEach(comment -> {
                                    if (comment != null) {
                                        CustomerComment customerComment = new CustomerComment();
                                        if (comment.getSite() != null && comment.getSite().getSiteID() != null) {
                                            customerComment.setSiteId(comment.getSite().getSiteID().toString());
                                        }
                                        customerComment.setNumber(String.valueOf(comment.getNumber()));
                                        customerComment.setText(comment.getText());
                                        if (comment.getCreatedDate() != null) {
                                            customerComment.setCreateDate(Dates.toZonedDateTime(comment.getCreatedDate()));
                                        }
                                        if (comment.getExpirationDate() != null) {
                                            customerComment.setExpiryDate(Dates.toZonedDateTime(comment.getExpirationDate()));
                                        }
                                        customerComment.setIsPrivate(comment.isIsPrivate());
                                        customerComment.setIsHighPriority(comment.isIsHighPriority());
                                        customerComments.add(customerComment);
                                    }
                                });
                            }
                        } catch (Exception e) {
                            logger.error(String.format("Error {%d}: %s {}", Errors.UNABLE_TO_CALL_BACKEND,
                                    "Error while converting the response data from StoredProcedureQuery response in CommentTransformer: " + e.getMessage()), e);
                            
                            throw new AppRuntimeException(Errors.UNABLE_TO_CALL_BACKEND, e,
                                    "Error while converting the response data from StoredProcedureQuery response in CommentTransformer: " + e.getMessage());
                        }
                    } else {
                        logger.error(String.format("Error {%d}: %s {}", Errors.NO_PLAYER_COMMENT, "Unable to find customer comment using this criteria."));
                        throw new AppRuntimeException(Errors.NO_PLAYER_COMMENT, "Unable to find customer comment using this criteria.");
                    }
                }
            });
        }
        return customerComments;
    }
}
