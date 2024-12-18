package com.mgmresorts.loyalty.transformer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.persistence.internal.sessions.ArrayRecord;

import com.mgmresorts.common.logging.Logger;
import com.mgmresorts.common.utils.Utils;
import com.mgmresorts.loyalty.data.entity.GiftPoints;
import com.mgmresorts.loyalty.data.to.GiftPointsWrapper;

public class BalanceGiftPointsTransformer implements IBalanceTransformer<GiftPointsWrapper, List<GiftPoints>> {

    private final Logger logger = Logger.get(BalanceGiftPointsTransformer.class);

    @SuppressWarnings({ "rawtypes", "unused" })
    @Override
    public GiftPointsWrapper toLeft(List<GiftPoints> right) {
        GiftPointsWrapper giftPointsWrapper = new GiftPointsWrapper();
        if (Utils.isEmpty(right)) {
            logger.info("Gift points call returned an empty response.");
            return giftPointsWrapper;
        }
        giftPointsWrapper.setGiftPoints(new com.mgmresorts.loyalty.dto.customer.GiftPoints());
        final List<GiftPoints> giftPoints = new ArrayList<>();
        for (Object object : right) {
            final ArrayRecord record = (ArrayRecord) object;
            final Vector fields = record.getFields();
            final Vector values = record.getValues();
            Iterator iteratorField = fields.iterator();
            final GiftPoints giftPoint = new GiftPoints();
            if (record.getValues().get(0) != null) {
                giftPoint.setPlayerId1(Integer.parseInt(record.getValues().get(0).toString()));
            }
            if (record.getValues().get(28) != null) {
                giftPointsWrapper.getGiftPoints().setHgsPoints(Double.parseDouble(record.getValues().get(28).toString()));
            }
            if (record.getValues().get(28) != null) {
                giftPointsWrapper.getGiftPoints().setGiftPointsLinked(Double.parseDouble(record.getValues().get(30).toString()));
            }
        }
        return giftPointsWrapper;
    }

    @Override
    public Class<GiftPointsWrapper> getLeftType() {
        return GiftPointsWrapper.class;
    }

    @Override
    public List<GiftPoints> toRight(GiftPointsWrapper giftBalancesWrapper) {
        return null;
    }
}
