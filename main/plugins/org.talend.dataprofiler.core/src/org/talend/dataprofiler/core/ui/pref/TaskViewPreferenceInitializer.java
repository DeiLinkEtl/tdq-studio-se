// ============================================================================
//
// Copyright (C) 2006-2019 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.pref;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.ui.internal.ide.IDEInternalPreferences;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.talend.commons.ui.runtime.CommonUIPlugin;

/**
 * created by msjian on 2015年11月6日 Detailled comment
 *
 */
public class TaskViewPreferenceInitializer extends AbstractPreferenceInitializer {

    private static final String MIGRATE_PREFERENCE_CONSTANT = "_MIGRATE";

    /**
     * TaskViewPreferenceInitializer constructor.
     */
    public TaskViewPreferenceInitializer() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    @SuppressWarnings({ "restriction", "deprecation" })
    @Override
    public void initializeDefaultPreferences() {
        // for commandline mode
        if (CommonUIPlugin.isFullyHeadless()) {
            return;
        }

        // constant was removed from
        // https://github.com/eclipse/eclipse.platform.ui/commit/e8aaa0aa525c27b16b932bda46e68fa3c254c6be
        // so here try to use the old String instead
        String migrationPreference = IDEInternalPreferences.TASKS_FILTERS + MIGRATE_PREFERENCE_CONSTANT;
        // Already migrated
        if (IDEWorkbenchPlugin.getDefault().getPreferenceStore().getBoolean(migrationPreference)) {
            return;
        }

        // Mark as migrated
        IDEWorkbenchPlugin.getDefault().getPreferenceStore().setValue(migrationPreference, true);

        // the task view's settings
        String writer = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<filterGroups andFilters=\"false\" markerLimit=\"100\" markerLimitEnabled=\"true\">\r\n<filterGroup IMemento.internal.id=\"TODOs\" enabled=\"true\" filterLimit=\"-1\" scope=\"0\">\r\n<fieldFilter IMemento.internal.id=\"org.eclipse.ui.ide.completionField\" completion=\"3\"/>\r\n<fieldFilter IMemento.internal.id=\"org.eclipse.ui.ide.priorityField\" selectedPriorities=\"7\"/>\r\n<fieldFilter IMemento.internal.id=\"org.eclipse.ui.ide.descriptionField\" containsModifier=\"CONTAINS\" containsText=\"\"/>\r\n<fieldFilter IMemento.internal.id=\"org.eclipse.ui.ide.markerType\" selectedTypes=\"org.eclipse.core.resources.taskmarker:\"/>\r\n</filterGroup>\r\n</filterGroups>"; //$NON-NLS-1$
        IDEWorkbenchPlugin.getDefault().getPreferenceStore()
                .putValue("org.eclipse.ui.internal.views.markers.CachedMarkerBuilderorg.eclipse.ui.views.TaskList", writer); //$NON-NLS-1$
        IDEWorkbenchPlugin.getDefault().savePluginPreferences();
    }

}
