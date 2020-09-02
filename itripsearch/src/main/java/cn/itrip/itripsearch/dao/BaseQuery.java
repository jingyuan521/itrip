package cn.itrip.itripsearch.dao;

import cn.itrip.common.Constants;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.Page;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 通用的封装的查询solr的基础查询类
 */
@Component
public class BaseQuery<T> {

    @Autowired
    private SolrClient solrClient;

    static Logger logger = Logger.getLogger(BaseQuery.class);


    /***
     * 使用SolrQuery 查询分页数据
     */
    public Page<T> queryPage(SolrQuery query, Integer pageNo, Integer pageSize, Class clazz) throws Exception {
        //设置起始页数
        Integer rows=EmptyUtils.isEmpty(pageSize) ? Constants.DEFAULT_PAGE_SIZE : pageSize;
        Integer currPage=(EmptyUtils.isEmpty(pageNo) ? Constants.DEFAULT_PAGE_NO - 1 : pageNo - 1);
        Integer start=currPage*rows;
        query.setStart(start);
        //一页显示多少条
        query.setRows(rows);
        QueryResponse queryResponse = solrClient.query(query);
        SolrDocumentList docs = queryResponse.getResults();
        Page<T> page = new Page(currPage+1, query.getRows(), new Long(docs.getNumFound()).intValue());
       /* List<T> list = queryResponse.getResults();*/
        page.setRows((List<T>) docs);
        return page;
    }

    /***
     * 使用SolrQuery 查询列表数据
     */
    public List<T> queryList(SolrQuery query, Integer pageSize, Class clazz) throws Exception {
        //设置起始页数
        query.setStart(0);
        //一页显示多少条
        query.setRows(EmptyUtils.isEmpty(pageSize) ? Constants.DEFAULT_PAGE_SIZE : pageSize);
        QueryResponse queryResponse = solrClient.query(query);
        List<T> list = queryResponse.getBeans(clazz);
        return list;
    }
}
