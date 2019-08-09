package com.xitu.app.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.beans.BeanCopier;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class BeanUtil {

    private static final Logger logger = LoggerFactory.getLogger(BeanUtil.class);


    /**
     * <pre>
     * 
     * </pre>
     * 
     * @param target
     * @param propertyExpression
     * @param value
     */
    public static void setValue(Map<String, Object> target, String propertyExpression, Object value) {
        if (target == null) {
            logger.warn("setValue the param:target is null, return null.");
            return;
        }
        if (propertyExpression == null) {
            logger.warn("setValue the param:propetyExpression is null, return null.");
            return;
        }
        int indexOf = propertyExpression.indexOf(".");
        if (indexOf == -1) { //没有[.]
            String property = propertyExpression;
            target.put(property, value);

        } else if (indexOf + 1 == propertyExpression.length()) { //[.]在末尾
            String property = propertyExpression.substring(0, indexOf);
            target.put(property, value);

        } else { //[.]在中间
            String property = propertyExpression.substring(0, indexOf);
            String expression = propertyExpression.substring(indexOf + 1);
            Object subTarget = target.get(property);
            // subTarget为空或者不是Map，则重新赋值一个Map个subTarget
            if (subTarget == null || !(subTarget instanceof Map)) { 
                subTarget = new HashMap<String, Object>();
            }
            target.put(property, subTarget);
            setValue((Map<String, Object>) subTarget, expression, value);
        }
    }



    /**
     * @param clazz
     * @return
     * @throws Exception
     */
    public static List<String> getPropertyNames(Class clazz) throws Exception {
        List<String> propertyNames = new ArrayList<String>();
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (!propertyName.equals("class")) {
                propertyNames.add(propertyName);
            }
        }
        return propertyNames;
    }

    /**
     * @param clazz
     * @return
     * @throws Exception
     */
    public static List<String> getPropertyNamesAndType(Class clazz) throws Exception {
        List<String> propertyNames = new ArrayList<String>();
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor property = propertyDescriptors[i];
            String propertyName = property.getName();
            Class<?> propertyType = property.getPropertyType();
            if (!propertyName.equals("class")) {

                //                System.out.println("---------------"+propertyName);
                //                Field field = clazz.getField(propertyName);
                //                Annotation[] annotations = field.getAnnotations();
                //                System.out.println(annotations);

                String typeName = propertyType.getName();
                propertyNames.add(typeName + "\t" + propertyName);
            }
        }
        return propertyNames;
    }


    /**
     * 将一个 Map 对象转化为一个 JavaBean
     * 
     * @param type 要转化的类型
     * @param map 包含属性值的 map
     * @return 转化出来的 JavaBean 对象
     * @throws IntrospectionException 如果分析类属性失败
     * @throws IllegalAccessException 如果实例化 JavaBean 失败
     * @throws InstantiationException 如果实例化 JavaBean 失败
     * @throws InvocationTargetException 如果调用属性的 setter 方法失败
     */
    public static Object convertMap(Class type, Map map) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
        Object obj = type.newInstance(); // 创建 JavaBean 对象
        // 给 JavaBean 对象的属性赋值
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (map.containsKey(propertyName)) {
                // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
                Object value = map.get(propertyName);
                Object[] args = new Object[1];
                args[0] = value;
                descriptor.getWriteMethod().invoke(obj, args);
            }
        }
        return obj;
    }

    /**
     * 将一个 JavaBean 对象转化为一个 Map
     * 
     * @param bean 要转化的JavaBean 对象
     * @return 转化出来的 Map 对象
     * @throws IntrospectionException 如果分析类属性失败
     * @throws IllegalAccessException 如果实例化 JavaBean 失败
     * @throws InvocationTargetException 如果调用属性的 setter 方法失败
     */
    public static Map convertBean(Object bean) throws Exception {
        Class type = bean.getClass();
        Map returnMap = new HashMap();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (!propertyName.equals("class")) {
                Method readMethod = descriptor.getReadMethod();
                Object result = readMethod.invoke(bean, new Object[0]);
                if (result != null) {
                    returnMap.put(propertyName, result);
                } else {
                    returnMap.put(propertyName, "");
                }
            }
        }
        return returnMap;
    }
    
    /**
	 * 复制bean
	 * @param source 数据类
	 * @param target 目标类
	 */
	public static void copyBean(Object source, Object target){
			BeanCopier.create(source.getClass(), target.getClass(), false)
			.copy(source, target, null);
	}

}
