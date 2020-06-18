package com.ai.xiajw.app;

import com.ai.xiajw.util.ZkConfig;
import org.apache.zookeeper.KeeperException;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 自己实现的高可用测试程序
 *
 * @author  xiajw
 *
 */
public class MyLeaderLatchTest {

    public static void main(String[] args) throws UnknownHostException, InterruptedException, KeeperException {
        ZkConfig.initConf();

        String latchPath = ZkConfig.latchPath;
        String connectString = ZkConfig.connectString;
        String key = new StringBuilder("MyLeaderLatchTest#").append(InetAddress.getLocalHost().getHostAddress()).toString(); // 存放在znode上的data
        MyLeaderLatch leaderLatch = new MyLeaderLatch(connectString,latchPath,key);
        leaderLatch.start();
        while(true){
            if(leaderLatch.isLeader()){
                System.out.println("........leader now ..........");
                Thread.sleep(2000);
            }
        }
    }
}
