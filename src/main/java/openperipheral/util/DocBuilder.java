package openperipheral.util;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import openperipheral.adapter.AdapterWrapper;
import openperipheral.adapter.IDescriptable;
import openperipheral.adapter.IMethodExecutor;
import openperipheral.adapter.object.IObjectMethodExecutor;
import openperipheral.adapter.peripheral.IPeripheralMethodExecutor;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.base.Objects;
import com.google.common.base.Throwables;

public class DocBuilder {

	private final Document doc;
	private final Element root;

	public DocBuilder() {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
			root = doc.createElement("documentation");
			doc.appendChild(root);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	public void dump(File output) {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(output);
			transformer.transform(source, result);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

	public void createDocForTe(Class<?> cls, Map<String, IPeripheralMethodExecutor> methods) {
		if (methods.isEmpty()) return;
		Element result = doc.createElement("peripheral");
		fillDocForClass(result, cls, methods);

		final String teName = Objects.firstNonNull(PeripheralUtils.getClassToNameMap().get(cls), "null");
		result.appendChild(createProperty("name", teName));

		root.appendChild(result);
	}

	public void createDocForObject(Class<?> cls, Map<String, IObjectMethodExecutor> methods) {
		if (methods.isEmpty()) return;
		Element result = doc.createElement("luaObject");
		fillDocForClass(result, cls, methods);
		root.appendChild(result);
	}

	public void createDocForAdapter(String type, String location, Class<?> targetClass, AdapterWrapper<?> adapter) {
		Element result = doc.createElement("adapter");
		result.setAttribute("class", adapter.getAdapterClass().getName());
		result.setAttribute("type", type);
		result.setAttribute("location", location);

		result.appendChild(createProperty("target", adapter.getTargetClass().getName()));
		result.appendChild(createProperty("source", adapter.source()));

		fillMethods(result, adapter.getMethods());
		root.appendChild(result);
	}

	protected void fillMethods(Element result, Collection<? extends IMethodExecutor> methods) {
		for (IMethodExecutor method : methods) {
			Element methodDoc = doc.createElement("method");

			final IDescriptable description = method.description();

			Element names = doc.createElement("names");
			for (String name : description.getNames())
				names.appendChild(createProperty("name", name));
			methodDoc.appendChild(names);

			fillDocForDescriptable(methodDoc, description);
			result.appendChild(methodDoc);
		}
	}

	private <E extends IMethodExecutor> void fillDocForClass(Element result, Class<?> cls, Map<String, E> list) {
		result.setAttribute("class", cls.getName());
		result.appendChild(createProperty("simpleName", cls.getSimpleName()));

		for (Map.Entry<String, E> entry : list.entrySet()) {
			Element methodDoc = doc.createElement("method");

			methodDoc.setAttribute("name", entry.getKey());
			fillDocForDescriptable(methodDoc, entry.getValue().description());
			result.appendChild(methodDoc);
		}
	}

	private void fillDocForDescriptable(Element result, IDescriptable method) {

		result.appendChild(createProperty("signature", method.signature()));
		Element description = doc.createElement("extra");
		serializeMap(description, method.describe());
		result.appendChild(description);
	}

	private void serializeValue(Element output, Object value) {
		if (value == null) output.appendChild(doc.createTextNode("null"));
		else if (value instanceof Map) serializeMap(output, (Map<?, ?>)value);
		else if (value instanceof Collection) serializeCollection(output, (Collection<?>)value);
		else output.appendChild(doc.createTextNode(value.toString()));
	}

	private void serializeCollection(Element output, Collection<?> list) {
		int index = 0;
		for (Object o : list) {
			Element e = doc.createElement("e");
			e.setAttribute("index", Integer.toString(index++));
			serializeValue(e, o);
			output.appendChild(e);
		}
	}

	private void serializeMap(Element output, Map<?, ?> map) {
		for (Map.Entry<?, ?> e : map.entrySet()) {
			Object key = e.getKey();
			Element entry = doc.createElement(key == null? "null" : key.toString());
			serializeValue(entry, e.getValue());
			output.appendChild(entry);
		}
	}

	private Element createProperty(String tag, String value) {
		Element el = doc.createElement(tag);
		el.appendChild(doc.createTextNode(value));
		return el;
	}
}
