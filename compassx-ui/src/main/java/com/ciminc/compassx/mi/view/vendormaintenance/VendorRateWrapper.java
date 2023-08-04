/*
 * Copyright (C) 2018 Center for Information Management, Inc.
 *
 * This program is proprietary.
 * Redistribution without permission is strictly prohibited.
 * For more information, contact <http://www.ciminc.com>
 */
package com.ciminc.compassx.mi.view.vendormaintenance;

import com.ciminc.compass.entity.AgentCustomService;
import com.ciminc.compass.entity.PtsModifier;
import com.ciminc.compass.entity.PtsProcedure;
import com.ciminc.compass.entity.PtsServiceCombo;
import com.ciminc.compass.entity.PtsStandardRemark;
import com.ciminc.compass.entity.VendorRate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author julie
 * @version $LastChangedRevision $LastChangedDate Last Modified Author:
 * $LastChangedBy
 */
public class VendorRateWrapper {

    private static final Logger log = LoggerFactory.getLogger(VendorRateWrapper.class);

    private static final String LOG_MSG_VENDOR_RATE_WRAPPER_TO_STRING = "VendorRateWrapper{ guid: %s orig startDate: %tD orig stopDate: %tD startDate: %tD stopDate: %tD startLocalDate: %s stopLocalDate: %s "
            + "\n ptsProcedure: %s ptsModifier: %s ptsStandardRemark: %s agentCustomService: %s defaultUnits: %s orig CostPerUnit: %s updatedCostPerUnit: %s defaultCostPerUnit: %s "
            + "\n ratePermissionKeyChanged: %s deleting: %s ssRelatedCostUnitUpdated: %s ssRelatedServiceComboUpdated: %s ssRelatedDateRangeUpdated: %s }";
    private static final String DECIMAL_ZERO = "0.0";
    private static final String DECIMAL_DOUBLE_ZERO = "0.00";

    private VendorRate vendorRate;
    private PtsProcedure ptsProcedure;
    private PtsModifier ptsModifier;
    private PtsStandardRemark ptsStandardRemark;
    private PtsServiceCombo origPtsServiceCombo;
    private PtsServiceCombo updatedPtsServiceCombo;
    private AgentCustomService origAgentCustomService;
    private AgentCustomService updatedAgentCustomService;
    private BigDecimal origDefaultUnits;
    private BigDecimal origCostPerUnit;
    private BigDecimal updatedCostPerUnit;
    private boolean ratePermissionKeyChanged;
    private boolean deleting;
    private List<String> errorMessage = new ArrayList<>();

    private Date origStartDate;
    private Date origStopDate;
    private boolean ssRelatedCostUnitUpdated;
    private boolean ssRelatedServiceComboUpdated;
    private boolean ssRelatedDateRangeUpdated;

    public VendorRateWrapper() {
    }

    public VendorRateWrapper(VendorRate vendorRate) {
        initVendorRate(vendorRate);
    }

    public void setVendorRate(VendorRate vendorRate) {
        initVendorRate(vendorRate);
    }

    public VendorRate getVendorRate() {
        return vendorRate;
    }

    public PtsProcedure getPtsProcedure() {
        return ptsProcedure;
    }

    public void setPtsProcedure(PtsProcedure ptsProcedure) {
        this.ptsProcedure = ptsProcedure;
    }

    public void setPtsModifier(PtsModifier ptsModifier) {
        this.ptsModifier = ptsModifier;
    }

    public void setPtsStandardRemark(PtsStandardRemark ptsStandardRemark) {
        this.ptsStandardRemark = ptsStandardRemark;
    }

    public PtsModifier getPtsModifier() {
        return ptsModifier;
    }

    public PtsStandardRemark getPtsStandardRemark() {
        return ptsStandardRemark;
    }

    public String getProcedureCode() {
        return ptsProcedure != null ? ptsProcedure.getCode() : "";
    }

    public String getModifierCode() {
        return ptsModifier != null ? ptsModifier.getCode() : "";
    }

    public String getStandardRemarkCode() {
        return ptsStandardRemark != null ? ptsStandardRemark.getCode() : "";
    }

    public String getGuid() {
        return vendorRate.getGuid();
    }

    public void setGuid(String guid) {
        vendorRate.setGuid(guid);
    }

