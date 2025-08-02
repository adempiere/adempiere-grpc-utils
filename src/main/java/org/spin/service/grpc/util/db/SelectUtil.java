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

import java.util.regex.Pattern;

/**
 * Class for handle SQL SELECT clause and query columns
 * @author Edwin Betancourt, EdwinBetanc0urt@outlook.com, https://github.com/EdwinBetanc0urt
 */
public class SelectUtil {

	public static boolean isDistinct(String sql) {
		// verify if has DISTINCT
		String distinctPattern = "^\\s*SELECT\\s+DISTINCT\\b";
		boolean hasDistinct = Pattern.compile(
			distinctPattern,
			Pattern.CASE_INSENSITIVE | Pattern.DOTALL
		).matcher(sql).find();
		return hasDistinct;
	}

}
