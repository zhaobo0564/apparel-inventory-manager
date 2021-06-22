package com.bobo_chicke.apparelinventorymanager.manager;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@RestController
@CrossOrigin
@RequestMapping("/manager")
public class manager {
    private MongoClient mongo;
    private MongoDatabase db;
    public manager() {
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

    @GetMapping("/main")
    public MainData GetMainInformation() {
        MainData result = new MainData();
        List<Document> list;
        MongoCollection<Document> collection;

        collection = db.getCollection("in_stock");
        list = collection.find().into(new ArrayList<>());
        result.setIn_stock_count(list.size());

        collection = db.getCollection("out_stock");
        list = collection.find().into(new ArrayList<>());
        result.setOut_stock_count(list.size());

        collection = db.getCollection("cargo");
        list = collection.find().into(new ArrayList<>());
        result.setCargo_type_count(list.size());

        result.setCargo_count(0);

        return result;
    }

//    @PostMapping("/addcargo")
//    public ReturnState AddCargo(@RequestParam Map<String,Object> params) {
//        System.out.println(params.size());
//        ReturnState res = new ReturnState();
//        res.setState("ok");
//        return res;
//    }

}
