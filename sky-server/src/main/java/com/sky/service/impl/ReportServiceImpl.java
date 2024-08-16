package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
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
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;


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

    /**
     * 统计指定时间内用户数据
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin =  begin.plusDays(1);
            dateList.add(begin);
        }

        //截止到某一天总用户数量 //用户总量，以逗号分隔，例如：200,210,220
        //select count(id) from user where create_time <= ?
        List<Integer> totalUserList = new ArrayList<>();

        //新增用户数量
        //select count(id) from user where create_time > ? and create_time < ?
        List<Integer> newUserList = new ArrayList<>();

        for(LocalDate date : dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("end", endTime);
            //用户总数
            Integer totalUser = userMapper.countByMap(map);
            totalUser =  totalUser == null ? 0 : totalUser;
            totalUserList.add(totalUser);
            map.put("begin", beginTime);
            //新增用户
            Integer newUser = userMapper.countByMap(map);
            newUser = newUser == null ? 0 : newUser;
            newUserList.add(newUser);
        }
        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .build();
    }

    /**
     * 统计指定时间区间内的订单数据
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            begin =  begin.plusDays(1);
            dateList.add(begin);
        }
        //存放每天的订单总数
        List<Integer> orderCountList = new ArrayList<>();
        //存放每天的有效订单总数
        List<Integer> validOrderList = new ArrayList<>();
        //遍历集合，查询每天的有效订单数和订单总数
        for(LocalDate date : dateList){
            //查询订单总数   select count(id) from orders where order_time < ? and order_time > ?
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer orderCount = getOrderCount(beginTime, endTime, null);
            orderCountList.add(orderCount);
            //查询有效订单数  select count(id) from orders where order_time < ? and order_time > ? and status = 5
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);
            validOrderList.add(validOrderCount);
        }
        //计算时间区间内的总订单数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        //计算时间区间内的总有效订单数
        Integer totalValidOrderList = validOrderList.stream().reduce(Integer::sum).get();
        //计算订单完成率
        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0){
            orderCompletionRate =  totalValidOrderList.doubleValue()/totalOrderCount;
        }
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .orderCountList(StringUtils.join(orderCountList,","))
                .validOrderCountList(StringUtils.join(validOrderList,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(totalValidOrderList)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 订单数量查询
     * @param begin
     * @param end
     * @param status
     * @return
     */
    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end,Integer status){
        Map map = new HashMap();
        map.put("begin",begin);
        map.put("end",end);
        map.put("status",status);
        return orderMapper.countByMap(map);
    }

    /**
     * 销量排名top10
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        //查询orders 下状态完成 且 orders_detail 中销量最多的菜品
        // //商品名称列表，以逗号分隔，例如：鱼香肉丝,宫保鸡丁,水煮鱼
        // select od.name as name,sum(od.number) as number from order_detail as od , orders as o
        // where od.order_id = o.id and o.id = 5 and o.create_time > ? and o.create_time < ?
        // group by od.name order by number desc limit 0,10;
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<String> dishNameList = new ArrayList<>();
        List<Integer> dishSaleList = new ArrayList<>();
        List<GoodsSalesDTO> salesTop10List = orderMapper.getSalesTop10(beginTime, endTime);

        //这里是用stream流来完成遍历和组装到集合的
        //List<String> names = salesTop10List.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        //List<Integer> numbers = salesTop10List.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        for(GoodsSalesDTO salesTop10 : salesTop10List){
            dishNameList.add(salesTop10.getName());
            dishSaleList.add(salesTop10.getNumber());
        }
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(dishNameList,","))
                .numberList(StringUtils.join(dishSaleList,","))
                .build();
    }
}
