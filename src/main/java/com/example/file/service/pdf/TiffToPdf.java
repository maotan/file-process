package com.example.file.service.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.codec.TiffImage;
import com.sun.org.apache.xerces.internal.impl.Constants;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public class TiffToPdf {


    /**
     * 得到生成的PDF文件（多个合并）
     * @param savepath PDF文件保存的路径
     * @param pdfName pdf文件名称
     * @param files 需要进行合并的pdf文件
     * @return String
     */
    public static boolean getPDF(String savepath, String pdfName, String[] files){
        boolean flag=false;
        savepath = savepath+File.separator+pdfName+".pdf";
        //如果是多个PDF文件进行合并
        if (files.length > 0){
            flag = mergePdfFiles(files, savepath); //多个PDF文件合并
        }
        return flag;
    }
    /**
     * 多个PDF合并功能
     * @param files 多个PDF的路径
     * @param savepath 生成的新PDF路径
     * @return boolean boolean
     */
    public static boolean mergePdfFiles(String[]files,String savepath) {
        try{
            File saveFile = new File(savepath);
            if(!saveFile.exists() || saveFile.isDirectory()){//不存在或是个目录，都需要则新建
                saveFile.createNewFile();
            }
            com.lowagie.text.Document document = new com.lowagie.text.Document(new PdfReader(files[0]).getPageSize(1));
            PdfCopy copy = new PdfCopy(document, new FileOutputStream(savepath));
            document.open();
            for (int i = 0; i < files.length; i++){
                if(files[i].toUpperCase().endsWith(".PDF")){
                    PdfReader reader = new PdfReader(files[i]);
                    int n = reader.getNumberOfPages();
                    for (int j = 1; j <= n; j++){
                        document.newPage();
                        PdfImportedPage page = copy.getImportedPage(reader, j);
                        copy.addPage(page);
                    }
                }
            }
            document.close();
            return true;
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }catch(DocumentException e){
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 该方法用来将制定的tiff文件转换成pdf文件
     * @param pdfPath:生成后的pdf所在目录
     * @param tiff:需要进行转换的tiff文件
     * @param pdfName:生成的pdf文件名称，只需要规定名称即可，不需要规定后缀名
     * */
    public static boolean toPDF(String pdfPath,File tiff,String pdfName){
        boolean result = false;
        //1、判断给定的文件是否是tif文件:既不是tif格式结尾，也不是tiff格式结尾
        if(tiff.getName().toUpperCase().endsWith(".TIF") || tiff.getName().toUpperCase().endsWith(".TIFF")){//两种格式都是扫描文件格式
            //2、获取tiff文件
            pdfName = pdfName + ".pdf";//以当前tif文件命名pdf文件
//			System.out.println(pdf);//
            com.lowagie.text.Document document = new com.lowagie.text.Document(PageSize.A4); //设置文档大小
            int pages = 0, comps = 0;
            try{
                //获取实例
//	        	System.out.println("PDF目录："+pdfPath+File.separator+pdfName);
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfPath+File.separator+pdfName));
                document.open();//打开文档
                PdfContentByte cb = writer.getDirectContent();
                RandomAccessFileOrArray ra = null;
//	         	System.out.println("************\nTIFF文件所在目录："+tiff.getAbsolutePath());
                ra = new RandomAccessFileOrArray(tiff.getAbsolutePath());
                comps = TiffImage.getNumberOfPages(ra);
                for (int c = 0; c < comps; ++c){
                    Image img = TiffImage.getTiffImage(ra, c + 1);
                    if (img != null){
                        img.scalePercent(7200f / img.getDpiX(), 7200f / img.getDpiY());
                        document.setPageSize(new Rectangle(img.getScaledWidth(), img.getScaledHeight()));
                        img.setAbsolutePosition(0, 0);
                        cb.addImage(img);
                        document.newPage();
                        ++pages;
                    }
                }
                ra.close();
                document.close();
                result = true;
            } catch (Throwable e){
                e.printStackTrace();
            }
        }else{
            return result;
        }
        return  result ;
    }


    private Integer findTiff(String pdfPath,String path,Integer i){
        File mainFile = new File(path);
        if(mainFile.isDirectory() && mainFile.exists()){//是文件夹且存在
            File[] tiff=mainFile.listFiles();
            for(File tif:tiff){
                if(tif.getName().toUpperCase().endsWith(".TIF") || tif.getName().toUpperCase().endsWith(".TIFF")){//后缀名为tif或tiff的扫描文件
                    toPDF(pdfPath,tif,i.toString());
                    i++;
                }
            }
        }
        return i;
    }

    public String escapeExprSpecialWord(String keyword) {
        if(keyword!=null && keyword.trim().length()>0){
            String[] fbsArr = {"/","\\",":","*","?","\"","<",">","|", "(", ")","+", "[", "]", "^", "{", "}","、"};
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key,"");
                }
            }
        }
        return keyword;
    }

    /**
     * 该方法用来获取SXAReader对象
     * */
    public SAXReader getSAXReader(){
        SAXReader saxReader = new SAXReader();
        /* 在读取文件时，去掉dtd的验证，可以缩短运行时间  */
        try {
//			saxReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);//可能需要网络，所以不用它
            saxReader.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.LOAD_EXTERNAL_DTD_FEATURE, false);  //设置不需要校验头文件
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return saxReader;
    }



    /**
     * @Desc：该方法用来生成pdf文件
     * @param:父级文件夹名称
     * @param:需要处理的文件所在目录
     * */
    public  boolean createPDFFile(String parentFileName,String newFilePath) throws Exception {
        boolean result = false;
        File listFile = new File(newFilePath + File.separator + "201310018231NEW.XML");
        if (listFile.isFile() && listFile.exists()) {//是文件且存在
            String appNum = "", appName = "", appAJ = "", appPer = "";
            Document doc = (Document) this.getSAXReader().read(listFile);//读取文件
            //1、查找申请号
            List<Node> els = doc.selectNodes("//doc-number");
            if (els.size() == 0 || els.get(0).getText().equals("")) {
                String errorInfo = "没有申请号！\r\n";
                return false;
            } else {
                appNum = els.get(1).getText();//获取申请号内容
            }

            //2、查找名称
            els = doc.selectNodes("//invention-title");
            if (els.size() == 0 || els.get(0).getText().equals("")) {
                String errorInfo = "没有名称！\r\n";
                return false;
            } else {
                appName = els.get(0).getText();
            }

            //3、查找申请人信息
//			System.out.println("申请人信息所在目录："+(newFilePath+File.separator+parentFileName));
            File appInfoFile = new File(newFilePath + File.separator + parentFileName);
            if (appInfoFile.exists() && appInfoFile.isDirectory()) {
                String xmlName = "";
                Document xmlDoc = null;
                SAXReader xmlSAX = this.getSAXReader();
                for (File infoXml : appInfoFile.listFiles()) {
                    xmlName = infoXml.getName().toUpperCase();
                    if (xmlName.startsWith(parentFileName.toUpperCase()) && xmlName.endsWith(".XML")) {//是XML文件且以父级文件夹名称打头
//						System.out.println(infoXml.exists()+"\n"+infoXml.getAbsolutePath());
                        xmlDoc = xmlSAX.read(infoXml);//读取文件
                        els = xmlDoc.selectNodes("//cn-inventor");//查找申请人名称标签
                        appPer = els.get(0).getText();//获取申请人名称
                    }
                }
            }

            //4、查找内部编号
            els = doc.selectNodes("//doc-number");
            if (els.size() > 0) appAJ = els.get(0).getText();
            if (appAJ != null && appAJ.length() > 0) {//说明有内部编号
                if (appAJ.toUpperCase().startsWith("AJ")) {//避免出现小写aj的情况，所以全部转成大写进行比较
                    //内部编号只取XSQ后面的部分，但因为XSQ有可能不存在，所以不用XSQ进行截取，用最后一个下划线截取
                    appAJ = appAJ.substring(appAJ.lastIndexOf("_") + 1);
                }
            } else {
                appAJ = "";
            }

            //5、查找附件信息
            String[] prefix = null;
            //附件列表
            els = doc.selectNodes("//img");
            String text = "";
            if (els.size() > 0) {//说明有附件
                els = doc.selectNodes("//img");//获取附件列表
                prefix = new String[els.size()];
                for (int i = 0; i < els.size(); i++) {
                    text = els.get(i).getText();
                    if (text.indexOf(".") > 0)
                        prefix[i] = text.substring(0, text.indexOf(".")).trim();
                }
            }

            //6、处理主扫描件
            String tiffFolder = newFilePath + File.separator + parentFileName + File.separator + parentFileName;
//			System.out.println("主文件目录："+tiffFolder);
            Integer i = 1;
            i = findTiff(newFilePath, tiffFolder, i);

            //7、处理附件
            for (int m = 0; m < prefix.length; m++) {
                tiffFolder = newFilePath + File.separator + parentFileName + File.separator + prefix[m] + "(" + (m + 1) + ")";
//				System.out.println("附件目录："+tiffFolder);
                i = findTiff(newFilePath, tiffFolder, i);
            }

            //8、拼接扫描件文件集合，包括主扫描件和附件的扫描件
            String[] tiffList = new String[i - 1];
            for (int j = 0; j < tiffList.length; j++) {
                tiffList[j] = newFilePath + File.separator + (j + 1) + ".pdf";
            }
            //9、合成PDF
            appName = appName.replaceAll("<.*?>", "");//去掉当中的标签
            appName = escapeExprSpecialWord(appName);//去掉特殊字符
            String pdfName = "(" + parentFileName + ")" + appNum + appName + appPer + appAJ;//最后生成的pdf名称:申请号+名称+申请人+内部编号
//			String pdfName = appNum+appName+appPer+appAJ;//最后生成的pdf名称:申请号+名称+申请人+内部编号
//			System.out.println("PDF文件名称："+pdfName);
            result = getPDF("D:/UserData/Desktop/2013100182310", pdfName, tiffList);
        }
        return result;
    }


    public static void main(String[] args) {
        TiffToPdf tiffToPdf = new TiffToPdf();
        try {
            tiffToPdf.createPDFFile("2013100182310","D:/UserData/Desktop/2013100182310/2013100182310");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
