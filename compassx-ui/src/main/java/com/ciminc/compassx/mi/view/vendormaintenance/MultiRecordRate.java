/*
 * Copyright (C) 2018 Center for Information Management, Inc.
 *
 * This program is proprietary.
 * Redistribution without permission is strictly prohibited.
 * For more information, contact <http://www.ciminc.com>
 */
package com.ciminc.compassx.mi.view.vendormaintenance;

import com.ciminc.compass.entity.VendorRateServiceEventWrapper;
import static com.ciminc.compassx.CompassNextConstants.ACTIVE_RATES_CAPTION_AND_VALUE;
import static com.ciminc.compassx.CompassNextConstants.ALL_RATES_CAPTION_AND_VALUE;
import static com.ciminc.compassx.service.Locator.locator;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ciminc.compass.entity.Agent;
import com.ciminc.compass.entity.AgentCustomService;
import com.ciminc.compass.entity.Permission;
import com.ciminc.compass.entity.PtsModifier;
import com.ciminc.compass.entity.PtsProcedure;
import com.ciminc.compass.entity.PtsServiceCombo;
import com.ciminc.compass.entity.PtsStandardRemark;
import com.ciminc.compass.entity.Vendor;
import com.ciminc.compass.entity.VendorRate;
import com.ciminc.compass.entity.VendorRateServiceEventVo;
import com.ciminc.compass.entity.VendorRateUpdateResult;
import com.ciminc.compass.entity.VendorRateUpdateResult.FAILURE_REASON;
import com.ciminc.compass.mi.next.vendormaintenance.VendorRateValidation;
import com.ciminc.compass.mi.vendorview.logging.VendorviewUtils;
import com.ciminc.compass.navigator.NavConstants;
import com.ciminc.compassx.CompassNextConstants;
import com.ciminc.compassx.flow.util.CustomButtonThemes;
import com.ciminc.compassx.flow.util.CustomFieldThemes;
import com.ciminc.compassx.flow.util.InputTextHighlight;
import com.ciminc.compassx.flow.util.StringToUnitConverter;
import com.ciminc.compassx.flow.util.VaadinUtils;
import com.ciminc.compassx.mi.converter.BigDecimalDollarConverter;
import com.ciminc.compassx.util.CompassNextUtils;
import com.ciminc.compassx.util.NumberUtils;
import com.ciminc.compassx.util.multirecord.MultiRecordChangeService;
import com.ciminc.compassx.util.multirecord.MultiRecordControllerModel;
import com.ciminc.compassx.util.multirecord.MultiRecordDateField;
import com.ciminc.compassx.util.multirecord.MultiRecordException;
import com.ciminc.compassx.util.multirecord.MultiRecordTemplate;
import com.ciminc.compassx.util.multirecord.MultiRecordUtils;
import com.ciminc.compassx.event.CompassXEvent;
import static com.ciminc.compassx.util.CompassNextUtils.getFormattedDateTime;
import com.ciminc.compassx.util.multirecord.MultiRecordController;
import com.ciminc.compassx.view.audit.AuditEntityViewerPopupDialog;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridSortOrderBuilder;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.selection.SelectionEvent;
import com.vaadin.flow.data.value.ValueChangeMode;
import java.util.Objects;
import java.util.stream.Stream;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.owasp.encoder.Encode;

/**
 *
 *
 * @author david
 * @version $LastChangedRevision $LastChangedDate Last Modified Author:
 * $LastChangedBy
 */
public class MultiRecordRate extends MultiRecordTemplate<VendorRateWrapper> {

    private static final long serialVersionUID = 24128390249780L;
    private static final Logger log = LoggerFactory.getLogger(MultiRecordRate.class);

    public static final String PROCEDURE_CODE_CAPTION = "Procedure";
    public static final String MODIFIER_CODE_CAPTION = "Modifier";
    public static final String STD_REMARK_CODE_CAPTION = "Standard Remark";
    public static final String CUSTOM_SERVICE_CAPTION = "Custom Service";
    public static final String SHORT_NAME_CAPTION = "Short Name";
    public static final String START_DATE_CAPTION = "Start Date";
    public static final String STOP_DATE_CAPTION = "Stop Date";
    public static final String UNITS_CAPTION = "Unit";
    public static final String COST_PER_UNIT_CAPTION = "Cost/Unit";
    public static final String COST_PER_UNIT_FORMAT = "##,###,##0.00";
    public static final String ACTION_CAPTION = "Delete";
    public static final String HISTORY_CAPTION = "History";

    private static final String NO_HISTORY_DATA_IS_AVAILABLE = "No History Data is available. ";
    private static final String NOT_A_VALID_VALUE = "Not a valid value";
    private static final String SHORT_NAME_COLUMN_ID = "shortName";
    private static final String UNABLE_TO_CHANGE_ALL_ACTIVE_FILTER = "Unable to change all/active filter with unsaved edits pending";
    private static final String WARNING = "Warning";
    private static final String ERROR_LOAD_MASTER_CUSTOM_SERVICE_LIST_WITH_AGENT = "Error loadMasterCustomServiceList with agentGuid {} : {}";
    private static final String ERROR_IN_LOAD_MASTER_SERVICE_LIST = "Error in loadMasterServiceList ";
    private static final String ERROR_IN_SE_EXISTS = "Error in anyServiceEventExistsByVendorRate  vendorRateGuidList %s";
    private static final String ERROR_GET_MODIFIED_RATE_SE = "Error in getModifiedVendorRateServiceEvents vendorRateGuids %s";
    private static final String ERROR_LOAD_MASTER_RATE_LIST_WITH_VENDOR_GUID = "Error loadMasterRateList with vendorGuid {} : {}";
    private static final String NO_SERVICE_COMBO_RECORD_FOUND = "No service combo record found!";
    private static final String NULL_COMBO_PROVIDED_NOTHING_TO_FIND = "Null combo provided! Nothing to find!";
    private static final String STANDARD_DATE_FORMAT = "MM/dd/yyyy";
    private static final String NEWLINE_STR = "\n";
    private static final String TAB_STR = "\t";
    private static final String ERROR_SEPARATOR = ", ";
    private static final String CAPTION_GENERATOR_SEPARATOR = " | ";
    private static final String LOG_MSG_EDITOR_COMPONENT_VALUES = "Bound editor values - startDate: {} stopDate: {} ptsProcedure: {} ptsModifier:{} ptsStandardRemark: {} agentCustomService: {}";
    private static final String EMPTY_CAPTION_NA = "N/A";
    private static final String EMPTY_CAPTION_SELECT_CUSTOM = "Select Custom";
    private static final String LOG_MSG_INCLUDE_AGENT_CUSTOM_SERVICE = "includeAgentCustomService - procedure: {} - agent Custom Service: {}";
    private static final String LOG_MSG_INCLUDE_AGENT_CUSTOM_SERVICE_PROCEDURE_NULL = "includeAgentCustomService - procedure: NULL";
    private static final String LOG_MSG_INCLUDE_PTS_STANDARD_REMARK = "includePtsStandardRemark - Procedure: {} - Modifier: {} - Standard Remark: {}";
    private static final String LOG_MSG_INCLUDE_PTS_MODIFIER = "includePtsModifier - Procedure: {} - Modifier: {}";
    private static final String LOG_MSG_VENDOR_RATE_WRAPPER_WAS_NOT_DIRTY = "VendorRateWrapper was NOT dirty and update event was NOT dispatched for {}";
    private static final String LOG_MSG_VENDOR_RATE_WRAPPER_WAS_DIRTY = "VendorRateWrapper was dirty and update event dispatched for {} for the following fields {}";
    private static final String LOG_MSG_VENDOR_RATE_WRAPPER_DIRTY_TRACKING_STATUS = "VendorRateWrapper dirty tracking status is {}";
    private static final String LOG_MSG_PTS_SERVICE_COMBO_IN_VENDOR_RATE_DATE_RANGE = "ptsServiceComboInVendorRateDateRange - startDate: {} - stopDate: {} - procedure: {} - modifier: {} - standard remark: {} - vendorRateStartDate: {} - vendorRateStopDate: {} - returnResult: {}";
    private static final String LOG_MSG_VALIDATION_ERRORS_WERE_SET = "validation errors were set for the row components";
    private static final String LOG_MSG_PROCEDURE_EQUAL_PTS_SERVICE_COMBO = "isProcedureEqual - ptsServiceCombo: {} - ptsProcedure: {} - result: {}";
    private static final String LOG_MSG_MODIFIER_EQUAL_PTS_SERVICE_COMBO = "isModifierEqual - ptsServiceCombo: {} - ptsModifier: {} - result: {}";
    private static final String LOG_MSG_STANDARD_REMARK_EQUAL_PTS_SERVICE_COMBO = "isStandardRemarkEqual - ptsServiceCombo: {} - ptsStandardRemark: {} - result: {}";
    private static final String NOT_A_VALID__PTS_SERVICE_COMBO = "Not a valid PtsServiceCombo";
    private static final String LOG_MSG_PTS_SERVICE_COMBO_USING_WRAPPER_FIELDS = "getPtsServiceComboUsingWrapperFields - result: {}";
    private static final String LOG_MSG_NEEDS_UPDATING_EXISTING_VALUE = "needsUpdating - existingValue: {} - updatedValue: {} - result: {}";
    private static final String LOG_MSG_SHORT_NAME_FIELD_VALUE_SET = "shortNameField value set to: {}";
    private static final String LOG_MSG_ROW_VALUE_CHANGE = "rowValueChange \n {}";
    private static final String LOG_MESSAGE_VENDOR_RATE_WRAPPER_WAS_NULL = "VendorRateWrapper was null in rowValueChange - delete?";
    private static final String ERROR_MSG_SAVE_FAILURE = "A rate record was updated by another user. Changes cannot be saved.";
    private static final String ERROR_MSG_LOADING_AGENT_CUSTOM_SERVICE = "Error loading Agent Custom Service list from the database.";
    private static final String ERROR_MSG_LOADING_VENDOR_RATE_LIST = "Error loading Vendor Rate list from the database.";
    private static final String ERROR_MSG_LOADING_PTS__SERVICE__COMBO_LIST = "Error loading PTS Service Combo List from the database.";
    private static final String ERROR_MSG_PREVIOUS_DATA_LOADING_ERROR_OCCURRED = "Previous data loading error occurred from database. Unable to save data.";
    private static final String LOG_VALIDATION = "isValid {} \n errors {} \n warns {}";
    private static final String ERROR_DO_SAVE_DATA = "Error doSaveData ";

    private static final String NULL_LITERAL = "NULL";
    private static final String SORTED = "Sorted";
    private static final String UNSORTED = "Unsorted";
    private static final String LOG_MSG_VENDOR_RATE = "{}\t Procedure: {}\t Modifier: {}\t StandardRemark: {}\t Custom: {}\t Start Date: {}\t Stop Date:{}\t Default Units: {}\t Default Cost: {}";

    private static final String MIN_START_DATE = "10/01/2010";

    private static final String ERROR_MSG_START_DATE_REQUIRED = "Start Date required";
    private static final String ERROR_MSG_START_DATE_MUST_BE_ON_OR_AFTER = "Start Date must be on or after " + MIN_START_DATE;
    private static final String ERROR_MSG_STOP_DATE_MUST_BE_ON_OR_AFTER_START_DATE = "Stop date must be on or after Start Date";
    private static final String ROW_MARK_INVALID_SERVICE_COMBO = "Invalid Service Combination";
    private static final String ROW_MARK_UPDATED_BY_ANOTHER_USER = "Updated by another user";
    private static final String ROW_MARK_OVERLAPPING = "Overlapping";
    private static final String ROW_MARK_CONSECUTIVE = "Consecutive";
    private static final String ERROR_MSG_DEFAULT_COST_LESS_THAN_ZERO = "Default cost cannot be less than 0";
    private static final String ERROR_MSG_UNITS_LESS_THAN_ZERO = "Default units cannot be less than 0";
    private static final String ERROR_VR_SE_CHANGE_PERM_REQ = "Vendor Service Event Rate change permission is required";
    private static final String ERROR_UNABLE_SAVE_COST_UNIT = "Unable to save Service %s with Start Date: %s. Cost/Unit cannot be changed to 0. Service events exist which must have a Cost/Unit value.";
    private static final String WARN_UNABLE_SAVE_COST_UNIT = "Service %s with Start Date: %s has related service events that remain at their override rates.";
    public static final String WARN_UNABLE_SAVE_COST_UNIT_VALUE = "Cost/Unit changed for Service %s with Start Date: %s. This is used in %d Service Definition records for %d Participant(s).";
    private static final String CONTINUE_TO_CHANGE_RATE_OR_CANCEL = "Click Continue to make this rate change or Cancel.";
    private static final String ERROR_UNABLE_SAVE_SERVICE_COMBO = "Unable to save. Service combination cannot be changed...service schedule(s) exist.";
    private static final String ERROR_UNABLE_SAVE_DATE_RANGE = "Unable to save Service %s with Start Date: %s. Service Definition(s) exist outside proposed rate period.";
    private static final String ERROR_UNABLE_DELETE_RATE = "Unable to delete Service %s with Start Date: %s. service schedule(s) exist.";
    private static final String ERROR_UNABLE_SAVE_SE_DATE_RANGE = "Unable to save Service %s with Start Date: %s. Service Event(s) exist outside proposed rate period.";
    private static final String ERROR_UNABLE_DELETE_SE_RATE = "Unable to delete Service %s with Start Date: %s; Service Event(s) exist.";
    private static final String DEBUG_VALIDATE_SS_SE = "validateServiceScheduleRelatedChanges {}";
    private static final String DEBUG_ERR_OR_WARN_RAISED = "noErrorOrWarnRaised \n {} \n {}";
    public static final String NEWLINE = "<br/>";

