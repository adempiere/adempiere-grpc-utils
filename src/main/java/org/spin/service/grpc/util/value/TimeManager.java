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

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Util;

/**
 * Class for handle Time (TimesTamp, Date, Long) values
 * @author Edwin Betancourt, EdwinBetanc0urt@outlook.com, https://github.com/EdwinBetanc0urt
 */
public class TimeManager {

	/**	Date format	*/
	private static final String TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";
	private static final String DATE_FORMAT = "yyyy-MM-dd";


	public static SimpleDateFormat FORMAT = DisplayType.getDateFormat(
		DisplayType.DateTime,
		Env.getLanguage(Env.getCtx())
	);


	/**
	 * Validate Date
	 * @param value
	 * @return
	 */
	public static boolean isDate(String value) {
		return getTimestampFromString(value) != null;
	}


	public static Timestamp getTimestampFromObject(Object value) {
		Timestamp dateValue = null;
		if (value == null) {
			return dateValue;
		}
		if (value instanceof Long) {
			dateValue = TimeManager.getTimestampFromLong(
				(Long) value
			);
		} else if (value instanceof Integer) {
			dateValue = TimeManager.getTimestampFromInteger(
				(Integer) value
			);
		} else if (value instanceof String) {
			dateValue = TimeManager.getTimestampFromString(
				(String) value
			);
		} else if (value instanceof Timestamp) {
			dateValue = (Timestamp) value;
		}
		return dateValue;
	}


	/**
	 * Convert string to dates
	 * @param stringValue
	 * @return
	 */
	public static Timestamp getTimestampFromString(String stringValue) {
		if (Util.isEmpty(stringValue, true)) {
			return null;
		}
		String format = DATE_FORMAT;
		if(stringValue.length() == TIME_FORMAT.length()) {
			format = TIME_FORMAT;
		} else if(stringValue.length() != DATE_FORMAT.length()) {
			// throw new AdempiereException(
			// 	"Invalid date format, please use some like this: \"" + DATE_FORMAT + "\" or \"" + TIME_FORMAT + "\""
			// );
		}
		Date validDate = null;
		try {
			SimpleDateFormat dateConverter = new SimpleDateFormat(format);
			validDate = dateConverter.parse(stringValue);
		} catch (ParseException e) {
			// throw new AdempiereException(e);
		}

		//	Convert
		if(validDate != null) {
			return getTimestampFromLong(
				validDate.getTime()
			);
		}
		return null;
	}

	/**
	 * Convert Timestamp to String
	 * @param date
	 * @return
	 */
	public static String getTimestampToString(Timestamp date) {
		if(date == null) {
			return null;
		}
		return getTimestampToString(date, TIME_FORMAT);
	}

	/**
	 * Convert Timestamp to String
	 * @param date
	 * @param pattern default `yyyy-MM-dd hh:mm:ss`
	 * @return
	 */
	public static String getTimestampToString(Timestamp date, String pattern) {
		if(date == null) {
			return null;
		}
		if (Util.isEmpty(pattern, true)) {
			pattern = TIME_FORMAT;
		}
		return new SimpleDateFormat(pattern).format(date);
	}

	/**
	 * Convert Timestamp to String
	 * @param date
	 * @return
	 */
	public static Timestamp getTimestampFromDate(Date value) {
		if(value == null) {
			return null;
		}
		return new Timestamp(
			value.getTime()
		);
	}

	public static Timestamp getTimestampFromLong(long value) {
		if (value > 0) {
			return new Timestamp(value);
		}
		return null;
	}

	public static Timestamp getTimestampFromDouble(double value) {
		if (value > 0.0) {
			return new Timestamp(
				(int) value
			);
		}
		return null;
	}

	public static Timestamp getTimestampFromInteger(int value) {
		if (value > 0) {
			return new Timestamp(value);
		}
		return null;
	}


	/**
	 * Get long from Timestamp
	 * @param value
	 * @return
	 */
	public static long getLongFromTimestamp(Timestamp value) {
		if (value == null) {
			return 0L;
		}
		return value.getTime();
	}


	/**
	 * @link ValueManager.getDateFromTimestampDate
	 * @param value
	 * @return
	 * @see {@link ValueManager.getDateFromTimestampDate(com.google.protobuf.Timestamp)}
	 */
	@Deprecated
	public static Timestamp convertValueToDate(com.google.protobuf.Timestamp value) {
		return ValueManager.getDateFromTimestampDate(value);
	}

	public static com.google.protobuf.Timestamp convertDateToValue(Timestamp value) {
		return ValueManager.getTimestampFromDate(value);
	}

}
