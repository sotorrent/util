package org.sotorrent.util.collections;

import java.util.List;

public class CollectionUtils {
    public static List<Integer>[] split(List<Integer> integers, int partitionCount) {
        int partitionSize = (int)Math.ceil((double)integers.size()/partitionCount);
        @SuppressWarnings("unchecked")
        List<Integer>[] subLists = new List[partitionCount];
        int subListIndex = 0;
        for (int i=0; i<integers.size(); i+=partitionSize) {
            subLists[subListIndex] = integers.subList(i, Math.min(integers.size(), i+partitionSize));
            subListIndex++;
        }
        return subLists;
    }
}
