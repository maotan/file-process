package com.example.file.service.ofd;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.file.model.XmlEntity;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/*
 *  DOM4J类
 DOM4J定义了几个Java类。以下是最常见的类：
 Document - 表示整个XML文档。文档Document对象是通常被称为DOM树。
 Element - 表示一个XML元素。 Element对象有方法来操作其子元素，它的文本，属性和名称空间。
 Attribute - 表示元素的属性。属性有方法来获取和设置属性的值。它有父节点和属性类型。
 Node - 代表元素，属性或处理指令
            常见DOM4J的方法
            当使用DOM4J，还有经常用到的几种方法：
 SAXReader.read(xmlSource)() - 构建XML源的DOM4J文档。
 Document.getRootElement() - 得到的XML的根元素。
 Element.node(index) - 获得在元素特定索引XML节点。
 Element.attributes() - 获取一个元素的所有属性。
 Node.valueOf(@Name) - 得到元件的给定名称的属性的值。
 *
 * */
public class OfdXmlUtil {
    public static String ids = "";
    public static String getAttributeIdByPath(String path,String attribute) throws Exception {
        List<XmlEntity> xmlList = readXmlByPath(path);
        int mediaId = 0;
        for (XmlEntity xml : xmlList) {
            if (xml.getNode().equals(attribute) && xml.getAttributes().get("ID") != null) {
                mediaId = mediaId>Integer.parseInt(xml.getAttributes().get("ID"))?mediaId:Integer.parseInt(xml.getAttributes().get("ID"));
            }
        }
        String id=String.valueOf(mediaId+1);
        return id;
    }


    public static String getId(Element node, String element) {
        if(node.getName().equals(element)) {
            ids = node.valueOf("id");
        }

        // 当前节点下面子节点迭代器
        Iterator<Element> it = node.elementIterator();
        // 递归遍历
        while (it.hasNext()) {
            // 获取某个子节点对象
            Element e = it.next();
            // 对子节点进行遍历
            getId(e, element);
        }
        return ids;

    }

    public static String getLastIdByElement(String path, String element) throws Exception {
        File file = new File(path);

        // 创建saxReader对象
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        // 获取根节点元素对象
        Element node = document.getRootElement();
        String str = getId(node, element);

        return str;
    }

    /**
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static List<XmlEntity> readXmlByPath(String path) throws Exception {
        File file = new File(path);
        List<XmlEntity> xmlEntities = readXmlByFile(file);
        return xmlEntities;
    }

    /**
     * @param file
     * @return
     * @throws Exception
     */
    public static List<XmlEntity> readXmlByFile(File file) throws Exception {
        // 创建saxReader对象
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        // 获取根节点元素对象
        Element rootNode = document.getRootElement();
        Element contentNode = rootNode.element("Content");
        Element layerNode = contentNode.element("Layer");
        List<XmlEntity> xmlEntities = listNodes(layerNode);
        return xmlEntities;
    }

    /**
     * 遍历当前节点元素下面的所有(元素的)子节点
     *
     * @param node
     */
    public static List<XmlEntity> listNodes(Element node) {

        List<XmlEntity> xmlEntityList = new ArrayList<>();
        List<Element> elementList = node.elements();

        for (Element element: elementList){
            XmlEntity xmlEntity = new XmlEntity();
            xmlEntity.setNode(element.getName());
            // 获取当前节点的所有属性节点
            List<Attribute> list = element.attributes();
            // 遍历属性节点
            Map<String, String> attributeMap = list.stream()
                    .collect(Collectors.toMap(Attribute::getName, Attribute::getValue));
            xmlEntity.setAttributes(attributeMap);

            Element textElement = element.element("TextCode");
            if (textElement != null){
                xmlEntity.setContent(element.getTextTrim());
            }
            xmlEntityList.add(xmlEntity);
        }


        // 当前节点下面子节点迭代器
        //Iterator<Element> it = node.elementIterator();
        // 递归遍历
        //while (it.hasNext()) {
            // 获取某个子节点对象
        //    Element e = it.next();
            // 对子节点进行遍历
        //    listNodes(e, xmlEntities);
        //}
        return xmlEntityList;
    }

    /**
     * 根据节点名获取节点
     *
     * @param node
     */
    public static List<Element> getAllEle(Element node, List<Element> elems) {

        elems.add(node);
        List<Element> listElement = node.elements();// 所有一级子节点的list
        for (Element e : listElement) {// 遍历所有一级子节点
            getAllEle(e, elems);// 递归
        }

        return elems;
    }

    /**
     * 修改、增加xml属性值 元素无重复 不能修改根元素 String elem 需要修改的标签名, String key属性名, String
     * value修改后的值
     *
     * @return
     * @throws Exception
     */
    public static boolean edit(Document doc, String elem, String key, String value, String outUrl) throws Exception {
        Element element = doc.getRootElement();

        // 当前节点下面子节点迭代器
        Element tmpEle = doc.getRootElement();
        List<Element> elems = new ArrayList<Element>();
        List<Element> listElement = getAllEle(element, elems);
        if (listElement.size() != 0) {
            for (Element e : listElement) {// 遍历所有一级子节点
                if (e.getName().equals(elem)) {
                    tmpEle = e;
                }
            }
        }

        if (tmpEle.isRootElement()) {
            return false;
        } else {
            // 2.通过增加同名属性的方法，修改属性值
            tmpEle.addAttribute(key, value); // key相同，覆盖；不存在key，则添加

            // 指定文件输出的位置
            writer(doc, outUrl);
            return true;
        }

    }

    /**
     * 把document对象写入新的文件
     *
     * @param document
     * @throws Exception
     */
    public static void writer(Document document, String url) throws Exception {
        // 紧凑的格式
        // OutputFormat format = OutputFormat.createCompactFormat();
        // 排版缩进的格式
        OutputFormat format = OutputFormat.createPrettyPrint();
        // 设置编码
        format.setEncoding("UTF-8");
        // 创建XMLWriter对象,指定了写出文件及编码格式
        // XMLWriter writer = new XMLWriter(new FileWriter(new
        // File("src//a.xml")),format);
        XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(new File(url)), "UTF-8"), format);
        // 写入
        writer.setEscapeText(false);
        writer.write(document);
        // 立即写入
        writer.flush();
        // 关闭操作
        writer.close();
    }

    public static void createSignaturesXml(String signs,String signTmp) throws Exception {
        Document doc1 = new SAXReader().read(new File(signs + "/Signatures.xml"));
        Element Signatures = doc1.getRootElement();
        Element Signature = Signatures.addElement("ofd:Signature");
        Signature.addAttribute("ID", "1");
        Signature.addAttribute("Type", "Seal");
        Signature.addAttribute("BaseLoc", signTmp.substring(signTmp.length()-6) + "/Signature.xml");
        OfdXmlUtil.writer(doc1, signs + "/Signatures.xml");
    }


    public static void main(String [] args) throws Exception {
        getAttributeIdByPath("D:\\UserData\\Desktop\\P020210115388704606182-植物\\Doc_0\\Pages\\Page_1\\Content.xml", "aa");

    }
}
