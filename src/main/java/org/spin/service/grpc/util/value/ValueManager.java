/*************************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                              *
 * This program is free software; you can redistribute it and/or modify it           *
 * under the terms version 2 or later of the GNU General Public License as published *
 * by the Free Software Foundation. This program is distributed in the hope          *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied        *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.                  *
 * See the GNU General Public License for more details.                              *
 * You should have received a copy of the GNU General Public License along           *
 * with this program; if not, write to the Free Software Foundation, Inc.,           *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                            *
 * For the text or an alternative of this public license, you may reach us           *
 * Copyright (C) 2012-2023 E.R.P. Consultores y Asociados, S.A. All Rights Reserved. *
 * Contributor(s): Yamel Senih www.erpya.com                                         *
 *************************************************************************************/
package org.spin.service.grpc.util.value;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.adempiere.core.domains.models.I_C_Order;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MLookupInfo;
import org.compiere.model.PO;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Language;
import org.compiere.util.NamePair;
import org.compiere.util.TimeUtil;
import org.compiere.util.Util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;

/**
 * Class for handle Values from and to client
 * @author Yamel Senih, ysenih@erpya.com , http://www.erpya.com
 * @author Edwin Betancourt, EdwinBetanc0urt@outlook.com, https://github.com/EdwinBetanc0urt
 */
public class ValueManager {

	private static final String TYPE_KEY = "type";
	private static final String VALUE_KEY = "value";
	//	Types
	public static final String TYPE_DATE = "date";
	public static final String TYPE_DATE_TIME = "date_time";
	public static final String TYPE_DECIMAL = "decimal";


	/**
	 * Get Value 
	 * @param value
	 * @return
	 */
	public static Value.Builder getValueFromObject(Object value) {
		Value.Builder builder = Value.newBuilder();
		if(value == null) {
			return getValueFromNull();
		}
		//	Validate value
		if(value instanceof BigDecimal) {
			return getValueFromBigDecimal((BigDecimal) value);
		} else if (value instanceof Integer) {
			return getValueFromInteger((Integer)value);
		} else if (value instanceof String) {
			return getValueFromString((String) value);
		} else if (value instanceof Boolean) {
			return getValueFromBoolean((Boolean) value);
		} else if(value instanceof Timestamp) {
			return getValueFromTimestamp((Timestamp) value);
		}
		//	
		return builder;
	}


	/**
	 * Get value from null
	 * @return
	 */
	public static Value.Builder getValueFromNull(Object nullValue) {
		return getValueFromNull();
	}
	/**
	 * Get value from null
	 * @return
	 */
	public static Value.Builder getValueFromNull() {
		return Value.newBuilder().setNullValue(
			com.google.protobuf.NullValue.NULL_VALUE
		);
	}


	/**
	 * Get default empty value
	 * @param referenceId
	 * @return
	 */
	public static Value.Builder getEmptyValueByReference(int referenceId) {
		if (referenceId <= 0) {
			return getValueFromNull();
		}
		if (DisplayType.isID(referenceId) || DisplayType.Integer == referenceId) {
			int emptyId = 0;
			return getValueFromInteger(emptyId);
		} else if (DisplayType.isNumeric(referenceId)) {
			return getValueFromBigDecimal(null);
		} else if (DisplayType.isDate(referenceId)) {
			return getValueFromTimestamp(null);
		} else if (DisplayType.isText(referenceId) || DisplayType.List == referenceId) {
			;
		} else if (DisplayType.YesNo == referenceId) {
			return getValueFromBoolean(false);
		}
		return getValueFromNull();
	}


	/**
	 * Get value from Integer
	 * @param value
	 * @return
	 */
	public static Value.Builder getValueFromInteger(Integer value) {
		if(value == null) {
			return getValueFromNull();
		}
		//	default
		return getValueFromInt(
			value.intValue()
		);
	}
	/**
	 * Get value from Int
	 * @param value
	 * @return
	 */
	public static Value.Builder getValueFromInt(int value) {
		//	default
		return Value.newBuilder().setNumberValue(value);
	}


	/**
	 * Get value from a string
	 * @param value
	 * @return
	 */
	public static Value.Builder getValueFromString(String value) {
		if (value == null) {
			return getValueFromNull();
		}
		return Value.newBuilder().setStringValue(
			StringManager.getValidString(
				value
			)
		);
	}


