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
package org.talend.dataprofiler.core.download;

import org.talend.dataprofiler.service.ITOPChartService;

/**
 * created by yyin on 2015年1月12日 Detailled comment
 *
 */
public class DQChartJarsExtraUpdatesFactory extends AbstractDQMissingJarsExtraUpdatesFactory {

    @Override
    protected String getJarFileName() {
        return ITOPChartService.JAR_FILE_NAME;
    }

    @Override
    protected String getPluginName() {
        return ITOPChartService.PLUGIN_NAME;
    }

    @Override
    protected String getContextName() {
        return "context: org.talend.dataprofiler.top.chart"; //$NON-NLS-1$
    }

    @Override
    protected String getInforMessage() {
        return "DQ need the plugin: org.talend.dataprofiler.top.chart"; //$NON-NLS-1$
    }

    @Override
    protected String getDownloadName() {
        return "DownloadChartPluginJarFactory.name";
    }

}