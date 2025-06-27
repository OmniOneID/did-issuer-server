package org.omnione.did.base.datamodel.data.zkp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ZkpCredentionDefRows extends ZkpTableRows {

    @SerializedName("creddef_id")
    @Expose
    private String creddef_id;
    @SerializedName("creddef_id_hash")
    @Expose
    private String creddef_id_hash;
    @SerializedName("creddef_desc")
    @Expose
    private String creddef_desc;
    @SerializedName("creddef_value")
    @Expose
    private String creddef_value;

    public String getCreddef_id() {
        return creddef_id;
    }

    public void setCreddef_id(String creddef_id) {
        this.creddef_id = creddef_id;
    }

    public String getCreddef_id_hash() {
        return creddef_id_hash;
    }

    public void setCreddef_id_hash(String creddef_id_hash) {
        this.creddef_id_hash = creddef_id_hash;
    }

    public String getCreddef_desc() {
        return creddef_desc;
    }

    public void setCreddef_desc(String creddef_desc) {
        this.creddef_desc = creddef_desc;
    }

    public String getCreddef_value() {
        return creddef_value;
    }

    public void setCreddef_value(String creddef_value) {
        this.creddef_value = creddef_value;
    }
}
