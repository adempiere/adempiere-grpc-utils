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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.compiere.util.Util;
import org.spin.service.grpc.util.db.OperatorUtil;
import org.spin.service.grpc.util.value.TextManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 * A Stub class that represent a filters from request
 * [{"name":"AD_Client_ID", "operator":"equal", "values": 1000000}, {"name":"AD_Org_ID", "operator":"in", "values": [1000000, 11, 0]}]
 */
public class FilterManager {

	private List<Map<String, Object>> fillValues;

	/**
	 * read filters and convert to stub
	 * @param filter
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private FilterManager(String filter) {
		if(Util.isEmpty(filter, true)) {
			this.fillValues = new ArrayList<Map<String, Object>>();
		} else {
			ObjectMapper fileMapper = new ObjectMapper();
			try {
				/*
					[
						{"name: "C_BPartner_ID", "operator": "in", "values": [1234, 4321]},
						{"name": "C_Invoice", "operator": "equal", "values": 333}
					]
				*/
				fillValues = fileMapper.readValue(filter, List.class);
			} catch (IOException e) {
				try {
					/*
						{
							"C_BPartner_ID": 1234,
							"C_Invoice": 333
						}
					*/
					TypeReference<HashMap<String,Object>> valueType = new TypeReference<HashMap<String,Object>>() {};
					// JavaType valueType = fileMapper.getTypeFactory().constructMapLikeType(Map.class, String.class, Object.class);
					this.fillValues = new ArrayList<Map<String, Object>>();

					Map<String, Object> keyValueFilters = fileMapper.readValue(filter, valueType);
					if (keyValueFilters != null && !keyValueFilters.isEmpty()) {
						keyValueFilters.entrySet().forEach(entry -> {
							Map<String, Object> condition = new HashMap<String, Object>();

							// set column name as key filter
							condition.put(Filter.NAME, entry.getKey());

							// set value filter
							Object value = entry.getValue();
							condition.put(Filter.VALUES, value);

							// set operator filter
							condition.put(Filter.OPERATOR, OperatorUtil.EQUAL);
							if (value != null && value instanceof List) {
								condition.put(Filter.OPERATOR, OperatorUtil.IN);
							}

							fillValues.add(condition);
						});
					}
				} catch (IOException e2) {
					throw new RuntimeException("Invalid filter");
				}
			}
		}
	}

	public static FilterManager newInstance(String filters) {
		final String decodeFilters = TextManager.getDecodeUrl(
			filters
		);
		return new FilterManager(decodeFilters);
	}

	public List<Filter> getConditions() {
		if(fillValues == null) {
			return new ArrayList<Filter>();
		}
		return fillValues.stream()
			.map(value -> new Filter(value))
			.collect(Collectors.toList());
	}

	public static void main(String[] args) {
		String completeFilter = "[{\"name\":\"AD_Client_ID\", \"operator\":\"equal\", \"values\": 1000000}, {\"name\":\"AD_Org_ID\", \"operator\":\"in\", \"values\": [1000000, 11, 0]}]";
		FilterManager.newInstance(completeFilter)
			.getConditions()
			.forEach(condition -> {
				System.out.println(condition);
			})
		;
		String simplyFilter = "{\"AD_Client_ID\": 1000000, \"AD_Org_ID\": [1000000, 11, 0]}]";
		FilterManager.newInstance(simplyFilter)
			.getConditions()
			.forEach(condition -> {
				System.out.println(condition);
			})
		;
	}

}
