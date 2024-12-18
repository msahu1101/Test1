package com.mgmresorts.loyalty.task.balance;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.mgmresorts.common.cache.ICache;
import com.mgmresorts.common.concurrent.Task;
import com.mgmresorts.common.errors.SystemError;
import com.mgmresorts.common.exception.AppException;
import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.common.utils.ThreadContext;
import com.mgmresorts.common.utils.ThreadContext.TransactionContext;
import com.mgmresorts.loyalty.data.IBalanceAccess;
import com.mgmresorts.loyalty.data.to.balance.ICustomerBalanceResponseWrapper;
import com.mgmresorts.loyalty.transformer.IBalanceTransformer;

public class CustomerBalanceTask<W extends ICustomerBalanceResponseWrapper, E> extends Task<ICustomerBalanceResponseWrapper> {

    private final Logger logger = Logger.get(CustomerBalanceTask.class);
    private final IBalanceAccess<E> access;
    private final ICache<W> cache;
    private final IBalanceTransformer<W, List<E>> transformer;
    private final String cacheKey;
    private final int site;
    private final int player;
    private final String threadName;
    private final TransactionContext context;

    @Inject
    public CustomerBalanceTask(@Assisted("player") int player, @Assisted("site") int site, @Assisted String threadName, @Assisted TransactionContext context,
            IBalanceAccess<E> accessTier, ICache<W> cache, IBalanceTransformer<W, List<E>> transformer) {
        super();
        this.player = player;
        this.site = site;
        this.access = accessTier;
        this.cache = cache;
        this.transformer = transformer;
        this.threadName = threadName;
        this.cacheKey = player + ":" + site + ":" + threadName;
        this.context = context;
    }

    @Override
    protected void preExecute() throws AppException {
        ThreadContext.getContext().get().init(context);
    }

    /*
     * If the cache object is not null, nonBlockingGet is used to check the cache if
     * the configuration has caching on, or it uses the access object to call the
     * database if caching if off or if the cache doesn't have the needed data. Data
     * from the database is stored in the cache if caching is on and the database is
     * accessed.
     */
    @Override
    public W execute() throws AppException {
        try {
            logger.trace(String.format("%s thread is executing for player ID: %d and site ID: %d...", threadName, player, site));
            final W result;
            if (cache != null && access != null && transformer != null) {
                result = cache.nonBlockingGet(cacheKey, transformer.getLeftType(), (key) -> {
                    return transformer.toLeft(access.databaseCall(player, site));
                });
                logger.trace(String.format("%s thread is done executing for player ID: %d and site ID: %d.", threadName, player, site));
                return result;
            } else if (access != null && transformer != null) {
                logger.warn(String.format("Cache object for %s thread is null!", threadName));
                return transformer.toLeft(access.databaseCall(player, site));
            } else {
                logger.error(String.format("Error {%s}: Access object and/or transformer for %s thread failed to inject!", SystemError.UNEXPECTED_SYSTEM, threadName));
                throw new AppException(SystemError.UNEXPECTED_SYSTEM);
            }
        } finally {
            ThreadContext.getContext().get().reset();
        }
    }
}
