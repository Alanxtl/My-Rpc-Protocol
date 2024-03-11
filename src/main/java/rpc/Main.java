package rpc;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class Main {
    public static void main(String[] args) {
        final int BASE_SLEEP_TIME = 1000;
        final int MAX_RETRIES = 3;



        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                .connectString("localhost:2181")
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();

        try {
//            zkClient.delete().forPath("/test1/test1.1");
            zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/test1/test1.1", "test".getBytes());
            if (zkClient.checkExists().forPath("/test1/test1.1") != null) {
                String t = new String(zkClient.getData().forPath("/test1/test1.1"));
                System.out.println(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}