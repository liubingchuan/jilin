package com.xitu.app.mapper;

import com.xitu.app.annotation.ColumnName;
import com.xitu.app.annotation.DontMapping;
import com.xitu.app.annotation.IdFlag;
import com.xitu.app.annotation.TableName;
import com.xitu.app.common.PagedRequest;
import com.xitu.app.utils.StringUtil;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

public class BasedProvider {
    /**
     * The constant QUERY.
     */
    public static final String QUERY = "queryProvider";
    /**
     * The constant PAGED_QUERY.
     */
    public static final String PAGED_QUERY = "pagedQueryProvider";
    public static final String CUSTOMIZED_PAGED_QUERY = "customizedPagedQueryProvider";
    /**
     * The constant INSERT.
     */
    public static final String INSERT = "insertProvider";
    /**
     * The constant UPDATE.
     */
    public static final String UPDATE = "updateProvider";
    /**
     * The constant UPDATE_BY_EXAMPLE.
     */
    public static final String UPDATE_BY_EXAMPLE = "updateByExampleProvider";
    public static final String CUSTOMIZED_UPDATE_BY_EXAMPLE = "customizedUpdateByExampleProvider";
    /**
     * The constant TOTAL_COUNT.
     */
    public static final String TOTAL_COUNT = "totalCountProvider";
    public static final String CUSTOMIZED_TOTAL_COUNT = "customizedTotalCountProvider";
    private static final Logger LOGGER = LogManager.getLogger(BasedProvider.class);

    /**
     * Total count provider string.
     *
     * @param example the example
     * @return the string
     * @throws IllegalAccessException the illegal access exception
     */
    public String customizedTotalCountProvider(@Param("example") final Object example, @Param("other") final String other) throws IllegalAccessException {
        SQL sql = new SQL();
        sql.SELECT("count(1)");
        Class<?> clazz = example.getClass();
        List<Field> fields = getFields(clazz);
        TableName tableName = clazz.getAnnotation(TableName.class);
        sql.FROM(tableName.value());
        for (Field f : fields) {
            String columnName = getColumnName(f);
            String fieldName = f.getName();
            f.setAccessible(true);
            Object obj = f.get(example);
            Field childField = isEntity(f.getType());
            if (obj != null && childField != null) {
                childField.setAccessible(true);
                obj = childField.get(obj);
                fieldName = fieldName + "." + childField.getName();
            }
            if (obj != null) {
                sql.WHERE(String.format("`%s`=#{%s%s}", columnName, other == null ? "" : "param1.", fieldName));
            }
        }
        String result = sql.toString();
        if (!StringUtils.isEmpty(other)) {
            if (!StringUtils.isEmpty(other)) {
                result = formatOther(result, other);
            }
        }
        LOGGER.debug(result);
        return result;
    }

    /**
     * Total count provider string.
     *
     * @param example the example
     * @return the string
     * @throws IllegalAccessException the illegal access exception
     */
    public String totalCountProvider(@Param("example") final Object example) throws IllegalAccessException {
        return customizedTotalCountProvider(example, null);
    }

    /**
     * Update provider string.
     *
     * @param example the example
     * @return the string
     * @throws IllegalAccessException the illegal access exception
     */
    public String updateProvider(@Param("example") final Object example) throws IllegalAccessException {
        SQL sql = new SQL();
        Class<?> clazz = example.getClass();
        String idColumn = null, idField = null;
        List<Field> fields = getFields(clazz);
        for (Field f : fields) {
            IdFlag annotationId = f.getAnnotation(IdFlag.class);
            String columnName = getColumnName(f);
            String fieldName = f.getName();
            if (annotationId != null) {
                idColumn = columnName;
                idField = f.getName();
            } else {
                f.setAccessible(true);
                Object obj = f.get(example);
                Field childField = isEntity(f.getType());
                if (obj != null && childField != null) {
                    childField.setAccessible(true);
                    obj = childField.get(obj);
                    fieldName = fieldName + "." + childField.getName();
                }
                if (obj != null) {
                    sql.SET(String.format("`%s`=#{%s}", columnName, fieldName));
                }
            }
        }
        TableName tableName = clazz.getAnnotation(TableName.class);
        sql.UPDATE(tableName.value());
        sql.WHERE(String.format("`%s`=#{%s}", idColumn, idField));
        LOGGER.debug(sql.toString());
        return sql.toString();
    }

