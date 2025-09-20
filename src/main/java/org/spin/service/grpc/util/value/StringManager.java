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

package org.spin.service.grpc.util.value;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.compiere.util.Util;

/**
 * Class for handle String values
 * @author Edwin Betancourt, EdwinBetanc0urt@outlook.com, https://github.com/EdwinBetanc0urt
 */
public class StringManager {

	/**
	 * Convert null on ""
	 * @param value
	 * @return
	 */
	public static String getValidString(String value) {
		return Optional.ofNullable(value).orElse("");
	}


	/**
	 * Convert Object to String
	 * @param value
	 * @return
	 */
	public static String getStringFromObject(Object value) {
		if (value == null) {
			return null;
		}
		// return (String) value;
		return value.toString();
	}


	/**
	 * Get Decode URL value
	 * @param value
	 * @return
	 */
	public static String getDecodeUrl(String value) {
		// URL decode to change characteres
		return getDecodeUrl(
			value,
			StandardCharsets.UTF_8
		);
	}
	/**
	 * Get Decode URL value
	 * @param value
	 * @param charsetType
	 * @return
	 */
	public static String getDecodeUrl(String value, Charset charsetType) {
		if (Util.isEmpty(value, true)) {
			return value;
		}
		// URL decode to change characteres
		String parseValue = URLDecoder.decode(
			value,
			charsetType
		);
		return parseValue;
	}

}
