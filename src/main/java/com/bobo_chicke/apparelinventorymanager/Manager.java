package com.bobo_chicke.apparelinventorymanager;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.util.Properties;

public class Manager {
    protected MongoClient mongo;
    protected MongoDatabase db;
    protected Jedis jedis;
    public Manager(){
        Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream("/application.properties"));
            mongo = MongoClients.create(props.getProperty("mongodb_uri", "mongodb://localhost:27017"));
            db = mongo.getDatabase("apparel-inventory-manager");
            JedisPool jedispool = new JedisPool(new JedisPoolConfig(),
                    props.getProperty("redis.host", "localhost"),
                    Integer.parseInt(props.getProperty("redis.port", "6379"))
            );
            jedis = jedispool.getResource();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