    /**
     * Update by example provider string.
     *
     * @param data    the data
     * @param example the example
     * @return the string
     * @throws IllegalAccessException the illegal access exception
     */
    public String updateByExampleProvider(@Param("data") final Object data, @Param("example") final Object example) throws IllegalAccessException {
        return customizedUpdateByExampleProvider(data, example, null);
    }

    /**
     * Update by example provider string.
     *
     * @param data    the data
     * @param example the example
     * @return the string
     * @throws IllegalAccessException the illegal access exception
     */
    public String customizedUpdateByExampleProvider(@Param("data") final Object data, @Param("example") final Object example, @Param("other") final String other) throws IllegalAccessException {
        if (data.getClass() != example.getClass()) {
            throw new IllegalAccessException("data && example are not equal");
        }
        SQL sql = new SQL();
        Class<?> clazz = data.getClass();
        List<Field> fields = getFields(clazz);
        for (Field f : fields) {
            f.setAccessible(true);
            String columnName = getColumnName(f);
            String dataFieldName = f.getName();
            String exampleFieldName = f.getName();
            Object obj = f.get(data);
            Object exampleObj = f.get(example);

            Field childField = isEntity(f.getType());

            if (obj != null && childField != null) {
                childField.setAccessible(true);
                obj = childField.get(obj);
                dataFieldName = dataFieldName + "." + childField.getName();
            }
            if (obj != null) {
                sql.SET(String.format("`%s`=#{data.%s}", columnName, dataFieldName));
            }
            if (exampleObj != null && childField != null) {
                childField.setAccessible(true);
                exampleObj = childField.get(exampleObj);
                exampleFieldName = exampleFieldName + "." + childField.getName();
            }
            if (exampleObj != null) {
                sql.WHERE(String.format("`%s`=#{example.%s}", columnName, exampleFieldName));
            }
        }
        TableName tableName = clazz.getAnnotation(TableName.class);
        sql.UPDATE(tableName.value());
        String result = sql.toString();
        if (!StringUtils.isEmpty(other)) {
            if (!StringUtils.isEmpty(other)) {
                result = formatOther(result, other);
            }
        }
        LOGGER.debug(result);
        return result;
    }

    /**
     * Insert provider string.
     *
     * @param example the example
     * @return the string
     * @throws IllegalAccessException the illegal access exception
     */
    public String insertProvider(@Param("data") Object example) throws IllegalAccessException {
        SQL sql = new SQL();
        Class<?> clazz = example.getClass();
        List<Field> fields = getFields(clazz);
        for (Field f : fields) {
            String columnName = getColumnName(f);
            String fieldName = f.getName();

            f.setAccessible(true);
            Object obj = f.get(example);

            Field childField = isEntity(f.getType());
            if (obj != null && childField != null) {
                childField.setAccessible(true);
                fieldName = fieldName + "." + childField.getName();
            }

            IdFlag annotationId = f.getAnnotation(IdFlag.class);
            if (annotationId != null && annotationId.autowired()) {
                String value = UUID.randomUUID().toString();
                f.set(example, value);
                sql.VALUES(String.format("`%s`", columnName), String.format("#{%s}", fieldName));
            } else {
                if (obj != null) {
                    sql.VALUES(String.format("`%s`", columnName), String.format("#{%s}", fieldName));
                }
            }
        }
        TableName tableName = clazz.getAnnotation(TableName.class);
        sql.INSERT_INTO(tableName.value());
        LOGGER.debug(sql.toString());
        return sql.toString();
    }

    /**
     * Query provider string.
     *
     * @param example the example
     * @return the string
     * @throws IllegalAccessException the illegal access exception
     */
    public String queryProvider(@Param("example") final Object example) throws IllegalAccessException {
        return pagedQueryProvider(null, example);
    }

