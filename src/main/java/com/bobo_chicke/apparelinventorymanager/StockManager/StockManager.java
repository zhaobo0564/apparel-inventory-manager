package com.bobo_chicke.apparelinventorymanager.StockManager;

import com.bobo_chicke.apparelinventorymanager.Manager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/StockManager")
public class StockManager extends Manager {
    public StockManager() {
        super();
    }

}
