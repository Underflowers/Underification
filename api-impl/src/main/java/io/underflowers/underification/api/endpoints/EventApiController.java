package io.underflowers.underification.api.endpoints;

import io.underflowers.underification.api.EventsApi;
import io.underflowers.underification.api.RulesApi;
import io.underflowers.underification.api.model.Event;
import io.underflowers.underification.api.model.Rule;
import io.underflowers.underification.entities.*;
import io.underflowers.underification.repositories.BadgeRepository;
import io.underflowers.underification.repositories.PointScaleRepository;
import io.underflowers.underification.repositories.RuleRepository;
import io.underflowers.underification.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.servlet.ServletRequest;
import javax.validation.Valid;

@Controller
public class EventApiController implements EventsApi {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ServletRequest request;

    @Override
    public ResponseEntity<Event> triggerEvent(@Valid Event event) {
        // Fetch the linked application from the token passed in request attribute
        ApplicationEntity applicationEntity = (ApplicationEntity) request.getAttribute("applicationEntity");
        // Try to fetch the current user from the Event pass in parameter
        UserEntity userEntity = userRepository.findByAppUserId(event.getAppUserId());
        if(userEntity == null){
            // The user doesn't exists => create now in the fly
            userEntity = new UserEntity();
            userEntity.setAppUserId(event.getAppUserId());
            userEntity.setApplication(applicationEntity);
            userRepository.save(userEntity);
        }

        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }


}
