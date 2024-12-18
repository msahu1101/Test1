package com.mgmresorts.loyalty.data.impl;

import java.util.List;

import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.loyalty.data.ICommentAccess;
import com.mgmresorts.loyalty.data.entity.PlayerComment;
import com.mgmresorts.loyalty.data.support.SqlSupport;

public class CommentAccess extends SqlSupport<PlayerComment, String> implements ICommentAccess<PlayerComment> {

    private final Logger logger = Logger.get(CommentAccess.class);

    @Override
    public List<PlayerComment> databaseCallNative(String player, boolean isHighPriority) throws AppException {
        List<PlayerComment> comments = callByNamedProcedure(DB.REPORTING, PlayerComment.PLAYER_COMMENTS_CALL, PlayerComment.class, (sp) -> {
            sp.setParameter("XMLParam", mappingRequest(player, isHighPriority));
        });
        logger.debug("comment database response", comments.toString());
        return comments;
    }

    public String mappingRequest(String playerId, boolean isHighPriority) {
        String xml = "<?xml version=\"1.0\"?><CRMAcresMessage><Header><TimeStamp></TimeStamp><Operation Data=\"Comments\" Operand=\"Request\"/></Header><PlayerID>" + playerId
                + "</PlayerID><Body><Comments><Filter><HighPriority>" + isHighPriority + "</HighPriority></Filter></Comments></Body></CRMAcresMessage>";
        return xml;
    }

    @Override
    public Class<PlayerComment> getEntityType() {
        return PlayerComment.class;
    }
}
