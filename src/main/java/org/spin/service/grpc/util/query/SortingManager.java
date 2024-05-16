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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.compiere.util.Util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Yamel Senih, ysenih@erpya.com, ERPCyA http://www.erpya.com
 * A Stub class that represent a sort from request
 * [{"name":"Name", "type":"asc"}, {"name":"Value", "type":"desc"}]
 */
public class SortingManager {
	
	private List<Map<String, Object>> fillValues;
	
	/**
	 * read filters and convert to stub
	 * @param sorting
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private SortingManager(String sorting) {
		if(Util.isEmpty(sorting, true)) {
			this.fillValues = new ArrayList<>();
		} else {
			ObjectMapper fileMapper = new ObjectMapper();
			try {
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
					this.fillValues = new ArrayList<>();

					Map<String, String> keyValueFilters = fileMapper.readValue(sorting, valueType);
					if (keyValueFilters != null && !keyValueFilters.isEmpty()) {
						keyValueFilters.entrySet().forEach(entry -> {
							Map<String, Object> condition = new HashMap<>();
							condition.put(Order.NAME, entry.getKey());
							condition.put(Order.TYPE, Order.ASCENDING);
							Object value = entry.getValue();
							if (value != null) {
								condition.put(Order.TYPE, value);
							}

							this.fillValues.add(condition);
						});
					}
				} catch (IOException e2) {
					throw new RuntimeException("Invalid Order");
				}
			}
		}
	}

	public static SortingManager newInstance(String filters) {
		return new SortingManager(filters);
	}

	public List<Order> getSorting() {
		if(this.fillValues == null) {
			return new ArrayList<Order>();
		}
		return this.fillValues.stream()
			.map(value -> new Order(value))
			.collect(Collectors.toList());
	}

	public String getSotingAsSQL() {
		StringBuffer sortingAsSQL = new StringBuffer();
		this.getSorting().forEach(sotring -> {
			if(sortingAsSQL.length() > 0) {
				sortingAsSQL.append(", ");
			}
			sortingAsSQL.append(sotring.getColumnName());
			if(sotring.getSortType().equals(Order.ASCENDING)) {
				sortingAsSQL.append(" ASC");
			} else if(sotring.getSortType().equals(Order.DESCENDING)) {
				sortingAsSQL.append(" DESC");
			}
		});
		return sortingAsSQL.toString();
	}

	public static void main(String[] args) {
		String completeSorting = "[{\"name\":\"Name\", \"type\":\"asc\"}, {\"name\":\"Value\", \"type\":\"desc\"}]";
		SortingManager.newInstance(completeSorting)
			.getSorting().forEach(sort -> {
				System.out.println(sort);
			})
		;
		String simplySorting = "{\"Name\":\"asc\", \"Value\":\"desc\"}";
		SortingManager.newInstance(simplySorting)
			.getSorting().forEach(sort -> {
				System.out.println(sort);
			})
		;
	}

}
