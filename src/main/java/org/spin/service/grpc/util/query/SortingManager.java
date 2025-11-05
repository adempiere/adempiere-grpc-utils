/************************************************************************************
 * Copyright (C) 2012-2018 E.R.P. Consultores y Asociados, C.A.                     *
 * Contributor(s): Yamel Senih ysenih@erpya.com                                     *
 * This program is free software: you can redistribute it and/or modify             *
 * it under the terms of the GNU General Public License as published by             *
 * the Free Software Foundation, either version 2 of the License, or                *
 * (at your option) any later version.                                              *
 * This program is distributed in the hope that it will be useful,                  *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                   *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the                     *
 * GNU General Public License for more details.                                     *
 * You should have received a copy of the GNU General Public License                *
 * along with this program. If not, see <https://www.gnu.org/licenses/>.            *
 ************************************************************************************/

package org.spin.service.grpc.util.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.compiere.util.Util;
import org.spin.service.grpc.util.value.TextManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 * A Stub class that represent a sort from request
 * [{"name":"Name", "type":"asc"}, {"name":"Value", "type":"desc"}]
 */
public class SortingManager {

	private List<Map<String, String>> fillValues = new ArrayList<Map<String, String>>();

	/**
	 * read filters and convert to stub
	 * @param sorting
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private SortingManager(String sorting) {
		if(Util.isEmpty(sorting, true)) {
			this.fillValues = new ArrayList<Map<String, String>>();
		} else {
			ObjectMapper fileMapper = new ObjectMapper();
			try {
				/*
					[
						{"name: "C_BPartner_ID", "type": "asc"},
						{"name": "C_Invoice", "type": "desc"}
					]
				*/
				this.fillValues = fileMapper.readValue(sorting, List.class);
			} catch (IOException e) {
				// throw new RuntimeException("Invalid filter");
				try {
					/*
						{
							"C_BPartner_ID": "asc",
							"C_Invoice": "desc"
						}
					*/
					TypeReference<HashMap<String,String>> valueType = new TypeReference<HashMap<String,String>>() {};
					// JavaType valueType = fileMapper.getTypeFactory().constructMapLikeType(Map.class, String.class, Object.class);
					Map<String, String> keyValueFilters = fileMapper.readValue(sorting, valueType);
					
					this.fillValues = new ArrayList<Map<String, String>>();
					if (keyValueFilters != null && !keyValueFilters.isEmpty()) {
						keyValueFilters.entrySet().forEach(entry -> {
							Map<String, String> condition = new HashMap<String, String>();
							condition.put(Order.NAME, entry.getKey());
							condition.put(Order.TYPE, Order.ASCENDING);
							String sortType = entry.getValue();
							if (Util.isEmpty(sortType, true)) {
								condition.put(Order.TYPE, sortType);
							}
							this.fillValues.add(condition);
						});
					}
				} catch (IOException e2) {
					/**
						"DisplayColumn_C_BPartner_ID ASC, DocumentNo DESC"
					 */
					this.fillValues = new ArrayList<Map<String, String>>();

					// throw new RuntimeException("Invalid Order");
					List<String> sortColumns = Arrays.asList(sorting.split("\\s*,\\s*"));
					sortColumns.forEach(sortCondition -> {
						Map<String, String> condition = new HashMap<String, String>();
	
						List<String> sortValues = Arrays.asList(sortCondition.split("\\s"));
						String columnName = sortValues.get(0).trim();
						condition.put(Order.NAME, columnName);
						// default order type is ASC
						condition.put(Order.TYPE, Order.ASCENDING);

						if (sortValues.size() > 1) {
							String sortType = sortValues.get(1).trim().toLowerCase();
							// change order type is DESC
							if (sortType.equals(Order.DESCENDING)) {
								condition.put(Order.TYPE, Order.DESCENDING);
							}
						}

						this.fillValues.add(condition);
					});
				}
			}
		}
	}

	public static SortingManager newInstance(String sortings) {
		final String decodeSortings = TextManager.getDecodeUrl(
			sortings
		);
		return new SortingManager(decodeSortings);
	}

	public List<Order> getSorting() {
		if(fillValues == null) {
			return new ArrayList<Order>();
		}
		return fillValues.stream()
			.map(value -> new Order(value))
			.collect(Collectors.toList());
	}

	public String getSotingAsSQL() {
		StringBuffer sortingAsSQL = new StringBuffer();
		getSorting().forEach(sotring -> {
			if(sortingAsSQL.length() > 0) {
				sortingAsSQL.append(", ");
			}
			String columnName = sotring.getColumnName();
			if (columnName.startsWith("DisplayColumn")) {
				columnName = "\"" + columnName + "\"";
			}
			sortingAsSQL.append(columnName);
			if(sotring.getSortType().equals(Order.ASCENDING)) {
				sortingAsSQL.append(" ASC");
			} else if(sotring.getSortType().equals(Order.DESCENDING)) {
				sortingAsSQL.append(" DESC");
			}
		});
		return sortingAsSQL.toString();
	}

	public static void main(String[] args) {
		// TODO: Add support to DisplayType to DisplayColumn
		String completeSorting = "[{\"name\":\"Name\", \"type\":\"asc\"}, {\"name\":\"Description\", \"type\":\"desc\"}]";
		SortingManager.newInstance(completeSorting)
			.getSorting().forEach(sort -> {
				System.out.println(sort);
			})
		;

		String basicSorting = "{\"Name\":\"asc\", \"Description\":\"desc\"}";
		SortingManager.newInstance(basicSorting)
			.getSorting().forEach(sort -> {
				System.out.println(sort);
			})
		;

		String simplySorting = "Name ASC, Description DESC";
		SortingManager.newInstance(simplySorting)
			.getSorting().forEach(sort -> {
				System.out.println(sort);
			})
		;
	}

}
