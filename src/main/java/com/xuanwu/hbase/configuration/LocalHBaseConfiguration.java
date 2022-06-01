package com.xuanwu.hbase.configuration;

import com.xuanwu.hbase.config.HBaseConfig;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Map;

/**
 * @author YaokeHu
 * @description
 * @date 2022-05-31 19:27
 **/
@Configuration
public class LocalHBaseConfiguration {
    @Autowired
    private HBaseConfig hBaseConfig;

    @Bean
    public Admin admin() {
        Admin admin = null;
        try {
            Connection connection = ConnectionFactory.createConnection(configuration());
            admin = connection.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return admin;
    }

    public org.apache.hadoop.conf.Configuration configuration() {
        org.apache.hadoop.conf.Configuration configuration = HBaseConfiguration.create();
        for(Map.Entry<String, String> map : hBaseConfig.getConfig().entrySet()){
            configuration.set(map.getKey(), map.getValue());
        }
        return configuration;
    }
}
