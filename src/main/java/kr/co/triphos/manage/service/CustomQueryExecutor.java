package kr.co.triphos.manage.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CustomQueryExecutor {
    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;

    public CustomQueryExecutor(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.objectMapper = new ObjectMapper();
    }

    public List<?> executeDynamicQuery(String method, InQueryDTO inQuery) throws IOException {
        // JSON 파일에서 쿼리 가져오기
        String query = getQueryFromJson(inQuery.getNamespace(), inQuery.getTag());

        if (query == null) {
            throw new IllegalArgumentException("해당 쿼리를 찾을 수 없습니다: " + inQuery.getNamespace() + " - " + inQuery.getTag());
        }

        // 1. 쿼리 기본 뼈대 시작 (WHERE 1=1 추가)
        StringBuilder queryBuilder = new StringBuilder(query).append(" WHERE 1=1 ");

        // 2. 조건 태그 파싱 및 조건문 추가
        Map<String, Object> parameters = inQuery.getParam();
        Map<String, Object> queryParams = new HashMap<>();

        parameters.forEach((key, value) -> {
            if (value != null) {
                String condition = value.toString().trim();

                // 조건 태그 분석 (eq, like, gt, lt, between, in, or, subquery)
                if (condition.startsWith("<eq>")) {
                    queryBuilder.append(" AND ").append(key).append(" = :").append(key);
                    queryParams.put(key, condition.replace("<eq>", "").replace("</eq>", ""));
                }
                else if (condition.startsWith("<like>")) {
                    queryBuilder.append(" AND ").append(key).append(" LIKE :").append(key);
                    queryParams.put(key, "%" + condition.replace("<like>", "").replace("</like>", "") + "%");
                }
                else if (condition.startsWith("<gt>")) {
                    queryBuilder.append(" AND ").append(key).append(" > :").append(key);
                    queryParams.put(key, condition.replace("<gt>", "").replace("</gt>", ""));
                }
                else if (condition.startsWith("<lt>")) {
                    queryBuilder.append(" AND ").append(key).append(" < :").append(key);
                    queryParams.put(key, condition.replace("<lt>", "").replace("</lt>", ""));
                }
                else if (condition.startsWith("<between>")) {
                    String[] range = condition.replace("<between>", "").replace("</between>", "").split(" AND ");
                    if (range.length == 2) {
                        queryBuilder.append(" AND ").append(key).append(" BETWEEN :start").append(key)
                                .append(" AND :end").append(key);
                        queryParams.put("start" + key, range[0].trim());
                        queryParams.put("end" + key, range[1].trim());
                    }
                }
                else if (condition.startsWith("<in>")) {
                    String[] values = condition.replace("<in>", "").replace("</in>", "").split(",");
                    queryBuilder.append(" AND ").append(key).append(" IN (:").append(key).append("List)");
                    queryParams.put(key + "List", Arrays.asList(values));
                }
                else if (condition.startsWith("<or>")) {
                    String[] orConditions = condition.replace("<or>", "").replace("</or>", "").split("\\|");
                    queryBuilder.append(" AND (");
                    for (int i = 0; i < orConditions.length; i++) {
                        queryBuilder.append(key).append(" = :").append(key).append(i);
                        queryParams.put(key + i, orConditions[i]);
                        if (i < orConditions.length - 1) queryBuilder.append(" OR ");
                    }
                    queryBuilder.append(")");
                }
                else if (condition.startsWith("<subquery>")) {
                    queryBuilder.append(" AND ").append(key).append(" = (")
                            .append(condition.replace("<subquery>", "").replace("</subquery>", "")).append(")");
                }
                else {
                    queryBuilder.append(" AND ").append(key).append(" = :").append(key);
                    queryParams.put(key, value);
                }
            }
        });

        // 2. ORDER BY 처리
        if (parameters.containsKey("_sort")) {
            queryBuilder.append(" ORDER BY ").append(parameters.get("_sort"));
            if (parameters.containsKey("_sortDirection")) {
                queryBuilder.append(" ").append(parameters.get("_sortDirection"));
            }
        }

        // 3. 페이징 처리 (_limit / _page)
        int limit = parameters.getOrDefault("_limit", 10) instanceof Integer ? (int) parameters.get("_limit") : 20;
        int offset = parameters.getOrDefault("_page", 0) instanceof Integer ? (int) parameters.get("_page") : 1;

        Query jpaQuery = entityManager.createQuery(queryBuilder.toString());
        queryParams.forEach(jpaQuery::setParameter);

        jpaQuery.setFirstResult(offset);
        jpaQuery.setMaxResults(limit);

        return jpaQuery.getResultList();
    }

    public List<?> executeQuery(String namespace, String tag, Map<String, Object> parameters) throws IOException {
        // JSON 파일에서 쿼리 가져오기
        String query = getQueryFromJson(namespace, tag);

        if (query == null) {
            throw new IllegalArgumentException("해당 쿼리를 찾을 수 없습니다: " + namespace + " - " + tag);
        }

        // 1. 쿼리 기본 뼈대 시작 (WHERE 1=1 추가)
        StringBuilder queryBuilder = new StringBuilder(query).append(" WHERE 1=1 ");

        // 2. 조건 태그 파싱 및 조건문 추가
        Map<String, Object> queryParams = new HashMap<>();

        parameters.forEach((key, value) -> {
            if (value != null) {
                String condition = value.toString().trim();

                // 조건 태그 분석 (eq, like, gt, lt, between, in, or, subquery)
                if (condition.startsWith("<eq>")) {
                    queryBuilder.append(" AND ").append(key).append(" = :").append(key);
                    queryParams.put(key, condition.replace("<eq>", "").replace("</eq>", ""));
                }
                else if (condition.startsWith("<like>")) {
                    queryBuilder.append(" AND ").append(key).append(" LIKE :").append(key);
                    queryParams.put(key, "%" + condition.replace("<like>", "").replace("</like>", "") + "%");
                }
                else if (condition.startsWith("<gt>")) {
                    queryBuilder.append(" AND ").append(key).append(" > :").append(key);
                    queryParams.put(key, condition.replace("<gt>", "").replace("</gt>", ""));
                }
                else if (condition.startsWith("<lt>")) {
                    queryBuilder.append(" AND ").append(key).append(" < :").append(key);
                    queryParams.put(key, condition.replace("<lt>", "").replace("</lt>", ""));
                }
                else if (condition.startsWith("<between>")) {
                    String[] range = condition.replace("<between>", "").replace("</between>", "").split(" AND ");
                    if (range.length == 2) {
                        queryBuilder.append(" AND ").append(key).append(" BETWEEN :start").append(key)
                                .append(" AND :end").append(key);
                        queryParams.put("start" + key, range[0].trim());
                        queryParams.put("end" + key, range[1].trim());
                    }
                }
                else if (condition.startsWith("<in>")) {
                    String[] values = condition.replace("<in>", "").replace("</in>", "").split(",");
                    queryBuilder.append(" AND ").append(key).append(" IN (:").append(key).append("List)");
                    queryParams.put(key + "List", Arrays.asList(values));
                }
                else if (condition.startsWith("<or>")) {
                    String[] orConditions = condition.replace("<or>", "").replace("</or>", "").split("\\|");
                    queryBuilder.append(" AND (");
                    for (int i = 0; i < orConditions.length; i++) {
                        queryBuilder.append(key).append(" = :").append(key).append(i);
                        queryParams.put(key + i, orConditions[i]);
                        if (i < orConditions.length - 1) queryBuilder.append(" OR ");
                    }
                    queryBuilder.append(")");
                }
                else if (condition.startsWith("<subquery>")) {
                    queryBuilder.append(" AND ").append(key).append(" = (")
                            .append(condition.replace("<subquery>", "").replace("</subquery>", "")).append(")");
                }
                else {
                    queryBuilder.append(" AND ").append(key).append(" = :").append(key);
                    queryParams.put(key, value);
                }
            }
        });

        // 2. ORDER BY 처리
        if (parameters.containsKey("_sort")) {
            queryBuilder.append(" ORDER BY ").append(parameters.get("_sort"));
            if (parameters.containsKey("_sortDirection")) {
                queryBuilder.append(" ").append(parameters.get("_sortDirection"));
            }
        }

        Query jpaQuery = entityManager.createQuery(queryBuilder.toString());
        queryParams.forEach(jpaQuery::setParameter);

        return jpaQuery.getResultList();
    }

    private String getQueryFromJson(String namespace, String queryId) throws IOException {
        JsonNode rootNode = objectMapper.readTree(
                new File("src/main/resources/queries/" + namespace.toLowerCase() + "_queries.json")
        );

        return rootNode.get(namespace).get(queryId).asText();
    }



}
