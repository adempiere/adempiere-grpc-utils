/************************************************************************************
 * Copyright (C) 2018-present E.R.P. Consultores y Asociados, C.A.                  *
 * Contributor(s): Edwin Betancourt, EdwinBetanc0urt@outlook.com                    *
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
package org.spin.service.grpc.util.db;

import org.compiere.model.MQuery;
import org.compiere.util.DisplayType;

/**
 * Class for handle SQL Operators
 * @author Edwin Betancourt, EdwinBetanc0urt@outlook.com, https://github.com/EdwinBetanc0urt
 */
public class OperatorUtil {

	public static final String SQL_OPERATORS_REGEX = "(<>|<=|>=|!=|<|=|>|NOT\\s+IN|IN|NOT\\s+BETWEEN|BETWEEN|NOT\\s+LIKE|LIKE|IS\\s+NULL|IS\\s+NOT\\s+NULL)";


	public static final String VOID = "void";
	public static final String EQUAL = "equal";
	public static final String NOT_EQUAL = "not_equal";
	public static final String LIKE = "like";
	public static final String NOT_LIKE = "not_like";
	public static final String GREATER = "greater";
	public static final String GREATER_EQUAL = "greater_equal";
	public static final String LESS = "less";
	public static final String LESS_EQUAL = "less_equal";
	public static final String BETWEEN = "between";
	public static final String NOT_BETWEEN = "not_between";
	public static final String NOT_NULL = "not_null";
	public static final String NULL = "null";
	public static final String IN = "in";
	public static final String NOT_IN = "not_in";



	/**
	 * Convert operator from gRPC to SQL
	 * @param serviceOperator
	 * @return
	 */
	public static String convertOperator(String serviceOperator) {
		String operator = MQuery.EQUAL;
		if (serviceOperator == null) {
			return operator;
		}
		switch (serviceOperator.toLowerCase().trim()) {
			case BETWEEN:
			case ">-<":
				operator = MQuery.BETWEEN;
				break;
			case NOT_BETWEEN:
			case "<->":
				operator = " NOT BETWEEN ";
				break;
			case EQUAL:
			case "=":
				operator = MQuery.EQUAL;
				break;
			case GREATER_EQUAL:
			case ">=":
				operator = MQuery.GREATER_EQUAL;
				break;
			case GREATER:
			case ">":
				operator = MQuery.GREATER;
				break;
			case LESS_EQUAL:
			case "<=":
				operator = MQuery.LESS_EQUAL;
				break;
			case LESS:
			case "<":
				operator = MQuery.LESS;
				break;
			case NOT_EQUAL:
			case "!=":
				operator = MQuery.NOT_EQUAL;
				break;
			case LIKE:
			case "~":
			case "%":
				operator = MQuery.LIKE;
				break;
			case NOT_LIKE:
			case "!~":
			case "!%":
				operator = MQuery.NOT_LIKE;
				break;
			case IN:
			case "()":
				operator = " IN ";
				break;
			case NOT_IN:
			case "!()":
				operator = " NOT IN ";
				break;
			case NULL:
				operator = MQuery.NULL;
				break;
			case NOT_NULL:
				operator = MQuery.NOT_NULL;
				break;
			default:
				operator = MQuery.EQUAL;
				break;
		}
		return operator;
	}



	/**
	 * Get default operator by display type
	 * @param displayTypeId
	 * @return
	 */
	public static String getDefaultOperatorByDisplayType(int displayTypeId) {
		String operator = EQUAL;
		switch (displayTypeId) {
			case DisplayType.String:
			case DisplayType.Text:
			case DisplayType.TextLong:
			case DisplayType.Memo:
			case DisplayType.FilePath:
			case DisplayType.FileName:
			case DisplayType.FilePathOrName:
			case DisplayType.URL:
			case DisplayType.PrinterName:
				operator = LIKE;
				break;
			// case DisplayType.Date:
			// case DisplayType.DateTime:
			// 	operator = BETWEEN;
			// 	break;
			default:
				operator = EQUAL;
				break;
			}
		return operator;
	}

}