	/**
	 * Get value from a boolean value
	 * @param value
	 * @return
	 */
	public static Value.Builder getValueFromBoolean(boolean value) {
		return Value.newBuilder().setBoolValue(value);
	}
	/**
	 * Get value from a Boolean value
	 * @param value
	 * @return
	 */
	public static Value.Builder getValueFromBoolean(Boolean value) {
		if(value == null) {
			return getValueFromNull();
		}
		return getValueFromBoolean(value.booleanValue());
	}
	/**
	 * Get value from a String Boolean value ("Y" / "N")
	 * @param value
	 * @return
	 */
	public static Value.Builder getValueFromStringBoolean(String value) {
		return getValueFromBoolean(
			BooleanManager.getBooleanFromString(value)
		);
	}



	/**
	 * Get Value object from BigDecimal
	 * @param value
	 * @return
	 */
	public static Value.Builder getValueFromBigDecimal(BigDecimal value) {
		Struct.Builder decimalValue = Struct.newBuilder();
		decimalValue.putFields(
			TYPE_KEY,
			Value.newBuilder().setStringValue(TYPE_DECIMAL).build()
		);

		Value.Builder valueBuilder = Value.newBuilder();
		if (value == null) {
			valueBuilder = getValueFromNull();
		} else {
			String valueString = NumberManager.getBigDecimalToString(
				value
			);
			valueBuilder.setStringValue(
				valueString
			);
		}

		decimalValue.putFields(
			VALUE_KEY,
			valueBuilder.build()
		);
		return Value.newBuilder().setStructValue(decimalValue);
	}

	/**
	 * Get BigDecimal from Value object
	 * @param decimalValue
	 * @return
	 */
	public static BigDecimal getBigDecimalFromValue(Value decimalValue) {
		if(decimalValue == null
				|| decimalValue.hasNullValue()
				|| !(decimalValue.hasStringValue() || decimalValue.hasNumberValue() || decimalValue.hasStructValue())) {
			return null;
		}

		if (decimalValue.hasStructValue()) {
			Map<String, Value> values = decimalValue.getStructValue().getFieldsMap();
			if(values != null && !values.isEmpty()) {
				Value type = values.get(TYPE_KEY);
				if (type != null && TYPE_DECIMAL.equals(type.getStringValue())) {
					Value value = values.get(VALUE_KEY);
					if (value != null) {
						if (!Util.isEmpty(value.getStringValue(), false)) {
							return NumberManager.getBigDecimalFromString(
								value.getStringValue()
							);
						}
						if (value.hasNumberValue()) {
							return NumberManager.getBigDecimalFromDouble(
								value.getNumberValue()
							);
						}
					}
				}
			}
		}
		if (!Util.isEmpty(decimalValue.getStringValue(), false)) {
			return NumberManager.getBigDecimalFromString(
				decimalValue.getStringValue()
			);
		}
		if (decimalValue.hasNumberValue()) {
			return NumberManager.getBigDecimalFromDouble(
				decimalValue.getNumberValue()
			);
		}
		return null;
	}



	/**
	 * Get google.protobuf.Timestamp from Timestamp
	 * @param dateValue
	 * @return
	 */
	public static com.google.protobuf.Timestamp getTimestampFromDate(Timestamp dateValue) {
		Timestamp minDate = ValueManager.getDateFromTimestampDate(com.google.protobuf.util.Timestamps.MIN_VALUE);
		if (dateValue == null || minDate.equals(dateValue)) {
			// return com.google.protobuf.Timestamp.newBuilder().build(); // 1970-01-01T00:00:00Z
			// return com.google.protobuf.Timestamp.getDefaultInstance(); // 1970-01-01T00:00:00Z
			// return com.google.protobuf.util.Timestamps.EPOCH; // 1970-01-01T00:00:00Z
			return com.google.protobuf.util.Timestamps.MIN_VALUE; // 0001-01-01T00:00:00Z
		}
		return com.google.protobuf.util.Timestamps.fromMillis(
			dateValue.getTime()
		);
	}
	/**
	 * Get Date from value
	 * @param dateValue
	 * @return
	 */
	public static Timestamp getDateFromTimestampDate(com.google.protobuf.Timestamp dateValue) {
		if(dateValue == null || (dateValue.getSeconds() == 0 && dateValue.getNanos() == 0)) {
			return null;
		}
		LocalDateTime dateTime = LocalDateTime.ofEpochSecond(
			dateValue.getSeconds(),
			dateValue.getNanos(),
			ZoneOffset.UTC
		);
		return Timestamp.valueOf(dateTime);
	}

