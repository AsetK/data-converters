package com.epam.dataconverters;

import com.epam.dataconverters.enums.Drink;
import com.epam.dataconverters.enums.Seasons;
import com.epam.dataconverters.objectmapper.ObjectMapperConverter;
import com.epam.dataconverters.transformer.Season;
import com.epam.dataconverters.transformer.SomeClass;
import com.epam.dataconverters.transformer.XSLTTransformer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.microsoft.azure.servicebus.Message;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;


@RunWith(SpringRunner.class)
@SpringBootTest
public class DataConvertersApplicationTests {

    @Autowired
    ObjectMapperConverter objectMapperConverter;

    @Autowired
    XSLTTransformer xsltTransformer;

    @Autowired
    Person person;

    @Autowired
    Address address;


    @Test
    public void objectMapper() throws JsonProcessingException {
        String result = objectMapperConverter.convertToJSON(person);
        System.out.println(result);
    }

    @Test
    public void toAzureMessage() throws JsonProcessingException {
        String result = objectMapperConverter.convertToJSON(person);
        System.out.println(result);

        Message message = new Message(result, MediaType.APPLICATION_JSON_VALUE);
        System.out.println(new String(message.getBody(), UTF_8));
    }

    @Test
    public void setAzureMessageProperties() throws JsonProcessingException {
        String result = objectMapperConverter.convertToJSON(person);
        Message message = new Message(result, MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> properties = new HashMap<>();
        properties.put("key1", "value1");
        properties.put("key2", "value2");
        message.setProperties(properties);  //дополнительная информация что не фключена в messageBody.
        // Т.е. messageBody - это конвертированный объект класса с данными, properties - дополнительные данные в виде Map-ы

        System.out.println(new String(message.getBody(), UTF_8));
        System.out.println(new String(message.getMessageBody().getBinaryData().get(0)));
        System.out.println(message.getProperties().keySet().toString());
    }

    @Test
    public void messageToObject() throws IOException {
        String result = objectMapperConverter.convertToJSON(person);
        Message message = new Message(result, MediaType.APPLICATION_JSON_VALUE);

        String messageBody = new String(message.getMessageBody().getBinaryData().get(0));
        System.out.println("Message body: " + messageBody);

        Person person = (Person) objectMapperConverter.convertToObject(messageBody);
        System.out.println("Object: " + person);
    }

    @Test
    public void transform() throws IOException, TransformerException {
        String xml = "<Person><data><name>Alex</name></data><name>Bill</name></Person>";
        Map<String, Object> props = new HashMap<>();
        props.put("key1", "value1");
        props.put("key2", "value2");
        props.put("key3", address);

        String result = xsltTransformer.transform(xml, props);
        System.out.println(result);
    }

    @Test
    public void objectMapperMethods() throws IOException {
        String result = objectMapperConverter.convertToJSON(person);
        System.out.println(result);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(result);
        System.out.println(rootNode);

        JsonNode firstName = rootNode.get("firstName");
        System.out.println(firstName); // C кавычками

        String firstNameText = firstName.asText();
        System.out.println(firstNameText); // без кавычек
    }

    @Test
    public void enumValueOf() {
        String season = Enum.valueOf(Season.class, "winter").toString();
        System.out.println(season);
    }

    @Test
    public void classT() {
        Class className1 = Person.class;
        Class<Person> className2 = Person.class;
        Class<? extends AbstractPerson> className3 = SuperPerson.class;
        System.out.println(className1);
        System.out.println(className2);
        System.out.println(className3);
    }

    @Test
    public void enumGeneric() {
        Enum<Season> summer = Season.SUMMER;
        System.out.println(summer);
        System.out.println(summer.getClass());

        Enum<Season> winter = Season.WINTER;
        System.out.println(winter);
        System.out.println(((Season) winter).getValue());
        System.out.println(Season.WINTER.getValue());
    }

    @Test
    public void recursiveGeneric() {
        double percent = StringUtils.getJaroWinklerDistance("Asses", "AsetKen");
        System.out.println(percent);
    }

    @Test
    public void mapForeach() {
        Set<String> many_1 = new HashSet<>();
        many_1.add("1.1");
        many_1.add("1.2");

        Set<String> many_2 = new HashSet<>();
        many_2.add("2.1");
        many_2.add("2.2");

        Map<String, Set<String>> oneToMany = new HashMap<>();
        oneToMany.put("1", many_1);
        oneToMany.put("2", many_2);

        oneToMany.forEach((k, v) -> System.out.println(k + ":" + v));
        System.out.println(oneToMany.isEmpty());
        System.out.println(many_1.contains(null));
    }

    @Test
    public void multiMap() throws JsonProcessingException {
        System.out.println("Hash key, hashSet value:");
        SetMultimap<String, String> multimap = MultimapBuilder.hashKeys().hashSetValues().build();
        System.out.println(multimap.isEmpty());
        multimap.put("k_1", "v_1.1");
        multimap.put("k_1", "v_1.2");
        multimap.put("k_1", "v_1.1");   //should be ignored
        multimap.put("k_1", "v_1.3");
        multimap.put("k_2", "v_2.1");
        multimap.put("k_2", "v_2.2");
        System.out.println(multimap.keySet());
        System.out.println(multimap.values());
        System.out.println(multimap.get("k_1"));
        System.out.println(multimap);
        System.out.println(multimap.isEmpty());


        System.out.println("Tree key, hashSet value:");
        SetMultimap<Comparable, Object> multimap_2 = MultimapBuilder.treeKeys().hashSetValues().build();
        multimap_2.put("k_2", "v_2.1");
        multimap_2.put("k_2", "v_2.2");
        multimap_2.put("k_1", "v_1.1");
        multimap_2.put("k_1", "v_1.2");
        multimap_2.put("k_1", "v_1.3");
        System.out.println(multimap_2.keySet());
        System.out.println(multimap_2.values());
        System.out.println(multimap_2);

        System.out.println(multimap_2.asMap());

        System.out.println("Multimap + objectMapper");
        final Map<String, Set<String>> mapOfSets =
                (Map<String, Set<String>>) (Map<?, ?>) multimap.asMap();
        System.out.println(mapOfSets.getClass());
        SomeClass someClass = new SomeClass();
        someClass.setMultimap(multimap);
        someClass.setMap(mapOfSets);
        ObjectMapper objectMapper = new ObjectMapper();
        String value = objectMapper.writeValueAsString(someClass);
        System.out.println(value);

    }

    @Test
    public void hashMap() {
        Map<String, String> map = new HashMap<>();
        map.put("a", "a1");
        map.putIfAbsent("a", "a2");
        System.out.println(map);
        map.computeIfAbsent("b", v -> "???");
        System.out.println(map);

        Map<String, Set<String>> mapWithSet = new HashMap<>();
        putNew(mapWithSet, "key_1", "value_1");
        putNew(mapWithSet, "key_1", "value_1");
        putNew(mapWithSet, "key_1", "value_1.1");
        putNew(mapWithSet, "key_2", "value_2.1");
        putNew(mapWithSet, "key_1", "value_1.2");
        System.out.println(mapWithSet);

        List<Map.Entry<String, Set<String>>> key_1 = mapWithSet.entrySet().stream().
                filter(e -> e.getKey().equals("key_1")).
                collect(Collectors.toList());

        System.out.println("filtered");
        System.out.println(key_1);

        System.out.println("Pairs");
        Set<Pair<String, String>> pairList = new HashSet<>();
        mapWithSet.forEach((k, v) -> v.forEach(v2 -> pairList.add(Pair.of(k, v2))));
        System.out.println(pairList);

    }

    private void put(Map<String, Set<String>> mapWithSet, String key, String value) {
        if (mapWithSet.get(key) != null) {
            mapWithSet.get(key).add(value);
        } else {
            Set<String> set = new HashSet<>();
            set.add(value);
            mapWithSet.put(key, set);
        }
    }

    private void putNew(Map<String, Set<String>> mapWithSet, String key, String value) {
        mapWithSet.merge(key, Sets.newHashSet(value),
                (oldVal, newVal) -> {
                    oldVal.addAll(newVal);
                    return oldVal;
                }
        );
    }

    @Test
    public void set() {
        Set<Integer> set = new HashSet<>();
        set.add(1);
        set.add(2);

        Set<Integer> set_2 = new HashSet<>();
        set_2.add(3);
        set_2.add(4);
        set.addAll(set_2);
        System.out.println(set);
    }


    @Test
    public void xml() {
        String xml = "<EmailDetails><subject>HelloWorld</subject></EmailDetails>";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xml)));
            Element documentElement = document.getDocumentElement();
            System.out.println(documentElement.getTagName());

            NodeList subject = document.getElementsByTagName("subject");
            Node item = subject.item(0);
            System.out.println(item.getNodeName());

            Element element = document.createElement("new");
            element.setNodeValue("year");
            element.setTextContent("year2");
            documentElement.appendChild(element);

            TransformerFactory tf = TransformerFactory.newInstance();
            try {
                StringWriter writer = new StringWriter();
                Transformer transformer = tf.newTransformer();
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                transformer.transform(new DOMSource(document), new StreamResult(writer));
                System.out.println(writer.getBuffer().toString());
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            }


        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void hashCodeEquals() throws InterruptedException {
        SuperPerson superPerson_1 = new SuperPerson();
        SuperPerson superPerson_2 = new SuperPerson();
        System.out.println(superPerson_1 == superPerson_2);
        System.out.println(superPerson_1.equals(superPerson_2));
        System.out.println(superPerson_1.hashCode() == superPerson_2.hashCode());

        Person person = new Person();
        Person person2 = new Person();

        System.out.println(person.hashCode());
        System.out.println(person.hashCode());
        System.out.println(person2.hashCode());

        String text = "Text";
        System.out.println(text.hashCode());

        List<Person> personList = new ArrayList<>();
        personList.add(person);
        System.out.println(person.hashCode());
        System.out.println(personList.contains(person));
        System.out.println(personList.contains(person2));
    }

    @Test
    public void complexity() {
        List<String> arrayList = new ArrayList<>();
        arrayList.add("1");
        arrayList.add("2");
        arrayList.add("3");

        System.out.println(arrayList.get(1));
        arrayList.add(1, "shift?");
        System.out.println(arrayList.get(1));
        System.out.println(arrayList);
    }

    @Test
    public void zoneDateTime() {
        ZonedDateTime utc = ZonedDateTime.now(ZoneId.of("UTC"));
        System.out.println(utc.getZone());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss:SSSSSS");
        String formattedString = utc.format(formatter);
//        ZonedDateTime zonedDateTime = ZonedDateTime.parse(formattedString);


        LocalDateTime local = LocalDateTime.now(ZoneId.of("UTC"));
        System.out.println(local);
    }

    @Test
    public void zoneDateTim2() throws IOException {
        ZonedDateTime utc = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime now = ZonedDateTime.now();
        System.out.println(utc.getZone());
        System.out.println(now.getZone());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE); //for zone deserialization, without it will deserialize to UTC

        String utcSerialized = objectMapper.writeValueAsString(utc);
        ZonedDateTime utcRestored = objectMapper.readValue(utcSerialized, ZonedDateTime.class);
        System.out.println("Serialized UTC: " + utcSerialized);
        System.out.println("Restored UTC: " + utcRestored);

        String nowSerialized = objectMapper.writeValueAsString(now);
        String nowSerialized_0 = "\"2020-03-26T10:10:32.568463400+06:00[Asia/Almaty]\""; //works
        String nowSerialized_0_1 = "\"2020-03-26T10:10:32.568463400+06:00[Europe/Berlin]\""; //works        06:00 vs [Europe/Berlin] = [Europe/Berlin] wins
        String nowSerialized_1 = "\"2020-03-26T10:10:32.568463400+06:00\""; //works
        String nowSerialized_2 = "\"2020-03-26T10:10:32.568+06:00\""; //works
        String nowSerialized_3 = "\"2020-03-26T10:10:32+06:00\""; //works
        String nowSerialized_4 = "\"2020-03-26T10:10:32.568463400\""; //doesn't work
        ZonedDateTime nowRestored = objectMapper.readValue(nowSerialized, ZonedDateTime.class);
        System.out.println("Serialized NOW: " + nowSerialized);
        System.out.println("Restored NOW: " + nowRestored);
        System.out.println("Fixed NOW: " + nowRestored.withFixedOffsetZone());

    }

    @Test
    public void objectMapperListToString() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        List<String> list = new LinkedList<>();
        list.add("AAA");
        list.add("BBB");
        list.add("CCC");
        String s = objectMapper.writeValueAsString(list);
        System.out.println(s);

        Set<String> set = new HashSet<>();
        set.add("AAA");
        set.add("BBB");
        set.add("CCC");
        String s2 = objectMapper.writeValueAsString(set);
        System.out.println(s2);

        Map<String, String> map = new HashMap<>();
        map.put("AAA", "AAA");
        map.put("BBB", "BBB");
        map.put("CCC", "CCC");
        String s3 = objectMapper.writeValueAsString(map);
        System.out.println(s3);
    }

    @Test
    public void objectMapperStringToList() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String str = "[AAA,BBB]";
        List list = objectMapper.readValue(str, new TypeReference<List<String>>() {
        });
        System.out.println(list);

    }

    @Test
    public void stringSplit() throws IOException {
        String text = "Aaa Bbb  Ccc,DDD,,EEE ,FFF, GGG, , ,,,, III";
        List<String> result = Arrays.stream(text.split("[ ,]")).filter(s -> !s.isBlank()).map(String::trim).collect(Collectors.toList());
        System.out.println(result);

    }

    @Test
    public void stringJoin() throws IOException {
        String join = String.join(" ", null, null);
        System.out.println(join);

    }

    @Test
    public void ListToSet() throws IOException {
        List<String> list = new ArrayList<>();
        list.add("AAA");
        list.add("BBB");

        List<String> list2 = new ArrayList<>();
        list.add("AAA");
        list.add("CCC");

        Set<String> set = new HashSet<>();
        set.addAll(list);
        set.addAll(list2);

        System.out.println(set);
    }

    @Test
    public void deleteDuplicatesFromListByParameter() throws IOException {
        List<String> list = new ArrayList<>();
        list.add("A");
        list.add("BB");
        list.add("A");

        List<String> list2 = new ArrayList<>();
        list.add("AA");
        list.add("AAA");

        list.addAll(list2);

        HashSet<String> collect1 = list.stream().collect(Collectors.toCollection(() -> new HashSet<>()));

        TreeSet<String> collect2 = list.stream().collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingInt(String::length))));

        ArrayList<String> collect3 = list.stream().collect(Collectors.collectingAndThen(Collectors.toSet(), ArrayList::new));

        Object collect4 = list.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingInt(String::length))), ArrayList::new));


        System.out.println(collect1);
        System.out.println(collect2);
        System.out.println(collect3);
        System.out.println(collect4);

    }

    @Test
    public void JsonNodes() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonText = "";
        JsonNode jsonNode = mapper.readTree(jsonText);
    }

    @Test
    public void arrayIsCovariant() {
        String[] strings = new String[] {"a", "b", "c"};
        Object[] arr = strings; //cuz array is covariant
//        strings[0] = 42; //compile time error
//        arr[0] = 42; //execution time error

        //-------But next works--------//
        Object[] objects = new Object[] {"a",2};
        System.out.println(objects[0]);
        System.out.println(objects[1]);
    }

    @Test
    public void genericIsInvariant() {
        List<Integer> integers = Arrays.asList(1,2,3);
//        List<Number> objects = integers; //compile time error cuz generic is invariant

        //-----But if we use wildcards generic becomes covariant-----//
        List<? extends Number> nums = integers;
//        nums.add(Integer.valueOf(1)); //but unable to add any type.
        //The compiler will throw an error! Otherwise, we could add a double number to a collection which is designed to accept only integer numbers. You got this rule?

        List<? extends Object> nums2 = new ArrayList<>();
//        nums2.add(Integer.valueOf(1)); //do not work too


        //-----or contrvariant-----//
        List<Number> numbers = new ArrayList<Number>();
        List<? super Integer> ints = numbers;
        ints.add(1);
        ints.add(Integer.valueOf(2));
        System.out.println(ints);
        System.out.println(ints.get(0));


        //-------another example of contrvariant---------//
        List<AbstractPerson> abstractPersonList = new ArrayList<>();
        List<? super Person> list = abstractPersonList;
        list.add(new Person());
//        list.add(new AbstractPerson()); //do not work. why?


        List<AbstractPerson> abstractPersonList2 = new ArrayList<>();
        abstractPersonList2.add(new AbstractPerson());
        List<? super Person> mixed = abstractPersonList2;
        mixed.add(new Person());
        System.out.println(mixed);

    }

    @Test
    public void asd() {
        List<Object> objectList = new ArrayList<>();
        objectList.add("qweqwe");
        objectList.add(1);
        System.out.println(objectList);
    }

    @Test
    public void enums() {
        System.out.println(Seasons.AUTUMN);
        System.out.println(Seasons.AUTUMN.getValue());

        System.out.println(Seasons.SUMMER);
        System.out.println(Seasons.SUMMER.getValue());

        System.out.println(new Drink(Drink.Tea.CHINA));
        System.out.println(new Drink(Drink.Coffee.BRAZILIAN));

    }

}
