package com.example.reactorspring.web;

import com.example.reactorspring.cmm.Utils;
import com.example.reactorspring.dto.UserCreateRequest;
import com.example.reactorspring.dto.UserResponse;
import com.example.reactorspring.dto.UserUpdateRequest;
import com.example.reactorspring.service.MvcTestService;
import com.example.reactorspring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final UserService userService;

    private final MvcTestService mvcTestService;

    private final Utils utils;

    private List<String> msgList;
    @PostMapping("/user")
    public Mono<UserResponse> createUser(@RequestBody UserCreateRequest request) throws Exception{
        return userService.create(request.getName(), request.getEmail())
                .map(UserResponse::of);
    }

    @GetMapping("/users")
    public Flux<UserResponse> findAllUsers() throws Exception {
        var te = 1;
        return userService.findAll()
                .map(UserResponse::of);
    }

    @GetMapping("/user/{id}")
    public Mono<ResponseEntity<UserResponse>> findUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(u -> ResponseEntity.ok(UserResponse.of(u)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/user/{id}")
    public Mono<ResponseEntity<?>> deleteUser(@PathVariable Long id) {
        return userService.deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @PutMapping("/user/{id}")
    public Mono<ResponseEntity<UserResponse>> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        return userService.update(id, request.getName(), request.getEmail())
                .map(u -> ResponseEntity.ok(UserResponse.of(u)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PutMapping("/mvc/{msg}")
    public Mono<Map> testWebClient(@PathVariable String msg){
        return mvcTestService.testMvcToWebClient(msg);
    }

    /**
     * webClient 테스트
     * 일괄 flux 호출 후 mvc 스레드 동작 테스트
     * @return
     */
    @PutMapping("/mvcMany")
    public Flux<Map> testWebClientMany(){
        List<Mono<Map>> monoList = new ArrayList<>();

        for(var i=0; i<100; i++){
            monoList.add(mvcTestService.testMvcToWebClient(Integer.toString(i) + " : 요청"));
        }

        return Flux.concat(monoList);
    }

    /**
     * webClient 테스트
     * 순차 subscribe 호출 후 mvc 스레드 동작 테스트
     * @return
     */
    @PutMapping("/mvcMany2")
    public Flux<Object> testWebClientMany2() throws Exception{
        List list = new ArrayList<>();

        for(var i=0; i<100; i++){
            list.add(mvcTestService.testMvcToWebClient(Integer.toString(i)).subscribe());
        }
        return Flux.just(new HashMap().put("msg", "성공"));
    }

    /**
     * sse 접속 테스트
     * @return
     */
    @GetMapping("/testSSE")
    public Flux<ServerSentEvent<String>> connectSSE1() {

        Mono<String> firstResponse = Mono.just("첫번째 응답입니다😆");
        Mono<String> secondResponse = Mono.just("두번째 응답입니다😎");

        Flux<ServerSentEvent<String>> responseStream = Flux.concat(
                firstResponse.map(data -> ServerSentEvent.<String>builder().data(data).build()),
                secondResponse.delayElement(Duration.ofSeconds(5)).map(data -> ServerSentEvent.<String>builder().data(data).build())
        );

        return responseStream;
    }

    /**
     * sse 접속
     * @param id
     * @return
     */
    @GetMapping(value = "/testSSE2/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> connectSSE2(@PathVariable String id) {

        return utils.connect(id);
    }

    /**
     * sse 메시지 전달
     * @param id
     * @param msg
     * @return
     */
    @GetMapping(value = "/testSSE2/send/{id}/{msg}")
    public Mono<ResponseEntity<Boolean>> sseSuccessConnection(@PathVariable  String id, @PathVariable  String msg) {
        return utils.successMessageSend(id, msg)
                .map(isSuccess -> new ResponseEntity<>(isSuccess, HttpStatus.OK));
    }

    /**
     * sse 메시지 전달
     * @param msg
     * @return
     */
    @GetMapping(value = "/testSSE2/all/{msg}")
    @ResponseBody
    public Flux<Boolean> testSSE3NoParam(@PathVariable  String msg) {

        return utils.successMessageSendToAll(msg);
    }
}
