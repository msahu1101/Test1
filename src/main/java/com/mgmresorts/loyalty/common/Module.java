package com.mgmresorts.loyalty.common;

import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Names;
import com.mgmresorts.common.cache.ICache;
import com.mgmresorts.common.cache.RedisCache;
import com.mgmresorts.common.cache.RedisCluster;
import com.mgmresorts.common.concurrent.Executor;
import com.mgmresorts.common.concurrent.Executors;
import com.mgmresorts.common.concurrent.IShutdownHook;
import com.mgmresorts.common.concurrent.LoggerHook;
import com.mgmresorts.common.config.Runtime;
import com.mgmresorts.common.errors.ErrorManager.IError;
import com.mgmresorts.common.guice.BaseModule;
import com.mgmresorts.common.http.HttpService;
import com.mgmresorts.common.http.IHttpService;
import com.mgmresorts.loyalty.common.Providers.LmeDsProvider;
import com.mgmresorts.loyalty.common.Providers.LmeEmProvider;
import com.mgmresorts.loyalty.common.Providers.MlifeDsProvider;
import com.mgmresorts.loyalty.common.Providers.MlifeEmProvider;
import com.mgmresorts.loyalty.common.Providers.NonDatabaseEmProvider;
import com.mgmresorts.loyalty.common.Providers.PcisDsProvider;
import com.mgmresorts.loyalty.common.Providers.PcisEmProvider;
import com.mgmresorts.loyalty.common.Providers.PlayerDsProvider;
import com.mgmresorts.loyalty.common.Providers.PlayerEmProvider;
import com.mgmresorts.loyalty.common.Providers.ReportingDsProvider;
import com.mgmresorts.loyalty.common.Providers.ReportingEmProvider;
import com.mgmresorts.loyalty.common.Providers.WorkDsProvider;
import com.mgmresorts.loyalty.common.Providers.WorkEmProvider;
import com.mgmresorts.loyalty.data.IBalanceAccess;
import com.mgmresorts.loyalty.data.ICommentAccess;
import com.mgmresorts.loyalty.data.ILinkedPlayerInfoAccess;
import com.mgmresorts.loyalty.data.IPromotionsAccess;
import com.mgmresorts.loyalty.data.ISlotDollarBalanceAccess;
import com.mgmresorts.loyalty.data.IStopCodeAccess;
import com.mgmresorts.loyalty.data.ITaxInfoAccess;
import com.mgmresorts.loyalty.data.entity.Balance;
import com.mgmresorts.loyalty.data.entity.GiftBalances;
import com.mgmresorts.loyalty.data.entity.GiftPoints;
import com.mgmresorts.loyalty.data.entity.PlayerComment;
import com.mgmresorts.loyalty.data.entity.PlayerLink;
import com.mgmresorts.loyalty.data.entity.SlotDollarBalance;
import com.mgmresorts.loyalty.data.entity.StopCode;
import com.mgmresorts.loyalty.data.entity.Tier;
import com.mgmresorts.loyalty.data.impl.BalanceAccess;
import com.mgmresorts.loyalty.data.impl.BalanceGiftBalancesAccess;
import com.mgmresorts.loyalty.data.impl.BalanceGiftPointsAccess;
import com.mgmresorts.loyalty.data.impl.BalanceTierAccess;
import com.mgmresorts.loyalty.data.impl.CommentAccess;
import com.mgmresorts.loyalty.data.impl.LinkedPlayerInfoAccess;
import com.mgmresorts.loyalty.data.impl.PromotionsAccess;
import com.mgmresorts.loyalty.data.impl.SlotDollarBalanceAccess;
import com.mgmresorts.loyalty.data.impl.StopCodeAccess;
import com.mgmresorts.loyalty.data.impl.TaxInfoAccess;
import com.mgmresorts.loyalty.data.to.BalanceWrapper;
import com.mgmresorts.loyalty.data.to.CommentsWrapper;
import com.mgmresorts.loyalty.data.to.GiftBalancesWrapper;
import com.mgmresorts.loyalty.data.to.GiftPointsWrapper;
import com.mgmresorts.loyalty.data.to.LinkedPlayersWrapper;
import com.mgmresorts.loyalty.data.to.StopCodeWrapper;
import com.mgmresorts.loyalty.data.to.TierWrapper;
import com.mgmresorts.loyalty.data.to.balance.CustomerBalanceWrapperVisitor;
import com.mgmresorts.loyalty.data.to.balance.ICustomerBalanceWrapperVisitor;
import com.mgmresorts.loyalty.dto.customer.CustomerComment;
import com.mgmresorts.loyalty.dto.customer.CustomerPromotion;
import com.mgmresorts.loyalty.dto.customer.CustomerStopCode;
import com.mgmresorts.loyalty.dto.customer.CustomerTaxInfo;
import com.mgmresorts.loyalty.dto.customer.LinkedPlayer;
import com.mgmresorts.loyalty.dto.patron.taxinformation.ResultSet;
import com.mgmresorts.loyalty.errors.Errors;
import com.mgmresorts.loyalty.service.IBalanceService;
import com.mgmresorts.loyalty.service.ICommentService;
import com.mgmresorts.loyalty.service.ILinkedPlayerInfoService;
import com.mgmresorts.loyalty.service.IPromoEventTicketIssuanceService;
import com.mgmresorts.loyalty.service.IPromoEventsService;
import com.mgmresorts.loyalty.service.IPromotionsService;
import com.mgmresorts.loyalty.service.IRcxService;
import com.mgmresorts.loyalty.service.ISlotDollarBalanceService;
import com.mgmresorts.loyalty.service.IStopCodeService;
import com.mgmresorts.loyalty.service.ITaxInfoService;
import com.mgmresorts.loyalty.service.impl.BalanceService;
import com.mgmresorts.loyalty.service.impl.CommentService;
import com.mgmresorts.loyalty.service.impl.LinkedPlayerInfoService;
import com.mgmresorts.loyalty.service.impl.PromoEventTicketIssuanceService;
import com.mgmresorts.loyalty.service.impl.PromoEventsService;
import com.mgmresorts.loyalty.service.impl.PromotionsService;
import com.mgmresorts.loyalty.service.impl.RcxService;
import com.mgmresorts.loyalty.service.impl.SlotDollarBalanceService;
import com.mgmresorts.loyalty.service.impl.StopCodeService;
import com.mgmresorts.loyalty.service.impl.TaxInfoService;
import com.mgmresorts.loyalty.task.balance.CustomerBalanceTask;
import com.mgmresorts.loyalty.task.balance.ICustomerBalanceTaskFactory;
import com.mgmresorts.loyalty.transformer.BalanceGiftBalancesTransformer;
import com.mgmresorts.loyalty.transformer.BalanceGiftPointsTransformer;
import com.mgmresorts.loyalty.transformer.BalanceTierTransformer;
import com.mgmresorts.loyalty.transformer.BalanceTransformer;
import com.mgmresorts.loyalty.transformer.CommentTransformer;
import com.mgmresorts.loyalty.transformer.IBalanceTransformer;
import com.mgmresorts.loyalty.transformer.ITransformer;
import com.mgmresorts.loyalty.transformer.LinkedPlayerTransformer;
import com.mgmresorts.loyalty.transformer.PromoTransformer;
import com.mgmresorts.loyalty.transformer.StopCodeTransformer;
import com.mgmresorts.loyalty.transformer.TaxInfoTransformer;
import jakarta.persistence.EntityManager;
import redis.clients.jedis.JedisCluster;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public class Module extends BaseModule {


    @Override
    protected void preConfigure() {
        bind(IError.class).to(Errors.class).in(Scopes.SINGLETON);
    }

    @Override
    protected void postConfigure() {
        try {
            //bind(IAuthorizer.class).toInstance(RoleAuthorizer.get());
            //bind(IAuthorizer.class).to(EagerAuthorizer.class).in(Scopes.SINGLETON);
            bind(IHttpService.class).to(HttpService.class).in(Scopes.SINGLETON);
            bind(Executors.class).toInstance(Executors.instance());
            bind(IShutdownHook.class).to(LoggerHook.class).in(Scopes.SINGLETON);

            // Cache Injection
            bind(new TypeLiteral<ICache<GiftPointsWrapper>>() {
            }).toInstance(RedisCache.as(GiftPointsWrapper.class, "gift-points"));
            bind(new TypeLiteral<ICache<TierWrapper>>() {
            }).toInstance(RedisCache.as(TierWrapper.class, "tier"));
            bind(new TypeLiteral<ICache<BalanceWrapper>>() {
            }).toInstance(RedisCache.as(BalanceWrapper.class, "balance"));
            bind(new TypeLiteral<ICache<GiftBalancesWrapper>>() {
            }).toInstance(RedisCache.as(GiftBalancesWrapper.class, "gift-balances"));
            bind(new TypeLiteral<ICache<CommentsWrapper>>() {
            }).toInstance(RedisCache.as(CommentsWrapper.class, "player-comment"));

            // Customer Balance Access Injection
            bind(new TypeLiteral<IBalanceAccess<GiftBalances>>() {
            }).to(BalanceGiftBalancesAccess.class).in(Scopes.SINGLETON);
            bind(new TypeLiteral<IBalanceAccess<GiftPoints>>() {
            }).to(BalanceGiftPointsAccess.class).in(Scopes.SINGLETON);
            bind(new TypeLiteral<IBalanceAccess<Tier>>() {
            }).to(BalanceTierAccess.class).in(Scopes.SINGLETON);
            bind(new TypeLiteral<IBalanceAccess<Balance>>() {
            }).to(BalanceAccess.class).in(Scopes.SINGLETON);

            bind(new TypeLiteral<ICommentAccess<PlayerComment>>() {
            }).to(CommentAccess.class).in(Scopes.SINGLETON);
            bind(ITaxInfoAccess.class).to(TaxInfoAccess.class).in(Scopes.SINGLETON);

            // Customer Balance Transformer Injection
            bind(new TypeLiteral<IBalanceTransformer<GiftBalancesWrapper, List<GiftBalances>>>() {
            }).to(BalanceGiftBalancesTransformer.class).in(Scopes.SINGLETON);
            bind(new TypeLiteral<IBalanceTransformer<GiftPointsWrapper, List<GiftPoints>>>() {
            }).to(BalanceGiftPointsTransformer.class).in(Scopes.SINGLETON);
            bind(new TypeLiteral<IBalanceTransformer<TierWrapper, List<Tier>>>() {
            }).to(BalanceTierTransformer.class).in(Scopes.SINGLETON);
            bind(new TypeLiteral<IBalanceTransformer<BalanceWrapper, List<Balance>>>() {
            }).to(BalanceTransformer.class).in(Scopes.SINGLETON);
            bind(new TypeLiteral<ITransformer<List<PlayerComment>, List<CustomerComment>>>() {
            }).to(CommentTransformer.class).in(Scopes.SINGLETON);

            bind(IStopCodeService.class).to(StopCodeService.class).in(Scopes.SINGLETON);
            bind(IBalanceService.class).to(BalanceService.class).in(Scopes.SINGLETON);
            bind(ITaxInfoService.class).to(TaxInfoService.class).in(Scopes.SINGLETON);
            bind(ICommentService.class).to(CommentService.class).in(Scopes.SINGLETON);

            bind(IRcxService.class).to(RcxService.class).in(Scopes.SINGLETON);

            bind(new TypeLiteral<ITransformer<CustomerTaxInfo, ResultSet>>() {
            }).to(TaxInfoTransformer.class).in(Scopes.SINGLETON);
            bind(ICustomerBalanceWrapperVisitor.class).to(CustomerBalanceWrapperVisitor.class);

            bind(DataSource.class).annotatedWith(Names.named("lme")).toProvider(LmeDsProvider.class).in(Scopes.SINGLETON);
            bind(EntityManager.class).annotatedWith(Names.named("lme")).toProvider(LmeEmProvider.class);

            bind(DataSource.class).annotatedWith(Names.named("pcis")).toProvider(PcisDsProvider.class).in(Scopes.SINGLETON);
            bind(EntityManager.class).annotatedWith(Names.named("pcis")).toProvider(PcisEmProvider.class);

            bind(DataSource.class).annotatedWith(Names.named("mlife")).toProvider(MlifeDsProvider.class).in(Scopes.SINGLETON);
            bind(EntityManager.class).annotatedWith(Names.named("mlife")).toProvider(MlifeEmProvider.class);

            bind(DataSource.class).annotatedWith(Names.named("work")).toProvider(WorkDsProvider.class).in(Scopes.SINGLETON);
            bind(EntityManager.class).annotatedWith(Names.named("work")).toProvider(WorkEmProvider.class);

            bind(DataSource.class).annotatedWith(Names.named("player")).toProvider(PlayerDsProvider.class).in(Scopes.SINGLETON);
            bind(EntityManager.class).annotatedWith(Names.named("player")).toProvider(PlayerEmProvider.class);

            bind(DataSource.class).annotatedWith(Names.named("reporting")).toProvider(ReportingDsProvider.class).in(Scopes.SINGLETON);
            bind(EntityManager.class).annotatedWith(Names.named("reporting")).toProvider(ReportingEmProvider.class);

            bind(EntityManager.class).annotatedWith(Names.named("no-backend")).toProvider(NonDatabaseEmProvider.class);

            // stop code
            bind(new TypeLiteral<ICache<StopCodeWrapper>>() {
            }).toInstance(RedisCache.as(StopCodeWrapper.class, "stop-code"));
            bind(new TypeLiteral<ITransformer<List<CustomerStopCode>, List<StopCode>>>() {
            }).to(StopCodeTransformer.class).in(Scopes.SINGLETON);
            bind(IStopCodeAccess.class).to(StopCodeAccess.class).in(Scopes.SINGLETON);

            bind(Executor.class).annotatedWith(Names.named("customer-balance")).toProvider(Providers.CustomerBalanceExecutorProvider.class).in(Scopes.SINGLETON);

            // Linked Player Info
            bind(ILinkedPlayerInfoService.class).to(LinkedPlayerInfoService.class).in(Scopes.SINGLETON);
            bind(ILinkedPlayerInfoAccess.class).to(LinkedPlayerInfoAccess.class).in(Scopes.SINGLETON);
            bind(new TypeLiteral<ICache<LinkedPlayersWrapper>>() {
            }).toInstance(RedisCache.as(LinkedPlayersWrapper.class, "player-link"));
            bind(new TypeLiteral<ITransformer<List<LinkedPlayer>, List<PlayerLink>>>() {
            }).to(LinkedPlayerTransformer.class).in(Scopes.SINGLETON);

            // Customer Promotions
            bind(IPromotionsService.class).to(PromotionsService.class).in(Scopes.SINGLETON);
            bind(IPromotionsAccess.class).to(PromotionsAccess.class).in(Scopes.SINGLETON);
            bind(new TypeLiteral<ITransformer<List<CustomerPromotion>, com.mgmresorts.loyalty.dto.patron.customerpromo.CRMAcresMessage>>() {
            }).to(PromoTransformer.class).in(Scopes.SINGLETON);
            bind(new TypeLiteral<ICache<String>>() {
            }).toInstance(RedisCache.as(String.class, "customer-promo"));

            bind(IPromoEventTicketIssuanceService.class).to(PromoEventTicketIssuanceService.class).in(Scopes.SINGLETON);
            bind(IPromoEventsService.class).to(PromoEventsService.class).in(Scopes.SINGLETON);

            // Slot dollar balance
            bind(ISlotDollarBalanceService.class).to(SlotDollarBalanceService.class).in(Scopes.SINGLETON);
            bind(ISlotDollarBalanceAccess.class).to(SlotDollarBalanceAccess.class).in(Scopes.SINGLETON);
            bind(new TypeLiteral<ICache<SlotDollarBalance>>() {
            }).toInstance(RedisCache.as(SlotDollarBalance.class, "slot-dollar-balance"));

            /*
             * Instance
             */
            bind(Runtime.class).toInstance(Runtime.get());
            bind(JaxbContext.class).toInstance(JaxbContext.getInstance());
            if (!Boolean.parseBoolean(Runtime.get().getConfiguration("cache.disabled.global"))) {
                bind(new TypeLiteral<Optional<JedisCluster>>() {
                }).toInstance(Optional.of(new RedisCluster()));
            } else {
                bind(new TypeLiteral<Optional<JedisCluster>>() {
                }).toInstance(Optional.empty());
            }
            /*
             * Factories
             */
            binder().install(new FactoryModuleBuilder().implement(CustomerBalanceTask.class, Names.named("balance"),
                    new TypeLiteral<CustomerBalanceTask<BalanceWrapper, Balance>>() {
            }).implement(CustomerBalanceTask.class, Names.named("gift points"), new TypeLiteral<CustomerBalanceTask<GiftPointsWrapper, GiftPoints>>() {
            }).implement(CustomerBalanceTask.class, Names.named("gift balances"), new TypeLiteral<CustomerBalanceTask<GiftBalancesWrapper, GiftBalances>>() {
            }).implement(CustomerBalanceTask.class, Names.named("tier"), new TypeLiteral<CustomerBalanceTask<TierWrapper, Tier>>() {
            }).build(ICustomerBalanceTaskFactory.class));

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }



}
