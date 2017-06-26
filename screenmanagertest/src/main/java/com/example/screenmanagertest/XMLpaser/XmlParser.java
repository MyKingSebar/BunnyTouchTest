/*
 * Copyright (C) 2013 poster PCE
 * YoungSee Inc. All Rights Reserved
 * Proprietary and Confidential. 
 * @author LiLiang-Ping
 */

package com.example.screenmanagertest.XMLpaser;

import android.util.Xml;


import com.example.screenmanagertest.common.Logger;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/*
 * 注意：在调用该类做XML文件解析时，建议在用户线程中进行。
 * 因为XML文件越大，解析速度越慢，这样会导致耗时太长，所以需要
 * 创建一个用户线程来完成解析。
 */
public class XmlParser
{
    /**
     * 该方法用于解析含有多个节点的XML文件，解析完后，结果保存在一个list中
     * 
     * @param is
     *            XML file stream
     * @param clazz
     *            描述XML内容的class
     * @param startName
     *            节点起始位置tag名称
     */
    @SuppressWarnings(
    { "rawtypes", "unchecked" })
    public List getXmlList(InputStream is, Class<?> clazz, String startName)
    {
        List list = null;
        Object object = null;
        try
        {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(is, "UTF-8");
            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                switch (eventType)
                {
                case XmlPullParser.START_DOCUMENT:
                    list = new ArrayList<Object>();
                    break;
                    
                case XmlPullParser.START_TAG:
                    String tagName = parser.getName();

                    if (startName.equalsIgnoreCase(tagName))
                    {
                        object = clazz.newInstance();
                    }

                    // 如果没有属性或内容，则不需要保存
                    if ((parser.getAttributeCount() <= 0) &&
                        (eventType = parser.next()) != XmlPullParser.TEXT)
                    {
                        break;
                    }
                    
                    // 保存属性值
                    if (object != null)
                    {
                        setXmlValue(parser, object, tagName);
                    }
                    
                    break;
                    
                case XmlPullParser.END_TAG:
                    if (startName.equalsIgnoreCase(parser.getName()))
                    {
                        list.add(object);
                        object = null;
                    }
                    break;
                }
                eventType = parser.next();
            }
        }
        catch (Exception e)
        {
            list = null;
            Logger.e("xml pull parser List error.");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (is != null)
                    is.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        
        return list;
    }

    private class NodeObject
    {
        public String name = null;
        public Object obj = null;
        public NodeObject parent = null;
    }
    
