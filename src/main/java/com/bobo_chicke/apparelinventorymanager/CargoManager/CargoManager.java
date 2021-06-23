package com.bobo_chicke.apparelinventorymanager.CargoManager;

import com.bobo_chicke.apparelinventorymanager.Manager;
import com.bobo_chicke.apparelinventorymanager.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.*;

import static com.mongodb.client.model.Filters.*;
import org.bson.Document;

import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;

@RestController
@CrossOrigin
@RequestMapping("/CargoManager")
public class CargoManager extends Manager {
    public CargoManager() {
        super();
    }

    @GetMapping(path = "Get")
    public ArrayList<Cargo> GetCargo() throws JsonProcessingException {
        MongoCollection<Document> collection;
        collection = db.getCollection("cargo");
        ArrayList<Document> query = collection.find().into(new ArrayList<Document>());
        ArrayList<Cargo> result = new ArrayList<Cargo>();
        for (Document q:query) {
            q.remove("_id");
            ObjectMapper objectMapper = new ObjectMapper();
            Cargo cargo = objectMapper.readValue(q.toJson(), Cargo.class);
            result.add(cargo);
        }
        return result;
    }

    @PostMapping(path = "/Add", consumes = "application/json", produces = "application/json")
    public ReturnState AddCargo(@RequestBody String str) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        AddCargo addcargo = objectMapper.readValue(str, AddCargo.class);
        Document ins_doc = new Document();

        MongoCollection<Document> collection;
        collection = db.getCollection("cargo");

        IdGenerater idGenerater = new IdGenerater();
        String id = idGenerater.generate(16);

        ArrayList<Document> query = collection.find(eq("id", id)).into(new ArrayList<Document>());
        while (query.size() != 0) {
            id = idGenerater.generate(16);
            query = collection.find(eq("id", id)).into(new ArrayList<Document>());
        }

        ins_doc.append("id", id);
        ins_doc.append("name", addcargo.getName());
        ins_doc.append("count", 0);

        query = collection.find(eq("name", addcargo.getName())).into(new ArrayList<Document>());
        ReturnState res = new ReturnState();
        if(query.size() == 0){
            collection.insertOne(ins_doc);
            res.setState("ok");
            return res;
        } else {
            res.setState("error");
            return res;
        }
    }

    @PostMapping(path = "/Remove", consumes = "application/json", produces = "application/json")
    public ReturnState RemoveCargo(@RequestBody String str) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<String> Removecargo = objectMapper.readValue(str, ArrayList.class);

        ReturnState res = new ReturnState();

        MongoCollection<Document> collection;
        collection = db.getCollection("cargo");
        ArrayList<String> Fail = new ArrayList<String>();
        for(String cargo: Removecargo) {
            Document document = new Document();
            ArrayList<Document> check = collection.find(document.append("id", cargo)).into(new ArrayList<Document>());
            for (Document doc: check) {
                if (!doc.get("count").toString().equals("0")) {
                    Fail.add(cargo);
                } else {
                    document.clear();
                    collection.deleteMany(document.append("id", cargo));
                }
            }
        }

        if(Fail.size() != 0) {
            res.setState("warning");
            res.setFail(Fail);
        } else {
            res.setState("ok");
        }

        return res;
    }

    @PostMapping(path = "/Query", consumes = "application/json", produces = "application/json")
    public AddCargo QueryCargo(@RequestBody String str) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        QueryCargo queryCargo = objectMapper.readValue(str, QueryCargo.class);
        MongoCollection<Document> collection;
        collection = db.getCollection("cargo");
        Document document = new Document();
        document.put("id", queryCargo.getId());
        ArrayList<Document> check = collection.find(document).into(new ArrayList<Document>());
        AddCargo res = new AddCargo();
        for (Document tmp: check) {
            res.setName(tmp.get("name").toString());
        }
        return res;
    }
}
