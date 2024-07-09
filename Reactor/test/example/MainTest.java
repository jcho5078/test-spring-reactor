package example;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class MainTest {

    private Main testMain = new Main();

    @Test
    void test1() {
        StepVerifier.create(testMain.test1())
                .expectNext(4,5)
                .verifyComplete();
    }

    @Test
    void test2() {
        StepVerifier.create(testMain.test3())
                .expectNext(1,2,3)
                .verifyComplete();
    }

    @Test
    void test3(){
        StepVerifier.create(testMain.test4())
                .expectNextCount(100)
                .verifyComplete();
    }

    @Test
    void test4(){
        StepVerifier.create(testMain.test5())
                .expectNextCount(100)
                .verifyComplete();
    }

    @Test
    void test5(){
        StepVerifier.create(testMain.test6())
                .expectNextCount(10)
                .verifyComplete();
    }

    @Test
    void test6_1(){
        StepVerifier.create(testMain.test7_1())
                .expectNext(30)
                .verifyComplete();
    }

    @Test
    void test6_2(){
        StepVerifier.create(testMain.test7_2())
                .expectNext(0)
                .verifyComplete();
    }

    @Test
    void test7_1(){
        StepVerifier.create(testMain.test8_1())
                .expectNext(1,2,3,4,5,6)
                .verifyComplete();
    }

    @Test
    void test7_2(){
        StepVerifier.create(testMain.test8_2())
                .expectNext(2,4,6)
                .verifyComplete();
    }

    @Test
    void test8(){
        StepVerifier.create(testMain.test9())
                .expectNext(10L)
                .verifyComplete();
    }

    @Test
    void test9(){
        StepVerifier.create(testMain.test10())
                .expectNext(1,2,3,4,10)
                .verifyComplete();
    }

    @Test
    void test10(){
        StepVerifier.create(testMain.test11())
                .expectNext(55)
                .verifyComplete();
    }

    @Test
    void test11(){
        testMain.test12();
    }
}