/************************************************************************************
 * Copyright (C) 2012-2018 E.R.P. Consultores y Asociados, C.A.                     *
 * Contributor(s): Yamel Senih ysenih@erpya.com                                     *
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
package org.spin.server.config;

import org.compiere.util.Ini;

/**
 * Database configuration
 * @author Yamel Senih
 *
 */
public class DatabaseConfig {
	public static final String CONFIG_DB_IDLE_TIMEOUT = "DB|CONFIG_DB_IDLE_TIMEOUT";
	public static final String CONFIG_DB_MINIMUM_IDLE = "DB|CONFIG_DB_MINIMUM_IDLE";
	public static final String CONFIG_DB_MAXIMUM_POOL_SIZE = "DB|CONFIG_DB_MAXIMUM_POOL_SIZE";
	public static final String CONFIG_DB_CONNECTION_TIMEOUT = "DB|CONFIG_DB_CONNECTION_TIMEOUT";
	public static final String CONFIG_DB_MAX_LIFETIME = "DB|CONFIG_DB_MAX_LIFETIME";
	public static final String CONFIG_DB_CONNECTION_TEST_QUERY = "DB|CONFIG_DB_CONNECTION_TEST_QUERY";
	public static final String CONFIG_DB_KEEPALIVE_TIME = "DB|CONFIG_DB_KEEPALIVE_TIME";
	
	public static void setConnectionTestQuery(String query) {
		if(query != null) {
			Ini.setProperty(CONFIG_DB_CONNECTION_TEST_QUERY, query);
		}
	}
	
	public static void setIdleTimeout(long value) {
		if(value >= 0) {
			Ini.setProperty(CONFIG_DB_IDLE_TIMEOUT, String.valueOf(value));
		}
	}

	public static void setConnectionTimeout(long value) {
		if(value >= 0) {
			Ini.setProperty(CONFIG_DB_CONNECTION_TIMEOUT, String.valueOf(value));
		}
	}
	
	public static void setMinimumIdle(int value) {
		if(value > 0) {
			Ini.setProperty(CONFIG_DB_MINIMUM_IDLE, String.valueOf(value));
		}
	}
	
	public static void setMaximumPoolSize(int value) {
		if(value >= 0) {
			Ini.setProperty(CONFIG_DB_MAXIMUM_POOL_SIZE, String.valueOf(value));
		}
	}
	
	public static void setMaxLifetime(long value) {
		if(value >= 0) {
			Ini.setProperty(CONFIG_DB_MAX_LIFETIME, String.valueOf(value));
		}
	}
	
	public static void setKeepaliveTime(long value) {
		if(value >= 0) {
			Ini.setProperty(CONFIG_DB_KEEPALIVE_TIME, String.valueOf(value));
		}
	}
}
