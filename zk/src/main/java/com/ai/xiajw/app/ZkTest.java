package com.ai.xiajw.app;

import com.ai.xiajw.curator.ZkLeaderLatch;
import com.ai.xiajw.util.ZkConfig;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于Curator组件实现的高可用程序测试
 *
 */
public class ZkTest {

    public static void main(String[] args) throws InterruptedException {
        ZkConfig.initConf();
        ZkLeaderLatch zkLeaderLatch = new ZkLeaderLatch();
        AtomicInteger ai = new AtomicInteger(0);
        while(true){
            if(zkLeaderLatch.isLeader()){
                System.out.println(" exec count : "+ai.getAndIncrement());
            }
        }
    }
}
