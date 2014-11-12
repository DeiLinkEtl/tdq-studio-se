// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.utils;

import java.io.IOError;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ui.editor.analysis.drilldown.DrillDownEditorInput;
import org.talend.dataprofiler.core.ui.editor.preview.model.MenuItemEntity;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.analysis.AnalyzedDataSet;
import org.talend.dataquality.indicators.FrequencyIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.LengthIndicator;
import org.talend.dataquality.indicators.columnset.SimpleStatIndicator;
import org.talend.dataquality.indicators.mapdb.AbstractDB;
import org.talend.dataquality.indicators.mapdb.StandardDBName;
import org.talend.dq.indicators.preview.table.ChartDataEntity;

/**
 * the drilldown's utility class which associated with UI.
 */
public class DrillDownUtils {

    private static final String DRILL_DOWN_EDITOR = "org.talend.dataprofiler.core.ui.editor.analysis.drilldown.drillDownResultEditor"; //$NON-NLS-1$

    public static final int MENU_VALUE_TYPE = 1;

    public static final int MENU_VALID_TYPE = 2;

    public static final int MENU_INVALID_TYPE = 3;

    /**
     * Get MapDB which store the drill down data for current indicator
     * 
     * @param analysis
     * 
     * @return
     */
    public static AbstractDB<Object> getMapDB(final ChartDataEntity dataEntity, Analysis analysis, MenuItemEntity itemEntitie) {
        AnalysisType analysisType = analysis.getParameters().getAnalysisType();
        if (AnalysisType.COLUMN_SET == analysisType) {
            return getColumnSetAnalysisMapDB(analysis);
        }

        Indicator indicator = dataEntity.getIndicator();
        String selectValue = dataEntity.getLabel();
        String dbMapName = getDBMapName(analysisType, indicator, selectValue, itemEntitie);
        return indicator.getMapDB(dbMapName);
    }

    /**
     * Get the name of MapDB
     * 
     * @return
     */
    public static String getDBMapName(AnalysisType analysisType, Indicator indicator, String selectValue,
            MenuItemEntity itemEntitie) {
        String dbMapName = getDefaultMapName(analysisType, itemEntitie);
        if (FrequencyIndicator.class.isInstance(indicator)) {
            dbMapName = selectValue;
        } else if (LengthIndicator.class.isInstance(indicator)) {
            String selectValueLength;
            Long length = ((LengthIndicator) indicator).getLength();
            if (length != null) {
                selectValueLength = length.toString();
            } else {
                Double realValue = ((LengthIndicator) indicator).getRealValue();
                if (realValue == null) {
                    return dbMapName;
                }
                selectValueLength = realValue.toString();
            }
            dbMapName = selectValue + selectValueLength;
        } else if (AnalysisType.COLUMN_SET == analysisType) {
            dbMapName = StandardDBName.dataSection.name();
        }

        return dbMapName;
    }

    /**
     * DOC talend Comment method "getDefaultMapName".
     * 
     * @return
     */
    private static String getDefaultMapName(AnalysisType analysisType, MenuItemEntity itemEntitie) {
        if (AnalysisType.MULTIPLE_COLUMN == analysisType) {
            // unique duplicate phoneNumber indicator
            if (judgeMenuType(itemEntitie.getLabel(), MENU_VALUE_TYPE)) {

                // pattern
                if (judgeMenuType(itemEntitie.getLabel(), MENU_INVALID_TYPE)) {
                    return StandardDBName.invalidDrillDownValues.name();
                } else {
                    return StandardDBName.drillDownValues.name();
                }
            } else {
                // pattern
                if (judgeMenuType(itemEntitie.getLabel(), MENU_INVALID_TYPE)) {
                    return StandardDBName.invalidDrillDown.name();
                }
            }

        }
        return StandardDBName.drillDown.name();
    }

