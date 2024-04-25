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


	/**
	 * Validate if is valid data base boolean
	 * @param value
	 * @return
	 */
	public static boolean isValidDataBaseBoolean(String value) {
		if (Util.isEmpty(value, true)) {
			return false;
		}
		//	
		return value.equals("Y")
			|| value.equals("N")
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
	 * "Y" / "N" , "Yes" / "Not", "true" / "false"
	 * @param value
	 * @return "Y" / "N"
	 */
	public static String getBooleanToString(String value) {
		return getBooleanToString(
			getBooleanFromString(value)
		);
	}
	/**
	 * @deprecated
	 * @see {@link BooleanManager.getBooleanToTranslated(String)}
	 * @param value
	 * @param translated
	 * @return
	 */
	public static String getBooleanToString(String value, boolean translated) {
		if (translated) {
			return getBooleanToTranslated(value);
		}
		return getBooleanToString(
			getBooleanFromString(value)
		);
	}
	public static String getBooleanToString(boolean value) {
		String convertedValue = "N";
		if (value) {
			convertedValue = "Y";
		}
		return convertedValue;
	}

	public static String getBooleanToTranslated(boolean value) {
		return getBooleanToTranslated(
			value,
			Env.getAD_Language(Env.getCtx())
		);
	}
	public static String getBooleanToTranslated(boolean value, String language) {
		return Msg.getMsg(
			language,
			getBooleanToString(value)
		);
	}

	public static String getBooleanToTranslated(String value) {
		String acceptedValue = getBooleanToString(value);
		return getBooleanToTranslated(
			acceptedValue,
			Env.getAD_Language(Env.getCtx())
		);
	}
	public static String getBooleanToTranslated(String value, String language) {
		String acceptedValue = getBooleanToString(value);
		return Msg.getMsg(
			language,
			acceptedValue
		);
	}

}
