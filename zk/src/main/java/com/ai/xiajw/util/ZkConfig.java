package com.ai.xiajw.util;

import org.apache.zookeeper.common.ZKConfig;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class ZkConfig {

    public static String connectString;

    public static String latchPath;

    private static Map loadConfig(){
        Yaml yaml = new Yaml();
        InputStream inputStream = ZKConfig.class.getClassLoader().getResourceAsStream("zk.yml");
        Map map = yaml.load(inputStream);
        return map;
    }

    public static void initConf(){
        Map map = loadConfig();
        if(!map.containsKey("zk")){
            System.out.println("yaml config err!");
            return;
        }
        Map zkConfig = (Map) map.get("zk");
        connectString = (String) zkConfig.get(ConfigEnum.CONNECT_STRING.toString());
        System.out.println("connect-string:"+connectString);
        latchPath = (String) zkConfig.get(ConfigEnum.LATCHER_PATH.toString());
        System.out.println("latcher-path:"+latchPath);
        System.out.println("conf init success!");
    }

}
