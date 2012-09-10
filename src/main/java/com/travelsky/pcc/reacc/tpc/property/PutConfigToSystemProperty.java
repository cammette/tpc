package com.travelsky.pcc.reacc.tpc.property;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 该类主要的目的是解决hornetq配置只能引用java系统的环境变量
 * 从spring的配置上下问题获取变量的值，然后设置到java系统的环境变量中
 * @author bingo
 *
 */
public class PutConfigToSystemProperty {
	private Map<String, String> propertiesMap = new HashMap<String, String>();

	public Map<String, String> getPropertiesMap() {
		return propertiesMap;
	}

	public void setPropertiesMap(Map<String, String> propertiesMap) {
		this.propertiesMap = propertiesMap;
	}
	
	public void putCofingToSystemProperty(){
		Set<String> keys = propertiesMap.keySet();
		for (String key : keys) {
			String value = propertiesMap.get(key);
			System.setProperty(key, value);
		}
	}
}
