package org.sotorrent.util.collections;

import java.util.List;

public class CollectionUtils {
    public static <T> List<T>[] split(List<T> list, int partitionCount) {
        int partitionSize = (int)Math.ceil((double)list.size()/partitionCount);
        @SuppressWarnings("unchecked")
        List<T>[] subLists = new List[partitionCount];
        int subListIndex = 0;
        for (int i=0; i<list.size(); i+=partitionSize) {
            subLists[subListIndex] = list.subList(i, Math.min(list.size(), i+partitionSize));
            subListIndex++;
        }
        return subLists;
    }
}
