/************************************************************************************
 * Copyright (C) 2012-2018 E.R.P. Consultores y Asociados, C.A.                     *
 * Contributor(s): Yamel Senih ysenih@erpya.com                                     *
 * This program is free software: you can redistribute it and/or modify             *
 * it under the terms of the GNU General Public License as published by             *
 * the Free Software Foundation, either version 2 of the License, or                *
 * (at your option) any later version.                                              *
 * This program is distributed in the hope that it will be useful,                  *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                   *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the                     *
 * GNU General Public License for more details.                                     *
 * You should have received a copy of the GNU General Public License                *
 * along with this program. If not, see <https://www.gnu.org/licenses/>.            *
 ************************************************************************************/
package org.spin.service.grpc.util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.adempiere.core.domains.models.X_AD_Table;
import org.adempiere.exceptions.AdempiereException;
import org.adempiere.pipo.IDFinder;
import org.compiere.model.MTable;
import org.compiere.util.Env;
import org.compiere.util.Util;
import org.spin.service.grpc.util.value.NumberManager;
import org.spin.service.grpc.util.value.ValueManager;

import com.google.protobuf.Timestamp;
import com.google.protobuf.Value;

public class Service {
	
	/** Table Allows Records with Zero Identifier */
	public static List<String> ALLOW_ZERO_ID = Arrays.asList(
		X_AD_Table.ACCESSLEVEL_All,
		X_AD_Table.ACCESSLEVEL_SystemPlusClient,
		X_AD_Table.ACCESSLEVEL_ClientPlusOrganization
	);
	
	/**
	 * Get ID for record from table name and uuid
	 * @param tableName
	 * @param uuid
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
	 * Validate Value
	 * @param value
	 * @param message
	 */
	public static java.sql.Timestamp validateDate(com.google.protobuf.Timestamp value, String message) {
		if(value == null) {
			throw new AdempiereException(message);
		}
		if(value == null
				|| (value.getSeconds() == 0 && value.getNanos() == 0)) {
			throw new AdempiereException(message);
		}
		return ValueManager.getDateFromTimestampDate(value);
	}
	
	/**
	 * Validate Value
	 * @param value
	 * @param message
	 */
	public static String validateString(String value, String message) {
		if(value == null) {
			throw new AdempiereException(message);
		}
		if(Util.isEmpty(value)) {
			throw new AdempiereException(message);
		}
		return value;
	}
	
	/**
	 * Validate Value
	 * @param value
	 * @param message
	 */
	public static BigDecimal validateNumber(Value value, String message) {
		if(value == null) {
			throw new AdempiereException(message);
		}
		if(value.hasStringValue()) {
			return NumberManager.getBigDecimalFromString(value.getStringValue());
		} else if(value.hasNumberValue()) {
			return new BigDecimal(value.getNumberValue());
		} else {
			throw new AdempiereException(message);
		}
	}
	
	public static int validateReference(String value, String tableName, String message) {
		if(value == null) {
			throw new AdempiereException(message);
		}
		if(Util.isEmpty(value)) {
			throw new AdempiereException(message);
		}
		int referenceId = getIdFromUuid(tableName, value, null);
		if(referenceId <= 0) {
			throw new AdempiereException(message);
		}
		return referenceId;
	}
	
	/**
	 * Validate Value
	 * @param value
	 * @param message
	 */
	public static void validateValue(Object value, String message) {
		if(value == null) {
			throw new AdempiereException(message);
		}
		if(value instanceof com.google.protobuf.Timestamp) {
			com.google.protobuf.Timestamp timestampValue = (Timestamp) value;
			validateDate(timestampValue, message);
		} else if(value instanceof String) {
			validateString((String) value, message);
		}
	}
	
	/**
	 * Evaluate if is valid identifier
	 * @param id
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
}
