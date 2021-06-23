package com.bobo_chicke.apparelinventorymanager.UserManager;

import com.bobo_chicke.apparelinventorymanager.Manager;
import com.bobo_chicke.apparelinventorymanager.util.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@RestController
@CrossOrigin
@RequestMapping("/UserManager")
public class UserManager extends Manager {
    private String GeneraterToken(User user) {
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyyMMddHHmmss");// a为am/pm的标记
        Date date = new Date();// 获取当前时间

        String res = user.getUsername() + user.getPassword() + sdf.format(date);

        try {
            res = MD5.getResult(res);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return res;
    }

    @PostMapping(path = "/login", consumes = "application/json", produces = "application/json")
    public ReturnState login(HttpServletResponse response, @RequestBody String str) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        User loginuser = objectMapper.readValue(str, User.class);
        Document doc = new Document();
        doc.append("username", loginuser.getUsername());
        doc.append("password", loginuser.getPassword());
        ArrayList<Document> documents = db.getCollection("User").find(doc).into(new ArrayList<Document>());
        ReturnState res = new ReturnState();
        if(documents.size() != 0) {
            res.setState("ok");
            String token = GeneraterToken(loginuser);
            res.setToken(token);
            Document Doc = documents.get(0);
            jedis.set(token, Doc.get("auth").toString());
            jedis.expire(token, 60 * 30);
        } else {
          res.setState("fail");
        }

        return res;
    }

    @PostMapping(path = "/logout", consumes = "application/json", produces = "application/json")
    public ReturnState logout(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("sessionId")){
                   jedis.del(cookie.getValue());
                }
            }
        }
        ReturnState res = new ReturnState();
        res.setState("ok");
        return  res;
    }

    @PostMapping(path = "/signup", consumes = "application/json", produces = "application/json")
    public ReturnState signup(@RequestBody String str) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        signupUser adduser = objectMapper.readValue(str, signupUser.class);
        Document doc = new Document();
        doc.append("username", adduser.getUsername());
        ArrayList<Document> documents = db.getCollection("User").find(doc).into(new ArrayList<Document>());
        ReturnState res = new ReturnState();
        if(documents.size() != 0){
            res.setState("fail");
            return res;
        }
        doc.append("password", adduser.getPassword());
        doc.append("auth", "user");
        db.getCollection("User").insertOne(doc);
        res.setState("ok");
        return res;
    }

    @GetMapping(path = "/GetUser")
    public ArrayList<UserInfo> GetUser() throws JsonProcessingException {
        MongoCollection<Document> collection;
        collection = db.getCollection("User");
        ArrayList<Document> query = collection.find().into(new ArrayList<Document>());
        ArrayList<UserInfo> res = new ArrayList<UserInfo>();
        for (Document q:query) {
            if(q.get("auth").toString().equals("superadmin")) {
                continue;
            }
            q.remove("_id");
            q.remove("password");
            ObjectMapper objectMapper = new ObjectMapper();
            UserInfo userinfo = objectMapper.readValue(q.toJson(), new TypeReference<UserInfo>(){});
            res.add(userinfo);
        }
        return res;
    }

    @PostMapping(path = "/ChangAuth", consumes = "application/json", produces = "application/json")
    public ReturnState ChangAuth(@RequestBody String str) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ChangAuth changauth = objectMapper.readValue(str, new TypeReference<ChangAuth>(){});

        Document tmp = new Document();
        tmp.append("$set", new Document("auth", changauth.getAuth()));

        db.getCollection("User").updateOne(new Document("username", changauth.getUsername()), tmp);

        ReturnState res = new ReturnState();
        res.setState("ok");

        return res;
    }

    @PostMapping(path = "/Delete", consumes = "application/json", produces = "application/json")
    public ReturnState Delete(@RequestBody String str) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ChangAuth changauth = objectMapper.readValue(str, new TypeReference<ChangAuth>(){});
        db.getCollection("User").deleteOne(new Document("username", changauth.getUsername()));
        ReturnState res = new ReturnState();
        res.setState("ok");
        return res;
    }
}
