package org.lamisplus.modules.base.web.util;

import io.searchbox.core.search.aggregation.TermsAggregation;
import org.lamisplus.modules.base.web.vm.AggregateVM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregateUtil {

    public static List<AggregateVM> getAggregates(TermsAggregation aggregation, String field, String title) {
        List<AggregateVM> responseAggregates = new ArrayList<>();
        aggregation.getBuckets().forEach(entry -> {
            AggregateVM aggregate = new AggregateVM();
            aggregate.setField(field);
            aggregate.setTitle(title);
            aggregate.setKey(entry.getKeyAsString());
            aggregate.setCount(entry.getCount());
            responseAggregates.add(aggregate);
        });
        return responseAggregates;
    }

    public static Map<String, List<AggregateVM>> aggregateMap(List<AggregateVM> aggregates) {
        Map<String, List<AggregateVM>> map = new HashMap<>();
        aggregates.forEach(aggregate -> {
            if (map.containsKey(aggregate.getTitle())) {
                List<AggregateVM> agg = map.get(aggregate.getTitle());
                agg.add(aggregate);
                map.put(aggregate.getTitle(), agg);
            } else {
                List<AggregateVM> agg = new ArrayList<>();
                agg.add(aggregate);
                map.put(aggregate.getTitle(), agg);
            }
        });
        return map;
    }
}
