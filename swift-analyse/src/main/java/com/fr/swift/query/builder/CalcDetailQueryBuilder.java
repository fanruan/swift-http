package com.fr.swift.query.builder;

import com.fr.swift.SwiftContext;
import com.fr.swift.analyse.CalcSegment;
import com.fr.swift.analyse.DetailSegment;
import com.fr.swift.config.service.SwiftMetaDataService;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.query.filter.info.FilterInfo;
import com.fr.swift.query.info.bean.parser.QueryInfoParser;
import com.fr.swift.query.info.bean.query.DetailQueryInfoBean;
import com.fr.swift.query.info.detail.DetailQueryInfo;
import com.fr.swift.query.info.element.dimension.Dimension;
import com.fr.swift.query.limit.Limit;
import com.fr.swift.query.post.meta.SwiftMetaDataUtils;
import com.fr.swift.segment.Segment;
import com.fr.swift.source.SwiftMetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: lucifer
 * @Description:
 * @Date: Created in 2020/12/16
 */
public class CalcDetailQueryBuilder extends BaseQueryBuilder {

    DetailQueryInfo detailQueryInfo;
    SwiftMetaData queriedMetadata;

    CalcDetailQueryBuilder(DetailQueryInfo detailQueryInfo, SwiftMetaData queriedMetadata) {
        this.detailQueryInfo = detailQueryInfo;
        this.queriedMetadata = queriedMetadata;
    }

    public static CalcDetailQueryBuilder of(DetailQueryInfoBean detailQueryInfoBean) throws SwiftMetaDataException {
        DetailQueryInfo queryInfo = (DetailQueryInfo) QueryInfoParser.parse(detailQueryInfoBean);
        SwiftMetaData queriedMetadata = SwiftMetaDataUtils.createMetaData(detailQueryInfoBean);
        return new CalcDetailQueryBuilder(queryInfo, queriedMetadata);
    }

    public CalcSegment buildCalcSegment() throws SwiftMetaDataException {
        //根据分开规则提前区分segment
        List<Segment> segments = filterQuerySegs(detailQueryInfo);
        List<Dimension> dimensions = detailQueryInfo.getDimensions();

        List<FilterInfo> filterInfos = new ArrayList<>();
        if (detailQueryInfo.getFilterInfo() != null) {
            filterInfos.add(detailQueryInfo.getFilterInfo());
        }
        Limit limit = detailQueryInfo.getLimit();
        SwiftMetaData meta = SwiftContext.get().getBean(SwiftMetaDataService.class).getMeta(detailQueryInfo.getTable());
        return new DetailSegment(segments, dimensions, filterInfos, limit, meta, queriedMetadata);
    }
}
