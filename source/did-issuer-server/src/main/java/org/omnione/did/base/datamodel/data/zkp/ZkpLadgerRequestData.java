package org.omnione.did.base.datamodel.data.zkp;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ZkpLadgerRequestData implements Serializable {
    @SerializedName("table")
    @Expose
    private String table;
    @SerializedName("scope")
    @Expose
    private String scope;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("index_position")
    @Expose
    private int index_position;
    @SerializedName("key_type")
    @Expose
    private String key_type;
    @SerializedName("lower_bound")
    @Expose
    private String lower_bound;
    @SerializedName("json")
    @Expose
    private boolean json;
//    private int limit;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isJson() {
        return json;
    }

    public void setJson(boolean json) {
        this.json = json;
    }

//    public int getLimit() {
//        return limit;
//    }
//
//    public void setLimit(int limit) {
//        this.limit = limit;
//    }

    public int getIndex_position() {
        return index_position;
    }

    public void setIndex_position(int index_position) {
        this.index_position = index_position;
    }

    public String getKey_type() {
        return key_type;
    }

    public void setKey_type(String key_type) {
        this.key_type = key_type;
    }

    public String getLower_bound() {
        return lower_bound;
    }

    public void setLower_bound(String lower_bound) {
        this.lower_bound = lower_bound;
    }
}