    /**
     * 复杂的XML文件解析函数
     * 
     * @param is
     *            XML file stream
     * @param clazz
     *            描述XML内容的class
     * @return  Object
     *            与XML文件内容相符的class实例
     */
    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    public Object getXmlObject(InputStream is, Class<?> clazz)
    {
        Object object = null;
        NodeObject currentNode = new NodeObject();
        try
        {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(is, "UTF-8");
            int eventType = parser.getEventType();
            boolean isSkipTag = false;

            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                switch (eventType)
                {
                case XmlPullParser.START_DOCUMENT:
                    object = clazz.newInstance();
                    currentNode.obj = object;
                    break;
                    
                case XmlPullParser.START_TAG:
                    String tagName = parser.getName();

                    if (currentNode.name == null)
                    {
                        currentNode.name = tagName;
                    }
                    
                    // 查找保存当前节点属性值的成员变量
                    Field[] f = currentNode.obj.getClass().getDeclaredFields();
                    for (int i = 0; i < f.length; i++)
                    {
                        // 具有多个属性的行可以用tag+AttributeName表示，所以只要包含tag就表示该tag对应的值可能需要保存
                        if (f[i].getName().contains(tagName))  
                        {
                            // 当前节点中含有子节点，则新建一个子节点的实类例
                            if ((f[i].getName().equals(tagName)) && 
                                (f[i].getGenericType() instanceof ParameterizedType) &&
                                (f[i].getType().getName().equals("java.util.List")))
                            {
                                // 如果该节点对应的List成员变量为空，则新建一个List
                                if (f[i].get(currentNode.obj) == null)
                                {
                                    f[i].setAccessible(true);
                                    f[i].set(currentNode.obj, new ArrayList<Object>());
                                }

                                // 创建子节点的class实例
                                Type type = f[i].getGenericType();
                                Class<?> subClazz = (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
                                Object subObject = subClazz.newInstance();

                                // 创建子节点
                                NodeObject subNode = new NodeObject();
                                subNode.obj = subObject;
                                subNode.name = tagName;

                                // 交换节点信息
                                subNode.parent = currentNode;
                                currentNode = subNode;
                            }
                            
                            // 保存属性值
                            if ((parser.getAttributeCount() <= 0) && 
                                (eventType = parser.next()) != XmlPullParser.TEXT)
                            {
                                // 如果没有属性或内容，则不需要保存
                                isSkipTag = true;
                            }
                            else
                            {
                                // 保存当前节点的值到对应的类实例中
                                setXmlValue(parser, currentNode.obj, tagName);
                            }

                            break;
                        }
                    }
                    break;
                    
                case XmlPullParser.END_TAG:
                    
                    tagName = parser.getName();
                    if (currentNode != null && 
                        currentNode.parent != null &&
                        currentNode.name.equals(tagName))
                    {
                        // 把subNode添加到父节点的List中
                        Field[] parentF = currentNode.parent.obj.getClass().getDeclaredFields();
                        for (int i = 0; i < parentF.length; i++)
                        {
                            if (parentF[i].getName().equals(tagName) &&
                                parentF[i].getType().getName().equals("java.util.List"))
                            {
                                List list = (List)parentF[i].get(currentNode.parent.obj);
                                if (list != null)
                                {
                                    list.add(currentNode.obj);
                                }
                                
                                break;
                            }
                        }
                        
                        // 将currentNode指向父节点
                        currentNode = currentNode.parent;
                    }
                    break;
                }
                
                if (!isSkipTag)
                {
                    eventType = parser.next();
                }
                else
                {
                    isSkipTag = false;
                }
            }
        }
        catch (Exception e)
        {
            object = null;
            Logger.e("xml pull parser OBJ error.");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                currentNode = null;
                if (is != null)
                {
                    is.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        
        return object;
    }

    /**
     * 该方法用于设置类实例的属性值，该属性值是xml文件对应的节点属性值
     * @param xmlParser
     *            XML解析器
     * @param obj
     *            需要保存xml文件的类实例
     * @param tagName
     *            标签名称
     */
    private void setXmlValue(XmlPullParser xmlParser, Object obj, String tagName)
    {
        try
        {
            int cnt = xmlParser.getAttributeCount();
            if (cnt > 0)
            {
                // 若有HashMap存在，则直接转换成HashMap
                Field[] f = obj.getClass().getDeclaredFields();
                for (int i = 0; i < f.length; i++)
                {
                    if ((f[i].getName().equals(tagName)) &&
                        (f[i].getGenericType() instanceof ParameterizedType) &&
                        (f[i].getType().getName().equals("java.util.HashMap")))
                    {
                        HashMap<String, String> attributeMap = new HashMap<String, String>(); 
                        
                        for (int j = 0; j < cnt; j++)
                        {
                            attributeMap.put(xmlParser.getAttributeName(j), xmlParser.getAttributeValue(j));
                        }
                        
                        f[i].setAccessible(true);
                        f[i].set(obj, attributeMap);
                        return;
                    }
                    else if ((f[i].getName().equals(tagName)) && 
                             (f[i].getGenericType() instanceof ParameterizedType) && 
                             (f[i].getType().getName().equals("java.util.concurrent.ConcurrentHashMap")))
                    {
                        ConcurrentHashMap<String, String> attributeMap = new ConcurrentHashMap<String, String>();
                        
                        for (int j = 0; j < cnt; j++)
                        {
                            attributeMap.put(xmlParser.getAttributeName(j), xmlParser.getAttributeValue(j));
                        }
                        
                        f[i].setAccessible(true);
                        f[i].set(obj, attributeMap);
                        return;
                    }
                }
                
                // 若没用HashMap表示属性集，则尝试用tag+AttributeName
                for (int j = 0; j < cnt; j++)
                {
                    setClassValue(obj, (tagName + xmlParser.getAttributeName(j)), xmlParser.getAttributeValue(j));
                }
            }
            else
            {
                setClassValue(obj, tagName, xmlParser.getText());
            }
        }
        catch (Exception e)
        {
            Logger.e("xml set value error");
            e.printStackTrace();
        }
    }
    
    /**
     * 该方法用于设置类实例的属性值
     * @param t
     *            需要保存xml文件的类实例
     * @param name
     *            属性名称
     * @param value
     *            属性值
     */
    private void setClassValue(Object t, String name, String value)
    {
        try
        {
            Field[] f = t.getClass().getDeclaredFields();
            for (int i = 0; i < f.length; i++)
            {
                if (f[i].getName().equalsIgnoreCase(name))
                {
                    f[i].setAccessible(true);
                    Class<?> fieldType = f[i].getType();
                    if (fieldType == String.class)
                    {
                        f[i].set(t, value);
                    }
                    else if (fieldType == Integer.TYPE)
                    {
                        f[i].set(t, Integer.parseInt(value));
                    }
                    else if (fieldType == Float.TYPE)
                    {
                        f[i].set(t, Float.parseFloat(value));
                    }
                    else if (fieldType == Double.TYPE)
                    {
                        f[i].set(t, Double.parseDouble(value));
                    }
                    else if (fieldType == Long.TYPE)
                    {
                        f[i].set(t, Long.parseLong(value));
                    }
                    else if (fieldType == Short.TYPE)
                    {
                        f[i].set(t, Short.parseShort(value));
                    }
                    else if (fieldType == Boolean.TYPE)
                    {
                        f[i].set(t, Boolean.parseBoolean(value));
                    }
                    else
                    {
                        f[i].set(t, value);
                    }
                    break;
                }
            }
        }
        catch (Exception e)
        {
            Logger.e("set class value error");
            e.printStackTrace();
        }
    }
}