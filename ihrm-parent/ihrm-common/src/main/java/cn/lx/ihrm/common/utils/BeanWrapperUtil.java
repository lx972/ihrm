package cn.lx.ihrm.common.utils;

import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

/**
 * cn.lx.ihrm.common.utils
 *
 * @Author Administrator
 * @date 15:52
 */
public class BeanWrapperUtil {

    /**
     * 获取对象中属性值为null的属性名集合
     * @param object
     * @return
     */
    public static String[] getNullFieldNames(Object object){
        //使用beanwrapper操作对象
        final BeanWrapperImpl beanWrapper = new BeanWrapperImpl(object);
        //获取对象属性详细信息的集合
        PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
        //声明一个set集合用来保存空属性值的属性名
        Set<String> emptyNames=new HashSet<String>();
        //遍历所有属性
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            //获取属性名
            String name = propertyDescriptor.getName();
            //通过属性名获取属性值
            Object value = beanWrapper.getPropertyValue(name);
            //值为null，就加入set集合中
            if (value==null){
                emptyNames.add(name);
            }
        }
        //set集合转化为string数组
        String[] result = emptyNames.toArray(new String[0]);
        return result;
    }
}
