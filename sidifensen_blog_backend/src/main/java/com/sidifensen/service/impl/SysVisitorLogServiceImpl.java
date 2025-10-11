package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.SysVisitorLogQueryDto;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.entity.SysVisitorLog;
import com.sidifensen.domain.vo.SysVisitorLogVo;
import com.sidifensen.domain.vo.VisitorStatisticsVo;
import com.sidifensen.domain.vo.VisitorTrendVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.mapper.SysVisitorLogMapper;
import com.sidifensen.service.SysVisitorLogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 访客记录 Service 实现类
 *
 * @author sidifensen
 * @since 2025-10-06
 */
@Service
@Slf4j
public class SysVisitorLogServiceImpl extends ServiceImpl<SysVisitorLogMapper, SysVisitorLog> implements SysVisitorLogService {

    @Resource
    private SysVisitorLogMapper sysVisitorLogMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public void insertVisitorRecord(SysVisitorLog sysVisitorLog) {
        // 构建锁的key：userId(或null) + IP + 设备类型
        String lockKey = "visitor:" + 
                         (sysVisitorLog.getUserId() != null ? sysVisitorLog.getUserId() : "guest") + 
                         ":" + sysVisitorLog.getIp() + 
                         ":" + sysVisitorLog.getDevice();
        
        // 使用 synchronized 锁定该访客的操作，防止并发插入重复数据 intern() 确保相同字符串使用同一个对象实例作为锁
        synchronized (lockKey.intern()) {
            // 计算1小时前的时间
            Date oneHourAgo = new Date(System.currentTimeMillis() - 60 * 60 * 1000);
            
            // 查重逻辑：基于用户id、IP、设备类型进行查重，且访问时间在1小时内
            LambdaQueryWrapper<SysVisitorLog> qw = new LambdaQueryWrapper<SysVisitorLog>()
                    .eq(sysVisitorLog.getUserId() != null, SysVisitorLog::getUserId, sysVisitorLog.getUserId())
                    .isNull(sysVisitorLog.getUserId() == null, SysVisitorLog::getUserId)
                    .eq(SysVisitorLog::getIp, sysVisitorLog.getIp())
                    .eq(SysVisitorLog::getDevice, sysVisitorLog.getDevice())
                    .ge(SysVisitorLog::getVisitTime, oneHourAgo); // 1小时内的记录

            // 查询是否已存在1小时内相同的访客记录
            SysVisitorLog existingLog = sysVisitorLogMapper.selectOne(qw);

            if (existingLog != null) {
                // 存在1小时内的记录，更新访问时间
                existingLog.setVisitTime(sysVisitorLog.getVisitTime());
                sysVisitorLogMapper.updateById(existingLog);
            } else {
                // 不存在1小时内的记录，执行插入
                sysVisitorLogMapper.insert(sysVisitorLog);
            }
        }
    }

