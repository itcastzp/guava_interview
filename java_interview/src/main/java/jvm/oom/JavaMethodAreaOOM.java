package jvm.oom;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @program: guava_interview
 * @description: 测试方法区内存溢出
 * @author: AlphaGO
 * @create: 2019-08-15 16:54
 **/
public class JavaMethodAreaOOM {

    /**
     * -vmargs: -XX:PermSize=10M -XX:MaxPermSize=10M
     * throw Exception: java.lang.OutOfMemoryError thrown from the UncaughtExceptionHandler in thread "main"
     *
     * @param args
     */
    public static void main(String[] args) {
        for (; ; ) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(OOMExample1.OOMObject.class);
            enhancer.setCallback(new MethodInterceptor() {
                @Override
                public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

                    return methodProxy.invokeSuper(o, objects);
                }
            });

            enhancer.create();


        }


    }


}
