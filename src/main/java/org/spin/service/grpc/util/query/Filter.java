/************************************************************************************
 * Copyright (C) 2012-2023 E.R.P. Consultores y Asociados, C.A.                     *
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

package org.spin.service.grpc.util.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 * A Stub class that represent a filters from request
 */
public class Filter {

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
	//	
	public static final String NAME = "name";
	public static final String OPERATOR = "operator";
	public static final String VALUES = "values";
	//	Values
	public static final int FROM = 0;
	public static final int TO = 1;

	private Map<String, Object> condition;

	public Filter(Map<String, Object> newCondition) {
		condition = newCondition;
	}

	public void setColumnName(String columnName) {
		condition.put(NAME, columnName);
	}
	public String getColumnName() {
		Object key = condition.get(NAME);
		if (key == null) {
			return null;
		}
		return (String) key;
	}

	public void setOperator(String operator) {
		this.condition.put(OPERATOR, operator);
	}
	public String getOperator() {
		Object operator = condition.get(OPERATOR);
		if (operator == null) {
			// assumes the default operator
			return EQUAL;
		}
		return (String) operator;
	}

	public void setValue(Object value) {
		this.condition.put(VALUES, value);
	}
	public Object getValue() {
		return condition.get(VALUES);
	}

	@SuppressWarnings("unchecked")
	public List<Object> getValues() {
		Object value = condition.get(VALUES);
		if (value == null) {
			return null;
		}
		if(value instanceof List) {
			return (List<Object>) value;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private List<Object> getOrCreateValuesList() {
		Object values = condition.get(VALUES);
		List<Object> valuesList;
		if (values instanceof List) {
			valuesList = (List<Object>) values;
		} else {
			valuesList = new ArrayList<Object>();
		}
		// Ensure that the list has at least two elements
		while (valuesList.size() < 2) {
			valuesList.add(null); // Prevent out-of-bounds index
		}
		return valuesList;
	}

	public void setFromValue(Object valueFrom) {
		List<Object> valuesList = getOrCreateValuesList();
		valuesList.set(FROM, valueFrom);
		this.condition.put(VALUES, valuesList);
	}
	public Object getFromValue() {
		List<Object> values = getValues();
		if(values == null || values.isEmpty()) {
			return getValue();
		}
		return values.get(FROM);
	}

	public void setToValue(Object valueTo) {
		List<Object> valuesList = getOrCreateValuesList();
		valuesList.set(TO, valueTo);
		this.condition.put(VALUES, valuesList);
	}
	public Object getToValue() {
		List<Object> values = getValues();
		if(values == null || values.isEmpty() || values.size() > 2) {
			return null;
		}
		return values.get(TO);
	}

	@Override
	public String toString() {
		return "Filter [getColumnName()=" + getColumnName() +
			", getOperator()=" + getOperator() +
			", getValue()=" + getValue() + ", getValues()=" + getValues() +
			", getFromValue()=" + getFromValue() + ", getToValue()=" + getToValue() + "]";
	}

}