    @Override
    public List<SysVisitorLogVo> getVisitorLogList() {
        // 查询所有访客日志，按访问时间倒序
        LambdaQueryWrapper<SysVisitorLog> qw = new LambdaQueryWrapper<SysVisitorLog>()
                .orderByDesc(SysVisitorLog::getVisitTime);

        List<SysVisitorLog> logs = sysVisitorLogMapper.selectList(qw);

        // 转换为VO并填充用户名
        return logs.stream().map(log -> {
            SysVisitorLogVo vo = BeanUtil.copyProperties(log, SysVisitorLogVo.class);

            // 如果有用户ID，查询用户名
            if (log.getUserId() != null) {
                SysUser user = sysUserMapper.selectById(log.getUserId());
                if (user != null) {
                    vo.setUsername(user.getUsername());
                }
            }

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SysVisitorLogVo> searchVisitorLog(SysVisitorLogQueryDto queryDto) {
        // 构建查询条件
        LambdaQueryWrapper<SysVisitorLog> qw = new LambdaQueryWrapper<SysVisitorLog>()
                .eq(queryDto.getUserId() != null, SysVisitorLog::getUserId, queryDto.getUserId())
                .like(StrUtil.isNotBlank(queryDto.getIp()), SysVisitorLog::getIp, queryDto.getIp())
                .eq(StrUtil.isNotBlank(queryDto.getDevice()), SysVisitorLog::getDevice, queryDto.getDevice())
                .ge(queryDto.getVisitTimeStart() != null, SysVisitorLog::getVisitTime, queryDto.getVisitTimeStart())
                .le(queryDto.getVisitTimeEnd() != null, SysVisitorLog::getVisitTime, queryDto.getVisitTimeEnd())
                .orderByDesc(SysVisitorLog::getVisitTime);

        List<SysVisitorLog> logs = sysVisitorLogMapper.selectList(qw);

        // 转换为VO并填充用户名
        return logs.stream().map(log -> {
            SysVisitorLogVo vo = BeanUtil.copyProperties(log, SysVisitorLogVo.class);

            // 如果有用户ID，查询用户名
            if (log.getUserId() != null) {
                SysUser user = sysUserMapper.selectById(log.getUserId());
                if (user != null) {
                    vo.setUsername(user.getUsername());
                }
            }

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVisitorLogs(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BlogException(BlogConstants.VisitorLogIdsRequired);
        }

        // 使用 MyBatis-Plus 的 removeByIds 方法批量删除
        boolean success = this.removeByIds(ids);
        if (success) {
        } else {
            log.warn("批量删除访客日志失败");
        }
    }

    @Override
    public List<VisitorTrendVo> getVisitorTrend(Integer days) {

        // 参数校验
        if (days == null || days <= 0) {
            throw new BlogException(BlogConstants.QueryDaysMustGreaterThanZero);
        }

        if (days > 365) {
            throw new BlogException(BlogConstants.QueryDaysCannotExceed365);
        }

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        // 查询日期范围内的所有访客记录
        Date startDateTime = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());//转换为当天的0点
        Date endDateTime = Date.from(endDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());//转换为当天的23:59:59

        LambdaQueryWrapper<SysVisitorLog> qw = new LambdaQueryWrapper<SysVisitorLog>()
                .ge(SysVisitorLog::getVisitTime, startDateTime)
                .le(SysVisitorLog::getVisitTime, endDateTime);

        List<SysVisitorLog> logs = sysVisitorLogMapper.selectList(qw);

        // 按日期分组统计
        Map<String, Long> dateCountMap = logs.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getVisitTime().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate() //提取日期,去掉时间部分
                                .toString(),
                        Collectors.counting()
                ));

        // 构建结果列表（确保所有日期都有数据，没有访问的日期补0）
        List<VisitorTrendVo> result = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            String dateStr = currentDate.toString();
            VisitorTrendVo vo = new VisitorTrendVo()
                    .setDate(dateStr)
                    .setCount(dateCountMap.getOrDefault(dateStr, 0L));
            result.add(vo);
            currentDate = currentDate.plusDays(1);
        }

        return result;
    }

    @Override
    public VisitorStatisticsVo getVisitorStatistics() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate weekStart = today.minusDays(6);
        LocalDate monthStart = today.withDayOfMonth(1);

        VisitorStatisticsVo vo = new VisitorStatisticsVo();

        // 今日访客数
        vo.setTodayCount(countByDate(today, today));

        // 昨日访客数
        vo.setYesterdayCount(countByDate(yesterday, yesterday));

        // 本周访客数（最近7天）
        vo.setWeekCount(countByDate(weekStart, today));

        // 本月访客数
        vo.setMonthCount(countByDate(monthStart, today));

        // 总访客数
        vo.setTotalCount(sysVisitorLogMapper.selectCount(null));

        // PC端访客数
        LambdaQueryWrapper<SysVisitorLog> pcQw = new LambdaQueryWrapper<SysVisitorLog>()
                .eq(SysVisitorLog::getDevice, "PC");
        vo.setPcCount(sysVisitorLogMapper.selectCount(pcQw));

        // 移动端访客数
        LambdaQueryWrapper<SysVisitorLog> mobileQw = new LambdaQueryWrapper<SysVisitorLog>()
                .eq(SysVisitorLog::getDevice, "Mobile");
        vo.setMobileCount(sysVisitorLogMapper.selectCount(mobileQw));

        return vo;
    }

    @Override
    public Long getTodayVisitorCount() {
        return countByDate(LocalDate.now(), LocalDate.now());
    }

    /**
     * 统计指定日期范围内的访客数
     */
    private Long countByDate(LocalDate startDate, LocalDate endDate) {
        Date startDateTime = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDateTime = Date.from(endDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        LambdaQueryWrapper<SysVisitorLog> qw = new LambdaQueryWrapper<SysVisitorLog>()
                .ge(SysVisitorLog::getVisitTime, startDateTime)
                .le(SysVisitorLog::getVisitTime, endDateTime);

        return sysVisitorLogMapper.selectCount(qw);
    }

}

