package cn.itrip.itripsearch.controller;


import cn.itrip.itripsearch.beans.vo.ItripHotelVO;
import cn.itrip.itripsearch.service.SearchHotelService;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "api")
public class SolrController {
    @Autowired
    private SolrClient solrClient;
    @Resource
    private SearchHotelService searchHotelService;

    @RequestMapping("/")
    public SolrDocumentList getlist(){
        List<ItripHotelVO> list=null;
        SolrDocumentList documentList=null;

        //获取唯一的一个对象,直接给id，不需要筛选条件对象
        //  SolrDocument solrDocument= solrClient.getById("2");

        //查询条件
        SolrQuery solrQuery =new SolrQuery("*:*");
        solrQuery.setFields("id","hotelName");
/*        //根据条件执行查询
        try {
            QueryResponse queryResponse= solrClient.query(solrQuery);
            //1.第一种 自动封装到object
            list=queryResponse.getBeans(ItripHotelVO.class);

            for (ItripHotelVO v:list
            ) {
                System.out.println(v.getHotelNmae()+"--"+v.getId());
            }*/

/*            //第二种
             documentList = queryResponse.getResults();
            for (SolrDocument sd : documentList) {
                System.out.println("solrDocument==============" + sd+"--"+sd.get("id")+"--"+sd.get("hotelName"));
            }
            System.out.println("============================================");
               System.out.println(solrDocument.getFieldValue("hotelName"));
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return documentList;
    }
}
