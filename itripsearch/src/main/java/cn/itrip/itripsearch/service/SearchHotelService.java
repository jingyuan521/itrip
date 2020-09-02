package cn.itrip.itripsearch.service;

import cn.itrip.beans.vo.hotel.SearchHotelVO;
import cn.itrip.common.Page;
import cn.itrip.itripsearch.beans.vo.ItripHotelVO;

import java.util.List;

public interface SearchHotelService {
    //搜索旅馆
    public Page<ItripHotelVO> searchHotelPage(SearchHotelVO vo,Integer pageNo,Integer pageSize) throws Exception;
    //根据热门城市查找酒店
    public List<ItripHotelVO> searchHotelListByHotcity(Integer cityid,Integer pageSize) throws Exception;
}
