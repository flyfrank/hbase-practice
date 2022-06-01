package com.xuanwu.hbase.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author YaokeHu
 * @description
 * @date 2022-05-30 20:16
 **/
@Configuration
@ConfigurationProperties(prefix = "hbase")
public class HBaseConfig {
    private Map<String, String> config = new HashMap<>();

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }
}
