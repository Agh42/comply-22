/*
 * Copyright (c) 2022 Alexander Koderman <a@koderman.de>.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package io.cstool.comply22.adapter;

import org.neo4j.driver.internal.value.FloatValue;
import org.neo4j.driver.internal.value.IntegerValue;
import org.neo4j.driver.internal.value.ValueAdapter;

import java.util.HashMap;
import java.util.Map;

public class DynPropsSerializer {

    /*
     * We need to provide mappers from certain SDN types to basic types
     * or Jackson will trip on them during serialization.
     */
    public Map<String, Object> serialize(Map<String, Object> dynamicProperties) {
        Map<String, Object> result = new HashMap<>(dynamicProperties.size());
        dynamicProperties.forEach((k, v) -> {
            if (v instanceof IntegerValue)
                result.put(k, ((IntegerValue) v).asInt());
            else if (v instanceof FloatValue)
                result.put(k, ((FloatValue) v).asDouble());
            else if (v instanceof ValueAdapter)
                result.put(k, ((ValueAdapter) v).asString());
            else
                result.put(k, v);
        });
        return result;
    }
}