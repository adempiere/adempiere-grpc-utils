/*************************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                              *
 * This program is free software; you can redistribute it and/or modify it           *
 * under the terms version 2 or later of the GNU General Public License as published *
 * by the Free Software Foundation. This program is distributed in the hope          *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied        *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.                  *
 * See the GNU General Public License for more details.                              *
 * You should have received a copy of the GNU General Public License along           *
 * with this program; if not, write to the Free Software Foundation, Inc.,           *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                            *
 * For the text or an alternative of this public license, you may reach us           *
 * Copyright (C) 2012-2018 E.R.P. Consultores y Asociados, S.A. All Rights Reserved. *
 * Contributor(s): Yamel Senih www.erpya.com                                         *
 *************************************************************************************/
package org.spin.service.grpc.util.base;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.adempiere.exceptions.AdempiereException;
import org.adempiere.pipo.IDFinder;
import org.adempiere.core.domains.models.I_AD_Element;
import org.adempiere.core.domains.models.I_C_Order;
import org.adempiere.core.domains.models.X_AD_Table;
import org.compiere.model.MColumn;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.compiere.util.Util;

/**
 * Class for handle records utils values
 * @author Yamel Senih, ysenih@erpya.com , http://www.erpya.com
 */
public class RecordUtil {

	/** Table Allows Records with Zero Identifier */
	public static final List<String> ALLOW_ZERO_ID = Arrays.asList(
		X_AD_Table.ACCESSLEVEL_All,
		X_AD_Table.ACCESSLEVEL_SystemPlusClient,
		X_AD_Table.ACCESSLEVEL_ClientPlusOrganization
	);


	/** Column names on records to client validations */
	public static final List<String> RECORDS_COLUMN_NAMES = Arrays.asList(
		I_AD_Element.COLUMNNAME_AD_Client_ID,
		I_AD_Element.COLUMNNAME_IsActive,
		I_C_Order.COLUMNNAME_Processed,
		I_C_Order.COLUMNNAME_Processing
	);


	public static final String TRANSLATION_SUFFIX = "_Trl";


	/**
	 * get Entity from Table ID and Record UUID / Record ID
	 * @param context
	 * @param tableId
	 * @param uuid
	 * @param recordId
	 * @param transactionName
	 * @return
	 */
	public static PO getEntity(Properties context, int tableId, String uuid, int recordId, String transactionName) {
		if (tableId <= 0) {
			throw new AdempiereException("@AD_Table_ID@ @NotFound@");
		}

		String tableName = MTable.getTableName(context, tableId);
		return getEntity(context, tableName, uuid, recordId, transactionName);
	}
	/**
	 * get Entity from Table ID and Record ID
	 * @param context
	 * @param tableName
	 * @param recordId
	 * @param recordId
	 * @return
	 */
	public static PO getEntity(Properties context, String tableName, int recordId, String transactionName) {
		return getEntity(context, tableName, null, recordId, transactionName);
	}
	/**
	 * get Entity from Table and UUID / Record ID
	 * @param context
	 * @param tableName
	 * @param uuid
	 * @param recordId
	 * @param transactionName
	 * @return
	 */
	public static PO getEntity(Properties context, String tableName, String uuid, int recordId, String transactionName) {
		// Vaidate and get Table
		final MTable table = RecordUtil.validateAndGetTable(tableName);
		//	Validate UUID/ID
		boolean isId = isValidId(recordId, table.getAccessLevel());
		if (Util.isEmpty(uuid, true) && !isId) {
			throw new AdempiereException("@FillMandatory@ @Record_ID@ / @UUID@");
		}

		StringBuffer whereClause = new StringBuffer();
		List<Object> filtersList = new ArrayList<>();
		if (!Util.isEmpty(uuid, true)) {
			whereClause.append(I_AD_Element.COLUMNNAME_UUID + " = ?");
			filtersList.add(uuid);
		} else if (isId) {
			for (final String keyColumnName: table.getKeyColumns()) {
				MColumn column = table.getColumn(keyColumnName);
				if (DisplayType.isID(column.getAD_Reference_ID())) {
					if (whereClause.length() > 0) {
						whereClause.append(" OR ");
					}
					whereClause.append(keyColumnName + " = ?");
					filtersList.add(recordId);
				}
			}
		} else {
			throw new AdempiereException("@Record_ID@ / @UUID@ @NotFound@");
		}
		//	Default
		return new Query(
			context,
			table,
			whereClause.toString(),
			transactionName
		)
			.setParameters(filtersList)
			.first()
		;
	}

	/**
	 * get Entity from Table and where clause
	 * @param context
	 * @param tableName
	 * @param whereClause
	 * @param parameters
	 * @param transactionName
	 * @return
	 */
	public static PO getEntity(Properties context, String tableName, String whereClause, List<Object> parameters, String transactionName) {
		//	Validate ID
		if(Util.isEmpty(whereClause)) {
			throw new AdempiereException("@WhereClause@ @NotFound@");
		}
		
		MTable table = validateAndGetTable(tableName);
		//	Default
		return new Query(
			context,
			table,
			whereClause,
			transactionName
		)
			.setParameters(parameters)
			.first();
	}



