package example;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.LinkedList;

public class Main {

    public static void main(String[] args){
        /*test1().subscribe(i -> {System.out.println(i);});

        test2().subscribe(i -> {System.out.println(i);});*/

        /*test12().subscribe();*/
        test13();
    }

    /**
     * 기본 flux 사용
     * @return
     */
    public static Flux<Integer> test1(){
        Integer[] arr = {1,2,3,4};
        return Flux.fromArray(arr).map(i -> {
            return i+1;
        }).filter(i -> i>3);
    }

    /**
     * 기본 mono 사용
     * @return
     */
    public static Mono<Integer> test2(){
        return Mono.just(1).map(i -> ++i);
    }

    /**
     * take  사용 (변환없이 갯수 반환)
     * @return
     */
    public static Flux<Integer> test3(){
        return Flux.range(1,10)
                .take(3).log();
    }

    /**
     * flatMap  사용(비동기적으로처리, 순서 보장안됨.)
     * @return
     */
    public static Flux<Integer> test4(){
        return Flux.range(1,10)
                .flatMap(i -> Flux.range(i*10, 10))
                .delayElements(Duration.ofMillis(10))//병렬 딜레이처리
                .log();
    }

    /**
     * concatMap  사용(flatMap과 달리 순서 적용함)
     * @return
     */
    public static Flux<Integer> test5(){
        return Flux.range(1,10)
                .concatMap(i -> Flux.range(i*10, 10))
                .delayElements(Duration.ofMillis(10))
                .log();
    }

    /**
     * flatMapMany  사용(여러개의 스트림을 변환할때 사용)
     * @return
     */
    public static Flux<Integer> test6(){
        return Mono.just(10)
                .flatMapMany(i -> Flux.range(1, i))
                .log();
    }

    /**
     * switchIfEmpty  사용(전달받은 값이 없을때 처리방법 추가)
     * defaultIfEmpty
     * @return
     */
    public static Mono<Integer> test7_1(){
        return Mono.just(0)
                .filter(i -> i>10)
                .defaultIfEmpty(30)
                .log();
    }

    /**
     * switchIfEmpty  사용(전달받은 값이 없을때 처리방법 추가)
     * defaultIfEmpty
     * @return
     */
    public static Flux<Integer> test7_2(){
        /*return Flux.just(1,2,3,4,5)
                .filter(i -> i>5)
                .switchIfEmpty(Flux.just(0).log());*/

        return Flux.just(1,2,3,4,5)
                .filter(i -> i>5)
                .switchIfEmpty(Flux.error(new Exception("Not exist value")))
                .log();
    }

    /**
     * merge  사용(다수의 publisher을 하나의 컬렉션으로 합산)
     * @return
     */
    public static Flux<Integer> test8_1(){
        return Flux.range(1,3)
                .mergeWith(Flux.range(4, 3))
                .log();
    }

    /**
     * zip  사용(다수의 publisher을 요소별로 합산)
     * @return
     */
    public static Flux<Integer> test8_2(){
        return Flux.zip(Flux.range(1,3), Flux.range(1,3))
                .map(i -> i.getT1() + i.getT2())
                .log();
    }

    /**
     *  count 사용
     * @return
     */
    public static Mono<Long> test9(){
        return Flux.range(1, 10)
                .count()
                .log();
    }

    /**
     *  distinct 사용
     * @return
     */
    public static Flux<Integer> test10(){
        return Flux.range(1, 10)
                .mergeWith(Flux.range(5,5))
                .distinct()
                .log();
    }

    /**
     *  Reduce 사용 (컬렉션 요소 전부 합산) - 연속되는 숫자 계산시 용이
     * @return
     */
    public static Mono<Integer> test11(){
        return Flux.range(1, 10)
                .reduce((i, j) -> i+j)
                .log();
    }

    /**
     *  groupBy 사용 (동일한 값, 특정값을 묶어서 사용할 수 있도록)
     * @return
     */
    public static Flux<Integer> test12(){
        return Flux.range(1, 10)
                .groupBy(i -> i % 2 == 0 ? "key1" : "key2")
                .flatMap(group -> {
                    return group.scan((idx, value) -> {
                        System.out.println("idx : " + idx);
                        System.out.println("group : " + group.key());
                        System.out.println("value : " + value);
                        return value;
                    });
                });
    }

        public static void test13(){
            Flux.range(1, 10)
                    .groupBy(i -> i % 2 == 0 ? "key1" : "key2")
                    .subscribe(group -> group.scan(new LinkedList<>(), (list, value) -> {
                        System.out.println("list : " + list.toString());
                        System.out.println("group : " + group.key());
                        System.out.println("value : " + value);
                        list.add(value);
                        return list;
                    }).filter(list -> !list.isEmpty()).subscribe(data -> {
                        System.out.println("result key : " + group.key());
                        System.out.println("result data : " + data);
                    }));


                    /*.flatMap(group -> {
                        return group.scan(new LinkedList<>(), (list, value) -> {
                            System.out.println("list : " + list.toString());
                            System.out.println("group : " + group.key());
                            System.out.println("value : " + value);
                            list.add(value);
                            return list;
                        });
                    }).filter(list -> !list.isEmpty())
                    .subscribe(data -> System.out.println("result key : " + data));*/
        }
}
