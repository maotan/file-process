package com.example.file.service.xml;

import com.example.file.model.PatentSecond;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
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
public class PatentXmlUtil {
    /*public static String getAttributeIdByPath(String path,String attribute) throws Exception {
        List<XmlEntity> xmlList = readXmlByPath(path);
        int mediaId = 0;
        for (XmlEntity xml : xmlList) {
            if (xml.getNode().equals(attribute) && xml.getAttributes().get("ID") != null) {
                mediaId = mediaId>Integer.parseInt(xml.getAttributes().get("ID"))?mediaId:Integer.parseInt(xml.getAttributes().get("ID"));
            }
        }
        String id=String.valueOf(mediaId+1);
        return id;
    }*/

    /**
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static void readXmlByPath(String path, PatentSecond patentSecond) throws Exception {
        File file = new File(path);
        // 创建saxReader对象
        SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        // 获取根节点元素对象
        Element rootNode = document.getRootElement();
        Element bibliographicNode = rootNode.element("BibliographicData");
        readTitle(bibliographicNode, patentSecond);
        readApplicationNum(bibliographicNode, patentSecond);
        readPublicationNum(bibliographicNode, patentSecond);
        readClassNum(bibliographicNode, patentSecond);
        readApplicantInfo(bibliographicNode, patentSecond);

        Element abstractNode = rootNode.element("Abstract");
        readSummary(abstractNode, patentSecond);

    }


    /** 申请人/发明者/代理 信息 */
    public static void readApplicantInfo(Element bibliographicNode, PatentSecond patentSecond){
        Element partiesNode = bibliographicNode.element("Parties");
        // 申请人
        Element applicantNode = partiesNode.element("ApplicantDetails").element("Applicant");
        Element addressBookNode =  applicantNode.element("AddressBook");
        Element nameNode = addressBookNode.element("Name");
        Element addressNode = addressBookNode.element("Address");
        Element addressTextNode = addressNode.element("Text");
        patentSecond.setApplicant(nameNode.getTextTrim());
        patentSecond.setPresentApplicant(nameNode.getTextTrim());
        patentSecond.setAddress(addressTextNode.getTextTrim());
        patentSecond.setPresentAddress(addressTextNode.getTextTrim());

        //发明者
        Element inventorNode = partiesNode.element("InventorDetails").element("Inventor");
        Element inventAddressNode = inventorNode.element("AddressBook");
        Element inventNameNode = inventAddressNode.element("Name");
        patentSecond.setInventor(inventNameNode.getTextTrim());

        //代理
        Element agentNode = partiesNode.element("AgentDetails").element("Agent");
        Element agentAddressNode = agentNode.element("AddressBook");
        Element agencyNode = agentNode.element("Agency");
        patentSecond.setAgency(agencyNode.element("AddressBook").element("OrganizationName").getTextTrim());
        patentSecond.setAgentPerson(agentAddressNode.element("Name").getTextTrim());
    }

    /** InventionTitle */
    public static void readTitle(Element bibliographicNode, PatentSecond patentSecond){
        Element inventionTitleNode = bibliographicNode.element("InventionTitle");
        patentSecond.setTitle(inventionTitleNode.getTextTrim());
    }

    /** 读取分类号 */
    public static void readClassNum(Element bibliographicNode, PatentSecond patentSecond){
        Element classificationIPCRDetailsNode = bibliographicNode.element("ClassificationIPCRDetails");
        List<Element> classificationIPCRNodeList = classificationIPCRDetailsNode.elements();
        StringBuilder sb = new StringBuilder();
        for (Element element: classificationIPCRNodeList){
            Element sectionNode = element.element("Section");
            Element mainClassNode = element.element("MainClass");
            Element subclassNode = element.element("Subclass");
            Element mainGroupNode = element.element("MainGroup");
            Element subgroupNode = element.element("Subgroup");
            Element ipcVersionDateNode = element.element("IPCVersionDate");

            sb.append(sectionNode.getTextTrim());
            sb.append(mainClassNode.getTextTrim());
            sb.append(subclassNode.getTextTrim());
            sb.append(mainGroupNode.getTextTrim());
            sb.append("/");
            sb.append(subgroupNode.getTextTrim());
            sb.append("(");
            sb.append(ipcVersionDateNode.getTextTrim());
            sb.append(")");
            sb.append(";");
        }
        if (sb.length()>0){
            sb.deleteCharAt(sb.length()-1);
        }
        patentSecond.setClassNum(sb.toString());
    }

    /** 读取公告号 公告日期 */
    public static void readPublicationNum(Element bibliographicNode, PatentSecond patentSecond){
        Element applicationReferenceNode = bibliographicNode.element("PublicationReference");
        Element documentIDNode =  applicationReferenceNode.element("DocumentID");
        Element applicantNumCnNode =  documentIDNode.element("WIPOST3Code");
        Element applicantDocNumberNode = documentIDNode.element("DocNumber");
        Element applicantDateNode = documentIDNode.element("Date");
        patentSecond.setPublicNum(applicantNumCnNode.getTextTrim() + applicantDocNumberNode.getTextTrim());
        patentSecond.setPublicTime(applicantDateNode.getTextTrim());
    }

    /** 读取申请号 申请日期 */
    public static void readApplicationNum(Element bibliographicNode, PatentSecond patentSecond){
        Element applicationReferenceNode = bibliographicNode.element("ApplicationReference");
        Element documentIDNode =  applicationReferenceNode.element("DocumentID");
        Element applicantNumCnNode =  documentIDNode.element("WIPOST3Code");
        Element applicantDocNumberNode = documentIDNode.element("DocNumber");
        Element applicantDateNode = documentIDNode.element("Date");

        patentSecond.setApplyNum(applicantNumCnNode.getTextTrim() + applicantDocNumberNode.getTextTrim());
        patentSecond.setApplyTime(applicantDateNode.getTextTrim());

    }

    /** 读取摘要 */
    public static void readSummary(Element abstractNode, PatentSecond patentSecond){
        Element paragraphs = abstractNode.element("Paragraphs");
        patentSecond.setSummary(paragraphs.getTextTrim());
    }


    public static void explainDir(String dirPath) throws Exception {
        File dirFile = new File(dirPath);
        if (dirFile.isDirectory()){
            File[] listFiles = dirFile.listFiles();
            for (File file: listFiles){
                String name = file.getName();
                Integer len = name.length();
                String suffix = name.substring(len-3, len);
                String fullPath = dirPath + "/" + name;
                if( len>3 ){
                    if (suffix.equals("XML")){
                        PatentSecond patentSecond = new PatentSecond();
                        readXmlByPath(fullPath, patentSecond);
                    } else if(suffix.equals("TIF")){
                        Files.copy(Paths.get(fullPath), Paths.get(fullPath));
                    }
                }
            }
        }

    }


    public static void main(String [] args) throws Exception {
        PatentSecond patentSecond = new PatentSecond();
        readXmlByPath("D:\\UserData\\Desktop\\20200804\\20200804-1-001\\1\\CN102019000019562CN00001114814930ABIAZH20200804CN009\\CN102019000019562CN00001114814930ABIAZH20200804CN009.XML", patentSecond);
        //Files.copy()
    }
}
