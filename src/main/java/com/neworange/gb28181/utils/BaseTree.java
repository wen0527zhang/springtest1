package com.neworange.gb28181.utils;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.text.Collator;
import java.util.Comparator;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/4/10 19:26
 * @description
 */
@Data
public class BaseTree <T> implements Comparable<BaseTree>{
    private String id;

    private String deviceId;
    private String pid;
    private String name;
    private boolean parent;

    private T basicData;
    @Override
    public int compareTo(@NotNull BaseTree treeNode) {
        if (this.parent || treeNode.isParent()) {
            if (!this.parent && !treeNode.isParent()) {
                Comparator cmp = Collator.getInstance(java.util.Locale.CHINA);
                return cmp.compare(treeNode.getName(), this.getName());
            }else {
                if (this.isParent()) {
                    return 1;
                }else {
                    return -1;
                }
            }
        }else{
            Comparator cmp = Collator.getInstance(java.util.Locale.CHINA);
            return cmp.compare(treeNode.getName(), this.getName());
        }
    }
}
