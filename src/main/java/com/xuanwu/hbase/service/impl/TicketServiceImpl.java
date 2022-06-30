package com.xuanwu.hbase.service.impl;

import com.xuanwu.hbase.entity.Ticket;
import com.xuanwu.hbase.entity.TicketHBaseColumnEntity;
import com.xuanwu.hbase.entity.TicketHBaseRowEntity;
import com.xuanwu.hbase.service.TicketService;
import com.xuanwu.hbase.util.HBaseUtils;
import com.xuanwu.hbase.util.SnowflakeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.util.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class TicketServiceImpl implements TicketService {
    private static final String TABLE_NAME = "gsms_sms_ticket";
    private static final String COLUMN_FAMILY_INFO = "info";
    private static final String COLUMN_FAMILY_BIZ = "biz";
    private static final int GROUP_COUNT = 1000;
    private SnowflakeGenerator snowflakeGenerator = new SnowflakeGenerator(0, 0);

    @PostConstruct
    public void init() {
        int count = 100 * 10000;
        saveTicketList(count);
    }

    @Override
    public void saveTicket(Ticket ticket) {
        String id = UUID.randomUUID().toString();
        buildTicketHBaseRowEntity(ticket);

        log.info("Save ticket to HBase, ticket id:{}", id);
    }

    @Override
    public void saveTicketList(int count) {
        Map<Integer, List<Integer>> collect = Stream.iterate(0, index -> index + 1)
            .limit(count)
            .collect(Collectors.groupingBy(index -> index % GROUP_COUNT));
        collect.forEach((key, value) -> {
            long startTime = System.currentTimeMillis();
            List<TicketHBaseRowEntity> ticketRowList = value.stream()
                .map(index -> buildTicketHBaseRowEntity(createTicket()))
                .collect(Collectors.toList());
            log.info("==== save 1000 tickets to HBase");
            HBaseUtils.putRows(TABLE_NAME, ticketRowList);
            log.info("==== cost time:{}", System.currentTimeMillis() -startTime);
        });
    }

    private TicketHBaseRowEntity buildTicketHBaseRowEntity(Ticket ticket) {
        TicketHBaseColumnEntity infoColumnEntity = new TicketHBaseColumnEntity()
            .setColumnFamilyName(COLUMN_FAMILY_INFO)
            .setPairList(buildInfoColumnPairs(ticket));
        TicketHBaseColumnEntity bizColumnEntity = new TicketHBaseColumnEntity()
            .setColumnFamilyName(COLUMN_FAMILY_BIZ)
            .setPairList(buildBizColumnPairs(ticket));
        String rowKey = UUID.randomUUID().toString();
        //log.info("Generate rowKey:{}", rowKey);
        return new TicketHBaseRowEntity()
            .setRowKey(rowKey)
            .setInfoColumn(infoColumnEntity)
            .setBizColumn(bizColumnEntity);
    }


    private Ticket createTicket() {
        return new Ticket()
            .setId(String.valueOf(snowflakeGenerator.nextId()))
            .setPrimary("1")
            .setUuid(UUID.randomUUID().toString())
            .setBiztypeId("1")
            .setState("0")
            .setBatchName(UUID.randomUUID().toString())
            .setSendType("1")
            .setMsgType("9")
            .setValidFrameCount("100")
            .setSpecNum("106900000000000001")
            .setCustomNum("10001")
            .setScheduleTime("2022-06-01 12:00:00")
            .setDeadline("2022-06-03 12:00:00")
            .setBoeTime("2022-06-01 13:00:00")
            .setEoeTime("2022-06-01 14:00:00")
            .setPostTime("2022-06-01: 10:00:00")
            .setCommitTime("2022-06-01: 10:00:01")
            .setCommitFrom("1")
            .setAppId("99")
            .setEnterpriseId("80")
            .setUserId("10");

    }

    private List<Pair<String, String>> buildInfoColumnPairs(Ticket ticket) {
        List<Pair<String, String>> columnPairs = new ArrayList<>();
        columnPairs.add(new Pair<>("id", String.valueOf(ticket.getId())));
        columnPairs.add(new Pair<>("primary", ticket.getPrimary()));
        columnPairs.add(new Pair<>("uuid", ticket.getUuid()));
        columnPairs.add(new Pair<>("biztypeId", ticket.getBiztypeId()));
        columnPairs.add(new Pair<>("state", ticket.getState()));
        columnPairs.add(new Pair<>("batchName", ticket.getBatchName()));
        columnPairs.add(new Pair<>("sendType", ticket.getSendType()));
        columnPairs.add(new Pair<>("msgType", ticket.getMsgType()));
        columnPairs.add(new Pair<>("validFrameCount", ticket.getValidFrameCount()));
        columnPairs.add(new Pair<>("specNum", ticket.getSpecNum()));
        columnPairs.add(new Pair<>("customNum", ticket.getCustomNum()));
        columnPairs.add(new Pair<>("scheduleTime", ticket.getScheduleTime()));
        columnPairs.add(new Pair<>("deadline", ticket.getDeadline()));
        columnPairs.add(new Pair<>("boeTime", ticket.getBoeTime()));
        columnPairs.add(new Pair<>("eoeTime", ticket.getEoeTime()));
        columnPairs.add(new Pair<>("postTime", ticket.getPostTime()));
        columnPairs.add(new Pair<>("commitTime", ticket.getCommitTime()));
        columnPairs.add(new Pair<>("commitFrom", ticket.getCommitFrom()));
        return columnPairs;
    }

    private List<Pair<String, String>> buildBizColumnPairs(Ticket ticket) {
        List<Pair<String, String>> columnPairs = new ArrayList<>();
        columnPairs.add(new Pair<>("appId", ticket.getAppId()));
        columnPairs.add(new Pair<>("enterpriseId", ticket.getEnterpriseId()));
        columnPairs.add(new Pair<>("userId", ticket.getUserId()));
        return columnPairs;
    }
}