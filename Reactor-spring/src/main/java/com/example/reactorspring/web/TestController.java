package com.example.reactorspring.web;

import com.example.reactorspring.dto.UserCreateRequest;
import com.example.reactorspring.dto.UserResponse;
import com.example.reactorspring.dto.UserUpdateRequest;
import com.example.reactorspring.service.MvcTestService;
import com.example.reactorspring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    private List<String> msgList;
    @PostMapping("/user")
    public Mono<UserResponse> createUser(@RequestBody UserCreateRequest request) throws Exception{
        return userService.create(request.getName(), request.getEmail())
                .map(UserResponse::of);
    }

    @GetMapping("/users")
    public Flux<UserResponse> findAllUsers() throws Exception {
        var users = userService.findAll();
        return users.map(UserResponse::of);
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
}
