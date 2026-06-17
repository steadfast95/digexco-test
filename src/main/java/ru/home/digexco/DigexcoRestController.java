package ru.home.digexco;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpResponse;

@RestController
public class DigexcoRestController {
    private final DigexcoGrpcClient client;

    public DigexcoRestController(DigexcoGrpcClient client) {
        this.client = client;
    }

    @GetMapping(path = "/call")
    public HttpStatus call(){
        client.calling();
        return HttpStatus.OK;
    }
}