    private static final boolean KEEP_GUID = false;
    private static final boolean CLEAR_GUID = true;
    public static final String CHANGED_ROW_CLASS = "changed-row";
    public static final String NEWROW_CLASS = "new-row";

    private static final String[] RATE_PROP_ARRAY = {VendorRate.START_DATE_PROP_NAME,
        VendorRate.PTS_SERVICE_COMBO_PROP_NAME, VendorRate.AGENT_CUSTOM_SERVICE_PROP_NAME};
    private static final String[] RATE_GROUP_SPLIT_PROP_ARRAY = {VendorRate.PTS_SERVICE_COMBO_PROP_NAME, VendorRate.AGENT_CUSTOM_SERVICE_PROP_NAME};

    private Binder<VendorRateWrapper> binder;

    private List<PtsServiceCombo> ptsServiceComboList = new ArrayList<>();
    private List<VendorRate> vendorRateList = new ArrayList<>();
    private List<VendorRate> inactiveVendorRateList = new ArrayList<>();

    private List<String> validationErrorMessageList = new ArrayList<>();
    //TODO: this should be moved into VendorRateValidationResult
    private List<String> validationWarnMessageList = new ArrayList<>();
    private boolean lastValidationCallResult = true;

    private RadioButtonGroup<String> activeOrAllRadioButtonGroup;
    private Agent selectedAgent;
    private Vendor selectedVendor;
    private boolean vendorSeRateChangePermission = false;

    private long newTemporaryVendorRateGuid = 0;

    private Column<VendorRateWrapper> startDateColumn;
    private Column<VendorRateWrapper> ptsProcedureColumn;
    private Column<VendorRateWrapper> ptsModifierColumn;
    private Column<VendorRateWrapper> ptsStandardRemarkColumn;
    private Column<VendorRateWrapper> agentCustomServiceColumn;

    private MultiRecordDateField stopDateField;
    private MultiRecordDateField startDateField;
    private ComboBox<PtsProcedure> ptsProcedureComboBox;
    private ComboBox<PtsModifier> ptsModifierComboBox;
    private ComboBox<PtsStandardRemark> ptsStandardRemarkComboBox;
    private ComboBox<AgentCustomService> agentCustomServiceComboBox;
    private TextField defaultUnitsField;
    private TextField defaultCostField;
    private TextField shortNameField;
    private Button historyButton;

    private List<PtsProcedure> ptsProcedureList = new ArrayList<>();
    private List<PtsModifier> ptsModifierList = new ArrayList<>();
    private List<PtsStandardRemark> ptsStandardRemarkList = new ArrayList<>();
    private List<AgentCustomService> agentCustomServiceList = new ArrayList<>();

    private ListDataProvider<PtsProcedure> ptsProcedureListDataProvider;
    private ListDataProvider<PtsModifier> ptsModifierListDataProvider;
    private ListDataProvider<PtsStandardRemark> ptsStandardRemarkListDataProvider;
    private ListDataProvider<AgentCustomService> agentCustomServiceListDataProvider;

    boolean web_service_list_errors = false;

    public MultiRecordRate(Button addButton) {
        super(VendorRateWrapper.class,
                locator().identityService().hasPermission(Permission.VENDOR_MAINTENANCE, NavConstants.ACTION_MODIFY),
                locator().identityService().hasPermission(Permission.VENDOR_MAINTENANCE, NavConstants.ACTION_DELETE),
                addButton);
        init();
    }

    private void init() {
        vendorSeRateChangePermission = locator().identityService().hasPermission(Permission.VENDOR_SERVICE_EVENT_RATE_CHANGE, NavConstants.ACTION_VIEW);
        initializeGridListeners();
        addActiveOrAllPanel();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        initListProviders();
    }

    private boolean needsUpdating(PtsServiceCombo existingValue, PtsServiceCombo updatedValue) {
        boolean returnResult = (updatedValue == null && existingValue != null)
                || (updatedValue != null && existingValue == null)
                || (updatedValue != null && existingValue != null && !updatedValue.equals(existingValue));
        log.debug(LOG_MSG_NEEDS_UPDATING_EXISTING_VALUE,
                getPtsServiceComboCodes(existingValue), getPtsServiceComboCodes(updatedValue), returnResult);
        return returnResult;
    }

    private void rowValueChange(VendorRateWrapper vendorRateWrapper) {
        if (vendorRateWrapper != null) {
            boolean startDateChanged = CompassNextUtils.DateChanged(vendorRateWrapper.getOrigStartDate(), vendorRateWrapper.getStartDate());
            boolean stopDateChanged = CompassNextUtils.DateChanged(vendorRateWrapper.getOrigStopDate(), vendorRateWrapper.getStopDate());
            vendorRateWrapper.setSsRelatedDateRangeUpdated(startDateChanged || stopDateChanged || vendorRateWrapper.isSsRelatedDateRangeUpdated());

            boolean serviceComboNeedsUpdating = doesServiceComboNeedUpdate(vendorRateWrapper);
            hasVendorRatePermissionKeyChanged(vendorRateWrapper, serviceComboNeedsUpdating);
            log.debug(LOG_MSG_ROW_VALUE_CHANGE, vendorRateWrapper);

            if (serviceComboNeedsUpdating) {
                vendorRateWrapper.getVendorRate().setPtsServiceCombo(vendorRateWrapper.getUpdatedPtsServiceCombo());
                String shortName = vendorRateWrapper.getVendorRate().getPtsServiceCombo() != null
                        ? vendorRateWrapper.getVendorRate().getPtsServiceCombo().getShortName()
                        : "";
                shortNameField.setValue(shortName);
                log.debug(LOG_MSG_SHORT_NAME_FIELD_VALUE_SET, shortName);
            }
            log.debug(LOG_MSG_VENDOR_RATE_WRAPPER_DIRTY_TRACKING_STATUS, vendorRateWrapper.getVendorRate().isDirtyTrackingEnabled());
            if (vendorRateWrapper.valuesUpdated()) {
                editPanelController.getConfig().getMultiRecordChangeService().updateRecord(vendorRateWrapper);
                editPanelController.enableButtonEditingMode();
                locator().eventBus().post(new CompassXEvent.VendorUpdateInProgressEvent(true));
                log.debug(LOG_MSG_VENDOR_RATE_WRAPPER_WAS_DIRTY, vendorRateWrapper, vendorRateWrapper.getVendorRate().getDirtyFields().keySet().toString());
            } else {
                log.debug(LOG_MSG_VENDOR_RATE_WRAPPER_WAS_NOT_DIRTY, vendorRateWrapper);
            }
        } else {
            log.debug(LOG_MESSAGE_VENDOR_RATE_WRAPPER_WAS_NULL);
        }
    }

    private boolean doesServiceComboNeedUpdate(VendorRateWrapper vendorRateWrapper) {
        PtsServiceCombo existingPtsServiceComboValue = vendorRateWrapper.getOrigPtsServiceCombo();
        PtsServiceCombo updatedPtsServiceComboValue = getPtsServiceComboUsingWrapperFields(vendorRateWrapper);
        vendorRateWrapper.setUpdatedPtsServiceCombo(updatedPtsServiceComboValue);
        boolean serviceComboNeedsUpdating = needsUpdating(existingPtsServiceComboValue, updatedPtsServiceComboValue);
        return serviceComboNeedsUpdating;
    }

    private void hasVendorRatePermissionKeyChanged(VendorRateWrapper vendorRateWrapper, boolean serviceComboNeedsUpdating) {
        vendorRateWrapper.setUpdatedAgentCustomService(vendorRateWrapper.getAgentCustomService());
        vendorRateWrapper.setUpdatedCostPerUnit(vendorRateWrapper.getDefaultCostPerUnit());

        // To-Do CM-6235: I don't think this is the right time or place to do the permission check.
        // Another rate line could be added which replaces this rate line exactly as it was ...
        // without any cost or custom service changes.  It seems that we cannot do this check until
        // the user is done editing.  If that is truly the case, then I'm not sure that we need all of
        // these fields in the wrapper class.
        boolean customServiceChanged = VendorviewUtils.entityChanged(vendorRateWrapper.getOrigAgentCustomService(), vendorRateWrapper.getUpdatedAgentCustomService());
        boolean costPerUnitChanged = VendorviewUtils.bigDecimalChanged(vendorRateWrapper.getOrigCostPerUnit(), vendorRateWrapper.getUpdatedCostPerUnit());

        vendorRateWrapper.setSsRelatedCostUnitUpdated(costPerUnitChanged || vendorRateWrapper.isSsRelatedCostUnitUpdated());
        vendorRateWrapper.setRatePermissionKeyChanged(serviceComboNeedsUpdating || customServiceChanged || vendorRateWrapper.isSsRelatedCostUnitUpdated() || vendorRateWrapper.isRatePermissionKeyChanged());
        vendorRateWrapper.setSsRelatedServiceComboUpdated(serviceComboNeedsUpdating || customServiceChanged || vendorRateWrapper.isSsRelatedServiceComboUpdated());
    }

    private void initializeGridListeners() {

        listGrid.addCellEditStartedListener(event -> {
            VendorRateWrapper vendorRateWrapper = event.getItem();
            binder.setBean(vendorRateWrapper);
            listGrid.select(vendorRateWrapper);
            refreshAllListDataProviders();
        });

        listGrid.addItemPropertyChangedListener(event -> {
            rowValueChange(event.getItem());
            binder.setBean(null);
        });
    }

    @Override
    public boolean gridShouldBeActive() {
        return selectedVendor != null;
    }

    @Override
    protected MultiRecordControllerModel<VendorRateWrapper> customizeConfig(MultiRecordControllerModel.Builder configBuilder) {
        return configBuilder.multiRecordTemplate(this).validator(this).build();
    }

    @Override
    public void setGridColumns() {
        binder = new Binder<>(VendorRateWrapper.class);

        binder.withValidator((value, context) -> validateFields(value, context, true));
        binder.addValueChangeListener(this::rowBinderValueChangeEvent);
        listGrid.removeAllColumns();
        if (hasDeletePermission()) {
            createAndBindDeleteColumn();
        }
        createAndBindStartDateColumn();
        createAndBindStopDateColumn();
        createAndBindProcedureColumn();
        createAndBindModifier();
        createAndBindStandardRemarkColumn();
        createAndBindShortNameColumn();
        createAndBindAgentCustomServiceColumn();
        createAndBindDefaultUnitsColumn();
        createAndBindDefaultCostPerUnitColumn();
        createAndBindHistoryColumn();

        listGrid.getColumns().forEach(col -> col.setResizable(true));
        listGrid.setClassNameGenerator((t) -> {
            return rowStyleClasses(t);
        });
        Map<String, String> colKeyToHeader = Map.of(SHORT_NAME_COLUMN_ID, SHORT_NAME_CAPTION,
                HISTORY_CAPTION, HISTORY_CAPTION);
        VaadinUtils.withHideableColumns(listGrid, colKeyToHeader::get, SHORT_NAME_COLUMN_ID, HISTORY_CAPTION);
    }

    protected String rowStyleClasses(VendorRateWrapper t) {
        String updatedClass = t.valuesUpdated() ? "updated" : "";
        String erroredClass = t.isInError() ? ERRORED_STYLECLASS : "";
        return (updatedClass.isEmpty()) ? erroredClass : updatedClass + " " + erroredClass;
    }
    protected static final String ERRORED_STYLECLASS = "errored";

