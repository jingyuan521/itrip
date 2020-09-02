package cn.itrip.itriptrade.service;

import cn.itrip.beans.pojo.ItripHotelOrder;

public interface OrderService {
    //加载酒店订单
    public ItripHotelOrder LoadItripHotelOrder(String orderNo) throws Exception;
    //判断该订单是否已经被处理过
    public boolean processed(String orderNo) throws Exception;
    //支付成功
    public void paySuccess(String order,int payType,String tradeNo) throws Exception;
    //支付失败
    public void payFailed(String orderNo,int payType,String tradeNo) throws Exception;

}
