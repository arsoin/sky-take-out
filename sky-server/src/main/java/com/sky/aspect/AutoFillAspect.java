package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/*
* 自定义切面，实现公共字段自动填充的逻辑
*
* */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /*
    * 切入点
    * mapper包下的所有类，所有方法，以及任意参数
    * */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {}


    /*
    *
    * 前置通知，在通知中进行公共字段的赋值
    * */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始进行公共字段的自动填充....");

        //因为有不同的操作，因此需要先获取到当前被拦截的方法上的数据库操作类型，通过我们的那个注解来判断
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();  //方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);  //获得方法上的注解对象
        OperationType operationType = autoFill.value();  //获得数据库的操作类型，就是那个枚举类定义的几个类型

        //获取到当前被拦截的方法的参数，即那个实体对象 ,如果参数过多，那么一定把实体参数放在第一个，就是mapper那里。因为这里只获取第一个
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            return;
        }
        Object entity = args[0];

        //准备赋值数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //根据不同的操作类型，为对应的属性通过 反射 来赋值
        if(operationType == OperationType.INSERT){
            //需要为4个公共字段都赋值,通过反射来赋值
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射为对象属性赋值
                setCreateTime.setAccessible(true);
                setCreateUser.setAccessible(true);
                setUpdateTime.setAccessible(true);
                setUpdateUser.setAccessible(true);
                setCreateTime.invoke(entity,now);
                setUpdateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpdateUser.invoke(entity,currentId);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }else if(operationType == OperationType.UPDATE){
            //只需要为2个公共字段赋值
            entity = args[0];
            try {
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                setUpdateUser.setAccessible(true);
                setUpdateUser.setAccessible(true);
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
