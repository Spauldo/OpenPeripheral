package openperipheral.adapter;

import java.lang.reflect.Method;
import java.util.*;

import openmods.Log;
import openperipheral.api.LuaCallable;
import openperipheral.api.LuaType;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public abstract class AdaptedClass<E extends IMethodExecutor> {

	private final Map<String, E> methodsByName;
	private final Map<Integer, E> methodsByIndex;
	public final String[] methodNames;

	protected AdaptedClass(AdapterManager<?, E> manager, Class<?> cls) {
		methodsByName = collectAdaptersMethods(manager, cls);

		ImmutableMap.Builder<Integer, E> methodsByIndex = ImmutableMap.builder();
		methodNames = new String[methodsByName.size()];
		int id = 0;
		for (Map.Entry<String, E> e : methodsByName.entrySet()) {
			methodNames[id] = e.getKey();
			methodsByIndex.put(id++, e.getValue());
		}
		this.methodsByIndex = methodsByIndex.build();
	}

	private Map<String, E> collectAdaptersMethods(AdapterManager<?, E> manager, Class<?> cls) {
		Map<String, E> result = Maps.newTreeMap();

		final List<Class<?>> classHierarchy = Lists.reverse(listSuperClasses(cls));

		Set<Class<?>> allSuperInterfaces = Sets.newHashSet();
		for (Class<?> c : classHierarchy)
			allSuperInterfaces.addAll(listSuperInterfaces(c));

		for (Class<?> c : allSuperInterfaces)
			addExternalAdapters(manager, result, c);

		for (Class<?> c : classHierarchy) {
			addExternalAdapters(manager, result, c);
			addInlineAdapter(manager, result, c);
		}

		AdditionalHelperMethods helper = new AdditionalHelperMethods();
		for (Method method : AdditionalHelperMethods.class.getMethods()) {
			LuaCallable callableMeta = method.getAnnotation(LuaCallable.class);
			if (callableMeta != null) {
				MethodDeclaration decl = new MethodDeclaration(method, callableMeta);
				result.put(decl.name, createDummyWrapper(helper, decl));
			}
		}

		return ImmutableMap.copyOf(result);
	}

	private static <E extends IMethodExecutor> void addExternalAdapters(AdapterManager<?, E> manager, Map<String, E> result, Class<?> cls) {
		for (AdapterWrapper<E> wrapper : manager.getExternalAdapters(cls))
			addAdapterMethods(result, wrapper);
	}

	private static <E extends IMethodExecutor> void addInlineAdapter(AdapterManager<?, E> manager, Map<String, E> result, Class<?> cls) {
		AdapterWrapper<E> wrapper = manager.getInlineAdapter(cls);
		addAdapterMethods(result, wrapper);
	}

	private static <E extends IMethodExecutor> void addAdapterMethods(Map<String, E> result, AdapterWrapper<E> wrapper) {
		for (Map.Entry<String, E> e : wrapper.methods.entrySet()) {
			final String name = e.getKey();
			final E previous = result.put(name, e.getValue());
			if (previous != null) Log.info("Previous defininition of Lua method '%s' overwritten by adapter %s", name, wrapper.adapterClass);
		}

	}

	public E getMethod(int index) {
		return methodsByIndex.get(index);
	}

	public Collection<E> getMethods() {
		return Collections.unmodifiableCollection(methodsByIndex.values());
	}

	private static List<Class<?>> listSuperClasses(Class<?> cls) {
		List<Class<?>> superClasses = Lists.newArrayList();
		Class<?> currentClass = cls;
		while (currentClass != Object.class) {
			superClasses.add(currentClass);
			currentClass = currentClass.getSuperclass();
		}
		return superClasses;
	}

	private static Set<Class<?>> listSuperInterfaces(Class<?> cls) {
		Set<Class<?>> superInterfaces = Sets.newHashSet();
		Queue<Class<?>> tbd = Lists.newLinkedList();
		tbd.addAll(Arrays.asList(cls.getInterfaces()));

		Class<?> currentClass;
		while ((currentClass = tbd.poll()) != null) {
			superInterfaces.add(currentClass);
			tbd.addAll(Arrays.asList(currentClass.getInterfaces()));
		}
		return superInterfaces;
	}

	protected abstract E createDummyWrapper(Object lister, MethodDeclaration method);

	private class AdditionalHelperMethods {
		@LuaCallable(returnTypes = LuaType.STRING, description = "List all the methods available")
		public String listMethods() {
			List<String> info = Lists.newArrayList();
			for (IMethodExecutor e : methodsByName.values()) {
				final MethodDeclaration m = e.getWrappedMethod();
				info.add(m.signature());
			}
			return Joiner.on(", ").join(info);
		}

		@LuaCallable(returnTypes = LuaType.TABLE, description = "Get a complete table of information about all available methods")
		public Map<?, ?> getAdvancedMethodsData() {
			Map<String, Object> info = Maps.newHashMap();
			for (IMethodExecutor e : methodsByName.values()) {
				final MethodDeclaration m = e.getWrappedMethod();
				info.put(m.name, m.describe());
			}
			return info;
		}
	}

}
