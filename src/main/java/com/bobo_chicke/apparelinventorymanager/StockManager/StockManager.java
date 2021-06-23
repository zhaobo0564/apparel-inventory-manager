package com.bobo_chicke.apparelinventorymanager.StockManager;

import com.bobo_chicke.apparelinventorymanager.Manager;
import com.bobo_chicke.apparelinventorymanager.util.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.mongodb.client.model.Filters.eq;

@RestController
@CrossOrigin
@RequestMapping("/StockManager")
public class StockManager extends Manager {
    public StockManager() {
        super();
    }

    @GetMapping("/GetInStock")
    public ArrayList<Stock> GetInStock() throws JsonProcessingException {
        MongoCollection<Document> collection;
        collection = db.getCollection("InStock");
        ArrayList<Document> query = collection.find().into(new ArrayList<Document>());
        ArrayList<Stock> res = new ArrayList<Stock>();
        for (Document q:query) {
            q.remove("_id");
            ObjectMapper objectMapper = new ObjectMapper();
            Stock stock = objectMapper.readValue(q.toJson(), new TypeReference<Stock>(){});
            res.add(stock);
        }
        return res;
    }

    @PostMapping(path = "/AddInStock", consumes = "application/json", produces = "application/json")
    public ReturnState AddInStock(@RequestBody String str) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<Cargo> addinstock = objectMapper.readValue(str, new TypeReference<ArrayList<Cargo>>(){});

        MongoCollection<Document> collection;
        collection = db.getCollection("InStock");

        IdGenerater idGenerater = new IdGenerater();
        String id = idGenerater.generate(32);

        ArrayList<Document> query = collection.find(eq("id", id)).into(new ArrayList<Document>());
        while (query.size() != 0) {
            id = idGenerater.generate(32);
            query = collection.find(eq("id", id)).into(new ArrayList<Document>());
        }

        Document Insert = new Document();
        ArrayList<Document> cargolist = new ArrayList<Document>();
        for(Cargo cargo:addinstock) {
            Document tmp = new Document();
            tmp.append("id", cargo.getId());
            tmp.append("name", cargo.getName());
            tmp.append("count", cargo.getCount());
            cargolist.add(tmp);
            Document inc = new Document();
            inc.append("$inc", new Document().append("count", cargo.getCount()));
            db.getCollection("cargo").updateOne(new Document().append("id", cargo.getId()), inc);
        }
        Insert.append("id", id);
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");
        Date date = new Date();
        Insert.append("date", sdf.format(date));
        Insert.append("cargos", cargolist);

        db.getCollection("InStock").insertOne(Insert);

        ReturnState res = new ReturnState();
        res.setState("ok");
        return res;
    }

    @GetMapping("/GetOutStock")
    public ArrayList<Stock> OutInStock() throws JsonProcessingException {
        MongoCollection<Document> collection;
        collection = db.getCollection("OutStock");
        ArrayList<Document> query = collection.find().into(new ArrayList<Document>());
        ArrayList<Stock> res = new ArrayList<Stock>();
        for (Document q:query) {
            q.remove("_id");
            ObjectMapper objectMapper = new ObjectMapper();
            Stock stock = objectMapper.readValue(q.toJson(), new TypeReference<Stock>(){});
            res.add(stock);
        }
        return res;
    }

    @PostMapping(path = "/AddOutStock", consumes = "application/json", produces = "application/json")
    public ReturnState AddOutStock(@RequestBody String str) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<Cargo> addinstock = objectMapper.readValue(str, new TypeReference<ArrayList<Cargo>>(){});

        MongoCollection<Document> collection;
        collection = db.getCollection("OutStock");

        IdGenerater idGenerater = new IdGenerater();
        String id = idGenerater.generate(32);

        ArrayList<Document> query = collection.find(eq("id", id)).into(new ArrayList<Document>());
        while (query.size() != 0) {
            id = idGenerater.generate(32);
            query = collection.find(eq("id", id)).into(new ArrayList<Document>());
        }

        Document Insert = new Document();
        ArrayList<Document> cargolist = new ArrayList<Document>();
        for(Cargo cargo:addinstock) {
            Document tmp = new Document();
            tmp.append("id", cargo.getId());
            tmp.append("name", cargo.getName());
            tmp.append("count", cargo.getCount());
            cargolist.add(tmp);
            Document inc = new Document();
            inc.append("$inc", new Document().append("count", -cargo.getCount()));
            db.getCollection("cargo").updateOne(new Document().append("id", cargo.getId()), inc);
            Document mx = new Document();
            mx.append("$max", new Document().append("count", 0));
            db.getCollection("cargo").updateOne(new Document().append("id", cargo.getId()), mx);
        }
        Insert.append("id", id);
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");
        Date date = new Date();
        Insert.append("date", sdf.format(date));
        Insert.append("cargos", cargolist);

        db.getCollection("OutStock").insertOne(Insert);

        ReturnState res = new ReturnState();
        res.setState("ok");
        return res;
    }
}