	/**
	 * Get Date from a value
	 * @param dateValue
	 * @return
	 */
	public static Timestamp getTimestampFromValue(Value dateValue) {
		if(dateValue == null
				|| dateValue.hasNullValue()
				|| !(dateValue.hasStringValue() || dateValue.hasNumberValue() || dateValue.hasStructValue())) {
			return null;
		}

		if (dateValue.hasStructValue()) {
			Map<String, Value> values = dateValue.getStructValue().getFieldsMap();
			if(values == null) {
				return null;
			}
			Value type = values.get(TYPE_KEY);
			Value value = values.get(VALUE_KEY);
			if(type == null || value == null) {
				return null;
			}
			String validType = StringManager.getValidString(
				type.getStringValue()
			);
			String validValue = StringManager.getValidString(
				value.getStringValue()
			);
			if((!validType.equals(TYPE_DATE)
					&& !validType.equals(TYPE_DATE_TIME))
					|| validValue.length() == 0) {
				return null;
			}
			return TimeManager.getTimestampFromString(
				validValue
			);
		}
		if (dateValue.hasStringValue()) {
			return TimeManager.getTimestampFromString(
				dateValue.getStringValue()
			);
		}
		if (dateValue.hasNumberValue()) {
			return TimeManager.getTimestampFromDouble(
				dateValue.getNumberValue()
			);
		}
		return null;
	}


	/**
	 * Get value from a date
	 * @param value
	 * @return
	 */
	public static Value.Builder getValueFromTimestamp(Timestamp value) {
		Struct.Builder date = Struct.newBuilder();
		date.putFields(
			TYPE_KEY,
			Value.newBuilder().setStringValue(
				TYPE_DATE
			).build()
		);

		Value.Builder valueBuilder = Value.newBuilder();
		if (value == null) {
			valueBuilder = getValueFromNull();
		} else {
			String valueString = TimeManager.getTimestampToString(
				value
			);
			valueBuilder.setStringValue(
				valueString
			);
		}

		date.putFields(
			VALUE_KEY,
			valueBuilder.build()
		);
		return Value.newBuilder().setStructValue(date);
	}


	/**
	 * Get String from a value
	 * @param value
	 * @return
	 */
	public static String getStringFromValue(Value value) {
		return getStringFromValue(value, false);
	}
	/**
	 * Get String from a value
	 * @param value
	 * @param uppercase
	 * @return
	 */
	public static String getStringFromValue(Value value, boolean uppercase) {
		if (value == null) {
			return null;
		}
		String stringValue = value.getStringValue();
		if(Util.isEmpty(stringValue, true)) {
			return null;
		}
		//	To Upper case
		if(uppercase) {
			stringValue = stringValue.toUpperCase();
		}
		return stringValue;
	}


	/**
	 * Get integer from a value
	 * @param value
	 * @return
	 */
	public static int getIntegerFromValue(Value value) {
		if (value == null) {
			return 0;
		}
		int intValue = (int) value.getNumberValue();
		if (intValue == 0 && value.hasStringValue()) {
			intValue = NumberManager.getIntFromString(
				value.getStringValue()
			);
		}
		return intValue;
	}
	
	/**
	 * Get Boolean from a value
	 * @param value
	 * @return
	 */
	public static boolean getBooleanFromValue(Value value) {
		if (value == null) {
			return false;
		}
		if (!Util.isEmpty(value.getStringValue(), true)) {
			return BooleanManager.getBooleanFromString(
				value.getStringValue()
			);
		}

		return value.getBoolValue();
	}

