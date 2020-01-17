package org.lamisplus.modules.base.web.vm;

import lombok.Data;

@Data
public class AggregateVM {
    private String title;
    private String field;
    private String key;
    private Long count = 0L;
}