    /**
     * Paged pagedQuery provider string.
     *
     * @param request the request
     * @param example the example
     * @return the string
     * @throws IllegalAccessException the illegal access exception
     */
    public String pagedQueryProvider(@Param("request") final PagedRequest request, @Param("example") final Object example) throws IllegalAccessException {
        return customizedPagedQueryProvider(request, example, null);
    }

    public String customizedPagedQueryProvider(@Param("request") final PagedRequest request, @Param("example") final Object example, @Param("other") final String other) throws IllegalAccessException {
        SQL sql = new SQL();
        Class<?> clazz = example.getClass();
        List<Field> fields = getFields(clazz);
        TableName tableName = clazz.getAnnotation(TableName.class);
        sql.SELECT("*");
        sql.FROM(tableName.value());
        for (Field f : fields) {
            String columnName = getColumnName(f);
            String fieldName = f.getName();
            f.setAccessible(true);
            Object obj = f.get(example);
            Field childField = isEntity(f.getType());
            if (obj != null && childField != null) {
                childField.setAccessible(true);
                obj = childField.get(obj);
                fieldName = fieldName + "." + childField.getName();
            }
            if (obj != null) {
                sql.WHERE(String.format("%s=#{%s}", columnName, request == null ? fieldName : "param2." + fieldName));
            }
        }
        String result = sql.toString();
        if (!StringUtils.isEmpty(other)) {
            result = formatOther(result, other);
        }
        if (request != null) {
            result += " \n limit " + ((request.getPageIndex() - 1) * request.getPageSize()) + "," + request.getPageSize();
        }
        LOGGER.debug(result);
        return result;
    }

    private static final List<String> sqlKeyWord = Arrays.asList("WHERE", "AND", "OR", "GROUP BY", "LIMIT", "HAVING", "ORDER BY",
            "JOIN", "INNER JOIN", "OUTER JOIN", "LEFT OUTER JOIN", "RIGHT OUTER JOIN");

    private String formatOther(final String sql, final String other) {
        List<String> sql_split = Arrays.asList(sql.split("\\s"));
        String tmp = other;
        if (sql_split.contains("WHERE")) {
            if (!other.toUpperCase().contains("WHERE")) {
                if (!other.trim().toUpperCase().startsWith("AND")) {
                    tmp = "AND " + tmp.replace("where", "").replace("WHERE", "");
                }
            }
            tmp = tmp.replace("where", "").replace("WHERE", "");
        } else {
            if (!other.toUpperCase().contains("WHERE")) {
                if (other.trim().toUpperCase().startsWith("AND")) {
                    tmp = "WHERE " + tmp.substring(tmp.toUpperCase().indexOf("AND") + 3);
                } else if (other.trim().toUpperCase().startsWith("OR")) {
                    tmp = "WHERE " + tmp.substring(tmp.toUpperCase().indexOf("OR") + 2);
                } else {
                    tmp = "WHERE " + tmp;
                }
            }
        }
        return sql + "\n " + tmp.trim();
    }

    private List<Field> getFields(Class clazz) {
        List<Field> result = new ArrayList<>();
        if (clazz != null) {
            Class superClazz = clazz.getSuperclass();
            if (superClazz != Object.class) {
                result.addAll(getFields(superClazz));
            }
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                DontMapping dontMapping = field.getAnnotation(DontMapping.class);
                if (dontMapping == null) {
                    result.add(field);
                }
            }
        }
        return result;
    }

    private String getColumnName(Field field) {
        ColumnName columnName = field.getAnnotation(ColumnName.class);
        if (columnName == null || StringUtil.isNull(columnName.value())) {
            String fieldName = field.getName();
            String regex = "^(\\w+)([A-Z]+\\w*)*$";
            if (fieldName.matches(regex)) {
                return fieldName.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase();
            }
            return fieldName;
        } else {
            return columnName.value();
        }
    }


    private Field isEntity(Class clazz) {
        if (clazz != null && clazz.getClassLoader() != null) {
            List<Field> fields = getFields(clazz);
            for (Field f : fields) {
                IdFlag idFlag = f.getAnnotation(IdFlag.class);
                if (idFlag != null) {
                    return f;
                }
            }
        }
        return null;
    }
}
