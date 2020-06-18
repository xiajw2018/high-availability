package com.ai.xiajw.watch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.atomic.AtomicInteger;

public class LeaderNodeWatcher implements Watcher {

    private AtomicInteger state;

    public LeaderNodeWatcher(AtomicInteger state){
        this.state = state;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        Event.KeeperState keeperState = watchedEvent.getState();
        Event.EventType eventType = watchedEvent.getType();
        if(eventType.equals(Event.EventType.NodeDeleted)){
            System.out.println("leader node deleted!");
            state.getAndIncrement(); // 更改状态
        }
    }
}
