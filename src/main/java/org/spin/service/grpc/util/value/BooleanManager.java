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

import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;

/**
 * Class for handle Boolean values
 * @author Edwin Betancourt, EdwinBetanc0urt@outlook.com, https://github.com/EdwinBetanc0urt
 */
public class BooleanManager {

	/**
	 * Validate if is boolean
	 * @param value
	 * @return
	 */
	public static boolean isBoolean(String value) {
		if (Util.isEmpty(value, true)) {
			return false;
		}
		//	
		return value.equals("Y")
			|| value.equals("N")
			|| value.equals("Yes")
			|| value.equals("No")
			|| value.equals("true")
			|| value.equals("false")
		;
	}



	public static boolean getBooleanFromString(String stringValue) {
		if (Util.isEmpty(stringValue, true)) {
			return false;
		}
		if ("Y".equals(stringValue) || "Yes".equals(stringValue) || "true".equals(stringValue)) {
			return true;
		}
		return false;
	}



	/**
	 * "Y" / "N" , "Yes" / "Not"
	 * @param value
	 * @return
	 */
	public static String getBooleanToString(String value) {
		return getBooleanToString(
			getBooleanFromString(value),
			false
		);
	}
	public static String getBooleanToString(String value, boolean translated) {
		return getBooleanToString(
			getBooleanFromString(value),
			translated
		);
	}

	public static String getBooleanToString(boolean value) {
		return getBooleanToString(value, false);
	}
	public static String getBooleanToString(boolean value, boolean translated) {
		String convertedValue = "N";
		if (value) {
			convertedValue = "Y";
		}
		if (translated) {
			return Msg.getMsg(
				Env.getCtx(),
				convertedValue
			);
		}
		return convertedValue;
	}

}
