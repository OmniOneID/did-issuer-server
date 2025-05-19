package org.omnione.did.base.datamodel.data.zkp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ZkpTableRows implements Serializable {
    @SerializedName("p_key")
    @Expose
    private int p_key;

    public int getP_key() {
        return p_key;
    }

    public void setP_key(int p_key) {
        this.p_key = p_key;
    }


}
