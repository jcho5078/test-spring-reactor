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
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class SampleController {

    private final UserService userService;

    private final MvcTestService mvcTestService;

    private final Utils utils;

    private List<String> msgList;
    @PostMapping("/")
    public Mono<UserResponse> createUser(@RequestBody UserCreateRequest request) throws Exception{
        return userService.create(request.getName(), request.getEmail())
                .map(UserResponse::of);
    }

    @GetMapping("/")
    public Flux<UserResponse> findAllUsers() throws Exception {
        var te = 1;
        return userService.findAll()
                .map(UserResponse::of);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> findUser(@PathVariable Long id) {
        return userService.findById(id)
                .map(u -> ResponseEntity.ok(UserResponse.of(u)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<?>> deleteUser(@PathVariable Long id) {
        return userService.deleteById(id).then(Mono.just(ResponseEntity.noContent().build()));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        // user (x): 404 not found
        // user (o): 200 ok
        return userService.update(id, request.getName(), request.getEmail())
                .map(u -> ResponseEntity.ok(UserResponse.of(u)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PutMapping("/mvc/{msg}")
    public Mono<Map> testWebClient(@PathVariable String msg){
        return mvcTestService.testMvcToWebClient(msg);
    }


    @GetMapping("/testSSE")
    public Flux<ServerSentEvent<String>> testSSE() {

        Mono<String> firstResponse = Mono.just("첫번째 응답입니다😆");
        Mono<String> secondResponse = Mono.just("두번째 응답입니다😎");

        Flux<ServerSentEvent<String>> responseStream = Flux.concat(
                firstResponse.map(data -> ServerSentEvent.<String>builder().data(data).build()),
                secondResponse.delayElement(Duration.ofSeconds(5)).map(data -> ServerSentEvent.<String>builder().data(data).build())
        );

        return responseStream;
    }

    @GetMapping(value = "/testSSE2/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> testSSE2(@PathVariable String id) {

        return utils.connect(id);
    }

    @GetMapping(value = "/testSSE2/send/{id}/{msg}")
    public Mono<ResponseEntity<Boolean>> sseSuccessConnection(@PathVariable  String id, @PathVariable  String msg) {
        return utils.successMessageSend(id, msg)
                .map(isSuccess -> new ResponseEntity<>(isSuccess, HttpStatus.OK));
    }

    @GetMapping(value = "/testSSE2/all/{msg}")
    @ResponseBody
    public Flux<Boolean> testSSE3NoParam(@PathVariable  String msg) {

        return utils.successMessageSendToAll(msg);
    }
}
