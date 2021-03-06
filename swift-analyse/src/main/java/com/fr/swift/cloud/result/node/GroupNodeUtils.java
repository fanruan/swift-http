package com.fr.swift.cloud.result.node;

import com.fr.swift.cloud.result.SwiftNode;
import com.fr.swift.cloud.result.SwiftNodeUtils;
import com.fr.swift.cloud.structure.iterator.MapperIterator;
import com.fr.swift.cloud.util.function.Function;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lyon on 2018/5/22.
 */
public class GroupNodeUtils {

    public static void updateNodeData(SwiftNode root, final List<Map<Integer, Object>> dictionaries) {
        // 从计算结果中提取要展示的结果集
        Iterator<SwiftNode> iterator = new MapperIterator<SwiftNode, SwiftNode>(SwiftNodeUtils.dftNodeIterator(root), new Function<SwiftNode, SwiftNode>() {
            @Override
            public SwiftNode apply(SwiftNode p) {
                // 设置节点的data
                if (p.getDepth() != -1 && !dictionaries.isEmpty() && dictionaries.get(p.getDepth()) != null) {
                    p.setData(dictionaries.get(p.getDepth()).get(p.getDictionaryIndex()));
                }
                return p;
            }
        });
        while (iterator.hasNext()) {
            iterator.next();
        }
    }
}
