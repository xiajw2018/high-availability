package com.ai.xiajw.watch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

public class DefaultWatcher implements Watcher {

    private CountDownLatch countDownLatch;

    public DefaultWatcher(CountDownLatch countDownLatch){
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        Event.KeeperState keeperState = watchedEvent.getState();
        Event.EventType eventType = watchedEvent.getType();

        if(keeperState.equals(Event.KeeperState.SyncConnected) && eventType.equals(Event.EventType.None)){ // zookeeper连接成功
            countDownLatch.countDown();
            System.out.println("zookeeper 创建连接成功........");
        }
    }
}
