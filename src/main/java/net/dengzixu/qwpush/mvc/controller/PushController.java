package net.dengzixu.qwpush.mvc.controller;

import net.dengzixu.qwpush.mvc.service.PushService;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = {"/push", "/v1/push"})
public class PushController {

    private final PushService pushService;

    @Autowired
    public PushController(PushService pushService) {
        this.pushService = pushService;
    }

    @GetMapping("/{authKey}/{messageType}/{messageContent}")
    public ResponseEntity<String> push(@PathVariable String authKey,
                                       @PathVariable String messageContent,
                                       @PathVariable String messageType,
                                       @RequestParam(required = false, defaultValue = "false") boolean base64,
                                       @RequestParam(required = false, defaultValue = "false") boolean debug) {


        if (base64) {
            messageContent = new String(Base64.decodeBase64(messageContent));
        }


        if (!debug){
            pushService.push(messageType, messageContent);
        }

        return ResponseEntity.ok("success");
    }


}
