package com.bobo_chicke.apparelinventorymanager.util;

import lombok.Data;

@Data
public class Cargo {
    private String id;
    private String name;
    private String type;
    private String manufacturer;
    private int count;
}
