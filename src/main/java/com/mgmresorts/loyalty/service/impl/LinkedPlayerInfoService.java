
package com.mgmresorts.loyalty.service.impl;

import java.util.List;

import javax.inject.Inject;

import com.mgmresorts.common.cache.ICache;
import com.mgmresorts.common.errors.SystemError;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.exec.Circuit;
import com.mgmresorts.common.function.HeaderBuilder;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.common.utils.Utils;
import com.mgmresorts.loyalty.data.ILinkedPlayerInfoAccess;
import com.mgmresorts.loyalty.data.entity.PlayerLink;
import com.mgmresorts.loyalty.data.to.LinkedPlayersWrapper;
import com.mgmresorts.loyalty.dto.customer.LinkedPlayer;
import com.mgmresorts.loyalty.dto.services.LinkedPlayersResponse;
import com.mgmresorts.loyalty.errors.ApplicationError;
import com.mgmresorts.loyalty.errors.Errors;
import com.mgmresorts.loyalty.service.ILinkedPlayerInfoService;
import com.mgmresorts.loyalty.transformer.ITransformer;

public class LinkedPlayerInfoService implements ILinkedPlayerInfoService {

    private final Logger logger = Logger.get(LinkedPlayerInfoService.class);
    private final Circuit circuit = Circuit.ofConfig("circuit.breaker.linkedplayer");

    @Inject
    private ICache<LinkedPlayersWrapper> playerLinkedCache;
    @Inject
    private ILinkedPlayerInfoAccess linkedPlayerInfoAccess;
    @Inject
    private ITransformer<List<LinkedPlayer>, List<PlayerLink>> transformer;

    public LinkedPlayersResponse getLinkedPlayerInfo(String player) throws AppException {
        logger.trace("Linked Player Info getLinkedPlayerInfo service invoked...");

        int playerId = Utils.isNumeric(player) ? Integer.parseInt(player) : -1;
        if (playerId == -1) {
            throw new AppException(Errors.INVALID_PATRON_ID, player);
        }
        final LinkedPlayersWrapper linkedPlayersCache = circuit.flow(() -> playerLinkedCache.nonBlockingGet(String.valueOf(playerId), LinkedPlayersWrapper.class, (s) -> {
            final List<PlayerLink> playerLinksResult = linkedPlayerInfoAccess.getLinkedPlayerInfo(playerId);
            final LinkedPlayersWrapper wrapper = new LinkedPlayersWrapper();
            wrapper.setLinkedPlayers(playerLinksResult);
            return wrapper;
        }), SystemError.UNABLE_TO_CALL_BACKEND);

        if (Utils.isEmpty(linkedPlayersCache.getLinkedPlayers())) {
            logger.info("Player has no linked members..");
            throw new AppException(ApplicationError.NO_LINKED_MEMBERS, "Player has no linked members");
        }
        final LinkedPlayersResponse response = new LinkedPlayersResponse();
        response.setLinkedPlayers(transformer.toLeft(linkedPlayersCache.getLinkedPlayers()));
        response.setHeader(HeaderBuilder.buildHeader());
        return response;
    }
}
