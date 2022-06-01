package com.xuanwu.hbase.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Ticket {
    private String id;
    private String primary;
    private String uuid;
    private String biztypeId;
    private String state;
    private String batchName;
    private String appId;
    private String enterpriseId;
    private String sendType;
    private String msgType;
    private String validFrameCount;
    private String specNum;
    private String customNum;
    private String scheduleTime;
    private String deadline;
    private String boeTime;
    private String eoeTime;
    private String postTime;
    private String commitTime;
    private String commitFrom;
    private String userId;
    private String validTickets;
    private String invalidTickets;
    private String version;
    private String remark;
    private String smsType;
}
