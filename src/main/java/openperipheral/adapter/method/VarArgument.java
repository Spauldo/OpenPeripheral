package openperipheral.adapter.method;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import openperipheral.api.LuaArgType;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class VarArgument extends Argument {

	public VarArgument(String name, String description, LuaArgType luaType, Class<?> javaType, int javaArgIndex) {
		super(name, description, luaType, javaType, javaArgIndex);
	}

	@Override
	protected Class<?> getArgType(Class<?> javaArgClass) {
		// something went terribly wrong
		Preconditions.checkArgument(javaArgClass.isArray(), "Vararg type must be array");
		return javaArgClass.getComponentType();
	}

	protected void checkArgument(Object value) {
		Preconditions.checkNotNull(value, "Vararg parameter '%s' has null value, but is not marked as nullable", name);
	}

	@Override
	public Object convert(Iterator<Object> args) {
		List<Object> allArgs = Lists.newArrayList(args);

		Object vararg = Array.newInstance(javaType, allArgs.size());

		for (int i = 0; i < allArgs.size(); i++) {
			Object value = allArgs.get(i);
			checkArgument(value);
			Object converted = convertSingleArg(value);
			Array.set(vararg, i, converted);
		}

		return vararg;
	}

	@Override
	public Map<String, Object> describe() {
		Map<String, Object> result = super.describe();
		result.put("vararg", true);
		return result;
	}

	@Override
	public String toString() {
		return name + "...";
	}

}
