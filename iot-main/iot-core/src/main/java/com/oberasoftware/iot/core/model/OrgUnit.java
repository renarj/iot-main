package com.oberasoftware.iot.core.model;

import java.util.Optional;

public class OrgUnit {
    private String orgId;

    private Optional<String> parentOrg;

    public OrgUnit(String orgId, Optional<String> parentOrg) {
        this.orgId = orgId;
        this.parentOrg = parentOrg;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public Optional<String> getParentOrg() {
        return parentOrg;
    }

    public void setParentOrg(Optional<String> parentOrg) {
        this.parentOrg = parentOrg;
    }
}
