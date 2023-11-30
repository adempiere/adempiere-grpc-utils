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

import static com.google.protobuf.util.Timestamps.fromMillis;

import java.io.IOException;
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
import java.util.Optional;

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
		if (DisplayType.isID(referenceId) || DisplayType.Integer == referenceId) {
			int emptyId = 0;
			return getValueFromInteger(emptyId);
		} else if (DisplayType.isNumeric(referenceId)) {
			return getValueFromBigDecimal(null);
		} else if (DisplayType.isDate(referenceId)) {
			return getValueFromTimestamp(null);
		} else if (DisplayType.isText(referenceId)) {
			return getValueFromString(null);
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
			validateNull(value)
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
	public static com.google.protobuf.Timestamp getTimestampFromDate(Timestamp value) {
		if (value == null) {
			// return com.google.protobuf.Timestamp.newBuilder().build(); // 1970-01-01T00:00:00Z
			// return com.google.protobuf.Timestamp.getDefaultInstance(); // 1970-01-01T00:00:00Z
			// return com.google.protobuf.util.Timestamps.EPOCH; // 1970-01-01T00:00:00Z
			return com.google.protobuf.util.Timestamps.MIN_VALUE; // 0001-01-01T00:00:00Z
		}
		return fromMillis(value.getTime());
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
	 * @deprecated {@link #getTimestampFromValue(Value)}
	 * @param dateValue
	 * @return
	 */
	public static Timestamp getDateFromValue(Value dateValue) {
		return getTimestampFromValue(dateValue);
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
		Map<String, Value> values = dateValue.getStructValue().getFieldsMap();
		if(values == null) {
			return null;
		}
		Value type = values.get(TYPE_KEY);
		Value value = values.get(VALUE_KEY);
		if(type == null
				|| value == null) {
			return null;
		}
		String validType = Optional.ofNullable(type.getStringValue()).orElse("");
		String validValue = Optional.ofNullable(value.getStringValue()).orElse("");
		if((!validType.equals(TYPE_DATE)
				&& !validType.equals(TYPE_DATE_TIME))
				|| validValue.length() == 0) {
			return null;
		}
		return TimeManager.getTimestampFromString(
			validValue
		);
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
	 * @param uppercase
	 * @return
	 */
	public static String getStringFromValue(Value value, boolean uppercase) {
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
	 * Get String from a value
	 * @param value
	 * @return
	 */
	public static String getStringFromValue(Value value) {
		return getStringFromValue(value, false);
	}
	
	/**
	 * Get integer from a value
	 * @param value
	 * @return
	 */
	public static int getIntegerFromValue(Value value) {
		return (int) value.getNumberValue();
	}
	
	/**
	 * Get Boolean from a value
	 * @param value
	 * @return
	 */
	public static boolean getBooleanFromValue(Value value) {
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
		//	Validate values
		if (DisplayType.isID(referenceId) || DisplayType.Integer == referenceId) {
			Integer integerValue = NumberManager.getIntegerFromObject(
				value
			);
			return getValueFromInteger(integerValue);
		} else if(DisplayType.isNumeric(referenceId)) {
			BigDecimal bigDecimalValue = NumberManager.getBigDecimalFromObject(
				value
			);
			return getValueFromBigDecimal(bigDecimalValue);
		} else if(DisplayType.YesNo == referenceId) {
			if (value instanceof String) {
				return getValueFromStringBoolean((String) value);
			}
			return getValueFromBoolean((Boolean) value);
		} else if(DisplayType.isDate(referenceId)) {
			Timestamp dateValue = TimeManager.getTimestampFromObject(
				value
			);
			return getValueFromTimestamp(dateValue);
		} else if(DisplayType.isText(referenceId)) {
			return getValueFromString((String) value);
		} else if (DisplayType.List == referenceId) {
	 		// TODO: Verify if text and list y same data type
			return getValueFromObject(value);
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
				return getValueFromString(
					(String) value
				);
			}
			return getValueFromObject(value);
		}
		//
		return builderValue;
	}

	public static String getDisplayedValueFromReference(Object value, String columnName, int displayTypeId, int referenceValueId) {
		String displayedValue = null;

		if (value == null) {
			return displayedValue;
		}
		if (DisplayType.isText (displayTypeId)) {
			;
		} else if (displayTypeId == DisplayType.YesNo) {
			displayedValue = BooleanManager.getBooleanToString(
				value.toString(),
				true
			);
		} else if (displayTypeId == DisplayType.Integer) {
			DecimalFormat intFormat = DisplayType.getNumberFormat(
				DisplayType.Integer,
				Env.getLanguage(Env.getCtx())
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
				DecimalFormat numberFormat = DisplayType.getNumberFormat(
					displayTypeId,
					Env.getLanguage(Env.getCtx())
				);
				displayedValue = numberFormat.format(
					NumberManager.getBigDecimalFromString(
						value.toString()
					)
				);
			}
		} else if (DisplayType.isDate(displayTypeId)) {
			SimpleDateFormat dateTimeFormat = DisplayType.getDateFormat(
				DisplayType.DateTime,
				Env.getLanguage(Env.getCtx())
			);
			displayedValue = dateTimeFormat.format(
				Timestamp.valueOf(
					value.toString()
				)
			);
		} else if (DisplayType.isLookup(displayTypeId) && displayTypeId != DisplayType.Button && displayTypeId != DisplayType.List) {
			Language language = Env.getLanguage(Env.getCtx());
			MLookupInfo lookupInfo = MLookupFactory.getLookupInfo(Env.getCtx(), 0, 0, displayTypeId, language, columnName, referenceValueId, false, null, false);
			MLookup lookup = new MLookup(lookupInfo, 0);
			NamePair pp = lookup.get(value);
			if (pp != null) {
				displayedValue = pp.getName();
			}
		} else if((DisplayType.Button == displayTypeId || DisplayType.List == displayTypeId) && referenceValueId != 0) {
			MLookupInfo lookupInfo = MLookupFactory.getLookup_List(
				Env.getLanguage(Env.getCtx()),
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
	 * JSON as string to Map<key, value>
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
		} catch (IOException e) {
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
			Object valueItem = getObjectFromValue(valueBuilder);
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
		String validType = Optional.ofNullable(type.getStringValue()).orElse("");
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
		String validType = Optional.ofNullable(
			type.getStringValue()
		).orElse("");
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
		//	Validate values
		if(isLookup(referenceId)
				|| DisplayType.isID(referenceId)) {
			return getObjectFromValue(value);
		} else if(DisplayType.Integer == referenceId) {
			return getIntegerFromValue(value);
		} else if(DisplayType.isNumeric(referenceId)) {
			return getBigDecimalFromValue(value);
		} else if(DisplayType.YesNo == referenceId) {
			return getBooleanFromValue(value);
		} else if(DisplayType.isDate(referenceId)) {
			return getTimestampFromValue(value);
		} else if(DisplayType.isText(referenceId)) {
			return getStringFromValue(value);
		}
		//	
		return null;
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
	 * @return
	 */
	public static String validateNull(String value) {
		if(value == null) {
			value = "";
		}
		//	
		return value;
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
