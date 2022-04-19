package com.ozangunalp;

import java.time.Duration;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.logging.Logger;

import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.Record;

@Path("/clicks")
public class ClickResource {

    @Inject
    Logger log;

    @Channel("messages-out")
    MutinyEmitter<Record<String, PointerEvent>> emitter;

    @Channel("messages")
    Multi<PointerEvent> clickEvents;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Void> sendMessage(@HeaderParam("user-agent") String userAgent,
                                 PointerEvent event) {
        log.infof("Click from %s : %s", userAgent, event);
        return emitter.send(Record.of(event.userId, event));
    }

    public void startup(@Observes StartupEvent event) {
        clickEvents.log().group().intoLists().of(3, Duration.ofSeconds(2))
                .subscribe().with(ms -> log.infof("Received messages %s", ms));
    }

}