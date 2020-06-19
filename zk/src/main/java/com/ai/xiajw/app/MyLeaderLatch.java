package com.ai.xiajw.app;

import com.ai.xiajw.watch.DefaultWatcher;
import com.ai.xiajw.watch.LeaderNodeWatcher;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class MyLeaderLatch {

    private ZooKeeper client;

    private String connectString;

    private String latchPath;

    private String key;

    private final static String lock_suffix = "latch-";

    private CountDownLatch countDownLatch;

    private boolean isLeader = false;

    private String leaderPath;

    private String myPath;

    private AtomicInteger state = new AtomicInteger(State.NONE.getValue());

    public MyLeaderLatch(String connectString,String latchPath,String key){
        this.connectString = connectString;
        this.latchPath = latchPath;
        this.key = key;
        ZooKeeper client = null;
        countDownLatch = new CountDownLatch(1);
        try {
            client = new ZooKeeper(connectString,3000,new DefaultWatcher(countDownLatch));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            // 等待zookeeper连接成功再进行其他操作
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.client = client;
    }

    public void start() throws KeeperException, InterruptedException {
        Stat stat = client.exists(latchPath,false);
        if(stat == null){
            client.create(latchPath,new byte[0],ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
        }
        seizeLeader();
        if(checkLeaderShip()){
            isLeader = true;
            System.out.println("i'm the leader now");
        }else{
            isLeader = false;
            System.out.println("i'm not the leader");
            while(!isLeader){
                Thread.sleep(1000);
                if(state.compareAndSet(State.LEADER_DELETED.getValue(),State.NONE.getValue())){ // leader deleted
                    seizeLeader();
                    isLeader = checkLeaderShip();
                }
            }
        }
    }

    private void seizeLeader() throws KeeperException, InterruptedException {
        if(myPath !=null && !"".equals(myPath)){
            client.delete(myPath,-1);
        }
        this.myPath = client.create(latchPath+"/"+lock_suffix,key.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
    }

    /**
     * 检查leader关系
     *
     * @return true:是leader false:非leader
     */
    private boolean checkLeaderShip() throws KeeperException, InterruptedException {
        List<String> children = client.getChildren(latchPath,false);
        Collections.sort(children,new ChildrenComparator());
        this.leaderPath = latchPath + "/" +children.get(0);
        String data = new String(client.getData(latchPath+"/"+children.get(0),new LeaderNodeWatcher(state), new Stat()));
        return key.equals(data);
    }

    private static int getSequential(String s){
        return Integer.parseInt(s.substring(s.lastIndexOf(lock_suffix)+lock_suffix.length()));
    }

    public boolean isLeader(){
        return isLeader;
    }

    class ChildrenComparator implements Comparator<String>{
        @Override
        public int compare(String o1, String o2) {
            int oo1 = getSequential(o1);
            int oo2 = getSequential(o2);
            return oo1 > oo2 ? 1 : oo1 < oo2 ? -1 : 0;
        }
    }

    enum State{

        NONE(0),
        LEADER_DELETED(1);

        private int intValue;
        State(int value){
            intValue = value;
        }

        public int getValue(){
            return intValue;
        }
    }
}
