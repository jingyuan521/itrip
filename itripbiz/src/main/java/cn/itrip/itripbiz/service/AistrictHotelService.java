package cn.itrip.itripbiz.service;

import cn.itrip.beans.pojo.ItripAreaDic;
import cn.itrip.beans.pojo.ItripLabelDic;
import cn.itrip.beans.vo.ItripImageVO;
import cn.itrip.beans.vo.hotel.HotelVideoDescVO;
import cn.itrip.beans.vo.hotel.ItripSearchDetailsHotelVO;
import cn.itrip.beans.vo.hotel.ItripSearchFacilitiesHotelVO;
import cn.itrip.beans.vo.hotel.ItripSearchPolicyHotelVO;

import java.util.List;
import java.util.Map;

public interface AistrictHotelService {

    public ItripAreaDic getTtripAreaDicById(Long id) throws Exception;

    //查询热门城市商圈
    public List<ItripAreaDic> getItripAreaDicListByMap(Map<String,Object> par) throws Exception;

    //查询酒店图片
    public List<ItripImageVO> getItripImageMap(Map<String,Object> par) throws Exception;

    //根据酒店id查询酒店特色，商圈，酒店名称
    public HotelVideoDescVO getVideoDescById(Long id) throws Exception;

    //根据酒店的id查询酒店的设施
    public ItripSearchFacilitiesHotelVO getItripHotelFacilById(Long id) throws Exception;

    //查询酒店特色列表
    public List<ItripSearchDetailsHotelVO> queryHotelDetails(Long id) throws Exception;

    //查询特色列表
    public List<ItripLabelDic> getItripLabelDicByMap(Map<String,Object> par) throws Exception;

    //根据id查询酒店的政策
    public ItripSearchPolicyHotelVO queryHotelPolicy(Long id) throws Exception;




}