    public Integer getVersion() {
        return vendorRate.getVersion();
    }

    public void setVersion(Integer version) {
        vendorRate.setVersion(version);
    }

    public Date getStartDate() {
        return vendorRate.getStartDate();
    }

    public void setStartDate(Date startDate) {
        vendorRate.setStartDate(startDate);
    }

    public Date getStopDate() {
        return vendorRate.getStopDate();
    }

    public void setStopDate(Date stopDate) {
        vendorRate.setStopDate(stopDate);
    }

    public boolean isDeleting() {
        return deleting;
    }

    public void setDeleting(boolean deleting) {
        this.deleting = deleting;
    }

    public Date getOrigStartDate() {
        return origStartDate;
    }

    public void setOrigStartDate(Date origStartDate) {
        this.origStartDate = origStartDate;
    }

    public Date getOrigStopDate() {
        return origStopDate;
    }

    public void setOrigStopDate(Date origStopDate) {
        this.origStopDate = origStopDate;
    }

    public String getShortName() {
        return (vendorRate.getPtsServiceCombo() != null && vendorRate.getPtsServiceCombo().getShortName() != null ? vendorRate.getPtsServiceCombo().getShortName() : "");
    }

    public void setShortName(String shortName) {
        // Do nothing. This is a dumb way to allow folks to arrow through the short name without the danger of updates
    }

