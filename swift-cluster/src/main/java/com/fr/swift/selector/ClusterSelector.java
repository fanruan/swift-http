package com.fr.swift.selector;

import com.fr.swift.ClusterNodeManager;
import com.fr.swift.basics.Selector;

/**
 * This class created on 2018/6/13
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class ClusterSelector implements Selector<ClusterNodeManager> {

    private ClusterNodeManager clusterNodeManager;

    enum DefaultNodeManager implements ClusterNodeManager {

        INSTANCE;

        @Override
        public void setMasterNode(Object masterNode) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getMasterNode() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getCurrentNode() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getCurrentId() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getMasterId() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isCluster() {
            return false;
        }

        @Override
        public void setCluster(boolean cluster) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isMaster() {
            throw new UnsupportedOperationException();
        }
    }

    public ClusterSelector() {
        clusterNodeManager = DefaultNodeManager.INSTANCE;
    }

    private static final ClusterSelector INSTANCE = new ClusterSelector();

    public static ClusterSelector getInstance() {
        return INSTANCE;
    }

    @Override
    public ClusterNodeManager getFactory() {
        synchronized (ClusterSelector.class) {
            return this.clusterNodeManager;
        }
    }

    @Override
    public void switchFactory(ClusterNodeManager clusterNodeManager) {
        synchronized (ClusterSelector.class) {
            this.clusterNodeManager = clusterNodeManager;
        }
    }
}
