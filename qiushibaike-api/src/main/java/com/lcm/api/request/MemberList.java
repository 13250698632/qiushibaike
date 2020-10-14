package com.lcm.api.request;

import java.io.Serializable;
import java.util.List;

public class MemberList implements Serializable {
    private List<String> list;

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
