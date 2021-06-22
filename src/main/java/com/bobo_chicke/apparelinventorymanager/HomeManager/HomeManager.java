package com.bobo_chicke.apparelinventorymanager.HomeManager;

import com.bobo_chicke.apparelinventorymanager.Manager;
import com.bobo_chicke.apparelinventorymanager.util.MainData;
import com.mongodb.client.*;

import org.bson.Document;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/HomeManager")
public class HomeManager extends Manager {
    public HomeManager() {
       super();
    }

    @GetMapping("/Get")
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
}
