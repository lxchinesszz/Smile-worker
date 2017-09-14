> 本案例测试Master-Worker并发设计模式，网上关于该模式，都大差不差，本案例使用线程池的方式，重新实现，并进行测试对比。笔者希望进行优化，并作为工具使用，技术水平有限，请多多指教，提出不同意见，共同进步。

# 目录

- 使用方法
- 测试对比

```
测试ThreadMap result:216474736
测试ExecutorService result:216474736
StopWatch '': running time (millis) = 652
-----------------------------------------
ms     %     Task name
-----------------------------------------
00432  066%  测试ThreadMap
00220  034%  测试ExecutorService
```

## 使用方法


#### SmileWorkerHandler

实现 `SmileWorkerHandler` 类，为每个任务写处理方法

```
public interface SmileWorkerHandler<T> {
    default  T handler(T input) {
        return input;
    }
}

```

#### SmileMaster

该类任务主要用于任务分配，提供多种构造，运行自定义线程池和线程工厂

```
 /**
  * @param worker      Worker进程逻辑
  * @param countWorker Worker进程数量
  */
 public SmileMaster(SmileWorker worker, int countWorker)
 
 /**
  * @param worker        Worker进程逻辑
  * @param countWorker   Worker进程数量
  * @param threadFactory 自定义线程工厂
  */
 public SmileMaster(SmileWorker worker, int countWorker, ThreadFactory threadFactory)
 
 
  /**
   * @param worker   Worker进程逻辑
   * @param executor 线程池
   *                 使用线程池来维护工作队列
   */
 public SmileMaster(SmileWorker worker, int countWorker, ExecutorService executor)
```


### 测试对比及使用代码

运行返回值就是本文开头的代码块，多余的不解释，直接撸代码，欢迎提出问题，共同解决，进步

```
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
        StopWatch watch = new StopWatch();
        watch.start("测试ThreadMap");
        Integer testthreadMapResult = testthreadMap(10,100000);
        System.out.println("测试ThreadMap result:"+testthreadMapResult);
        watch.stop();



        /**
         * 使用线程池维护现场
         */
        watch.start("测试ExecutorService");
        Integer testExecutorServiceResult = testExecutorService(10,100000);
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
```