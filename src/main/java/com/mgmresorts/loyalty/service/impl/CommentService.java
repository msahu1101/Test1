package com.mgmresorts.loyalty.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.mgmresorts.common.cache.ICache;
import com.mgmresorts.common.errors.SystemError;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.exec.Circuit;
import com.mgmresorts.common.function.HeaderBuilder;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.common.utils.Utils;
import com.mgmresorts.loyalty.data.ICommentAccess;
import com.mgmresorts.loyalty.data.entity.PlayerComment;
import com.mgmresorts.loyalty.data.to.CommentsWrapper;
import com.mgmresorts.loyalty.dto.customer.CustomerComment;
import com.mgmresorts.loyalty.dto.services.PlayerCommentsResponse;
import com.mgmresorts.loyalty.errors.Errors;
import com.mgmresorts.loyalty.service.ICommentService;
import com.mgmresorts.loyalty.transformer.ITransformer;

public class CommentService implements ICommentService {
    private final Logger logger = Logger.get(CommentService.class);
    private final Circuit circuit = Circuit.ofConfig("circuit.breaker.comment");

    @Inject
    private ICommentAccess<PlayerComment> patronCustomerCommentAccess;

    @Inject
    private ITransformer<List<PlayerComment>, List<CustomerComment>> patronCustomerCommentTransformer;

    @Inject
    private ICache<CommentsWrapper> caches;

    @Override
    public PlayerCommentsResponse getCustomerCommentsFromPatron(String player, String highPriority) throws AppException {
        if (Utils.isNumeric(player)) {
            final boolean isHighPriority = Boolean.valueOf(highPriority);
            final PlayerCommentsResponse response = new PlayerCommentsResponse();
            final CommentsWrapper comments = circuit.flow(() -> caches.nonBlockingGet(player, CommentsWrapper.class, (s) -> {
                List<CustomerComment> customerComments = new ArrayList<>();
                CommentsWrapper commentsWrapper = new CommentsWrapper();
                List<PlayerComment> result = patronCustomerCommentAccess.databaseCallNative(player, isHighPriority);
                customerComments = patronCustomerCommentTransformer.toRight(result);
                commentsWrapper.setComments(customerComments);
                return commentsWrapper;
            }), SystemError.UNABLE_TO_CALL_BACKEND);
            response.setComments(comments.getComments());
            response.setHeader(HeaderBuilder.buildHeader());
            return response;
        } else {
            logger.error("Invalid Patron Id.");
            throw new AppException(Errors.INVALID_PATRON_ID, player);
        }
    }
}
