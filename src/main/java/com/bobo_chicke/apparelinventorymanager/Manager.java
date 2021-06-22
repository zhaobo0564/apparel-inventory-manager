package com.bobo_chicke.apparelinventorymanager;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.io.IOException;
import java.util.Properties;

public class Manager {
    protected MongoClient mongo;
    protected MongoDatabase db;

    public Manager(){
        Properties props = new Properties();
        try {
            props.load(getClass().getResourceAsStream("/application.properties"));
            System.out.println(props.getProperty("mongodb_uri", "mongodb://49.232.191.195:27017"));
            mongo = MongoClients.create(props.getProperty("mongodb_uri", "mongodb://49.232.191.195:27017"));
            db = mongo.getDatabase("apparel-inventory-manager");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
