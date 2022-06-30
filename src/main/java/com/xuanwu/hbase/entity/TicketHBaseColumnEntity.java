package com.xuanwu.hbase.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.hadoop.hbase.util.Pair;

import java.util.List;

/**
 * @author YaokeHu
 * @description
 * @date 2022-06-01 20:32
 **/
@Data
@Accessors(chain = true)
public class TicketHBaseColumnEntity {
    private String columnFamilyName;
    private List<Pair<String, String>> pairList;
}
