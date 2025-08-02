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

/**
 * Class for handle SQL Count records
 * @author Edwin Betancourt, EdwinBetanc0urt@outlook.com, https://github.com/EdwinBetanc0urt
 */
public class SqlUtil {
	
	/**
     * Cleans the SQL string by:
     * 1. Removing multi-line comments (/* ... * /).
     * 2. Removing single-line comments (-- ...).
     * 3. Replacing any sequence of whitespace characters (spaces, tabs, newlines, etc.) with a single space.
     * 4. Trimming leading/trailing whitespace.
     * This standardization simplifies subsequent regex matching and SQL parsing.
     *
	 * @param originalSql The original SQL string to clean.
	 * @return A cleaned, single-line SQL string with standardized whitespace.
	 */
	public static String cleanSQL(String originalSql) {
		final String sql = originalSql
			.replaceAll("/\\*.*?\\*/", "") // Remove multi-line comments
			.replaceAll("--.*?\n", "") // Remove single-line comments
			.replaceAll("\\s+", " ") // Replace multiple spaces/newlines with single space
			.trim() // Trim leading/trailing whitespace from the entire string
		;
		return sql;
	}

}
