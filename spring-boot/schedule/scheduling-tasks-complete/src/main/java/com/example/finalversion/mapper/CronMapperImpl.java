package com.example.finalversion.mapper;


import com.example.finalversion.mapper.CronMapper;

import java.util.HashMap;
import java.util.Map;

public class CronMapperImpl implements CronMapper {

    private static final Map<Integer, String> DATA = new HashMap<>();

    static {
        DATA.put(1, "0/5 * * * * ?");
        DATA.put(2, "0/8 * * * * ?");
    }

    @Override
    public String getCronById(int id) {
        return DATA.get(id);
    }
}