	/**
	 * Get Value from reference
	 * @param value
	 * @param referenceId reference of value
	 * @return
	 */
	public static Value.Builder getValueFromReference(Object value, int referenceId) {
		Value.Builder builderValue = Value.newBuilder();
		if(value == null) {
			// getEmptyValueByReference(referenceId);
			return getValueFromNull();
		}
		if (referenceId <= 0) {
			return getValueFromObject(value);
		}
		//	Validate values
		if (DisplayType.isID(referenceId) || DisplayType.Integer == referenceId) {
			Integer integerValue = NumberManager.getIntegerFromObject(
				value
			);
			if (integerValue == null && (DisplayType.Search == referenceId || DisplayType.Table == referenceId)) {
				// no casteable for integer, as `AD_Language`, `EntityType`
				return getValueFromObject(value);
			}
			return getValueFromInteger(integerValue);
		} else if(DisplayType.isNumeric(referenceId)) {
			BigDecimal bigDecimalValue = NumberManager.getBigDecimalFromObject(
				value
			);
			return getValueFromBigDecimal(bigDecimalValue);
		} else if(DisplayType.YesNo == referenceId) {
			if (value instanceof String) {
				String stringValue = StringManager.getStringFromObject(
					value
				);
				return getValueFromStringBoolean(stringValue);
			}
			return getValueFromBoolean((Boolean) value);
		} else if(DisplayType.isDate(referenceId)) {
			Timestamp dateValue = TimeManager.getTimestampFromObject(
				value
			);
			return getValueFromTimestamp(dateValue);
		} else if(DisplayType.isText(referenceId) || DisplayType.List == referenceId) {
			String stringValue = StringManager.getStringFromObject(
				value
			);
			return getValueFromString(
				stringValue
			);
		} else if (DisplayType.Button == referenceId) {
			if (value instanceof Integer) {
				return getValueFromInteger((Integer) value);
			} else if (value instanceof Long) {
				Integer integerValue = NumberManager.getIntegerFromLong(
					(Long) value
				);
				return getValueFromInt(integerValue);
			} else if(value instanceof BigDecimal) {
				Integer bigDecimalValue = NumberManager.getIntegerFromBigDecimal(
					(BigDecimal) value
				);
				return getValueFromInteger(bigDecimalValue);
			} else if (value instanceof String) {
				String stringValue = StringManager.getStringFromObject(
					value
				);
				return getValueFromString(
					stringValue
				);
			}
			return getValueFromObject(value);
		} else {
			builderValue = getValueFromObject(value);
		}
		//
		return builderValue;
	}


	/**
	 * Get Display Value from reference
	 * @param value
	 * @param columnName data base column name
	 * @param displayTypeId display type of field
	 * @param referenceValueId reference of list or table
	 * @return
	 */
	public static String getDisplayedValueFromReference(Object value, String columnName, int displayTypeId, int referenceValueId) {
		return getDisplayedValueFromReference(
			Env.getCtx(),
			columnName,
			displayTypeId,
			referenceValueId
		);
	}
	public static String getDisplayedValueFromReference(Properties context, Object value, String columnName, int displayTypeId, int referenceValueId) {
		String displayedValue = null;
		if (value == null) {
			return displayedValue;
		}
		if (displayTypeId <= 0) {
			return displayedValue;
		}
		if (context == null) {
			context = Env.getCtx();
		}
		if (DisplayType.isText(displayTypeId)) {
			;
		} else if (displayTypeId == DisplayType.YesNo) {
			displayedValue = BooleanManager.getBooleanToTranslated(
				value.toString(),
				Env.getAD_Language(context)
			);
		} else if (displayTypeId == DisplayType.Integer) {
			// necessary condition do not to enter the condition for decimal struct
			Language language = Env.getLanguage(context);
			DecimalFormat intFormat = DisplayType.getNumberFormat(
				DisplayType.Integer,
				language
			);
			displayedValue = intFormat.format(
				Integer.valueOf(value.toString())
			);
		} else if (DisplayType.isNumeric(displayTypeId)) {
			if (I_C_Order.COLUMNNAME_ProcessedOn.equals(columnName)) {
				if (value.toString().indexOf(".") > 0) {
					value = value.toString().substring(0, value.toString().indexOf("."));
				}
				long longValue = new BigDecimal(
					value.toString()
				).longValue();
				displayedValue = TimeUtil.formatElapsed(
					System.currentTimeMillis() - longValue
				);
			} else {
				Language language = Env.getLanguage(context);
				DecimalFormat numberFormat = DisplayType.getNumberFormat(
					displayTypeId,
					language
				);
				displayedValue = numberFormat.format(
					NumberManager.getBigDecimalFromString(
						value.toString()
					)
				);
			}
		} else if (DisplayType.isDate(displayTypeId)) {
			Language language = Env.getLanguage(context);
			SimpleDateFormat dateTimeFormat = DisplayType.getDateFormat(
				DisplayType.DateTime,
				// displayTypeId,
				language
			);
			displayedValue = dateTimeFormat.format(
				Timestamp.valueOf(
					value.toString()
				)
			);
		} else if (DisplayType.isLookup(displayTypeId) && displayTypeId != DisplayType.Button && displayTypeId != DisplayType.List) {
			Language language = Env.getLanguage(context);
			MLookupInfo lookupInfo = MLookupFactory.getLookupInfo(
				context, 0,
				0, displayTypeId, language, columnName,
				referenceValueId, false,
				null, false
			);
			MLookup lookup = new MLookup(lookupInfo, 0);
			NamePair pp = lookup.get(value);
			if (pp != null) {
				displayedValue = pp.getName();
			}
		} else if((DisplayType.Button == displayTypeId || DisplayType.List == displayTypeId) && referenceValueId != 0) {
			Language language = Env.getLanguage(context);
			MLookupInfo lookupInfo = MLookupFactory.getLookup_List(
				language,
				referenceValueId
			);
			MLookup lookup = new MLookup(lookupInfo, 0);
			if (value != null) {
				Object key = value;
				NamePair pp = lookup.get(key);
				if (pp != null) {
					displayedValue = pp.getName();
				}
			}
		} else if (DisplayType.isLOB(displayTypeId)) {
			;
		}
		return displayedValue;
	}

