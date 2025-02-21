/*
 * Copyright (c) 2020, New Orange Group and/or its affiliates. All rights reserved.
 */

package com.neworange.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * The utility class to split the list to list groups.
 *
 * @author liuhongding@neworangegroup.com
 * @date 2020-07-04
 */
public class ListSpliter {
    public static <T> List<List<T>> splitGroupList(List<T> dataList, int stepSize) {
        int size = dataList.size();
        int groupSize = size / stepSize;
        if (size > groupSize * stepSize) {
            groupSize += 1;
        }


        List<List<T>> retList = new ArrayList<>(groupSize);
        for (int index = 0; index < groupSize; index++) {
            int start = index * stepSize;
            int end = start + stepSize;
            if (end >= size) {
                end = size;
            }
            retList.add(dataList.subList(start, end));
        }

        return retList;
    }
}
