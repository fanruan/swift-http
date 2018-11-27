package com.fr.swift.service;

import com.fineio.FineIO;
import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.general.ComparatorUtils;
import com.fr.swift.basics.base.selector.ProxySelector;
import com.fr.swift.config.bean.SegmentKeyBean;
import com.fr.swift.config.service.SwiftSegmentLocationService;
import com.fr.swift.config.service.SwiftSegmentService;
import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.CubeUtil;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.event.base.EventResult;
import com.fr.swift.event.history.TransCollateLoadEvent;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.repository.SwiftRepositoryManager;
import com.fr.swift.segment.SegmentKey;
import com.fr.swift.segment.SegmentUtils;
import com.fr.swift.segment.event.SegmentEvent;
import com.fr.swift.selector.ClusterSelector;
import com.fr.swift.service.listener.RemoteSender;
import com.fr.swift.structure.Pair;
import com.fr.swift.task.service.ServiceTaskExecutor;

import java.util.Collections;

/**
 * @author anchore
 * @date 2018/9/11
 * @see SegmentEvent#UPLOAD_HISTORY
 */
public class UploadHistoryListener extends Listener<SegmentKey> {

    private static final SwiftRepositoryManager REPO = SwiftContext.get().getBean(SwiftRepositoryManager.class);

    private static final ServiceTaskExecutor SVC_EXEC = SwiftContext.get().getBean(ServiceTaskExecutor.class);

    private static final SwiftSegmentService SEG_SVC = SwiftContext.get().getBean("segmentServiceProvider", SwiftSegmentService.class);

    private static final SwiftSegmentLocationService LOCATION_SVC = SwiftContext.get().getBean(SwiftSegmentLocationService.class);

    @Override
    public void on(Event event, final SegmentKey segKey) {
        upload(segKey);
    }

    private static void upload(final SegmentKey segKey) {
        FineIO.doWhenFinished(new Runnable() {
            @Override
            public void run() {
                if (ClusterSelector.getInstance().getFactory().isCluster()) {
                    String local = CubeUtil.getAbsoluteSegPath(segKey);
                    String remote = String.format("%s/%s", segKey.getSwiftSchema().getDir(), segKey.getUri().getPath());
                    try {
                        REPO.currentRepo().copyToRemote(local, remote);

                        notifyDownload(segKey);
                    } catch (Exception e) {
                        SwiftLoggers.getLogger().error("Cannot upload Segment which path is {}", local, e);
                    }
                } else {
                    SegmentKey realtimeSegKey = getRealtimeSegKey(segKey);
                    SEG_SVC.removeSegments(Collections.singletonList(realtimeSegKey));
                    SegmentUtils.clearSegment(realtimeSegKey);
                }
            }
        });

    }

    private static void notifyDownload(final SegmentKey segKey) {
        final String currentClusterId = ClusterSelector.getInstance().getFactory().getCurrentId();
        EventResult result = (EventResult) ProxySelector.getInstance().getFactory().getProxy(RemoteSender.class).trigger(
                new TransCollateLoadEvent(Pair.of(segKey.getTable().getId(), Collections.singletonList(segKey.toString())), currentClusterId));
        if (result.isSuccess()) {
            String clusterId = result.getClusterId();
            SegmentKey realtimeSegKey = getRealtimeSegKey(segKey);
            SEG_SVC.removeSegments(Collections.singletonList(realtimeSegKey));
            SegmentUtils.clearSegment(realtimeSegKey);

            // 删除本机上的history分布
            if (!ComparatorUtils.equals(clusterId, currentClusterId)) {
                LOCATION_SVC.delete(segKey.getTable().getId(), currentClusterId, segKey.toString());
                SegmentUtils.clearSegment(segKey);
            }
        } else {
            SwiftLoggers.getLogger().error(result.getError());
        }
    }

    private static SegmentKey getRealtimeSegKey(SegmentKey hisSegKey) {
        return new SegmentKeyBean(hisSegKey.getTable(), hisSegKey.getOrder(), StoreType.MEMORY, hisSegKey.getSwiftSchema());
    }

    private static final UploadHistoryListener INSTANCE = new UploadHistoryListener();

    public static void listen() {
        // todo 何时listen
        EventDispatcher.listen(SegmentEvent.UPLOAD_HISTORY, INSTANCE);
    }
}