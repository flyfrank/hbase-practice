package com.xuanwu.hbase.service.impl;

import com.xuanwu.hbase.entity.Ticket;
import com.xuanwu.hbase.entity.TicketHBaseColumnEntity;
import com.xuanwu.hbase.entity.TicketHBaseRowEntity;
import com.xuanwu.hbase.service.TicketService;
import com.xuanwu.hbase.util.HBaseUtils;
import com.xuanwu.hbase.util.SnowflakeGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.util.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class TicketServiceImpl implements TicketService {
    private static final String TABLE_NAME = "gsms_sms_ticket";
    private static final String COLUMN_FAMILY_INFO = "info";
    private static final String COLUMN_FAMILY_BIZ = "biz";
    private static final int GROUP_COUNT = 2000;
    private SnowflakeGenerator snowflakeGenerator = new SnowflakeGenerator(0, 0);
    private ExecutorService executorService = new ThreadPoolExecutor(1, 1, 60, TimeUnit.MINUTES, new LinkedBlockingQueue<>());

    @PostConstruct
    public void init() {
        int count = 100 * 10000;
        HBaseUtils.createTable(TABLE_NAME, Stream.of(COLUMN_FAMILY_INFO, COLUMN_FAMILY_BIZ).collect(Collectors.toList()));
    }

    @Override
    public void saveTicket(Ticket ticket) {
        String id = UUID.randomUUID().toString();
        buildTicketHBaseRowEntity(ticket);

        log.info("Save ticket to HBase, ticket id:{}", id);
    }

    @Override
    public void saveTicketList(int count) {
        if (count == 0) {
            count = 100 * 10000;
        }
        Map<Integer, List<Integer>> collect = Stream.iterate(0, index -> index + 1)
            .limit(count)
            .collect(Collectors.groupingBy(index -> index / GROUP_COUNT));
        collect.forEach((key, value) -> {
            executorService.submit(() -> {
                batchSaveTicket(key, value);
            });
        });
    }

    @Override
    public Object getTickets() {
        String startKey = "0-20220729";
        String endKey = "z-20220729";
        FilterList filterList = new FilterList();
        ResultScanner scanner = HBaseUtils.getScanner(TABLE_NAME, startKey, endKey, filterList);
        List<String> results = new ArrayList<>();
        scanner.forEach(result -> {
            String rowString = new String(result.getRow());
            results.add(rowString);
            System.out.println(result);
        });
        return results;
    }

    private void batchSaveTicket(int groupIndex, List<Integer> value) {
        List<TicketHBaseRowEntity> ticketRowList = value.stream()
            .map(index -> buildTicketHBaseRowEntity(createTicket()))
            .collect(Collectors.toList());
        long startTime = System.currentTimeMillis();
        HBaseUtils.putRows(TABLE_NAME, ticketRowList);
        log.info("groupIndex:{}, save {} tickets to HBase, cost time:{}",groupIndex,  GROUP_COUNT, System.currentTimeMillis() -startTime);
    }

    private TicketHBaseRowEntity buildTicketHBaseRowEntity(Ticket ticket) {
        TicketHBaseColumnEntity infoColumnEntity = new TicketHBaseColumnEntity()
            .setColumnFamilyName(COLUMN_FAMILY_INFO)
            .setPairList(buildInfoColumnPairs(ticket));
        TicketHBaseColumnEntity bizColumnEntity = new TicketHBaseColumnEntity()
            .setColumnFamilyName(COLUMN_FAMILY_BIZ)
            .setPairList(buildBizColumnPairs(ticket));
        String rowKey = buildRowKey();
        return new TicketHBaseRowEntity()
            .setRowKey(rowKey)
            .setInfoColumn(infoColumnEntity)
            .setBizColumn(bizColumnEntity);
    }

    /**
     * ??????RowKey
     * ???????????????????????????
     * ????????????????????????Id
     * ??????????????????Id
     * ?????????????????????????????????
     * @return
     */
    private String buildRowKey() {
        StringBuilder builder = new StringBuilder();
        builder.append(Long.MAX_VALUE - System.currentTimeMillis())
            .append("~")
            .append("00000001")
            .append("~")
            .append(snowflakeGenerator.nextId());
        return builder.toString();
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