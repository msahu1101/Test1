package com.mgmresorts.loyalty.transformer;

import java.time.ZoneId;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.internal.sessions.ArrayRecord;

import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.common.utils.Dates;
import com.mgmresorts.common.utils.Utils;
import com.mgmresorts.loyalty.data.entity.GiftPoints;
import com.mgmresorts.loyalty.data.entity.Tier;
import com.mgmresorts.loyalty.data.to.TierWrapper;

public class BalanceTierTransformer implements IBalanceTransformer<TierWrapper, List<Tier>> {

    private final Logger logger = Logger.get(BalanceTierTransformer.class);

    @SuppressWarnings({ "rawtypes", "unused" })
    @Override
    public TierWrapper toLeft(List<Tier> right) {
        final TierWrapper tierWrapper = new TierWrapper();
        if (Utils.isEmpty(right)) {
            logger.info("Tier response was empty.");
            return tierWrapper;
        }
        for (Object object : right) {
            final ArrayRecord record = (ArrayRecord) object;
            final Vector fields = record.getFields();
            final Vector values = record.getValues();
            Iterator iteratorField = fields.iterator();
            final GiftPoints giftPoint = new GiftPoints();
            if (record.getValues().get(13) != null) {
                tierWrapper.getTier().setPlayerTierName(record.getValues().get(13).toString());
            }
            if (record.getValues().get(16) != null) {
                tierWrapper.getTier().setAchievedDate(Dates.toZonedDateTime(record.getValues().get(16).toString(), ZoneId.of("UTC")));
            }
            if (record.getValues().get(14) != null) {
                tierWrapper.getTier().setEarnedDate(Dates.toZonedDateTime(record.getValues().get(14).toString(),ZoneId.of("UTC")));
            }
            if (record.getValues().get(15) != null) {
                tierWrapper.getTier().setExpireDate(Dates.toZonedDateTime(record.getValues().get(15).toString(),ZoneId.of("UTC")));
            }
            if (record.getValues().get(17) != null) {
                tierWrapper.getTier().setNextTier(record.getValues().get(17).toString());
            }
            if (record.getValues().get(18) != null) {
                tierWrapper.getTier().setPreviousTier(record.getValues().get(18).toString());
            }
            if (record.getValues().get(19) != null) {
                tierWrapper.getTier().setCreditToNextTier(record.getValues().get(19).toString());
            }
            final Integer linkTierCredit = Integer.parseInt(record.getValues().get(11).toString());
            final String isPlayerLinked = record.getValues().get(9).toString();
            if (isPlayerLinked != null && isPlayerLinked.equals("YES") || linkTierCredit != null && linkTierCredit > 0) {
                tierWrapper.getTier().setTierCredits(linkTierCredit);
            } else {
                tierWrapper.getTier().setTierCredits(Integer.parseInt(record.getValues().get(8).toString()));
            }
        }
        return tierWrapper;
    }

    @Override
    public Class<TierWrapper> getLeftType() {
        return TierWrapper.class;
    }

    @Override
    public List<Tier> toRight(TierWrapper tierWrapper) {
        return null;
    }
}
