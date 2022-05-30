package com.xuanwu.hbase.entity;

import lombok.Data;

@Data
public class Ticket {
    private String id;
    private String primary;
    private String uuid;
    private String biztype_id;
    private String state;
    private String batch_name;
    private String app_id;
    private String enterprise_id;
    private String send_type;
    private String msg_type;
    private String valid_frame_count;
    private String spec_num;
    private String custom_num;
    private String schedule_time;
    private String deadline;
    private String boe_time;
    private String eoe_time;
    private String post_time;
    private String commit_time;
    private String commit_from;
    private String user_id;
    private String valid_tickets;
    private String invalid_tickets;
    private String version;
    private String remark;
    private String sms_type;
}
