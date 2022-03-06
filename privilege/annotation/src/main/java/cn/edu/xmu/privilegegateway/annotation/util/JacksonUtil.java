/**
 * Copyright School of Informatics Xiamen University
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package cn.edu.xmu.privilegegateway.annotation.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class JacksonUtil {

    private static final Log logger = LogFactory.getLog(JacksonUtil.class);
    private static final Set<String> primitiveType = new HashSet<>(){
        {
            add(Integer.class.getName());
            add(Long.class.getName());
            add(Double.class.getName());
            add(Float.class.getName());
            add(Boolean.class.getName());
        }
    };

    public static String parseString(String body, String field) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null) {
                return leaf.asText();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


    public static List<String> parseStringList(String body, String field) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);

            if (leaf != null) {
                return mapper.convertValue(leaf, new TypeReference<List<String>>() {
                });
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static <T> List<T> parseObjectList(String body, String field, Class<T> clazz ) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


    public static Integer parseInteger(String body, String field) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null) {
                return leaf.asInt();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static List<Integer> parseIntegerList(String body, String field) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);

            if (leaf != null) {
                return mapper.convertValue(leaf, new TypeReference<List<Integer>>() {
                });
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


    public static Boolean parseBoolean(String body, String field) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null) {
                return leaf.asBoolean();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static Short parseShort(String body, String field) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null) {
                Integer value = leaf.asInt();
                return value.shortValue();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static Byte parseByte(String body, String field) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.get(field);
            if (leaf != null) {
                Integer value = leaf.asInt();
                return value.byteValue();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static <T> T parseObject(String body, String field, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        JsonNode node;
        try {
            node = mapper.readTree(body);
            node = node.get(field);
            return mapper.treeToValue(node, clazz);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static Object toNode(String json) {
        if (json == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        try {

            return mapper.readTree(json);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public static Map<String, String> toMap(String data) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(data, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
    public static <T> T toObj(String data, Class<T> clazz){
        ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        try {
            return mapper.readValue(data, clazz);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
    public static String toJson(Object data) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> parseObjectList(String body, Class<T> clazz){
        ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());

        JsonNode node = (JsonNode) toNode(body);
        if (node != null) {
            List<JsonNode> retObj =mapper.convertValue(node, new TypeReference<List<JsonNode>>() {
            });
            List<T> ret = new ArrayList<>(retObj.size());
            for (JsonNode item:retObj) {
                String value = item.toString();
                T obj = null;
                try {
                    if (primitiveType.contains(clazz.getName())) {
                        obj = clazz.getConstructor(String.class).newInstance(value);
                    } else {
                        obj = toObj(value, clazz);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                ret.add(obj);
            }
            return ret;
        }
        return null;
    }

    public static List<String> parseSubnodeToStringList(String body, String field) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.at(field);

            if (leaf != null) {
                List<JsonNode> retObj =mapper.convertValue(leaf, new TypeReference<List<JsonNode>>() {
                });
                List<String> ret = new ArrayList<>(retObj.size());
                for (JsonNode item:retObj) {
                    ret.add(item.toString());
                }
                return ret;
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static String parseSubnodeToString(String body, String field) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.at(field);
            if (leaf != null) {
                return leaf.toString();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static <T> T parseSubnodeToObject(String body, String field, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.at(field);
            if (leaf != null) {
                String value = leaf.toString();
                if (primitiveType.contains(clazz.getName())){
                    return (T) clazz.getConstructor(String.class).newInstance(value);
                } else {
                    return toObj(value, clazz);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static <T>  List<T> parseSubnodeToObjectList(String body, String field, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        JsonNode node;
        try {
            node = mapper.readTree(body);
            JsonNode leaf = node.at(field);
            if (leaf != null) {
                List<JsonNode> retObj =mapper.convertValue(leaf, new TypeReference<List<JsonNode>>() {
                });
                List<T> ret = new ArrayList<>(retObj.size());
                for (JsonNode item:retObj) {
                    String value = item.toString();
                    T obj = null;
                    if (primitiveType.contains(clazz.getName())){
                        obj = clazz.getConstructor(String.class).newInstance(value);
                    } else {
                        obj = toObj(value, clazz);
                    }
                    ret.add(obj);
                }
                return ret;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}