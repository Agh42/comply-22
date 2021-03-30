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
        Map<String, Object> result = new HashMap<String, Object>(dynamicProperties.size());
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