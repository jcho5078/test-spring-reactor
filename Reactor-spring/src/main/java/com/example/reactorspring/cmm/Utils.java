package com.example.reactorspring.cmm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class Utils {

    private final Map<String, Sinks.Many<ServerSentEvent<String>>> sinks = new HashMap<>();


    public Flux<ServerSentEvent<String>> connect(String id){

        if(sinks.containsKey(id)){
            return sinks.get(id).asFlux();
        }

        sinks.put(id, Sinks.many().multicast().onBackpressureBuffer());
        return sinks.get(id).asFlux().doOnCancel(() -> {
            sinks.get(id).tryEmitComplete();
            //System.out.println(sink2.get(param).);
            sinks.remove(id);
        });
    }

    public Mono<Boolean> successMessageSend(String id, String msg) {
        return Mono.just(id)
                .flatMap(mapId -> {
                    if (sinks.containsKey(id)) {      //알림을 받을 사용자가 현재 SSE로 연결한 경우 알림 발송
                        sinks.get(id).tryEmitNext(
                                ServerSentEvent.<String>builder()
                                        .event("config")
                                        .data(msg)
                                        .comment("comment")
                                        .build()
                        );
                        return Mono.just(true);
                    }
                    return Mono.error(new Exception("존재하지 않는 SSE 채널입니다."));
                });
    }

    public Flux<Boolean> successMessageSendToAll(String msg){
        return Flux.just(sinks).flatMap(sink -> {
            sink.forEach((s, serverSentEventMany) -> {
                serverSentEventMany.tryEmitNext(ServerSentEvent.<String>builder()
                        .event("config")
                        .data(msg)
                        .comment("comment")
                        .build());
            });
            return Flux.just(true);
        }).onErrorReturn(false);
    }
}