    /**
     * 
     * Judge current name of menu whether is same to menuType
     * 
     * @param menuStr is the name of the menu
     * @param menuType is the type which we think it should be
     * @return return true if menuStr is adapt to menuType, else return false
     */
    public static boolean judgeMenuType(String menuStr, int menuType) {
        if (menuStr == null) {
            return false;
        }
        switch (menuType) {
        case MENU_VALUE_TYPE:
            return menuStr.toLowerCase().indexOf("values") > -1;//$NON-NLS-1$
        case MENU_VALID_TYPE:
            return menuStr.toLowerCase().indexOf("valid") > -1;//$NON-NLS-1$
        case MENU_INVALID_TYPE:
            return menuStr.toLowerCase().indexOf("invalid") > -1;//$NON-NLS-1$
        default:
            return false;
        }
    }

    /**
     * Get MapDB which store the drill down data for columnSet analysis
     * 
     * @param analysisType
     * @param analysis
     */
    public static AbstractDB<Object> getColumnSetAnalysisMapDB(Analysis analysis) {
        SimpleStatIndicator simpleStatIndicator = null;
        for (Indicator indicator : analysis.getResults().getIndicators()) {
            if (SimpleStatIndicator.class.isInstance(indicator)) {
                simpleStatIndicator = (SimpleStatIndicator) indicator;
                break;
            }
        }
        if (simpleStatIndicator != null) {
            return simpleStatIndicator.getMapDB(StandardDBName.dataSection.name());
        }
        return null;
    }

    /**
     * DOC talend Comment method "createDrillDownMenu".
     * 
     * @param analysis
     * @param dataEntity
     * @param menu
     * @param itemEntities
     * @param analysis
     */
    public static void createDrillDownMenuForJava(final ChartDataEntity dataEntity, Menu menu, MenuItemEntity[] itemEntities,
            final Analysis analysis) {
        final Indicator indicator = dataEntity != null ? dataEntity.getIndicator() : null;
        AnalyzedDataSet analyDataSet = analysis.getResults().getIndicToRowMap().get(indicator);
        boolean hasData = analyDataSet != null
                && (analyDataSet.getData() != null && analyDataSet.getData().size() > 0
                        || analyDataSet.getFrequencyData() != null && analyDataSet.getFrequencyData().size() > 0 || analyDataSet
                        .getPatternData() != null && analyDataSet.getPatternData().size() > 0);

        if (hasData) {
            createDrillDownMenu(dataEntity, menu, itemEntities, analysis);
        }
    }

    public static void createDrillDownMenuForMapDB(final ChartDataEntity dataEntity, Menu menu, MenuItemEntity[] itemEntities,
            final Analysis analysis) {
        final Indicator indicator = dataEntity != null ? dataEntity.getIndicator() : null;
        if (dataEntity == null || indicator == null) {
            return;
        }

        createDrillDownMenu(dataEntity, menu, itemEntities, analysis);
    }

    /**
     * DOC msjian Comment method "createDrillDownMenu".
     * 
     * @param dataEntity
     * @param menu
     * @param itemEntities
     * @param analysis
     */
    private static void createDrillDownMenu(final ChartDataEntity dataEntity, Menu menu, MenuItemEntity[] itemEntities,
            final Analysis analysis) {
        for (final MenuItemEntity itemEntity : itemEntities) {
            MenuItem item = new MenuItem(menu, SWT.PUSH);
            item.setText(itemEntity.getLabel());
            item.setImage(itemEntity.getIcon());
            try {
                int mapSize = DrillDownUtils.getMapDB(dataEntity, analysis, itemEntity).size();
                item.setEnabled(mapSize > 0);
            } catch (IOError e) {
                item.setEnabled(false);
            }
            item.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    CorePlugin.getDefault().openEditor(new DrillDownEditorInput(analysis, dataEntity, itemEntity),
                            DRILL_DOWN_EDITOR);
                }

            });
        }
    }

}
