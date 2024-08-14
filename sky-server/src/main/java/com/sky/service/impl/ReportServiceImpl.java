package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;


    /**
     * 统计指定日期的营业额
     * 查询订单表中 状态为完成的订单
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //先解决dataList 格式是： 日期，以逗号分隔，例如：2022-10-01,2022-10-02,2022-10-03
        //这个集合存放从开始日期到结束日期范围的每一天的日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            //计算指定日期的后一天，对应的日期
            begin =  begin.plusDays(1);
            dateList.add(begin);
        }
        //营业额，以逗号分隔，例如：406.0,1520.0,75.0 需要查询数据库
        //存放每天的营业额
        List<Double> turnoverList = new ArrayList<>();
        for(LocalDate date : dateList){
            //根据日期来查营业额,状态已完成的订单
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            // select sum(amount) from orders where order_time > beginTime and  order_time < endTime and status = 5
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            //返回的就是当天的营业额,如果当天营业额是 0 那么这里返回来的是 空 ，我们需要给他转成 0
            Double turnover =  orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }

        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))  //转换成字符串
                .turnoverList(StringUtils.join(turnoverList,",")) //转换成字符串
                .build();
    }
}