    private void createAndBindHistoryColumn() {
        listGrid.addComponentColumn(vendorRate -> {
            historyButton = new Button(VaadinIcon.TIME_BACKWARD.create());
            historyButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL);
            historyButton.addThemeName(CustomButtonThemes.COMPACT_EXTRA);
            VaadinUtils.setDescription(historyButton, HISTORY_CAPTION);
            historyButton.addClickListener(clickEvent -> {
                if (StringUtils.isNotEmpty(vendorRate.getGuid())) {
                    AuditEntityViewerPopupDialog viewer = new AuditEntityViewerPopupDialog(vendorRate.getVendorRate());
                    viewer.show(getHistoryDescription(vendorRate.getVendorRate()));
                } else {
                    VaadinUtils.showNotification(NO_HISTORY_DATA_IS_AVAILABLE);
                }
            });
            return historyButton;
        })
                .setHeader(HISTORY_CAPTION)
                .setKey(HISTORY_CAPTION)
                .setWidth("80px")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(0)
                .setVisible(false);
    }

    private String getHistoryDescription(VendorRate vendorRate) {
        StringBuilder sb = new StringBuilder();
        sb.append(selectedAgent.getName());
        sb.append(CompassNextConstants.SLASH_SEPARATOR);
        sb.append(selectedVendor.getName());
        sb.append(CompassNextConstants.SLASH_SEPARATOR);
        sb.append(vendorRate.getPtsServiceCombo().getPtsProcedure().getCode());

        if (vendorRate.getPtsServiceCombo().getPtsModifier() != null) {
            sb.append(CompassNextConstants.SLASH_SEPARATOR);
            sb.append(vendorRate.getPtsServiceCombo().getPtsModifier().getCode());
        }
        if (vendorRate.getPtsServiceCombo().getPtsStandardRemark() != null) {
            sb.append(CompassNextConstants.SLASH_SEPARATOR);
            sb.append(vendorRate.getPtsServiceCombo().getPtsStandardRemark().getCode());
        }
        if (vendorRate.getAgentCustomService() != null) {
            sb.append(CompassNextConstants.SLASH_SEPARATOR);
            sb.append(vendorRate.getAgentCustomService().getDescription());
        }
        sb.append(CompassNextConstants.SLASH_SEPARATOR);
        sb.append(CompassNextUtils.getFormattedDateTime(vendorRate.getStartDate()));

        return sb.toString();
    }

    private void createAndBindDeleteColumn() throws IllegalStateException, IllegalArgumentException {
        listGrid.addComponentColumn(vendorRateWrapper -> {
            Button button = new Button(VaadinIcon.TRASH.create());
            VaadinUtils.setDescription(button, ACTION_CAPTION);
            button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL);
            button.addClickListener(clickEvent -> {
                vendorRateWrapper.setDeleting(true);
                editPanelController.getConfig().getMultiRecordChangeService().deleteRecord(vendorRateWrapper);
                editPanelController.enableButtonEditingMode();
                locator().eventBus().post(new CompassXEvent.VendorUpdateInProgressEvent(true));
            });
            Div statusDiv = new Div(button);
            setupStatusIndicator(vendorRateWrapper, statusDiv);
            return statusDiv;
        })
                .setHeader(VaadinUtils.tooltipComponent(Span::new, ACTION_CAPTION))
                .setKey(ACTION_CAPTION)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setWidth("50px");
    }

    public void setupStatusIndicator(VendorRateWrapper vendorRateWrapper, Div statusDiv) {
        Tooltip tooltip = Tooltip.forComponent(statusDiv);
        handleNewRowStatus(vendorRateWrapper, statusDiv, tooltip);
        handleUpdatedRowStatus(vendorRateWrapper, statusDiv, tooltip);
        handleErrorStatus(vendorRateWrapper, statusDiv, tooltip);
    }

    private void handleNewRowStatus(VendorRateWrapper vendorRateWrapper, Div statusDiv, Tooltip tooltip) {
        if (!vendorRateWrapper.isPersisted()) {
            statusDiv.addClassName(NEWROW_CLASS);
        }
    }

    private void handleUpdatedRowStatus(VendorRateWrapper vendorRateWrapper, Div statusDiv, Tooltip tooltip) {
        if (vendorRateWrapper.valuesUpdated() && vendorRateWrapper.isPersisted()) {
            statusDiv.addClassName(CHANGED_ROW_CLASS);
        }
    }

    private void handleErrorStatus(VendorRateWrapper vendorRateWrapper, Div statusDiv, Tooltip tooltip) {
        if (vendorRateWrapper.isInError()) {
            statusDiv.addClassName(ERRORED_STYLECLASS);
            String currentTooltipText = tooltip.getText();
            String errorMsg = Jsoup.clean(String.join(". ", vendorRateWrapper.getErrorMessage()), Safelist.none());
            tooltip.setText(
                    (StringUtils.isEmpty(currentTooltipText) ? "" : currentTooltipText + ": ") + errorMsg);
        }
    }

    private void createAndBindDefaultCostPerUnitColumn() {
        defaultCostField = new TextField();
        defaultCostField.setReadOnly(!hasModifyPermission());
        defaultCostField.setValueChangeMode(ValueChangeMode.ON_BLUR);
        defaultCostField.addThemeName(CustomFieldThemes.COMPACT_EXTRA);
        defaultCostField.setWidthFull();
        defaultCostField.addFocusListener(e -> InputTextHighlight.highlightText(e.getSource()));
        VaadinUtils.applyDecimalMask(defaultCostField, CompassNextConstants.DEFAULT_DECIMAL_SCALE);
        binder.forField(defaultCostField)
                .withConverter(new BigDecimalDollarConverter(NOT_A_VALID_VALUE))
                .bind(VendorRateWrapper::getDefaultCostPerUnit, VendorRateWrapper::setDefaultCostPerUnit);
        Span header = new Span(COST_PER_UNIT_CAPTION);
        VaadinUtils.setDescription(header, COST_PER_UNIT_CAPTION);
        listGrid.addEditColumn(this::displayDefaultCostPerUnit)
                .custom(defaultCostField, (item, value) -> {
                })
                .setComparator(this::displayDefaultCostPerUnit)
                .setHeader(VaadinUtils.tooltipComponent(Span::new, COST_PER_UNIT_CAPTION))
                .setId(VendorRate.DEFAULT_COST_PER_UNIT_PROP_NAME);
    }

    private String displayDefaultCostPerUnit(VendorRateWrapper vendorRateWrapper) {
        return vendorRateWrapper.getDefaultCostPerUnit() != null ? NumberFormat.getCurrencyInstance().format((vendorRateWrapper.getDefaultCostPerUnit())) : "";
    }

    private void createAndBindDefaultUnitsColumn() {
        defaultUnitsField = new TextField();
        defaultUnitsField.setReadOnly(!hasModifyPermission());
        defaultUnitsField.setValueChangeMode(ValueChangeMode.ON_BLUR);
        defaultUnitsField.addThemeName(CustomFieldThemes.COMPACT_EXTRA);
        defaultUnitsField.setWidthFull();
        defaultUnitsField.addFocusListener(e -> InputTextHighlight.highlightText(e.getSource()));
        VaadinUtils.applyUnitsMask(defaultUnitsField, CompassNextConstants.DEFAULT_DECIMAL_SCALE);
        binder.forField(defaultUnitsField)
                .withConverter(new StringToUnitConverter(NOT_A_VALID_VALUE))
                .bind(VendorRateWrapper::getDefaultUnits, VendorRateWrapper::setDefaultUnits);
        listGrid.addEditColumn(this::displayDefaultUnits)
                .custom(defaultUnitsField, (item, value) -> {
                })
                .setComparator(this::displayDefaultUnits)
                .setHeader(VaadinUtils.tooltipComponent(Span::new, UNITS_CAPTION))
                .setId(VendorRate.DEFAULT_UNITS_PROP_NAME);
    }

    private String displayDefaultUnits(VendorRateWrapper vendorRateWrapper) {
        return Optional.ofNullable(vendorRateWrapper.getDefaultUnits()).map(NumberUtils::formatUnits).orElse("");
    }

    private void createAndBindShortNameColumn() {
        shortNameField = new TextField();
        shortNameField.setReadOnly(true);
        binder.forField(shortNameField).bind(VendorRateWrapper::getShortName, VendorRateWrapper::setShortName);
        listGrid.addColumn(this::displayShortName)
                .setSortable(false)
                .setHeader(VaadinUtils.tooltipComponent(Span::new, SHORT_NAME_CAPTION))
                .setKey(SHORT_NAME_COLUMN_ID)
                .setWidth("300px")
                .setId(SHORT_NAME_COLUMN_ID);
    }

    private String displayShortName(VendorRateWrapper vendorRateWrapper) {
        return vendorRateWrapper.getPtsServiceCombo() != null
                ? Optional.ofNullable(vendorRateWrapper.getPtsServiceCombo().getShortName()).orElse("")
                : "";
    }

    private void createAndBindAgentCustomServiceColumn() {
        agentCustomServiceComboBox = new ComboBox<>();
        agentCustomServiceComboBox.setReadOnly(!hasModifyPermission());
        agentCustomServiceComboBox.setWidthFull();
        VaadinUtils.setOverlayWidth(agentCustomServiceComboBox, "400px");
        agentCustomServiceComboBox.addThemeName(CustomFieldThemes.COMPACT_EXTRA);
        agentCustomServiceComboBox.setItemLabelGenerator(this::displayAgentCustomServiceCaption);
        agentCustomServiceComboBox.addFocusListener(e -> InputTextHighlight.highlightText(e.getSource()));
        binder.forField(agentCustomServiceComboBox).bind(VendorRateWrapper::getAgentCustomService, VendorRateWrapper::setAgentCustomService);
        agentCustomServiceColumn = listGrid.addEditColumn(VendorRateWrapper::getAgentCustomService, new TextRenderer<>(this::displayAgentCustomService))
                .custom(agentCustomServiceComboBox, (item, value) -> {
                })
                .setComparator(this::displayAgentCustomService)
                .setWidth("180px")
                .setHeader(VaadinUtils.tooltipComponent(Span::new, CUSTOM_SERVICE_CAPTION));
        agentCustomServiceColumn.setId(VendorRate.AGENT_CUSTOM_SERVICE_PROP_NAME);
    }

    private String displayAgentCustomServiceCaption(AgentCustomService agentCustomService) {
        return agentCustomService == null ? "" : Optional.ofNullable(agentCustomService.getDescription()).orElse("");
    }

    private String displayAgentCustomService(VendorRateWrapper vendorRateWrapper) {
        return displayAgentCustomServiceCaption(vendorRateWrapper.getAgentCustomService());
    }

    private void createAndBindStandardRemarkColumn() {
        ptsStandardRemarkComboBox = new ComboBox<>();
        ptsStandardRemarkComboBox.setReadOnly(!hasModifyPermission());
        ptsStandardRemarkComboBox.setWidthFull();
        VaadinUtils.setOverlayWidth(ptsStandardRemarkComboBox, "400px");
        ptsStandardRemarkComboBox.addThemeName(CustomFieldThemes.COMPACT_EXTRA);
        ptsStandardRemarkComboBox.setItemLabelGenerator(this::displayStandardRemarkCaption);
        ptsStandardRemarkComboBox.addFocusListener(e -> InputTextHighlight.highlightText(e.getSource()));
        binder.forField(ptsStandardRemarkComboBox).bind(VendorRateWrapper::getPtsStandardRemark, VendorRateWrapper::setPtsStandardRemark);
        ptsStandardRemarkColumn = listGrid.addEditColumn(VendorRateWrapper::getPtsStandardRemark, new TextRenderer<>(this::displayStandardRemark))
                .custom(ptsStandardRemarkComboBox, (item, value) -> {
                })
                .setComparator(this::displayStandardRemark)
                .setWidth("160px")
                .setHeader(VaadinUtils.tooltipComponent(Span::new, STD_REMARK_CODE_CAPTION));
        ptsStandardRemarkColumn.setId(PtsServiceCombo.PTS_STANDARD_REMARK_PROP_NAME);
    }

    private String displayStandardRemarkCaption(PtsStandardRemark item) {
        return (item != null) ? (item.getCode() + CAPTION_GENERATOR_SEPARATOR + item.getDescription()) : "";
    }

    private String displayStandardRemark(VendorRateWrapper vendorRateWrapper) {
        return vendorRateWrapper.getPtsStandardRemark() != null ? Optional.ofNullable(vendorRateWrapper.getPtsStandardRemark().getCode()).orElse("") : "";
    }

    private void createAndBindModifier() {
        ptsModifierComboBox = new ComboBox<>();
        ptsModifierComboBox.setReadOnly(!hasModifyPermission());
        ptsModifierComboBox.setWidthFull();
        VaadinUtils.setOverlayWidth(ptsModifierComboBox, "400px");
        ptsModifierComboBox.addThemeName(CustomFieldThemes.COMPACT_EXTRA);
        ptsModifierComboBox.setItemLabelGenerator(this::displayModifierCaption);
        ptsModifierComboBox.addFocusListener(e -> InputTextHighlight.highlightText(e.getSource()));
        binder.forField(ptsModifierComboBox).bind(VendorRateWrapper::getPtsModifier, VendorRateWrapper::setPtsModifier);
        ptsModifierColumn = listGrid.addEditColumn(VendorRateWrapper::getPtsModifier, new TextRenderer<>(this::displayModifier))
                .custom(ptsModifierComboBox, (item, value) -> {
                })
                .setComparator(this::displayModifier)
                .setHeader(VaadinUtils.tooltipComponent(Span::new, MODIFIER_CODE_CAPTION));
        ptsModifierColumn.setId(PtsServiceCombo.PTS_MODIFIER_PROP_NAME);
    }

    private String displayModifier(VendorRateWrapper rate) {
        return rate.getPtsModifier() != null ? Optional.ofNullable(rate.getPtsModifier().getCode()).orElse("") : "";
    }

    private String displayModifierCaption(PtsModifier ptsModifier) {
        return (ptsModifier != null) ? (ptsModifier.getCode() + CAPTION_GENERATOR_SEPARATOR + ptsModifier.getDescription()) : "";
    }

    private void createAndBindProcedureColumn() {
        ptsProcedureComboBox = new ComboBox<>();
        ptsProcedureComboBox.setReadOnly(!hasModifyPermission());
        ptsProcedureComboBox.setWidthFull();
        VaadinUtils.setOverlayWidth(ptsProcedureComboBox, "400px");
        ptsProcedureComboBox.addThemeName(CustomFieldThemes.COMPACT_EXTRA);
        ptsProcedureComboBox.setItemLabelGenerator(this::displayProcedureCaption);
        ptsProcedureComboBox.addFocusListener(e -> InputTextHighlight.highlightText(e.getSource()));
        binder.forField(ptsProcedureComboBox)
                .bind(VendorRateWrapper::getPtsProcedure, VendorRateWrapper::setPtsProcedure);
        ptsProcedureColumn = listGrid.addEditColumn(VendorRateWrapper::getPtsProcedure, new TextRenderer<>(this::displayProcedure))
                .custom(ptsProcedureComboBox, (item, value) -> {
                })
                .setComparator(this::displayProcedure)
                .setHeader(VaadinUtils.tooltipComponent(Span::new, PROCEDURE_CODE_CAPTION));
        ptsProcedureColumn.setId(PtsServiceCombo.PTS_PROCEDURE_PROP_NAME);
    }

    private String displayProcedure(VendorRateWrapper rate) {
        return rate.getPtsProcedure() != null ? Optional.ofNullable(rate.getPtsProcedure().getCode()).orElse("") : "";
    }

    private String displayProcedureCaption(PtsProcedure item) {
        return (item != null) ? (item.getCode() + CAPTION_GENERATOR_SEPARATOR + item.getDescription()) : "";
    }

    private void createAndBindStopDateColumn() throws IllegalArgumentException {
        stopDateField = new MultiRecordDateField(null);
        stopDateField.setReadOnly(!hasModifyPermission());
        stopDateField.setWidthFull();
        stopDateField.setAutoOpen(false);
        stopDateField.addThemeName(CustomFieldThemes.COMPACT_EXTRA);
        stopDateField.setPattern(MultiRecordDateField.STANDARD_DATE_MASK);
        stopDateField.addFocusListener(e -> InputTextHighlight.highlightText(e.getSource()));
        binder.forField(stopDateField)
                .bind(VendorRateWrapper::getStopLocalDate, VendorRateWrapper::setStopLocalDate);
        listGrid.addEditColumn(VendorRateWrapper::getStopLocalDate, new LocalDateRenderer<>(VendorRateWrapper::getStopLocalDate, STANDARD_DATE_FORMAT))
                .custom(stopDateField, (item, value) -> {
                })
                .setComparator(VendorRateWrapper::getStopLocalDate)
                .setHeader(VaadinUtils.tooltipComponent(Span::new, STOP_DATE_CAPTION))
                .setWidth("130px")
                .setId(VendorRate.STOP_DATE_PROP_NAME);
    }

    private void createAndBindStartDateColumn() throws IllegalArgumentException {
        startDateField = new MultiRecordDateField(null);
        startDateField.setReadOnly(!hasModifyPermission());
        startDateField.setWidthFull();
        startDateField.setAutoOpen(false);
        startDateField.addThemeName(CustomFieldThemes.COMPACT_EXTRA);
        startDateField.setPattern(MultiRecordDateField.STANDARD_DATE_MASK);
        startDateField.addFocusListener(e -> InputTextHighlight.highlightText(e.getSource()));
        binder.forField(startDateField)
                .bind(VendorRateWrapper::getStartLocalDate, VendorRateWrapper::setStartLocalDate);
        startDateColumn = listGrid
                .addEditColumn(VendorRateWrapper::getStartLocalDate, new LocalDateRenderer<>(VendorRateWrapper::getStartLocalDate, STANDARD_DATE_FORMAT))
                .custom(startDateField, (item, value) -> {
                })
                .setComparator(VendorRateWrapper::getStartLocalDate)
                .setWidth("130px")
                .setHeader(VaadinUtils.tooltipComponent(Span::new, START_DATE_CAPTION));
        startDateColumn.setId(VendorRate.START_DATE_PROP_NAME);
    }

    private void populatePtsProcedureList() {
        ptsProcedureList = new ArrayList<>();
        ptsServiceComboList.stream().filter(nextCombo -> !ptsProcedureList.contains(nextCombo.getPtsProcedure()))
                .forEach(nextCombo -> ptsProcedureList.add(nextCombo.getPtsProcedure()));
        Collections.sort(ptsProcedureList, Comparator.comparing(PtsProcedure::getCode));
    }

    private void populatePtsModifierList() {
        ptsModifierList = new ArrayList<>();
        ptsServiceComboList.stream().filter(nextCombo -> includeModifier(nextCombo)).forEach(nextCombo -> ptsModifierList.add(nextCombo.getPtsModifier()));
        Collections.sort(ptsModifierList, Comparator.comparing(PtsModifier::getCode));
    }

    private boolean includeModifier(PtsServiceCombo nextCombo) {
        return nextCombo.getPtsModifier() != null && !ptsModifierList.contains(nextCombo.getPtsModifier());
    }

    private void populatePtsStandardRemarkList() {
        ptsStandardRemarkList = new ArrayList<>();
        ptsServiceComboList.stream().filter(nextCombo -> includeStandardRemark(nextCombo))
                .forEach(nextCombo -> ptsStandardRemarkList.add(nextCombo.getPtsStandardRemark()));
        Collections.sort(ptsStandardRemarkList, Comparator.comparing(PtsStandardRemark::getCode));
    }

    private boolean includeStandardRemark(PtsServiceCombo nextCombo) {
        return nextCombo.getPtsStandardRemark() != null && !ptsStandardRemarkList.contains(nextCombo.getPtsStandardRemark());
    }

    public void addActiveOrAllPanel() {
        StatusRadioGroupLayout activeRatesLayout = new StatusRadioGroupLayout();
        activeOrAllRadioButtonGroup = activeRatesLayout.getContent();
        activeOrAllRadioButtonGroup.getStyle().set("margin-top", "1rem");
        activeOrAllRadioButtonGroup.setItems(ACTIVE_RATES_CAPTION_AND_VALUE, ALL_RATES_CAPTION_AND_VALUE);
        activeOrAllRadioButtonGroup.setValue(ACTIVE_RATES_CAPTION_AND_VALUE);
        activeOrAllRadioButtonGroup.addValueChangeListener(event -> {
            if (event.isFromClient()) {
                if (editPanelController.getConfig().getUnsavedEditIndicator().isVisible()) {
                    activeOrAllRadioButtonGroup.setValue(event.getOldValue());
                    VaadinUtils.showErrorNotification(UNABLE_TO_CHANGE_ALL_ACTIVE_FILTER);
                } else {
                    this.editPanelController.clearItems();
                    reset();
                }
            }
        });
        HorizontalLayout activeOrAllWithHelpLayout = new HorizontalLayout();
        activeOrAllWithHelpLayout.setWidthFull();
        activeOrAllWithHelpLayout.add(activeRatesLayout);
        MultiRecordHelpButton help = new MultiRecordHelpButton();
        activeOrAllWithHelpLayout.add(help);
        activeOrAllWithHelpLayout.expand(activeRatesLayout);

        dataPanel.addComponentAtIndex(0, activeOrAllWithHelpLayout);
    }

    @Override
    public void reset() {
        super.reset();
        activeOrAllRadioButtonGroup.setEnabled(selectedAgent != null);
        binder.setBean(null);
    }

    public void refresh() {
        this.editPanelController.doCancel();
    }

    private void loadPtsServiceComboList() {
        ptsServiceComboList = new ArrayList<>();
        try {
            ptsServiceComboList = locator().vendorRateService().getPtsServiceComboList();
        } catch (Exception ex) {
            web_service_list_errors = true;
            VaadinUtils.showErrorNotification(ERROR_MSG_LOADING_PTS__SERVICE__COMBO_LIST);
            log.error(ERROR_IN_LOAD_MASTER_SERVICE_LIST, ex);
        }
    }

    public void loadVendorRateList() {
        vendorRateList = new ArrayList<>();
        if (selectedVendor != null && selectedVendor.getGuid() != null && !selectedVendor.getGuid().isEmpty()) {
            try {
                vendorRateList = locator().vendorRateService().getAllVendorRates(selectedVendor.getGuid());
            } catch (Exception ex) {
                web_service_list_errors = true;
                VaadinUtils.showErrorNotification(ERROR_MSG_LOADING_VENDOR_RATE_LIST);
                log.error(ERROR_LOAD_MASTER_RATE_LIST_WITH_VENDOR_GUID, selectedVendor.getGuid(), ex);
            }
        }
    }

    public void loadAgentCustomServiceList() {
        agentCustomServiceList = new ArrayList<>();
        if (selectedAgent != null && selectedAgent.getGuid() != null && !selectedAgent.getGuid().isEmpty()) {
            try {
                agentCustomServiceList = locator().vendorRateService().getAgentCustomServiceList(selectedAgent.getGuid());
            } catch (Exception ex) {
                web_service_list_errors = true;
                VaadinUtils.showErrorNotification(ERROR_MSG_LOADING_AGENT_CUSTOM_SERVICE);
                log.error(ERROR_LOAD_MASTER_CUSTOM_SERVICE_LIST_WITH_AGENT, selectedAgent.getGuid(), ex);
            }
        }
    }

    public static boolean activeForStopDate(LocalDate stopDate) {
        return activeForStopDate(localDateToDate(stopDate));
    }

    public static boolean activeForStopDate(Date stopDate) {
        Date today = new Date();
        return stopDate == null || !stopDate.before(today);
    }

    public List<VendorRateWrapper> getVendorRateWrapperList() {
        loadVendorRateList();
        vendorRateList.stream().forEach(rate -> rate.setPtsServiceCombo(attachHydratedPtsServiceCombo(rate.getPtsServiceCombo())));

        loadAgentCustomServiceList();
        List<VendorRate> rateList = new ArrayList<>();
        inactiveVendorRateList.clear();
        if (activeOrAllRadioButtonGroup.getValue().equals(ACTIVE_RATES_CAPTION_AND_VALUE)) {
            rateList = vendorRateList.stream().filter(vendorRate -> activeForStopDate(vendorRate.getStopDate())).collect(Collectors.toList());
            inactiveVendorRateList = vendorRateList.stream().filter(nextRate -> !activeForStopDate(nextRate.getStopDate()))
                    .collect(Collectors.toList());
        } else {
            rateList.addAll(vendorRateList);
        }

        List<VendorRateWrapper> wrappedRateList = rateList.stream().map(VendorRateWrapper::new).collect(Collectors.toList());
        return wrappedRateList;
    }

    private VendorRate mapWrapperToRate(VendorRateWrapper vendorRateWrapper, boolean clearGuid) {
        if (clearGuid) {
            vendorRateWrapper.setGuid(null);
        }
        return vendorRateWrapper.getVendorRate();
    }

    @Override
    public boolean saveData() throws MultiRecordException {
        if (web_service_list_errors) {
            VaadinUtils.showErrorNotification(ERROR_MSG_PREVIOUS_DATA_LOADING_ERROR_OCCURRED);
        } else {
            MultiRecordChangeService<VendorRateWrapper> multiRecordChangeService = editPanelController.getConfig().getMultiRecordChangeService();
            try {
                List<VendorRate> updatedAndAddedRecords = new ArrayList<>();
                List<VendorRate> deletedRecords = new ArrayList<>();
                multiRecordChangeService.getAddedRecords().stream().map(vrw -> mapWrapperToRate(vrw, CLEAR_GUID)).forEach(vr -> updatedAndAddedRecords.add(vr));
                multiRecordChangeService.getUpdatedRecords().stream().map(vrw -> mapWrapperToRate(vrw, KEEP_GUID))
                        .forEach(vr -> updatedAndAddedRecords.add(vr));
                multiRecordChangeService.getDeletedRecords().stream().map(vrw -> mapWrapperToRate(vrw, KEEP_GUID)).forEach(vr -> deletedRecords.add(vr));
                MultiRecordUtils.mergeRecreatedDeletes(Arrays.asList(RATE_PROP_ARRAY), updatedAndAddedRecords, deletedRecords);

                if (!updatedAndAddedRecords.isEmpty() || !deletedRecords.isEmpty()) {
                    VendorRateUpdateResult updateResult = locator().vendorRateService().updateVendorRates(selectedVendor.getGuid(), updatedAndAddedRecords, deletedRecords);
                    if (!updateResult.isSuccess()) {
                        showErrorNotification(updateResult);
                        markRowsWithErrors(updateResult);
                    }
                    return updateResult.isSuccess();
                }

            } catch (Exception ex) {
                log.error("saveData caught exception: " + ex.getMessage());
                throw new MultiRecordException(ex.getMessage());
            }
        }
        return false;
    }

    @Override
    public VendorRateWrapper generateRecord() {
        VendorRateWrapper vendorRateWrapper = new VendorRateWrapper();
        vendorRateWrapper.setVendorRate(new VendorRate());
        vendorRateWrapper.setGuid(String.valueOf(newTemporaryVendorRateGuid++));
        vendorRateWrapper.setDefaultCostPerUnit(new BigDecimal(0));
        vendorRateWrapper.setDefaultUnits(new BigDecimal(0));
        vendorRateWrapper.setStartLocalDate(LocalDate.now());
        binder.setBean(null);
        return vendorRateWrapper;
    }

    @Override
    public void loadTableData() {
        setTableData(getVendorRateWrapperList());
        locator().eventBus().post(new CompassXEvent.VendorUpdateInProgressEvent(false));
    }

    private PtsServiceCombo attachHydratedPtsServiceCombo(PtsServiceCombo combo) {
        PtsServiceCombo ptsServiceCombo = null;
        if (combo != null) {
            ptsServiceCombo = ptsServiceComboList.stream().filter((serviceCombo) -> serviceCombo.equals(combo)).findAny().orElse(null);
        } else {
            log.error(NULL_COMBO_PROVIDED_NOTHING_TO_FIND);
        }

        if (ptsServiceCombo == null) {
            log.error(NO_SERVICE_COMBO_RECORD_FOUND);
        }
        return ptsServiceCombo;
    }

    @Override
    public void setSortOrder() {
        List<GridSortOrder<VendorRateWrapper>> listGridSortOrders = new GridSortOrderBuilder<VendorRateWrapper>()
                .thenAsc(ptsProcedureColumn)
                .thenAsc(ptsModifierColumn)
                .thenAsc(ptsStandardRemarkColumn)
                .thenAsc(agentCustomServiceColumn)
                .thenDesc(startDateColumn).build();
        listGrid.sort(listGridSortOrders);
    }

    private void logVendorRate(String messagePrefix, VendorRate vendorRate) {
        DateFormat dateFormat = new SimpleDateFormat(STANDARD_DATE_FORMAT);
        log.debug(LOG_MSG_VENDOR_RATE,
                messagePrefix,
                vendorRate.getPtsServiceCombo() != null && vendorRate.getPtsServiceCombo().getPtsProcedure() != null
                ? vendorRate.getPtsServiceCombo().getPtsProcedure().getCode()
                : "",
                vendorRate.getPtsServiceCombo() != null && vendorRate.getPtsServiceCombo().getPtsModifier() != null
                ? vendorRate.getPtsServiceCombo().getPtsModifier().getCode()
                : "",
                vendorRate.getPtsServiceCombo() != null && vendorRate.getPtsServiceCombo().getPtsStandardRemark() != null
                ? vendorRate.getPtsServiceCombo().getPtsStandardRemark().getCode()
                : "",
                vendorRate.getAgentCustomService() != null ? vendorRate.getAgentCustomService().getDescription() : "",
                vendorRate.getStartDate() != null ? dateFormat.format(vendorRate.getStartDate()) : "",
                vendorRate.getStopDate() != null ? dateFormat.format(vendorRate.getStopDate()) : "",
                vendorRate.getDefaultUnits(),
                vendorRate.getDefaultCostPerUnit());
    }

    /**
     *
     * @param vendorRateWrapper
     * @param valueContext
     * @param notifyErrors - pop up error notifications for any errors.
     * Otherwise just build error messages
     * @return
     */
    private ValidationResult validateFields(VendorRateWrapper vendorRateWrapper, ValueContext valueContext, boolean notifyErrors) {
        boolean ptsProcedureMissing = false;
        boolean ptsServiceComboMissing = false;
        vendorRateWrapper.clearErrors();
        if (valueContext == null) {
            ptsProcedureMissing = isMissing(vendorRateWrapper.getPtsProcedure());
            ptsServiceComboMissing = isMissing(vendorRateWrapper.getVendorRate().getPtsServiceCombo());
        }
        boolean startDateMissing = isMissing(vendorRateWrapper.getVendorRate().getStartDate());
        boolean startDateTooEarly = isStartDateTooEarly(vendorRateWrapper.getVendorRate().getStartDate());
        boolean stopDateTooEarly = isStopDateTooEarly(vendorRateWrapper.getVendorRate().getStartDate(),
                vendorRateWrapper.getVendorRate().getStopDate());
        boolean defaultUnitsInvalid = isDefaultUnitsInvalid(vendorRateWrapper.getDefaultUnits());
        boolean defaultCostPerUnitInvalid = isDefaultCostPerUnitInvalid(vendorRateWrapper.getVendorRate().getDefaultCostPerUnit());
        boolean valid = !(ptsProcedureMissing || ptsServiceComboMissing || startDateMissing
                || startDateTooEarly || defaultCostPerUnitInvalid || defaultUnitsInvalid || stopDateTooEarly);
        StringBuilder rowErrorStringBuilder = new StringBuilder();
        if (valid) {
            lastValidationCallResult = true;
            return ValidationResult.ok();
        } else {
            if (startDateMissing) {
                appendErrorAndNotify(vendorRateWrapper, rowErrorStringBuilder, ERROR_MSG_START_DATE_REQUIRED, notifyErrors);
            } else if (startDateTooEarly) {
                appendErrorAndNotify(vendorRateWrapper, rowErrorStringBuilder, ERROR_MSG_START_DATE_MUST_BE_ON_OR_AFTER, notifyErrors);
            }
            if (stopDateTooEarly) {
                appendErrorAndNotify(vendorRateWrapper, rowErrorStringBuilder, ERROR_MSG_STOP_DATE_MUST_BE_ON_OR_AFTER_START_DATE, notifyErrors);
            }

            if (defaultUnitsInvalid) {
                appendErrorAndNotify(vendorRateWrapper, rowErrorStringBuilder, ERROR_MSG_UNITS_LESS_THAN_ZERO, notifyErrors);
            }

            if (defaultCostPerUnitInvalid) {
                appendErrorAndNotify(vendorRateWrapper, rowErrorStringBuilder, ERROR_MSG_DEFAULT_COST_LESS_THAN_ZERO, notifyErrors);
            }
            listGrid.getDataProvider().refreshItem(vendorRateWrapper);
            lastValidationCallResult = false;
            return ValidationResult.error("");
        }
    }

    private void appendErrorAndNotify(VendorRateWrapper vendorRateWrapper, StringBuilder rowErrorStringBuilder, String errorMsg, boolean notifyErrors) {
        rowErrorStringBuilder.append(errorMsg);
        if (notifyErrors) {
            showFieldValidationError(errorMsg);
        } else {
            //not sure about this but I think it is confusing to show the error icon when editing because we revert validation errors.
            vendorRateWrapper.addErrorMessage(errorMsg);
        }
    }

    private boolean isDefaultCostPerUnitInvalid(BigDecimal defaultCostPerUnit) {
        return !isMissing(defaultCostPerUnit) && defaultCostPerUnit.compareTo(BigDecimal.ZERO) < 0;
    }

    private boolean isDefaultUnitsInvalid(BigDecimal defaultUnits) {
        return !isMissing(defaultUnits) && defaultUnits.compareTo(BigDecimal.ZERO) < 0;
    }

    private boolean isStopDateTooEarly(Date startDate, Date stopDate) {
        return !isMissing(startDate) && !isMissing(stopDate) && startDate.after(stopDate);
    }

    private boolean isStartDateTooEarly(LocalDate startLocalDate) {
        Date startDate = Date.from(startLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return isStartDateTooEarly(startDate);
    }

    private boolean isStartDateTooEarly(Date startDate) {
        DateFormat dateFormat = new SimpleDateFormat(STANDARD_DATE_FORMAT);
        Calendar minimumValidStartDate = getMinimumStartDate(dateFormat);
        boolean startDateTooEarly = !isMissing(startDate)
                && startDate.before(minimumValidStartDate.getTime());
        return startDateTooEarly;
    }

    private boolean isMissing(Object object) {
        return object == null;
    }

    public static final String ERROR_MSG_PROCEDURE_REQUIRED = "Procedure required";

    private Calendar getMinimumStartDate(DateFormat dateFormat) {
        Calendar validStartDate = Calendar.getInstance();
        try {
            validStartDate.setTime(dateFormat.parse(MIN_START_DATE));
        } catch (ParseException ex) {
            log.error("Should never happen as we control the date format");
        }
        return validStartDate;
    }

    @Override
    public boolean isValid(List<VendorRateWrapper> vendorRateWrappers) {
        validationWarnMessageList = new ArrayList<>();

        clearInvalid(vendorRateWrappers);
        VendorRateValiationResult rateValiationResult = validateRates(vendorRateWrappers);
        if (!rateValiationResult.ok()) {
            markRowsWithErrors(rateValiationResult);
        }
        log.debug(LOG_VALIDATION, rateValiationResult, validationWarnMessageList);
        displayValidationIssues(rateValiationResult);

        return rateValiationResult.ok();
    }

    protected void displayValidationIssues(VendorRateValiationResult rateValiationResult) {
        if (!rateValiationResult.ok()) {
            if (rateValiationResult.isError()) {
                String validationErrorMessages = buildValidationErrorMessages(rateValiationResult);
                VaadinUtils.showPersistentErrorNotification(validationErrorMessages);
            } else {
                showWarningConfirmationDialog(rateValiationResult);
            }
        }
    }

    protected void showWarningConfirmationDialog(VendorRateValiationResult rateValiationResult) {
        StringBuilder sb = new StringBuilder();
        for (String warnMessage : validationWarnMessageList) {
            sb.append(NEWLINE);
            sb.append(warnMessage);
        }
        sb.append(NEWLINE);
        sb.append(CONTINUE_TO_CHANGE_RATE_OR_CANCEL);
        new ConfirmDialog(CompassNextConstants.CONFIRM_DIALOG_CAPTION, sb.toString(), CompassNextConstants.CONTINUE, e -> {
            if (e.isFromClient()) {
                rateValiationResult.setPassedSsRelatedWarnValidation(true);
                doSaveData();
            }
        }, CompassNextConstants.CANCEL_BTN_LBL, e -> {
        }).open();
    }

    protected VendorRateValiationResult validateRates(List<VendorRateWrapper> vendorRateWrappers) {
        VendorRateValiationResult rateValiationResult = new VendorRateValiationResult();
        boolean fieldsValid = vendorRateWrappers.stream().allMatch(vrw -> !validateFields(vrw, null, false).isError());
        List<VendorRate> vendorRates = new ArrayList<>();
        vendorRates.addAll(vendorRateWrappers.stream().map(vrw -> vrw.getVendorRate()).collect(Collectors.toList()));
        vendorRates.addAll(inactiveVendorRateList); // Make sure we're validating against all the data

        List<VendorRate> overlappingRates = validateOverlappingVendorRates(vendorRates);
        List<VendorRate> consecutiveRates = validateConsecutiveServicesPresent(vendorRates);
        List<VendorRate> invalidRates = validateVendorRates(vendorRateWrappers);
        boolean passedPermissionValidation = validateSeRateChangePermission(vendorRateWrappers);
        boolean passedSsRelatedErrorValidation = validateServiceScheduleRelatedChanges(vendorRateWrappers);
        final VendorRateUpdateResult vendorRateUpdateResult = new VendorRateUpdateResult(overlappingRates, consecutiveRates);
        vendorRateUpdateResult.setInvalidRates(invalidRates);
        rateValiationResult.setRateUpdateResult(vendorRateUpdateResult);
        rateValiationResult.setFieldsValid(fieldsValid);
        rateValiationResult.setPassedPermissionValidation(passedPermissionValidation);
        rateValiationResult.setPassedSsRelatedErrorValidation(passedSsRelatedErrorValidation);
        return rateValiationResult;
    }

    protected String buildValidationErrorMessages(VendorRateValiationResult rateValiationResult) {
        StringBuilder sb = new StringBuilder();
        appendErrorMessage(sb, !rateValiationResult.isPassedPermissionValidation(), ERROR_VR_SE_CHANGE_PERM_REQ);
        appendErrorMessage(sb, !rateValiationResult.isPassedSsRelatedErrorValidation(), ERROR_UNABLE_SAVE_SERVICE_COMBO);

        List<String> consecutiveErrors = VendorRateValidation.getConsecutiveErrors(rateValiationResult.getRateUpdateResult().getConsecutiveRates());
        List<String> overlapErrors = VendorRateValidation.getOverlapErrors(rateValiationResult.getRateUpdateResult().getOverlappingRates());
        List<String> invalidErrors = VendorRateValidation.getInvalidServiceErrors(rateValiationResult.getRateUpdateResult().getInvalidRates());

        String result = Stream.concat(invalidErrors.stream(), Stream.concat(consecutiveErrors.stream(), overlapErrors.stream()))
                .collect(Collectors.joining(NEWLINE));
        sb.append(result);
        sb.append(NEWLINE);
        for (String warnMessage : validationWarnMessageList) {
            sb.append(NEWLINE);
            sb.append(warnMessage);
        }
        return sb.toString();
    }

    public void appendErrorMessage(StringBuilder sb, boolean condition, String errorMessage) {
        if (condition) {
            if (!sb.toString().endsWith(NEWLINE)) {
                sb.append(NEWLINE);
            }
            sb.append(errorMessage);
        }
    }

    public void doSaveData() {
        try {
            saveData();
            super.editPanelController.doReset();
        } catch (MultiRecordException e) {
            log.error(ERROR_DO_SAVE_DATA, e);
            VaadinUtils.showErrorNotification(e.getMessage());
        }
    }

    private VendorRateWrapper findMatchingVendorRate(String vendorRateGuid, List<VendorRateWrapper> vendorRateWrappers) {
        VendorRateWrapper returnValue = vendorRateWrappers.stream().
                filter((VendorRateWrapper vendorRate) -> vendorRateGuid.equals(vendorRate.getGuid())).findFirst().orElse(null);
        return returnValue;
    }

    private boolean validateServiceScheduleRelatedChanges(List<VendorRateWrapper> vendorRateWrappers) {
        boolean result = true;

        List<String> vendorRateGuids = getModifiedSsRelatedVendorRates();
        if (VaadinUtils.isEmpty(vendorRateGuids)) {
            return true;
        }

        VendorRateServiceEventWrapper rateSeWrapper = getModifiedVendorRateServiceEvents(vendorRateGuids);
        log.debug(DEBUG_VALIDATE_SS_SE, rateSeWrapper);
        if (rateSeWrapper == null || VaadinUtils.isEmpty(rateSeWrapper.getVendorRateServiceEventVoList())) {
            return true;
        }

        List<VendorRateServiceEventVo> rateSeVoList = rateSeWrapper.getVendorRateServiceEventVoList();
        addDeletedRecordsForValidation(vendorRateWrappers);

        boolean noErrorOrWarnRaised;
        for (VendorRateServiceEventVo rateSeVo : rateSeVoList) {
            String vendorRateGuid = rateSeVo.getVendorRateGuid();
            VendorRateWrapper updatedVendorRate = findMatchingVendorRate(vendorRateGuid, vendorRateWrappers);
            if (updatedVendorRate == null || VaadinUtils.isEmpty(rateSeVo.getServiceDefAllVoList())) {
                continue;
            }

            noErrorOrWarnRaised = noErrorOrWarnRaised(updatedVendorRate, rateSeVo, vendorRateWrappers);
            if (!noErrorOrWarnRaised) {
                result = false;
            }
        }

        return result;
    }

    private List<VendorRateWrapper> addDeletedRecordsForValidation(List<VendorRateWrapper> vendorRateWrappers) {
        MultiRecordChangeService<VendorRateWrapper> multiRecordChangeService = editPanelController.getConfig().getMultiRecordChangeService();
        multiRecordChangeService.getDeletedRecords().stream().forEach(vrw -> {
            vrw.setDeleting(true);
            vendorRateWrappers.add(vrw);
        });

        return vendorRateWrappers;
    }

    private boolean noErrorOrWarnRaised(VendorRateWrapper updatedVendorRate,
            VendorRateServiceEventVo matchingRateSeVo, List<VendorRateWrapper> vendorRateWrappers) {
        boolean result = true;

        String message;
        String serviceCodes = CompassNextUtils.getServiceCodes(updatedVendorRate);
        String formattedStartDate = CompassNextUtils.getFormattedDateTime(updatedVendorRate.getOrigStartDate());

        log.debug(DEBUG_ERR_OR_WARN_RAISED, updatedVendorRate, matchingRateSeVo);

        // When a Vendor Rate record is updated, we need to determine if a rate change was
        // made that affects existing Service Events and if we need to produce either an
        // error or a warning.
        //
        // With the multi-record-editor, a given Vendor Rate line can be changed, deleted, or
        // completely replaced by one or more other Vendor Rate lines. We cannot use the original
        // VendorRateServiceEventVo line in the grid to determine changes, because that line may
        // have been deleted or could represent a completely different service.
        //
        // The original VendorRateServiceEventVo line has the original rate and all of the
        // Service Definitions that use that rate line.  Using that information, we can find
        // the rate lines in the grid that are associated with the original Vendor Rate record.
        List<VendorRate> allVendorRates = new ArrayList<>();
        allVendorRates.addAll(vendorRateWrappers.stream()
                .map(vrw -> vrw.getVendorRate()).collect(Collectors.toList()));
        allVendorRates.addAll(inactiveVendorRateList);

        VendorRateServiceValidator serviceValidator = new VendorRateServiceValidator(updatedVendorRate,
                matchingRateSeVo, allVendorRates);

        // To-Do CM-6931: We may not want to call this if the Vendor Rate Line uses a Custom Service.
        if (updatedVendorRate.getOrigAgentCustomService() == null)
        {
            if (serviceValidator.isServiceDefinitionDateRangeCovered()) {
                if (updatedVendorRate.isDeleting()) {
                    message = String.format(ERROR_UNABLE_DELETE_RATE, serviceCodes, formattedStartDate);
                } else {
                    message = String.format(ERROR_UNABLE_SAVE_DATE_RANGE, serviceCodes, formattedStartDate);
                }
//              rateValiationResult.setPassedSsRelatedErrorValidation(false);
                validationErrorMessageList.add(message);
                return false;
            }
        }

        // To-Do CM-6931: It may be that we need to rework the isServiceEventDateRangeCovered() method to
        // handle checking that there is a rate line to cover the entire Service Definitions date range.  We need to
        // validate with Service Events. We need to hydrate the Agent Custom Service in the Service Events.
        if (updatedVendorRate.getOrigAgentCustomService() != null
                && updatedVendorRate.getOrigAgentCustomService().getGuid() != null) {
            if (serviceValidator.isServiceEventDateRangeCovered()) {
                if (updatedVendorRate.isDeleting()) {
                    message = String.format(ERROR_UNABLE_DELETE_SE_RATE, serviceCodes, formattedStartDate);
                } else {
                    message = String.format(ERROR_UNABLE_SAVE_SE_DATE_RANGE, serviceCodes, formattedStartDate);
                }
//                rateValiationResult.setPassedSsRelatedErrorValidation(false);
                validationErrorMessageList.add(message);
                return false;
            }
        }

        if (VaadinUtils.isEmpty(matchingRateSeVo.getServiceDefWithSeVoList())) {
            return true;
        }

        if (serviceValidator.hasCost(updatedVendorRate.getOrigCostPerUnit())) {
             if (serviceValidator.errorDefaultCostRemoved()) {
//                rateValiationResult.setPassedSsRelatedErrorValidation(false);
                message = String.format(ERROR_UNABLE_SAVE_COST_UNIT, serviceCodes, formattedStartDate);
                validationErrorMessageList.add(message);
                result = false;
            } else {
                message = serviceValidator.warnDefaultCostChanged(serviceCodes, formattedStartDate);
                if (message != null) {
//                    rateValiationResult.setPassedSsRelatedWarnValidation(false);
                    validationWarnMessageList.add(message);
                    result = false;
                }
            }
        } else {
            if (serviceValidator.warnDefaultCostAdded()) {
//                rateValiationResult.setPassedSsRelatedWarnValidation(false);
                message = String.format(WARN_UNABLE_SAVE_COST_UNIT, serviceCodes, formattedStartDate);
                validationWarnMessageList.add(message);
                result = false;
            }
        }

        return result;
    }

    private List<String> getModifiedSsRelatedVendorRates() {
        List<String> vendorRateGuids = new ArrayList();

        List<String> updatedVendorRateGuids = new ArrayList();
        List<String> deleteVendorRateGuids = new ArrayList();

        // To-Do CM-6235: Need to be mindful that this code is where it is deciding
        // if we need to validate the rate change against the Service Definition.
        // Currently, rates will not be validated if the vrw.hasSsRelatedUpdates()
        // returns false.
        MultiRecordChangeService<VendorRateWrapper> multiRecordChangeService = editPanelController.getConfig().getMultiRecordChangeService();
        multiRecordChangeService.getUpdatedRecords().stream().forEach(vrw -> {
            if (vrw.hasSsRelatedUpdates()) {
                updatedVendorRateGuids.add(vrw.getGuid());
            }
        });
        multiRecordChangeService.getDeletedRecords().stream().forEach(vrw -> {
            deleteVendorRateGuids.add(vrw.getGuid());
        });

        vendorRateGuids.addAll(updatedVendorRateGuids);
        vendorRateGuids.addAll(deleteVendorRateGuids);

        return vendorRateGuids;
    }

    private VendorRateServiceEventWrapper getModifiedVendorRateServiceEvents(List<String> vendorRateGuids) {
        VendorRateServiceEventWrapper returnValue = new VendorRateServiceEventWrapper();

        try {
            if (VaadinUtils.isEmpty(vendorRateGuids)) {
                return returnValue;
            }

            returnValue = locator().vendorRateService().getModifiedVendorRateServiceEvents(vendorRateGuids);
        } catch (Exception ex) {
            String message = String.format(ERROR_GET_MODIFIED_RATE_SE, StringUtils.join(vendorRateGuids));
            log.error(message, ex);
            VaadinUtils.showErrorNotification(message);
        }

        return returnValue;
    }
//TODO: change this to return the rates with permission errors so we can highlight them

    private boolean validateSeRateChangePermission(List<VendorRateWrapper> vendorRateWrappers) {
        boolean result = true;

        // To-Do CM-6235: This is where it is using the values set in the hasVendorRatePermissionKeyChanged()
        // method. We may be able to leverage/change the code in the noErrorOrWarnRaised method to determine
        // if the detault cost per unit changed and is being used in a Service Event.
        List<String> vendorRateGuidList = new ArrayList();
        for (VendorRateWrapper vrw : vendorRateWrappers) {
            if (vrw.isRatePermissionKeyChanged() && vrw.getVendorRate() != null
                    && isPreexistingVendorRate(vrw)) {
                //only validate permission for existing rates
                vendorRateGuidList.add(vrw.getGuid());
            }
        }

        // To-Do CM-6235: Do we need to make calls to the database twice? Here and also
        // in method getModifiedVendorRateServiceEvents()?  Also, if the user has the
        // vendorSeRateChangePermission, it doesn't seem like we should be making this
        // call to the database.
        boolean seExists = anyServiceEventExistsByVendorRate(vendorRateGuidList);
        if (seExists && !vendorSeRateChangePermission) {
            result = false;
        }

        return result;
    }

    private static boolean isPreexistingVendorRate(VendorRateWrapper vrw) {
        return vrw.getVendorRate().getGuid().length() == 32;
    }

    private boolean anyServiceEventExistsByVendorRate(List<String> vendorRateGuidList) {
        try {
            if (VaadinUtils.isEmpty(vendorRateGuidList)) {
                return false;
            }

            return locator().vendorRateService().anyServiceEventExistsByVendorRate(vendorRateGuidList);
        } catch (Exception ex) {
            String message = String.format(ERROR_IN_SE_EXISTS, vendorRateGuidList);
            log.error(message, ex);
            VaadinUtils.showErrorNotification(message);
        }

        return false;
    }

    private List<VendorRate> validateOverlappingVendorRates(List<VendorRate> vendorRates) {
        boolean valid = true;
        vendorRates.forEach(vr -> logVendorRate(UNSORTED, vr));
        VendorRateValidation.sortVendorRatesForValidation(vendorRates);
        vendorRates.forEach(vr -> logVendorRate(SORTED, vr));
        List<VendorRate> overlappingVendorRates = new ArrayList<>();
        VendorRate lastRateExamined = null;
        for (VendorRate vendorRate : vendorRates) {
            if (lastRateExamined != null && MultiRecordUtils.matchesOnKeyProperties(Arrays.asList(RATE_GROUP_SPLIT_PROP_ARRAY), vendorRate, lastRateExamined)) {
                // primed and no changed from one to the next, we should check for overlaps with
                // this and the last
                if (VendorRateValidation.datesOverlap(lastRateExamined.getStartDate(), lastRateExamined.getStopDate(), vendorRate.getStartDate(), vendorRate.getStopDate())) {
                    overlappingVendorRates.add(vendorRate);
                    if (!overlappingVendorRates.contains(lastRateExamined)) {
                        overlappingVendorRates.add(lastRateExamined);
                    }
                }
            }
            lastRateExamined = vendorRate;
        }
        return overlappingVendorRates;
    }

    private List<VendorRate> validateConsecutiveServicesPresent(List<VendorRate> vendorRates) {
        vendorRates.forEach(vr -> logVendorRate(UNSORTED, vr));
        VendorRateValidation.sortVendorRatesForValidation(vendorRates);
        vendorRates.forEach(vr -> logVendorRate(SORTED, vr));
        List<VendorRate> consecutiveServices = new ArrayList<>();
        VendorRate lastRateExamined = null;
        for (VendorRate vendorRate : vendorRates) {
            if (lastRateExamined != null && MultiRecordUtils.matchesOnKeyProperties(Arrays.asList(RATE_GROUP_SPLIT_PROP_ARRAY), vendorRate, lastRateExamined)) {
                // primed and no changed from one to the next, we should check for overlaps with
                // this and the last
                if (VendorRateValidation.datesAreConsecutive(lastRateExamined.getStopDate(), vendorRate.getStartDate())
                        && VendorRateValidation.vendorRateDefaultCostPerUnitEqual(lastRateExamined, vendorRate)) {
                    consecutiveServices.add(vendorRate);
                    if (!consecutiveServices.contains(lastRateExamined)) {
                        consecutiveServices.add(lastRateExamined);
                    }
                }
            }
            lastRateExamined = vendorRate;
        }
        return consecutiveServices;
    }

    private List<VendorRate> validateVendorRates(List<VendorRateWrapper> vendorRateWrappers) {
        List<VendorRateWrapper> invalidVendorRateWrappers = vendorRateWrappers.stream()
                .filter(vrw -> vrw.getPtsServiceCombo() == null)
                .collect(Collectors.toList());
        List<VendorRate> invalidVendorRates = invalidVendorRateWrappers.stream().
                map(t -> t.getVendorRate()).collect(Collectors.toList());
        return invalidVendorRates;
    }

    private static Date localDateToDate(LocalDate inDate) {
        return inDate != null ? java.sql.Date.valueOf(inDate) : null;
    }

    public void setSelectedAgent(Agent agent) {
        if (selectedAgent == null || agent == null
                || !selectedAgent.getGuid().equals(agent.getGuid())) {
            agentCustomServiceList = new ArrayList<>();
        }
        selectedAgent = agent;
    }

    public void setSelectedVendor(Vendor vendor) {
        if (selectedVendor == null || vendor == null || StringUtils.isEmpty(selectedVendor.getGuid())
                || !selectedVendor.getGuid().equals(vendor.getGuid())) {
            vendorRateList = null;
        }
        selectedVendor = vendor;
    }

    public static boolean ptsServiceComboInVendorRateDateRange(PtsServiceCombo ptsServiceCombo, Date vendorRateStartDate, Date vendorRateStopDate) {
        if (datesNotProperlyOrdered(vendorRateStartDate, vendorRateStopDate)
                || datesNotProperlyOrdered(ptsServiceCombo.getStartDate(), ptsServiceCombo.getStopDate())) {
            return false;
        }
        boolean returnResult = vendorRateStartDate != null && !ptsServiceCombo.getStartDate().after(vendorRateStartDate)
                && ((vendorRateStopDate == null && ptsServiceCombo.getStopDate() == null)
                || (vendorRateStopDate != null && ptsServiceCombo.getStopDate() == null)
                || (vendorRateStopDate != null && ptsServiceCombo.getStopDate() != null && !vendorRateStopDate.after(ptsServiceCombo.getStopDate())));
        log.debug(LOG_MSG_PTS_SERVICE_COMBO_IN_VENDOR_RATE_DATE_RANGE,
                ptsServiceCombo.getStartDate(),
                ptsServiceCombo.getStopDate(),
                ptsServiceCombo.getPtsProcedure() != null ? ptsServiceCombo.getPtsProcedure().getCode() : "",
                ptsServiceCombo.getPtsModifier() != null ? ptsServiceCombo.getPtsModifier().getCode() : NULL_LITERAL,
                ptsServiceCombo.getPtsStandardRemark() != null ? ptsServiceCombo.getPtsStandardRemark().getCode() : NULL_LITERAL,
                vendorRateStartDate,
                vendorRateStopDate,
                returnResult);

        return returnResult;
    }

    private static boolean datesNotProperlyOrdered(Date vendorRateStartDate, Date vendorRateStopDate) {
        if (vendorRateStartDate != null && vendorRateStopDate != null && vendorRateStartDate.after(vendorRateStopDate)) {
            return true;
        }
        return false;
    }

    private boolean includePtsProcedure(PtsProcedure ptsProcedure) {
        Date startDate = localDateToDate(startDateField.getValue());
        if (startDate == null) {
            return false;
        } else {
            Date stopDate = localDateToDate(stopDateField.getValue());
            return ptsServiceComboList.stream().anyMatch(ptsServiceCombo -> ptsServiceCombo.getPtsProcedure().equals(ptsProcedure)
                    && ptsServiceComboInVendorRateDateRange(ptsServiceCombo, startDate, stopDate));
        }
    }

    private boolean includePtsModifier(PtsModifier ptsModifier) {
        Date startDate = localDateToDate(startDateField.getValue());
        PtsProcedure ptsProcedure = ptsProcedureComboBox.getValue();
        log.debug(LOG_MSG_INCLUDE_PTS_MODIFIER,
                ptsProcedure != null ? ptsProcedure.getCode() : NULL_LITERAL,
                ptsModifier != null ? ptsModifier.getCode() : NULL_LITERAL);
        if (startDate == null || ptsProcedure == null) {
            return false;
        } else {
            Date stopDate = localDateToDate(stopDateField.getValue());
            return ptsServiceComboList.stream().anyMatch(p -> p.getPtsProcedure().equals(ptsProcedure)
                    && p.getPtsModifier() != null && p.getPtsModifier().equals(ptsModifier)
                    && ptsServiceComboInVendorRateDateRange(p, startDate, stopDate));
        }
    }

    private boolean includePtsStandardRemark(PtsStandardRemark ptsStandardRemark) {
        Date startDate = localDateToDate(startDateField.getValue());
        PtsProcedure ptsProcedure = ptsProcedureComboBox.getValue();
        PtsModifier ptsModifier = ptsModifierComboBox.getValue();
        log.debug(LOG_MSG_INCLUDE_PTS_STANDARD_REMARK,
                ptsProcedure != null ? ptsProcedure.getCode() : NULL_LITERAL,
                ptsModifier != null ? ptsModifier.getCode() : NULL_LITERAL,
                ptsStandardRemark != null ? ptsStandardRemark.getCode() : NULL_LITERAL);
        if (startDate == null || ptsProcedure == null) {
            return false;
        } else {
            Date stopDate = localDateToDate(stopDateField.getValue());
            return ptsServiceComboList.stream().anyMatch(p -> p.getPtsProcedure().equals(ptsProcedure)
                    && ((p.getPtsModifier() == null && ptsModifier == null) || ((p.getPtsModifier() != null && p.getPtsModifier().equals(ptsModifier))))
                    && p.getPtsStandardRemark() != null && p.getPtsStandardRemark().equals(ptsStandardRemark)
                    && ptsServiceComboInVendorRateDateRange(p, startDate, stopDate));
        }
    }

    private Optional<PtsServiceCombo> getCurrentPtsServiceCombo() {
        Date startDate = localDateToDate(startDateField.getValue());
        PtsProcedure ptsProcedure = ptsProcedureComboBox.getValue();
        if (startDate == null || ptsProcedure == null) {
            return Optional.empty();
        } else {
            Date stopDate = localDateToDate(stopDateField.getValue());
            PtsModifier ptsModifier = ptsModifierComboBox.getValue();
            PtsStandardRemark ptsStandardRemark = ptsStandardRemarkComboBox.getValue();
            return ptsServiceComboList.stream()
                    .filter(p -> matchesPtsServiceCombo(p, startDate, stopDate, ptsProcedure, ptsModifier, ptsStandardRemark)).findFirst();
        }
    }

    private static boolean matchesPtsServiceCombo(PtsServiceCombo ptsServiceCombo, Date startDate, Date stopDate, PtsProcedure ptsProcedure,
            PtsModifier ptsModifier, PtsStandardRemark ptsStandardRemark) {
        if (startDate == null) {
            return false;
        } else {
            final boolean procedureEqual = ptsServiceCombo.getPtsProcedure().equals(ptsProcedure);
            final boolean modifierEqual = (ptsServiceCombo.getPtsModifier() == null && ptsModifier == null)
                    || ((ptsServiceCombo.getPtsModifier() != null && ptsServiceCombo.getPtsModifier().equals(ptsModifier)));
            final boolean standardRemarkEqual = (ptsServiceCombo.getPtsStandardRemark() == null && ptsStandardRemark == null)
                    || ((ptsServiceCombo.getPtsStandardRemark() != null && ptsServiceCombo.getPtsStandardRemark().equals(ptsStandardRemark)));
            return procedureEqual && modifierEqual && standardRemarkEqual && ptsServiceComboInVendorRateDateRange(ptsServiceCombo, startDate, stopDate);
        }
    }

    private PtsServiceCombo getPtsServiceComboUsingWrapperFields(VendorRateWrapper vendorRateWrapper) {
        PtsServiceCombo returnResult = null;
        Date startDate = vendorRateWrapper.getStartDate();
        PtsProcedure ptsProcedure = vendorRateWrapper.getPtsProcedure();
        if (startDate != null && ptsProcedure != null) {
            Date stopDate = vendorRateWrapper.getStopDate();
            PtsModifier ptsModifier = vendorRateWrapper.getPtsModifier();
            PtsStandardRemark ptsStandardRemark = vendorRateWrapper.getPtsStandardRemark();
            returnResult = ptsServiceComboList.stream().filter((PtsServiceCombo ptsServiceCombo) -> isProcedureEqual(ptsServiceCombo, ptsProcedure)
                    && isModifierEqual(ptsServiceCombo, ptsModifier)
                    && isStandardRemarkEqual(ptsServiceCombo, ptsStandardRemark)
                    && ptsServiceComboInVendorRateDateRange(ptsServiceCombo, startDate, stopDate)).findFirst().orElse(null);
        }
        log.debug(LOG_MSG_PTS_SERVICE_COMBO_USING_WRAPPER_FIELDS, getPtsServiceComboCodes(returnResult));
        return returnResult;
    }

    private static String getPtsServiceComboCodes(PtsServiceCombo ptsServiceCombo) {
        if (ptsServiceCombo != null) {
            StringBuilder returnResult = new StringBuilder();
            returnResult.append(ptsServiceCombo.getPtsProcedure() != null ? ptsServiceCombo.getPtsProcedure().getCode() : NULL_LITERAL)
                    .append(CAPTION_GENERATOR_SEPARATOR)
                    .append(ptsServiceCombo.getPtsModifier() != null ? ptsServiceCombo.getPtsModifier().getCode() : NULL_LITERAL)
                    .append(CAPTION_GENERATOR_SEPARATOR)
                    .append(ptsServiceCombo.getPtsStandardRemark() != null ? ptsServiceCombo.getPtsStandardRemark().getCode() : NULL_LITERAL);
            return returnResult.toString();
        } else {
            return NOT_A_VALID__PTS_SERVICE_COMBO;
        }
    }

    private static String getPtsProcedureCode(PtsProcedure ptsProcedure) {
        return ptsProcedure != null ? ptsProcedure.getCode() : NULL_LITERAL;
    }

    private static String getPtsModifierCode(PtsModifier ptsModifier) {
        return ptsModifier != null ? ptsModifier.getCode() : NULL_LITERAL;
    }

    private static String getPtsStandardRemarkCode(PtsStandardRemark ptsStandardRemark) {
        return ptsStandardRemark != null ? ptsStandardRemark.getCode() : NULL_LITERAL;
    }

    private static boolean isStandardRemarkEqual(PtsServiceCombo ptsServiceCombo, PtsStandardRemark ptsStandardRemark) {
        boolean returnResult = (ptsServiceCombo.getPtsStandardRemark() == null && ptsStandardRemark == null)
                || ((ptsStandardRemark != null && ptsServiceCombo.getPtsStandardRemark() != null
                && ptsServiceCombo.getPtsStandardRemark().getGuid().equals(ptsStandardRemark.getGuid())));
        if (returnResult) {
            log.debug(LOG_MSG_STANDARD_REMARK_EQUAL_PTS_SERVICE_COMBO,
                    getPtsServiceComboCodes(ptsServiceCombo), getPtsStandardRemarkCode(ptsStandardRemark), returnResult);
        }
        return returnResult;
    }

    private static boolean isModifierEqual(PtsServiceCombo ptsServiceCombo, PtsModifier ptsModifier) {
        boolean returnResult = (ptsServiceCombo.getPtsModifier() == null && ptsModifier == null)
                || ((ptsModifier != null && ptsServiceCombo.getPtsModifier() != null
                && ptsServiceCombo.getPtsModifier().getGuid().equals(ptsModifier.getGuid())));
        if (returnResult) {
            log.debug(LOG_MSG_MODIFIER_EQUAL_PTS_SERVICE_COMBO,
                    getPtsServiceComboCodes(ptsServiceCombo), getPtsModifierCode(ptsModifier), returnResult);
        }
        return returnResult;
    }

    private static boolean isProcedureEqual(PtsServiceCombo ptsServiceCombo, PtsProcedure ptsProcedure) {
        boolean returnResult = ptsServiceCombo.getPtsProcedure().getGuid().equals(ptsProcedure.getGuid());
        if (returnResult) {
            log.debug(LOG_MSG_PROCEDURE_EQUAL_PTS_SERVICE_COMBO,
                    getPtsServiceComboCodes(ptsServiceCombo), getPtsProcedureCode(ptsProcedure), returnResult);
        }
        return returnResult;
    }

    private boolean includeAgentCustomService(AgentCustomService agentCustomService) {
        return includeAgentCustomService(agentCustomService, "");
    }

    private boolean includeAgentCustomService(AgentCustomService agentCustomService, String filter) {
        Date startDate = localDateToDate(startDateField.getValue());
        PtsProcedure currentPtsProcedure = ptsProcedureComboBox.getValue();
        if (startDate == null || currentPtsProcedure == null) {
            log.debug(LOG_MSG_INCLUDE_AGENT_CUSTOM_SERVICE_PROCEDURE_NULL);
            return false;
        } else {
            log.debug(LOG_MSG_INCLUDE_AGENT_CUSTOM_SERVICE,
                    currentPtsProcedure.getCode(), agentCustomService.getDescription());
            Optional<PtsServiceCombo> currentPtsServiceCombo = getCurrentPtsServiceCombo();
            return currentPtsServiceCombo.isPresent() && agentCustomService.getPtsServiceCombo().equals(currentPtsServiceCombo.get())
                    && applyFilter(agentCustomService, this::displayAgentCustomServiceCaption, filter);
        }
    }

    private <T> boolean applyFilter(T value, Function<T, String> valueCaption, String filter) {
        return StringUtils.isBlank(filter) || StringUtils.startsWithIgnoreCase(valueCaption.apply(value), filter);
    }

    private void filterDataLookupsSelectionEvent(SelectionEvent<Grid<VendorRateWrapper>, VendorRateWrapper> selectionEvent) {
        if (selectionEvent.getFirstSelectedItem().isPresent()) {
            refreshAllListDataProviders();
        }
    }

    private void initListProviders() {
        loadPtsServiceComboList();
        loadVendorRateList();
        loadAgentCustomServiceList();

        populatePtsProcedureList();
        ptsProcedureListDataProvider = new ListDataProvider<>(ptsProcedureList);
        ptsProcedureComboBox.setItems(ptsProcedureListDataProvider);

        populatePtsModifierList();
        ptsModifierListDataProvider = new ListDataProvider<>(ptsModifierList);
        ptsModifierComboBox.setItems(ptsModifierListDataProvider);

        populatePtsStandardRemarkList();
        ptsStandardRemarkListDataProvider = new ListDataProvider<>(ptsStandardRemarkList);
        ptsStandardRemarkComboBox.setItems(ptsStandardRemarkListDataProvider);

        loadAgentCustomServiceList();
        agentCustomServiceListDataProvider = new ListDataProvider<>(agentCustomServiceList);
        agentCustomServiceComboBox.setItems(agentCustomServiceListDataProvider);

        ptsProcedureListDataProvider.clearFilters();
        ptsProcedureListDataProvider.setFilter(this::includePtsProcedure);
        ptsModifierListDataProvider.clearFilters();
        ptsModifierListDataProvider.setFilter(this::includePtsModifier);
        ptsStandardRemarkListDataProvider.clearFilters();
        ptsStandardRemarkListDataProvider.setFilter(this::includePtsStandardRemark);
        agentCustomServiceListDataProvider.clearFilters();
        agentCustomServiceListDataProvider.setFilter(this::includeAgentCustomService);
    }

    private void rowBinderValueChangeEvent(ValueChangeEvent<?> valueChangeEvent) {
        if (valueChangeEvent.isFromClient() && lastValidationCallResult
                && !Objects.equals(valueChangeEvent.getValue(), valueChangeEvent.getOldValue())) {
            VendorRateWrapper currentRow = listGrid.getSelectedItems().iterator().next();
            processClearAndRefreshes(valueChangeEvent, currentRow);
        }
    }

    private void processClearAndRefreshes(ValueChangeEvent<?> valueChangeEvent, VendorRateWrapper currentRow) {
        if (valueChangeEvent.getHasValue().equals(startDateField)
                || valueChangeEvent.getHasValue().equals(stopDateField)) {
            refreshAllListDataProviders();
        } else if (valueChangeEvent.getHasValue().equals(ptsProcedureComboBox)) {
            clearProcedureDependentComboBoxes();
            clearProcedureDependentFields(currentRow);
            refreshProcedureDependentListDataProviders();
        } else if (valueChangeEvent.getHasValue().equals(ptsModifierComboBox)) {
            clearModifierDependentComboBoxes();
            clearModifierDependentFields(currentRow);
            refreshModifierDependentListDataProviders();
        } else if (valueChangeEvent.getHasValue().equals(ptsStandardRemarkComboBox)
                && valueChangeEvent.getValue() == null) {
            refreshStandardRemarkDependentListDataProviders();
        }
    }

    private void clearProcedureDependentFields(VendorRateWrapper currentRow) {
        currentRow.setPtsModifier(null);
    }

    private void clearProcedureDependentComboBoxes() {
        ptsModifierComboBox.setValue(null);
        clearModifierDependentComboBoxes();
    }

    private void refreshProcedureDependentListDataProviders() {
        ptsModifierListDataProvider.refreshAll();
        refreshModifierDependentListDataProviders();
    }

    private void refreshModifierDependentListDataProviders() {
        ptsStandardRemarkListDataProvider.refreshAll();
        refreshStandardRemarkDependentListDataProviders();
    }

    private void refreshStandardRemarkDependentListDataProviders() {
        agentCustomServiceListDataProvider.refreshAll();
    }

    private void clearModifierDependentFields(VendorRateWrapper currentRow) {
        currentRow.setPtsStandardRemark(null);
        currentRow.setAgentCustomService(null);
    }

    private void clearModifierDependentComboBoxes() {
        ptsStandardRemarkComboBox.setValue(null);
        agentCustomServiceComboBox.setValue(null);
        updateAgentCustomServiceNullSelection();
    }

    private void refreshAllListDataProviders() {
        ptsProcedureListDataProvider.refreshAll();
        refreshProcedureDependentListDataProviders();
    }

    private void updateAgentCustomServiceNullSelection() {
        // Twiddle the agent custom service combobox to deal with our flakey really null
        // vs null description but valid guid
        boolean agentCustomServicesExist = agentCustomServiceComboBox.getListDataView().getItems().anyMatch(this::includeAgentCustomService);
        boolean nullAgentCustomServicesExist = agentCustomServiceComboBox.getListDataView().getItems()
                .filter(acs -> this.includeAgentCustomService(acs, null))
                .anyMatch(acs -> acs.getDescription() == null || acs.getDescription().isEmpty());
        if (agentCustomServicesExist) {
            if (nullAgentCustomServicesExist) {
                agentCustomServiceComboBox.setPlaceholder(EMPTY_CAPTION_SELECT_CUSTOM);
            } else {
                agentCustomServiceComboBox.setPlaceholder(EMPTY_CAPTION_NA);
            }
        } else {
            agentCustomServiceComboBox.setPlaceholder(EMPTY_CAPTION_NA);
        }
    }

    private void logGridEditorComponentValues() {
        log.debug(LOG_MSG_EDITOR_COMPONENT_VALUES,
                startDateField.getValue(),
                stopDateField.getValue(),
                (ptsProcedureComboBox.getValue() != null ? ptsProcedureComboBox.getValue().getCode() : ""),
                (ptsModifierComboBox.getValue() != null ? ptsModifierComboBox.getValue().getCode() : ""),
                (ptsStandardRemarkComboBox.getValue() != null ? ptsStandardRemarkComboBox.getValue().getCode() : ""),
                (agentCustomServiceComboBox.getValue() != null ? agentCustomServiceComboBox.getValue().getDescription() : ""),
                defaultUnitsField.getValue(),
                defaultCostField.getValue());
    }

    public boolean hasUnsavedChanges() {
        return editPanelController.getConfig().getUnsavedEditIndicator().isVisible();
    }

    public void enableAddButton() {
        editPanelController.getConfig().getAddButton().setEnabled(true);
    }

    private void showFieldValidationError(String message) {
        Notification notification = new Notification();
        notification.setDuration(5000);
        notification.setPosition(Notification.Position.TOP_END);
        notification.setText(message);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.open();
    }

    private void clearInvalid(List<VendorRateWrapper> vendorRateWrappers) {
        for (VendorRateWrapper wrapper : vendorRateWrappers) {
            wrapper.clearErrors();
        }
    }

    private void showErrorNotification(MultiRecordRateUpdateException rateUpdateException) {
        showErrorNotification(rateUpdateException.getRateUpdateResult());
    }

    private void showErrorNotification(VendorRateUpdateResult rateUpdateResult) {
        if (!rateUpdateResult.isSuccess()) {
            List<VendorRateUpdateResult.FAILURE_REASON> failReasons = rateUpdateResult.getFailReasons();
//            StringBuilder errorMessages = new StringBuilder();
            List<String> errorMessages = new ArrayList<>();

            if (failReasons.contains(FAILURE_REASON.StaleObject)) {
                errorMessages.add(ERROR_MSG_SAVE_FAILURE);
            }
            if (failReasons.contains(FAILURE_REASON.Overlapping)) {
                errorMessages.addAll(VendorRateValidation.getOverlapErrors(rateUpdateResult.getOverlappingRates()));
            }
            if (failReasons.contains(FAILURE_REASON.ConsecutiveEqualCost)) {
                errorMessages.addAll(VendorRateValidation.getConsecutiveErrors(rateUpdateResult.getConsecutiveRates()));
            }
            if (failReasons.contains(FAILURE_REASON.Invalid)) {
                errorMessages.addAll(VendorRateValidation.getInvalidServiceErrors(rateUpdateResult.getInvalidRates()));
            }
            String error = errorMessages.stream().collect(Collectors.joining());
            VaadinUtils.showErrorNotification(Jsoup.clean(error, Safelist.basic()));
        }
    }

    private void markRowsWithErrors(VendorRateValiationResult rateValiationResult) {
        markRowsWithErrors(rateValiationResult.getRateUpdateResult());
    }

    private void markRowsWithErrors(VendorRateUpdateResult rateUpdateResult) {
        List<VendorRateWrapper> tableData = editPanelController.getConfig().getTableData();
        for (VendorRateWrapper rateWrapper : tableData) {
            if (rateWrapper.getGuid() != null) {
                boolean needsRefreshing = false;

                if (rateUpdateResult.getInvalidRates().contains(rateWrapper.getVendorRate())) {
                    rateWrapper.addErrorMessage(ROW_MARK_INVALID_SERVICE_COMBO);
                    needsRefreshing = true;
                } else {
                    if (rateUpdateResult.getConsecutiveRates().contains(rateWrapper.getVendorRate())) {
                        rateWrapper.addErrorMessage(ROW_MARK_CONSECUTIVE);
                        needsRefreshing = true;
                    }
                    if (rateUpdateResult.getOverlappingRates().contains(rateWrapper.getVendorRate())) {
                        rateWrapper.addErrorMessage(ROW_MARK_OVERLAPPING);
                        needsRefreshing = true;
                    }
                    if (rateUpdateResult.getStaleRates().contains(rateWrapper.getVendorRate())) {
                        rateWrapper.addErrorMessage(ROW_MARK_UPDATED_BY_ANOTHER_USER);
                        needsRefreshing = true;
                    }
                }

                if (needsRefreshing) {
                    listGrid.getDataProvider().refreshItem(rateWrapper);
                }
            }
        }
    }
}
