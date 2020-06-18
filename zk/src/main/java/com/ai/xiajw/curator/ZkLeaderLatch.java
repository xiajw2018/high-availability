package com.ai.xiajw.curator;

import com.ai.xiajw.util.ZkConfig;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.net.InetAddress;

public class ZkLeaderLatch {

    private static LeaderLatch leaderLatch;
    private static CuratorFramework zkClient;

    public ZkLeaderLatch(){
        try{
            final String id = String.format("zkLatchClient#%s", InetAddress.getLocalHost().getHostAddress());
            System.out.println("zk:"+id+"客户端初始化....server:"+ ZkConfig.connectString+",latch path:"+ZkConfig.latchPath);
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
            zkClient = CuratorFrameworkFactory.builder()
                    .connectString(ZkConfig.connectString)
                    .sessionTimeoutMs(3000)
                    .retryPolicy(retryPolicy)
                    .build();
            System.out.println("zk 客户端启动.....");
            zkClient.start();
            leaderLatch = new LeaderLatch(zkClient,ZkConfig.latchPath,id);
            LeaderLatchListener leaderLatchListener = new LeaderLatchListener() {
                public void isLeader() {
                    System.out.println("客户端："+id+"不是主节点");
                }

                public void notLeader() {
                    System.out.println("客户端："+id+" 成为主节点！");
                }
            };
            leaderLatch.addListener(leaderLatchListener);
            leaderLatch.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isLeader() {
        return leaderLatch.hasLeadership();
    }

    public CuratorFramework getClient(){
        return zkClient;
    }

    public LeaderLatch getLatch(){
        return leaderLatch;
    }
}
