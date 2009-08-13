// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.cwm.compare.factory.comparisonlevel;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.compare.diff.metamodel.AddModelElement;
import org.eclipse.emf.compare.diff.metamodel.RemoveModelElement;
import org.eclipse.emf.compare.diff.metamodel.UpdateAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.EMFSharedResources;
import org.talend.cwm.compare.DQStructureComparer;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import orgomg.cwm.objectmodel.core.Package;

/**
 * 
 * DOC mzhao class global comment. Detailled comment
 */
public class CatalogComparisonLevel extends AbstractComparisonLevel {

	public CatalogComparisonLevel(TdCatalog selObj) {
		super(selObj);
	}

	@Override
	protected TdDataProvider findDataProvider() {
		TdDataProvider provider = DataProviderHelper
				.getTdDataProvider((Package) selectedObj);
		return provider;
	}

	@Override
	protected Resource getLeftResource() throws ReloadCompareException {
		Package selectedPackage = (Package) selectedObj;
		Package findMatchPackage = DQStructureComparer.findMatchedPackage(
				selectedPackage, copyedDataProvider);
		List<TdSchema> schemas = new ArrayList<TdSchema>();
		schemas.addAll(CatalogHelper.getSchemas((TdCatalog) findMatchPackage));
		Resource leftResource = copyedDataProvider.eResource();
		leftResource.getContents().clear();
		for (TdSchema schema : schemas) {
			DQStructureComparer.clearSubNode(schema);
			leftResource.getContents().add(schema);
		}
		EMFSharedResources.getInstance().saveResource(leftResource);
		return leftResource;
	}

	@Override
	protected Resource getRightResource() throws ReloadCompareException {
		Package selectedPackage = (Package) selectedObj;
		Package toReloadObj = DQStructureComparer.findMatchedPackage(
				selectedPackage, tempReloadProvider);
		List<TdSchema> schemas = reloadElementOfPackage(toReloadObj);
		Resource rightResource = null;
		rightResource = tempReloadProvider.eResource();
		rightResource.getContents().clear();
		for (TdSchema schema : schemas) {
			DQStructureComparer.clearSubNode(schema);
			rightResource.getContents().add(schema);
		}
		EMFSharedResources.getInstance().saveResource(rightResource);
		return rightResource;
	}

	private List<TdSchema> reloadElementOfPackage(Package toReloadObj)
			throws ReloadCompareException {
		List<TdSchema> schemas = new ArrayList<TdSchema>();
		TdCatalog catalogObj = SwitchHelpers.CATALOG_SWITCH
				.doSwitch(toReloadObj);
		if (catalogObj != null) {
			schemas = CatalogHelper.getSchemas(catalogObj);
		}
		return schemas;
	}
	
	

	@Override
	protected EObject getSavedReloadObject() throws ReloadCompareException {
        Package selectedPackage = (Package) selectedObj;
		Package findMatchPackage = DQStructureComparer.findMatchedPackage(
				selectedPackage, tempReloadProvider);
		reloadElementOfPackage(findMatchPackage);
		return findMatchPackage;
	}

	@Override
	protected void handleAddElement(AddModelElement addElement) {
        EObject rightElement = addElement.getRightElement();
		TdSchema schema = SwitchHelpers.SCHEMA_SWITCH.doSwitch(rightElement);
		if (schema != null) {
			DataProviderHelper.addSchema(schema, oldDataProvider);
		}
	}

	@Override
	protected void handleRemoveElement(RemoveModelElement removeElement) {
        TdSchema removedSchema = SwitchHelpers.SCHEMA_SWITCH
				.doSwitch(removeElement.getLeftElement());
		if (removedSchema == null) {
			return;
		}
		popRemoveElementConfirm();
		((Package) selectedObj).getOwnedElement().remove(removedSchema);
		// CatalogHelper.(removedSchema, (Package) selectedObj);
	}

	@Override
	protected void handleUpdateElement(UpdateAttribute updateAttribute) {
       // MOD mzhao 2009-07-28 bug 8225
		EObject leftElement = updateAttribute.getLeftElement();
		// If columnSet name change.
		TdSchema schemaLeft = SwitchHelpers.SCHEMA_SWITCH
				.doSwitch(leftElement);
		// If columnSet attribute change.
		if (schemaLeft == null) {
			schemaLeft = (TdSchema) leftElement.eContainer();
		}
		EObject rightElement = updateAttribute.getRightElement();
		TdSchema schemaRight = SwitchHelpers.SCHEMA_SWITCH
				.doSwitch(rightElement);
		if (schemaRight == null) {
			schemaRight = (TdSchema) rightElement.eContainer();
		}
		if (schemaLeft != null && schemaRight != null) {
			Package pack = (Package) selectedObj;
			pack.getOwnedElement().remove(schemaLeft);
			pack.getOwnedElement().add(schemaRight);
		}

    }

}
