package flyer.worker.core;

import org.springframework.util.StopWatch;


import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Package: flyer.worker.core
 * @Description: 测试版本
 * @author: liuxin
 * @date: 2017/9/14 下午5:15
 */
public class SmileMasterTest {

    public static void main(String[] args) throws Exception {


        /**
         * 测试使用Map维护Thread
         */
        StopWatch watch = new StopWatch("Master-Worker测试");
        watch.start("测试ThreadMap");
        Integer testthreadMapResult = testthreadMap(10,100000);
        System.out.println("测试ThreadMap result:"+testthreadMapResult);
        watch.stop();



        /**
         * 使用线程池维护现场
         */
        watch.start("测试ExecutorService");
        Integer testExecutorServiceResult = testExecutorService(100,100000);
        System.out.println("测试ExecutorService result:"+testExecutorServiceResult);
        watch.stop();
        System.out.println(watch.prettyPrint());
    }


    public static Integer testthreadMap(int countWorker,int countTask) {
        SmileMaster<Integer> smileMaster = new SmileMaster(new SmileWorker(new DefaultWorkerHandler() {
            @Override
            public Integer handler(Integer input) {
                return input * input;
            }
        }), countWorker);

        //提交100个子任务
        for (int i = 0; i < countTask; i++) {
            smileMaster.submit(i);
        }

        smileMaster.execute();

        Map<String, Object> resultMap = smileMaster.getResultMap();
        int re = 0;
        for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
            String key = entry.getKey();
            Integer o = (Integer) resultMap.get(key);
            re += o;
            resultMap.remove(key);
        }
        return re;
    }

    public static Integer testExecutorService(int countWorker,int countTask) {
        ExecutorService executorService2 = Executors.newCachedThreadPool();
        SmileMaster smileMaster1 = new SmileMaster(new SmileWorker(new DefaultWorkerHandler() {
            @Override
            public Integer handler(Integer input) {
                return input * input;
            }
        }), countWorker, executorService2);
        for (int i = 0; i < countTask; i++) {
            smileMaster1.submit(i);
        }
        smileMaster1.executePool();
        Map<String, Object> resultMapPool = smileMaster1.getResultMapPool();
        int re1 = 0;
        for (Map.Entry<String, Object> entry : resultMapPool.entrySet()) {
            String key = entry.getKey();
            Integer o = (Integer) resultMapPool.get(key);
            re1 += o;
            resultMapPool.remove(key);
        }
        return re1;
    }
}