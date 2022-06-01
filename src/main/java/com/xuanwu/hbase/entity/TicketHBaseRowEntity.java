package com.xuanwu.hbase.entity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YaokeHu
 * @description
 * @date 2022-06-01 20:34
 **/
@Data
@Accessors(chain = true)
public class TicketHBaseRowEntity {
    private String rowKey;
    private TicketHBaseColumnEntity infoColumn;
    private TicketHBaseColumnEntity bizColumn;
}
