/*
 * Copyright (c) 2022年 by XuanWu Wireless Technology Co.Ltd.
 *             All rights reserved
 */
package com.xuanwu.hbase.controller;

import com.xuanwu.hbase.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huyaoke
 * @date 2022/7/29
 */
@RestController
@RequestMapping("/ticket")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping("/list")
    public Object getTickets() {
        return ticketService.getTickets();
    }


    @GetMapping("/insert")
    public Object insertTickets(@RequestParam("count") int count) {
        ticketService.saveTicketList(count);
        return "insert succeed";
    }
}