	/**
	 * Validate tableName and MTable, and get instance
	 * @param tableName
	 * @return
	 */
	public static MTable validateAndGetTable(String tableName) {
		if (Util.isEmpty(tableName, true)) {
			throw new AdempiereException("@FillMandatory@ @AD_Table_ID@");
		}
		MTable table = MTable.get(Env.getCtx(), tableName);
		if (table == null || table.getAD_Table_ID() <= 0) {
			throw new AdempiereException("@AD_Table_ID@ @NotFound@");
		}
		return table;
	}
	/**
	 * Validate tableName and MTable, and get instance
	 * @param tableId
	 * @return
	 */
	public static MTable validateAndGetTable(int tableId) {
		if (tableId <= 0) {
			throw new AdempiereException("@FillMandatory@ @AD_Table_ID@");
		}
		MTable table = MTable.get(Env.getCtx(), tableId);
		if (table == null || table.getAD_Table_ID() <= 0) {
			throw new AdempiereException("@AD_Table_ID@ @NotFound@");
		}
		return table;
	}


	/**
	 * Evaluate if is valid identifier
	 * @param id
	 * @param table
	 * @return
	 */
	public static boolean isValidId(int id, MTable table) {
		if (table == null || table.getAD_Table_ID() <= 0) {
			return false;
		}
		return isValidId(
			id,
			table.getAccessLevel()
		);
	}
	/**
	 * Evaluate if is valid identifier
	 * @param id Record Id
	 * @param accesLevel
	 * @return
	 */
	public static boolean isValidId(int id, String accesLevel) {
		if (id < 0) {
			return false;
		}
		if (id == 0 && !ALLOW_ZERO_ID.contains(accesLevel)) {
			return false;
		}
		return true;
	}


	/**
	 * Evaluate if is valid identifier
	 * @param id
	 * @param accesLevel
	 * @return
	 */
	public static boolean validateRecordId(int id, String accesLevel) {
		if (!isValidId(id, accesLevel)) {
			throw new AdempiereException("@FillMandatory@ @Record_ID@ / @UUID@");
		}
		return true;
	}



	/**
	 * Get ID for record from table name and uuid
	 * @param tableName
	 * @param uuid
	 * @param transactionName
	 * @return
	 */
	public static int getIdFromUuid(String tableName, String uuid, String transactionName) {
		if (Util.isEmpty(tableName, true) || Util.isEmpty(uuid, true)) {
			return -1;
		}
		//	Get
		return IDFinder.getIdFromUUID(Env.getCtx(), tableName, uuid, Env.getAD_Client_ID(Env.getCtx()), transactionName);
	}

	/**
	 * Get UUID from record id
	 * @param tableName
	 * @param id
	 * @return
	 */
	public static String getUuidFromId(String tableName, int id) {
		return getUuidFromId(tableName, id, null);
	}
	/**
	 * Get UUID from record id
	 * @param tableName
	 * @param id
	 * @param transactionName
	 * @return
	 */
	public static String getUuidFromId(String tableName, int id, String transactionName) {
		if (Util.isEmpty(tableName, true)) {
			return null;
		}
		MTable table = MTable.get(Env.getCtx(), tableName);
		if (table == null || table.getAD_Table_ID() <= 0) {
			return null;
		}

		//	Validate ID
		if (!isValidId(id, table.getAccessLevel())) {
			return null;
		}
		//	Get
		return IDFinder.getUUIDFromId(tableName, id, Env.getAD_Client_ID(Env.getCtx()), transactionName);
	}



	/**
	 * Verify if exist a column
	 * @param metaData
	 * @param columnName
	 * @return
	 * @throws SQLException
	 */
	public static int getColumnIndex(ResultSetMetaData metaData, String columnName) throws SQLException {
		if (metaData == null) {
			return -1;
		}
		for(int columnIndex = 1; columnIndex <= metaData.getColumnCount(); columnIndex++) {
			String metaDataColumnName = metaData.getColumnName(columnIndex);
			if(metaDataColumnName != null && metaDataColumnName.toLowerCase().equals(columnName.toLowerCase())) {
				return columnIndex;
			}
		}
		return -1;
	}



	/**
	 * Get translation if is necessary
	 * @param object
	 * @param columnName
	 * @return
	 */
	public static String getTranslation(PO object, String columnName) {
		if(object == null) {
			return null;
		}
		if(Language.isBaseLanguage(Env.getAD_Language(Env.getCtx()))) {
			return object.get_ValueAsString(columnName);
		}
		//	
		return object.get_Translation(columnName);
	}

}
