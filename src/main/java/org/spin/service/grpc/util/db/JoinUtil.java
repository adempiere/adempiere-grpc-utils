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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for handle SQL JOIN
 * @author Edwin Betancourt, EdwinBetanc0urt@outlook.com, https://github.com/EdwinBetanc0urt
 */
public class JoinUtil {

	public static String fixSqlFromClause(String sql) {
		String regex = "^\\s*(FROM\\s+\\S+(?:\\s*AS\\s*)?\\s+\\S+" // FROM tabla (AS) alias
			+ "(?:\\s+(?:LEFT|INNER|RIGHT|FULL)?\\s*JOIN\\s+\\S+(?:\\s*AS\\s*)?\\s+\\S+\\s+ON\\s*\\(.*?\\))*" // zero or more JOINs
			+ ")\\s*,\\s*(\\S+(?:\\s*AS\\s*)?\\s+\\S+)(.*)$"
		; // Comma, table (AS) alias, and the rest

		Pattern pattern = Pattern.compile(
			regex,
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL
		); // DOTALL to match `.` line breaks
		Matcher matcher = pattern.matcher(sql);

		if (matcher.matches()) {
			String initialSqlPart = matcher.group(1);
			String looseTableAlias = matcher.group(2);
			String remainingSql = matcher.group(3);

			// Separate table name and alias, optionally handling `AS`.
			String tableName;
			String aliasName;
			// We use a split pattern to handle `AS` and multiple spaces
			Pattern aliasSplitPattern = Pattern.compile("\\s*AS\\s+|\\s+", Pattern.CASE_INSENSITIVE);
			String[] parts = aliasSplitPattern.split(looseTableAlias);

			if (parts.length >= 2) {
				tableName = parts[0].trim();
				aliasName = parts[1].trim();
			} else {
				// If for some reason there is only one element (e.g. only the name of the table without alias)
				// This is a borderline case, but it is best handled.
				tableName = looseTableAlias.trim();
				aliasName = tableName; // Or some default value if there is no alias
				System.err.println("Warning: Could not parse alias for: " + looseTableAlias + ". Using the table name as alias.");
			}

			String newJoinClause = String.format("LEFT JOIN %s %s ON (1=1)", tableName, aliasName);

			final String parsedSql = " " + initialSqlPart + " " + newJoinClause + remainingSql;
			return parsedSql;
		}

		System.err.println("DEBUG: The pattern did NOT match the complete SQL. Input SQL: \n" + sql);
		return sql; // Returns the original SQL unchanged
	}

}
