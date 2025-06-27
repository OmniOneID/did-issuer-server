package org.omnione.did.base.datamodel.data.zkp;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class ZkpLadgerResponseData<T extends ZkpTableRows> implements Serializable {
    @SerializedName("rows")
    @Expose
    private List<T> rows;
    @SerializedName("more")
    @Expose
    private boolean more;
    @SerializedName("next_key")
    @Expose
    private String next_key;

    public List<T> getRows() {
        return rows;
    }

    public boolean isMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
    }

    public String getNext_key() {
        return next_key;
    }

    public void setNext_key(String next_key) {
        this.next_key = next_key;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}


/***
 {
 "rows":[
 {
 "p_key":0,
 "schema_id":"did:omn:NcYxiDXkpYi6ov5FcYDi1e:2:transcript:1.0:raon",
 "schema_id_hash":"ffb9d0a6106f6583bab5c6b41adc80751731e7f89ed7b7d14a7a6c843232ca3d",
 "schema_desc":"zkp 경력 증명서",
 "schema_value":"{\n \"id\":\"did:omn:NcYxiDXkpYi6ov5FcYDi1e:2:transcript:1.0:raon\",\n \"name\":\"경력증명서 발급\",\n \"version\":\"1.0\",\n \"attrNames\":[\n  \"address\",\n  \"name\",\n  \"salary\",\n  \"department\",\n  \"experience\"\n ],\n \"seqNo\":0\n}"
 }
 ],
 "more":false,
 "next_key":""
 }

* */