	/**
	 * JSON as string to Map(String, Object)
	 * Ej: `{"AD_Field_ID":123,"AD_Column_ID":345}`
	 * @param jsonValues
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> convertJsonStringToMap(String jsonValues) {
		Map<String, Object> fillValues = new HashMap<String, Object>();
		if (Util.isEmpty(jsonValues, true)) {
			return fillValues;
		}
		try {
			ObjectMapper fileMapper = new ObjectMapper();
			fillValues = fileMapper.readValue(
				jsonValues,
				HashMap.class
			);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fillValues;
	}

	/**
	 * Convert Selection values from gRPC to ADempiere values
	 * @param values
	 * @return
	 */
	public static Map<String, Object> convertValuesMapToObjects(Map<String, Value> values) {
		Map<String, Object> convertedValues = new HashMap<>();
		if (values == null || values.size() <= 0) {
			return convertedValues;
		}
		values.keySet().forEach(keyValue -> {
			Value valueBuilder = values.get(keyValue);
			Object valueItem = ValueManager.getObjectFromValue(valueBuilder);
			convertedValues.put(
				keyValue,
				valueItem
			);
		});
		//	
		return convertedValues;
	}

	/**
	 * Convert Selection values from gRPC to ADempiere values
	 * @param values
	 * @param displayTypeColumns Map(ColumnName, DisplayType)
	 * @return
	 */
	public static Map<String, Object> convertValuesMapToObjects(Map<String, Value> values, Map<String, Integer> displayTypeColumns) {
		Map<String, Object> convertedValues = new HashMap<>();
		if (values == null || values.size() <= 0) {
			return convertedValues;
		}
		if (displayTypeColumns == null || values.size() <= 0) {
			return ValueManager.convertValuesMapToObjects(
				values
			);
		}
		values.keySet().forEach(keyValue -> {
			Value valueBuilder = values.get(keyValue);
			Object valueItem = getObjectFromValue(valueBuilder);

			Integer displayType = displayTypeColumns.get(keyValue);
			if (displayType != null && displayType.intValue() > 0) {
				valueItem = ValueManager.getObjectFromReference(
					valueBuilder,
					displayType.intValue()
				);
			}

			convertedValues.put(
				keyValue,
				valueItem
			);
		});
		//	
		return convertedValues;
	}

	/**
	 * Convert Selection values from gRPC to ADempiere values
	 * @param values
	 * @return
	 */
	public static Value.Builder convertObjectMapToStruct(Map<String, Object> values) {
		Value.Builder convertedValues = Value.newBuilder();
		Struct.Builder mapValue = Struct.newBuilder();

		if (values != null && values.size() > 0) {
			values.keySet().forEach(keyValue -> {
				Object valueItem = values.get(keyValue);
				Value.Builder valueBuilder = getValueFromObject(
					valueItem
				);
				mapValue.putFields(
					keyValue,
					valueBuilder.build()
				);
			});
		}

		//	
		convertedValues.setStructValue(mapValue);
		return convertedValues;
	}
	
