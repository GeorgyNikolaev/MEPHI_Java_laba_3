package parser;

import exception.MissionParsingException;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Path;
import java.util.*;


public class XmlMissionParser extends MissionParser {

    @Override
    public Map<String, Object> parseToMap(Path file) throws MissionParsingException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file.toFile());
            doc.getDocumentElement().normalize();

            return (Map<String, Object>) elementToMap(doc.getDocumentElement());

        } catch (Exception e) {
            throw new MissionParsingException("Ошибка парсинга XML: " + file, e);
        }
    }


    @SuppressWarnings("unchecked")
    private Object elementToMap(Element element) {
        NodeList children = element.getChildNodes();

        // Собираем элементные и текстовые дети
        List<Element> elementChildren = new ArrayList<>();
        StringBuilder textContent = new StringBuilder();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                elementChildren.add((Element) child);
            } else if (child.getNodeType() == Node.TEXT_NODE) {
                String text = child.getTextContent().trim();
                if (!text.isEmpty()) {
                    textContent.append(text);
                }
            }
        }

        // Если только текст — возвращаем строку
        if (elementChildren.isEmpty()) {
            String text = textContent.toString().trim();
            return text.isEmpty() ? "" : text;
        }

        // 🔥 КЛЮЧЕВОЕ ИСПРАВЛЕНИЕ: если все дети имеют одинаковый тег — это список!
        String firstTag = elementChildren.get(0).getTagName();
        boolean allSameTag = elementChildren.stream()
                .allMatch(e -> e.getTagName().equals(firstTag));

        if (allSameTag && elementChildren.size() > 1) {
            // Возвращаем список значений напрямую
            List<Object> list = new ArrayList<>();
            for (Element child : elementChildren) {
                list.add(elementToMap(child));
            }
            return list;
        }

        // Иначе — группируем по тегам в Map
        Map<String, Object> result = new HashMap<>();
        Map<String, List<Object>> grouped = new HashMap<>();

        for (Element child : elementChildren) {
            String childTag = child.getTagName();
            Object childValue = elementToMap(child);
            grouped.computeIfAbsent(childTag, k -> new ArrayList<>()).add(childValue);
        }

        // Для каждого тега: один элемент → как есть, много → список
        for (Map.Entry<String, List<Object>> entry : grouped.entrySet()) {
            List<Object> values = entry.getValue();
            result.put(entry.getKey(), values.size() == 1 ? values.get(0) : values);
        }

        return result;
    }
}