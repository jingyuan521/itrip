package cn.itrip.itripsearch.service;

import cn.itrip.beans.vo.hotel.SearchHotelVO;
import cn.itrip.common.EmptyUtils;
import cn.itrip.common.Page;
import cn.itrip.itripsearch.beans.vo.ItripHotelVO;
import cn.itrip.itripsearch.dao.BaseQuery;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service("SearchHotelServiceimpl")
public class SearchHotelServiceimpl implements SearchHotelService {
    @Autowired
    private BaseQuery<ItripHotelVO> itripHotelVOBaseQuery;
    @Override
    //根据条件查询酒店分页
    public Page<ItripHotelVO> searchHotelPage(SearchHotelVO vo, Integer pageNo, Integer pageSize) throws Exception {
        SolrQuery solrQuery = new SolrQuery("*:*");
        StringBuffer stringBuffer = new StringBuffer();
        //用来判断追加 的sql是否是第一条语句
        int tempFlag =0 ;
        //非空判断
        if (EmptyUtils.isNotEmpty(vo)){
            //以目的地位查询条件
            if (EmptyUtils.isNotEmpty(vo)){
                stringBuffer.append("destination:"+vo.getDestination());
                tempFlag = 1;
            }
            //酒店星级（过滤查询）
            if (EmptyUtils.isNotEmpty(vo.getHotelLevel())){
                solrQuery.addFilterQuery("hotelLwevel"+vo.getHotelLevel());
            }
            if (EmptyUtils.isNotEmpty(vo.getKeywords())){
                if (tempFlag==1){
                    stringBuffer.append("AND keyword:"+vo.getKeywords());
                }else{
                    stringBuffer.append("keyword:"+vo.getKeywords());
                }
            }
            //通过酒店特色进行查询
            if (EmptyUtils.isNotEmpty(vo.getFeatureIds())){
                StringBuffer buffer = new StringBuffer("(");
                int flag = 0;
                String featureIdArray[] = vo.getFeatureIds().split(",");
                for (String featureId : featureIdArray){
                    if (flag == 0){
                        buffer.append("featureIds"+"*,"+featureId+",*");
                    }else{
                        buffer.append("OR featureIds:"+"*,"+featureId+",*");
                    }
                    flag++;
                }
                buffer.append(")");
                solrQuery.addFilterQuery(buffer.toString());
            }
            //城市
            if (EmptyUtils.isNotEmpty(vo.getTradeAreaIds())) {
                StringBuffer buffer = new StringBuffer("(");
                int flag = 0;
                String tradeAreaIdArray[] = vo.getTradeAreaIds().split(",");
                for (String tradeAreaId : tradeAreaIdArray) {
                    if (flag == 0) {
                        buffer.append(" tradingAreaIds:" + "*," + tradeAreaId + ",*");
                    } else {
                        buffer.append(" OR tradingAreaIds:" + "*," + tradeAreaId + ",*");
                    }
                    flag++;
                }
                buffer.append(")");
                solrQuery.addFilterQuery(buffer.toString());
            }
            //根据价格筛选
            if (EmptyUtils.isNotEmpty(vo.getMaxPrice())) {   //[* To 700]
                solrQuery.addFilterQuery("minPrice:" + "[* TO " + vo.getMaxPrice() + "]");
            }
            if (EmptyUtils.isNotEmpty(vo.getMinPrice())) {//[500 To *]
                solrQuery.addFilterQuery("minPrice:" + "[" + vo.getMinPrice() + " TO *]");
            }

            //评价排序
            if (EmptyUtils.isNotEmpty(vo.getAscSort())) {
                solrQuery.addSort(vo.getAscSort(), SolrQuery.ORDER.asc);
            }

            if (EmptyUtils.isNotEmpty(vo.getDescSort())) {
                solrQuery.addSort(vo.getDescSort(), SolrQuery.ORDER.desc);
            }
        }

        //根据目的地和关键字
        if (EmptyUtils.isNotEmpty(stringBuffer.toString())) {
            solrQuery.setQuery(stringBuffer.toString());
        }
        Page<ItripHotelVO> page = itripHotelVOBaseQuery.queryPage(solrQuery, pageNo, pageSize, ItripHotelVO.class);
        return page;
        }


//根据热门城市插叙酒店
    @Override
    public List<ItripHotelVO> searchHotelListByHotcity(Integer cityid, Integer pageSize) throws Exception {
        SolrQuery solrQuery = new SolrQuery("*:*");
        if (EmptyUtils.isNotEmpty(cityid)){
            solrQuery.addFilterQuery("cityid"+cityid);
        }else{
            return null;
        }
        List<ItripHotelVO> hotelVOS = itripHotelVOBaseQuery.queryList(solrQuery,pageSize,ItripHotelVO.class );
        return hotelVOS;
    }
}