    public LocalDate getStartLocalDate() {
        if (vendorRate.getStartDate() != null) {
            return vendorRate.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else {
            return null;
        }
    }

    public void setStartLocalDate(LocalDate startDate) {
        if (startDate != null) {
            vendorRate.setStartDate(Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        } else {
            vendorRate.setStartDate(null);
        }
    }

    public LocalDate getStopLocalDate() {
        if (vendorRate.getStopDate() != null) {
            return vendorRate.getStopDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else {
            return null;
        }
    }

    public void setStopLocalDate(LocalDate stopDate) {
        if (stopDate != null) {
            vendorRate.setStopDate(Date.from(stopDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        } else {
            vendorRate.setStopDate(null);
        }
    }

    public void setDefaultUnits(BigDecimal defaultUnits) {
        if (defaultUnits != null && defaultUnits.compareTo(new BigDecimal(DECIMAL_ZERO)) == 0) {
            defaultUnits = null;
        }
        if ((defaultUnits == null && vendorRate.getDefaultUnits() != null)
                || (defaultUnits != null && vendorRate.getDefaultUnits() == null)
                || ((defaultUnits != null) && (vendorRate.getDefaultUnits() != null) && defaultUnits.compareTo(vendorRate.getDefaultUnits()) != 0)) {
            vendorRate.setDefaultUnits(defaultUnits);
        }
    }

    public BigDecimal getDefaultUnits() {
        if (vendorRate.getDefaultUnits() != null) {
            return vendorRate.getDefaultUnits();
        } else {
            return new BigDecimal(DECIMAL_ZERO);
        }
    }

    public void setDefaultCostPerUnit(BigDecimal defaultCostPerUnit) {
        if (defaultCostPerUnit != null && defaultCostPerUnit.compareTo(new BigDecimal(DECIMAL_DOUBLE_ZERO)) == 0) {
            defaultCostPerUnit = null;
        }
        if ((defaultCostPerUnit == null && vendorRate.getDefaultCostPerUnit() != null)
                || (defaultCostPerUnit != null && vendorRate.getDefaultCostPerUnit() == null)
                || ((defaultCostPerUnit != null) && (vendorRate.getDefaultCostPerUnit() != null) && defaultCostPerUnit.compareTo(vendorRate.getDefaultCostPerUnit()) != 0)) {
            vendorRate.setDefaultCostPerUnit(defaultCostPerUnit);
        }
    }

    public BigDecimal getDefaultCostPerUnit() {
        if (vendorRate.getDefaultCostPerUnit() != null) {
            return vendorRate.getDefaultCostPerUnit();
        } else {
            return new BigDecimal(DECIMAL_DOUBLE_ZERO);
        }
    }

    public AgentCustomService getAgentCustomService() {
        return vendorRate.getAgentCustomService();
    }

    public void setAgentCustomService(AgentCustomService agentCustomService) {
        vendorRate.setAgentCustomService(agentCustomService);
    }

    public PtsServiceCombo getPtsServiceCombo() {
        return vendorRate.getPtsServiceCombo();
    }

    public void setPtsServiceCombo(PtsServiceCombo ptsServiceCombo) {
        vendorRate.setPtsServiceCombo(ptsServiceCombo);
    }

    public String getCustomDescription() {
        return getAgentCustomService() != null ? getAgentCustomService().getDescription() : "";
    }

    private void initVendorRate(VendorRate vendorRate) {
        this.vendorRate = vendorRate;
        this.vendorRate.setDirtyTrackingEnabled(true);
        this.origPtsServiceCombo = vendorRate.getPtsServiceCombo();
        this.origAgentCustomService = vendorRate.getAgentCustomService();
        this.origDefaultUnits = vendorRate.getDefaultUnits();
        this.origCostPerUnit = vendorRate.getDefaultCostPerUnit();
        this.origStartDate = vendorRate.getStartDate();
        this.origStopDate = vendorRate.getStopDate();
        this.ratePermissionKeyChanged = false;

        if (origPtsServiceCombo == null) {
            this.ptsProcedure = null;
            this.ptsModifier = null;
            this.ptsStandardRemark = null;
        } else {
            this.ptsProcedure = this.origPtsServiceCombo.getPtsProcedure();
            this.ptsModifier = this.origPtsServiceCombo.getPtsModifier();
            this.ptsStandardRemark = this.origPtsServiceCombo.getPtsStandardRemark();
        }
    }

    public PtsServiceCombo getOrigPtsServiceCombo() {
        return origPtsServiceCombo;
    }

    public void setOrigPtsServiceCombo(PtsServiceCombo origPtsServiceCombo) {
        this.origPtsServiceCombo = origPtsServiceCombo;
    }

    public PtsServiceCombo getUpdatedPtsServiceCombo() {
        return updatedPtsServiceCombo;
    }

    public void setUpdatedPtsServiceCombo(PtsServiceCombo updatedPtsServiceCombo) {
        this.updatedPtsServiceCombo = updatedPtsServiceCombo;
    }

    public AgentCustomService getOrigAgentCustomService() {
        return origAgentCustomService;
    }

    public AgentCustomService getUpdatedAgentCustomService() {
        return updatedAgentCustomService;
    }

    public void setUpdatedAgentCustomService(AgentCustomService updatedAgentCustomService) {
        this.updatedAgentCustomService = updatedAgentCustomService;
    }

    public BigDecimal getOrigDefaultUnits() {
        return origDefaultUnits;
    }

    public void setOrigDefaultUnits(BigDecimal origDefaultUnits) {
        this.origDefaultUnits = origDefaultUnits;
    }

    public BigDecimal getOrigCostPerUnit() {
        return origCostPerUnit;
    }

    public BigDecimal getUpdatedCostPerUnit() {
        return updatedCostPerUnit;
    }

    public void setUpdatedCostPerUnit(BigDecimal updatedCostPerUnit) {
        this.updatedCostPerUnit = updatedCostPerUnit;
    }

    public boolean isRatePermissionKeyChanged() {
        return ratePermissionKeyChanged;
    }

    public void setRatePermissionKeyChanged(boolean ratePermissionKeyChanged) {
        this.ratePermissionKeyChanged = ratePermissionKeyChanged;
    }

    public boolean isSsRelatedCostUnitUpdated() {
        return ssRelatedCostUnitUpdated;
    }

    public void setSsRelatedCostUnitUpdated(boolean ssRelatedCostUnitUpdated) {
        this.ssRelatedCostUnitUpdated = ssRelatedCostUnitUpdated;
    }

    public boolean isSsRelatedServiceComboUpdated() {
        return ssRelatedServiceComboUpdated;
    }

    public void setSsRelatedServiceComboUpdated(boolean ssRelatedServiceComboUpdated) {
        this.ssRelatedServiceComboUpdated = ssRelatedServiceComboUpdated;
    }

    public boolean isSsRelatedDateRangeUpdated() {
        return ssRelatedDateRangeUpdated;
    }

    public void setSsRelatedDateRangeUpdated(boolean ssRelatedDateRangeUpdated) {
        this.ssRelatedDateRangeUpdated = ssRelatedDateRangeUpdated;
    }

    public boolean hasSsRelatedUpdates() {
        return isSsRelatedCostUnitUpdated() || isSsRelatedServiceComboUpdated() || isSsRelatedDateRangeUpdated();
    }

    public boolean isInError() {
        return !getErrorMessage().isEmpty();
    }

    public List<String> getErrorMessage() {
        return errorMessage;
    }

    public void addErrorMessage(String errorMesage) {
        this.errorMessage.add(errorMesage);
    }

    public void clearErrors() {
        this.errorMessage.clear();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.getGuid());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VendorRateWrapper other = (VendorRateWrapper) obj;
        boolean returnResult = this.getGuid() != null && this.getGuid().equals(other.getGuid());
        return returnResult;
    }

    public boolean equivalentButNotEqual(Object obj) {
        if (!this.equals(obj) && obj != null) {
            final VendorRateWrapper other = (VendorRateWrapper) obj;
            return ((this.getStartDate() == null && other.getStartDate() == null) || (this.getStartDate() != null && this.getStartDate().equals(other.getStartDate())))
                    && ((this.getStopDate() == null && other.getStopDate() == null) || (this.getStopDate() != null && this.getStopDate().equals(other.getStopDate())))
                    && ((this.getPtsServiceCombo() == null && other.getPtsServiceCombo() == null) || (this.getPtsServiceCombo() != null && this.getPtsServiceCombo().equals(other.getPtsServiceCombo())))
                    && ((this.getAgentCustomService() == null && other.getAgentCustomService() == null) || (this.getAgentCustomService() != null && this.getAgentCustomService().equals(other.getAgentCustomService())));

        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format(LOG_MSG_VENDOR_RATE_WRAPPER_TO_STRING,
                this.getGuid(),
                this.getOrigStartDate(),
                this.getOrigStopDate(),
                this.getStartDate(),
                this.getStopDate(),
                this.getStartLocalDate(),
                this.getStopLocalDate(),
                (ptsProcedure != null ? ptsProcedure.getCode() : ""),
                (ptsModifier != null ? ptsModifier.getCode() : ""),
                (ptsStandardRemark != null ? ptsStandardRemark.getCode() : ""),
                (this.getAgentCustomService() != null ? this.getAgentCustomService().getDescription() : ""),
                this.getDefaultUnits() != null ? this.getDefaultUnits().toString() : "",
                this.getOrigCostPerUnit() != null ? this.getOrigCostPerUnit().toString() : "",
                this.getUpdatedCostPerUnit() != null ? this.getUpdatedCostPerUnit().toString() : "",
                this.getDefaultCostPerUnit() != null ? this.getDefaultCostPerUnit().toString() : "",
                ratePermissionKeyChanged, deleting, ssRelatedCostUnitUpdated,
                ssRelatedServiceComboUpdated, ssRelatedDateRangeUpdated);
    }

    public boolean hasSameServiceCombo(VendorRateWrapper thisWrapper) {
        return this.getOrigPtsServiceCombo() != null && thisWrapper.getOrigPtsServiceCombo() != null
                && Objects.equals(this.getOrigPtsServiceCombo().getGuid(), thisWrapper.getOrigPtsServiceCombo().getGuid())
                && ((this.getOrigAgentCustomService() == null && thisWrapper.getOrigAgentCustomService() == null)
                || (this.getOrigAgentCustomService() != null && thisWrapper.getOrigAgentCustomService() != null
                && Objects.equals(this.getOrigAgentCustomService().getGuid(), thisWrapper.getOrigAgentCustomService().getGuid())));
    }

    public boolean valuesUpdated() {
        return !Objects.equals(origPtsServiceCombo, vendorRate.getPtsServiceCombo())
                || !Objects.equals(origAgentCustomService, vendorRate.getAgentCustomService())
                || !Objects.equals(origDefaultUnits, vendorRate.getDefaultUnits())
                || !Objects.equals(origCostPerUnit, vendorRate.getDefaultCostPerUnit())
                || !Objects.equals(origStartDate, vendorRate.getStartDate())
                || !Objects.equals(origStopDate, vendorRate.getStopDate());
    }

    public boolean isPersisted() {
        if (StringUtils.isEmpty(getGuid()) || getGuid().length() != 32) {
            return false;
        }
        return true;
    }
}
