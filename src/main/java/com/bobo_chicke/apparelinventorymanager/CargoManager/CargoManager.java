package com.bobo_chicke.apparelinventorymanager.CargoManager;

import com.bobo_chicke.apparelinventorymanager.Manager;
import com.bobo_chicke.apparelinventorymanager.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.*;

import static com.mongodb.client.model.Filters.*;
import org.bson.Document;

import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;

@RestController
@CrossOrigin
@RequestMapping("/CargoManager")
public class CargoManager extends Manager {
    public CargoManager() {
        super();
    }

    @PostMapping(path = "/Get", consumes = "application/json", produces = "application/json")
    public ReturnGetCargo GetCargo(@RequestBody String str) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        CheckToken checktoken = objectMapper.readValue(str, CheckToken.class);
        String state = super.checkToken(checktoken.getToken(), "user");
        ReturnGetCargo res = new ReturnGetCargo();
        if (!state.equals("ok")) {
            res.setState(state);
            return res;
        }
        MongoCollection<Document> collection;
        collection = db.getCollection("cargo");
        ArrayList<Document> query = collection.find().into(new ArrayList<Document>());
        ArrayList<Cargo> result = new ArrayList<Cargo>();
        for (Document q:query) {
            q.remove("_id");
            Cargo cargo = objectMapper.readValue(q.toJson(), Cargo.class);
            result.add(cargo);
        }
        res.setCargos(result);
        res.setState("ok");
        return res;
    }

    @PostMapping(path = "/Add", consumes = "application/json", produces = "application/json")
    public ReturnState AddCargo(@RequestBody String str) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        AddCargo addcargo = objectMapper.readValue(str, AddCargo.class);

        String state = super.checkToken(addcargo.getToken(), "admin");
        ReturnState res = new ReturnState();
        if (!state.equals("ok")) {
            res.setState(state);
            return res;
        }

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
        ins_doc.append("type", addcargo.getType());
        ins_doc.append("manufacturer", addcargo.getManufacturer());
        ins_doc.append("count", 0);

        query = collection.find(eq("name", addcargo.getName())).into(new ArrayList<Document>());
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
        RemoveCargo removecargo = objectMapper.readValue(str, RemoveCargo.class);

        String state = super.checkToken(removecargo.getToken(), "admin");
        ReturnState res = new ReturnState();
        if (!state.equals("ok")) {
            res.setState(state);
            return res;
        }

        MongoCollection<Document> collection;
        collection = db.getCollection("cargo");
        ArrayList<String> Fail = new ArrayList<String>();
        for(String cargo: removecargo.getCargos()) {
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
            res.setState("error");
            res.setFail(Fail);
        } else {
            res.setState("ok");
        }

        return res;
    }

    @PostMapping(path = "/Query", consumes = "application/json", produces = "application/json")
    public ReturnQueryCargo QueryCargo(@RequestBody String str) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        QueryCargo queryCargo = objectMapper.readValue(str, QueryCargo.class);

        String state = super.checkToken(queryCargo.getToken(), "user");
        ReturnQueryCargo res = new ReturnQueryCargo();
        if (!state.equals("ok")) {
            res.setState(state);
            return res;
        }

        MongoCollection<Document> collection;
        collection = db.getCollection("cargo");
        Document document = new Document();
        document.put("id", queryCargo.getId());
        ArrayList<Document> check = collection.find(document).into(new ArrayList<Document>());
        for (Document tmp: check) {
            res.setName(tmp.get("name").toString());
        }
        res.setState("ok");
        return res;
    }
}
