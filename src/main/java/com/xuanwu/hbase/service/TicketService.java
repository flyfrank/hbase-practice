package com.xuanwu.hbase.service;

import com.xuanwu.hbase.entity.Ticket;

public interface TicketService {

    void saveTicket(Ticket ticket);

    void saveTicketList(int count);

    Object getTickets();
}
