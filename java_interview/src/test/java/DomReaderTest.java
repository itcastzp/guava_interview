import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.Iterator;
import java.util.List;

public class DomReaderTest {
    public static void main(String[] args) {
        String resultdept = "<?xml version=\"1.0\" encoding=\"GB2312\"?>" + "<hr version=\"7.0\">" +
                "<title>人力资源管理系统</title>" +
                "<language>zh-cn</language>" +
                "<element>" +
                "<b0110_0>010101</b0110_0>" +
                "<unique_id>FFF19040-7C6C-48AD-8192-1A67DD831A7C</unique_id>" +
                "<codesetid>UM</codesetid>" +
                "<codeitemdesc>集团领导</codeitemdesc>" +
                "<parentid>0101</parentid>" +
                "<parentdesc>集团总部</parentdesc>" +
                "<b01ae>1</b01ae>" +
                "<b01ad></b01ad>" +
                "<grade>3</grade>" +
                "<sdate>2019-07-08 15:52:52.0</sdate>" +
                "<a0000>3</a0000>" +
                "<corcode />" +
                "<b0125>62260001</b0125>" +
                "<b0110>010101</b0110>" +
                "<nc>1</nc>" +
                "</element>" +
                "<element>" +
                "<b0110_0>01010108</b0110_0>" +
                "<unique_id>507E027E-3E1D-4377-83A4-DBCCCCA5976E</unique_id>" +
                "<codesetid>UM</codesetid>" +
                "<codeitemdesc>集团警卫团</codeitemdesc>" +
                "<parentid>010101</parentid>" +
                "<parentdesc>集团领导</parentdesc>" +
                "<b01ae>1</b01ae>" +
                "<b01ad>02</b01ad>" +
                "<grade>4</grade>" +
                "<sdate>2019-07-08 15:52:52.0</sdate>" +
                "<a0000>13</a0000>" +
                "<corcode />" +
                "<b0125 />" +
                "<b0110>01010108</b0110>" +
                "<nc>1</nc>" +
                "</element>" +
                "</hr>";

        try {
            Document document = DocumentHelper.parseText(resultdept);
            Element rootElement = document.getRootElement();
            System.out.println(rootElement);
            //XPATH命名查询所有的名为element节点
            List<Element> list = rootElement.selectNodes("//element");
            for (Element element : list) {
                Iterator iterator = element.elementIterator();
                while (iterator.hasNext()) {
                    Element next = (Element) iterator.next();
                    System.out.println("---------");
                    System.out.println(next.getName() + "->" + next.getData());
                }
                System.out.println(element.getData() + element.getName());
            }


        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }
}