	/**
	 * Default get value from type
	 * @param valueToConvert
	 * @return
	 */
	public static Object getObjectFromValue(Value valueToConvert) {
		return getObjectFromValue(valueToConvert, false);
	}
	
	/**
	 * Get value from parameter type
	 * @param value
	 * @return
	 */
	public static Object getObjectFromValue(Value value, boolean uppercase) {
		if(value == null
				|| value.hasNullValue()) {
			return null;
		}
		if(value.hasStringValue()) {
			return getStringFromValue(value, uppercase);
		}
		if(value.hasNumberValue()) {
			return (int) value.getNumberValue();
		}
		if(value.hasBoolValue()) {
			return value.getBoolValue();
		}
		if(value.hasStructValue()) {
			if(isDecimalValue(value)) {
				return getBigDecimalFromValue(value);
			} else if(isDateValue(value)) {
				return getTimestampFromValue(value);
			}
		}
		return null;
	}
	
	/**
	 * Validate if a value is date
	 * @param value
	 * @return
	 */
	public static boolean isDateValue(Value value) {
		Map<String, Value> values = value.getStructValue().getFieldsMap();
		if(values == null) {
			return false;
		}
		Value type = values.get(TYPE_KEY);
		if(type == null) {
			return false;
		}
		String validType = StringManager.getValidString(
			type.getStringValue()
		);
		return validType.equals(TYPE_DATE) || validType.equals(TYPE_DATE_TIME);
	}
	
	/**
	 * Validate if is a decimal value
	 * @param value
	 * @return
	 */
	public static boolean isDecimalValue(Value value) {
		if (value == null) {
			return false;
		}
		Map<String, Value> values = value.getStructValue().getFieldsMap();
		if(values == null) {
			return false;
		}
		Value type = values.get(TYPE_KEY);
		if(type == null) {
			return false;
		}
		String validType = StringManager.getValidString(
			type.getStringValue()
		);
		return validType.equals(TYPE_DECIMAL);
	}
	
	/**
	 * Get Object from value based on reference
	 * @param value
	 * @param referenceId
	 * @return
	 */
	public static Object getObjectFromReference(Value value, int referenceId) {
		if(value == null) {
			return null;
		}
		if (referenceId <= 0) {
			return getObjectFromValue(value);
		}
		//	Validate values
		if(DisplayType.isID(referenceId) || DisplayType.Integer == referenceId) {
			if (DisplayType.Search == referenceId || DisplayType.Table == referenceId) {
				Object lookupValue = getObjectFromValue(value);
				try {
					// casteable for integer, except `AD_Language`, `EntityType`
					lookupValue = Integer.valueOf(
						lookupValue.toString()
					);
				} catch (Exception e) {
				}
				return lookupValue;
			}
			return getIntegerFromValue(value);
		} else if(DisplayType.isNumeric(referenceId)) {
			return getBigDecimalFromValue(value);
		} else if(DisplayType.YesNo == referenceId) {
			return getBooleanFromValue(value);
		} else if(DisplayType.isDate(referenceId)) {
			return getTimestampFromValue(value);
		} else if(DisplayType.isText(referenceId) || DisplayType.List == referenceId) {
			return getStringFromValue(value);
		} else if (DisplayType.Button == referenceId) {
			return getObjectFromValue(value);
		}
		//	
		return getObjectFromValue(value);
	}
	
	/**
	 * Is lookup include location
	 * @param displayType
	 * @return
	 */
	public static boolean isLookup(int displayType) {
		return DisplayType.isLookup(displayType)
				|| DisplayType.Account == displayType
				|| DisplayType.Location == displayType
				|| DisplayType.Locator == displayType
				|| DisplayType.PAttribute == displayType;
	}
	
	/**
	 * Convert null on ""
	 * @param value
	 * @deprecated Use {@link StringManager#getValidString()} instead.
	 * @return
	 */
	public static String validateNull(String value) {
		return StringManager.getValidString(
			value
		);
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


	/**
	 * Get translation if is necessary
	 * @param object
	 * @param columnName
	 * @return
	 */
	public static String getTranslation(PO object, String columnName) {
		if(object == null) {
			return null;
		}
		if(Language.isBaseLanguage(Env.getAD_Language(Env.getCtx()))) {
			return object.get_ValueAsString(columnName);
		}
		//	
		return object.get_Translation(columnName);
	}

}
