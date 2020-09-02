package cn.itrip.itripbiz.service;

import cn.itrip.beans.dto.Dto;
import cn.itrip.beans.pojo.*;
import cn.itrip.beans.vo.order.ItripPersonalOrderRoomVO;
import cn.itrip.beans.vo.store.StoreVO;
import cn.itrip.common.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface HotelordersService {

    public boolean validateRoomStore(Map<String,Object> param)throws Exception;

    public ItripHotelOrder getItripHotelOrderById(Long id)throws Exception;

    public ItripHotelRoom getItripHotelRoomById(Long id)throws Exception;
    /**
     * 通过订单id查看订单详情-具体房型信息等- add by hanlu

     */
    public ItripPersonalOrderRoomVO getItripHotelOrderRoomInfoById(Long orderId)throws Exception;

    public Page<ItripHotelOrder> queryItripHotelOrderPageByMap(Map<String,Object> param, Integer pageNo, Integer pageSize)throws Exception;

    public List<ItripHotelOrder> getItripHotelOrderListByMap(Map<String,Object> param)throws Exception;

    public Integer itriptxModifyItripTradeEnds(Map<String,Object> param)throws Exception;

    public List<ItripTradeEnds>	getItripTradeEndsListByMap(Map<String,Object> param)throws Exception;

    public boolean updateRoomStore(Map<String, Object> param) throws Exception;

    public List<StoreVO> queryRoomStore(Map<String,Object> param)throws Exception;

    public ItripHotel getItripHotelById(Long id)throws Exception;

    public BigDecimal getOrderPayAmount(int count, Long roomId) throws Exception;

    public Map<String, String> itriptxAddItripHotelOrder(ItripHotelOrder itripHotelOrder, List<ItripUserLinkUser> itripOrderLinkUserList)throws Exception;









}
