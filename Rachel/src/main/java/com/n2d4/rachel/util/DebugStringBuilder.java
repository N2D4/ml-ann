package com.n2d4.rachel.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A builder for readable JSON strings to be used in the <a href="http://www.example.com">debugging tool</a>.
 * <p>
 * Use this in the {@link java.lang.Object#toString() .toString()} method of a class that implements {@link Util.Debuggable Debuggable}.
 * <p>
 * This class has been designed to work with Eclipse' "Generate .toString()" feature (menu bar > source).
 * 
 * @see DebugStringBuilder.Debuggable
 */
public class DebugStringBuilder {
	
	public static final String[] invalidPrefixes = {"get_", "_"};
	public static final String[] invalidSuffixes = {"()", "_"};

	private static final Map<String, DebugStringBuilderImplementation> custImpsStrings = new HashMap<String, DebugStringBuilderImplementation>() {
		private static final long serialVersionUID = 1L; {
			put("org.nd4j.linalg.api.ndarray.INDArray", DebugStringBuilderImplementation.DEFAULT);
		}
	};
	protected static final Map<Class<?>, DebugStringBuilderImplementation> customImplementations = getCustomImplementations();
	
	private Map<String, String> contents = new HashMap<String, String>();
	
	public DebugStringBuilder() {
		add("_type", "Unknown", true);
		add("_interfaces", new Class<?>[0], true);
	}
	
	public DebugStringBuilder(Object obj) {
		add("_type", obj.getClass(), true);
		add("_interfaces", obj.getClass().getInterfaces(), true);
	}
	
	public DebugStringBuilder add(String name, Object obj) {
		return add(name, obj, false);
	}
	
	protected DebugStringBuilder add(String name, Object obj, boolean forceName) {
		name = ugly_case(name);
		name = jsonify(name.replaceAll("_+", "_"));
		
		boolean cont = true;
		while (!forceName && cont) {
			cont = false;
			for (String inv : invalidPrefixes) {
				if (name.toLowerCase().startsWith(inv)) {
					name = name.substring(inv.length());
					cont = true;
				}
			}
		}
		
		cont = true;
		while (!forceName && cont) {
			cont = false;
			for (String inv : invalidSuffixes) {
				if (name.toLowerCase().endsWith(inv)) {
					name = name.substring(0, name.length() - inv.length());
					cont = true;
				}
			}
		}
		
		
		contents.put(name, getJSONValue(obj));
		return this;
	}
	
	protected static String getJSONValue(Object obj) {
		if (obj == null) {
			return "null";
		}
		
		return getStringBuilderImplementation(obj).getForObject(obj);
	}
	
	protected static <T extends Object> DebugStringBuilderImplementation getStringBuilderImplementation(T obj) {
		for (Entry<Class<?>, DebugStringBuilderImplementation> entry : customImplementations.entrySet()) {
			if (entry.getKey().isAssignableFrom(obj.getClass())) return entry.getValue();
		}
		
		if (obj instanceof Number || obj instanceof Boolean || obj instanceof Debuggable) {
			return DebugStringBuilderImplementation.DEBUGGABLE;
		} else if (obj instanceof Class) {
			return DebugStringBuilderImplementation.CLASSES;
		} else if (Iterables.hasIterator(obj)) {
			return DebugStringBuilderImplementation.ITERABLES;
		} else {
			return DebugStringBuilderImplementation.DEFAULT;
		}
	}
	
	protected static final String jsonify(String s) {
		return s.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\"").replaceAll("\n", "\\\\n");
	}
	
	/**
	 * @see <a href="http://pastebin.com/raw/uW0ae0L9">Microsoft</a>
	 */
	protected String ugly_case(String s) {
		return s.replaceAll("([a-z]+)([A-Z])|([A-Z])([a-z]+)", "$1_$2$3$4");
	}
	
	
	@Override
	public String toString() {
		String result = "";
		Iterator<String> iterator = contents.keySet().iterator();
		while (iterator.hasNext()) {
			String next = iterator.next();
			result += "\"" + next + "\": " + contents.get(next);
			if (iterator.hasNext())
				result += ", ";
		}
		
		return "{" + result + "}";
	}
	
	
	
	
	
	
	/**
	 * An interface to indicate that the return value from the {@link java.lang.Object#toString() .toString()} method is valid JSON to be used in the <a href="http://www.example.com">debugging tool</a>.
	 * 
	 * @see DebugStringBuilder
	 */
	public static interface Debuggable {
		public String toString();
	}
	
	
	public static interface DebugStringBuilderImplementation {
		public String getForObject(Object obj);
		
		public static final DebugStringBuilderImplementation DEBUGGABLE = (a) -> a.toString();
		public static final DebugStringBuilderImplementation CLASSES = (a) -> "\"" + jsonify(((Class<?>) a).getName()) + "\"";
		public static final DebugStringBuilderImplementation ITERABLES = new DebugStringBuilderImplementation() {
			@Override public String getForObject(Object obj) {
				Requirements.hasIterator(obj, "object");
				Iterator<?> iterator = Iterables.iterator(obj);
				StringBuilder result = new StringBuilder("[");
				boolean k = false;
				while (iterator.hasNext()) {
					Object next = iterator.next();
					if (k) result.append(", ");
					else k = true;
					result.append(getJSONValue(next));
				}
				return result.append("]").toString();
			}
		};
		public static final DebugStringBuilderImplementation DEFAULT = (a) -> "\"" + jsonify(a.toString()) + "\"";
	}
	
	private final static Map<Class<?>, DebugStringBuilderImplementation> getCustomImplementations() {
		Map<Class<?>, DebugStringBuilderImplementation> map = new HashMap<>();
		for (Entry<String, DebugStringBuilderImplementation> entry : custImpsStrings.entrySet()) {
			try {
				Class<?> c = Class.forName(entry.getKey());
				if (c != null) map.put(c, entry.getValue());
			} catch (ClassNotFoundException e) {
				// Nothing to see here, move on
				continue;
			}
		}
		return map;
	}
}