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

import org.adempiere.exceptions.AdempiereException;
import org.compiere.util.Util;
import org.spin.service.grpc.util.base.RecordUtil;
import org.spin.service.grpc.util.value.NumberManager;
import org.spin.service.grpc.util.value.TimeManager;

import com.google.protobuf.Timestamp;
import com.google.protobuf.Value;

public class Service {

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
		return TimeManager.getTimestampFromProtoTimestamp(value);
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
		int referenceId = RecordUtil.getIdFromUuid(tableName, value, null);
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

}
